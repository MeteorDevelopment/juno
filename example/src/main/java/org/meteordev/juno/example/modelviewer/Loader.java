package org.meteordev.juno.example.modelviewer;

import de.javagl.jgltf.model.*;
import de.javagl.jgltf.model.io.GltfModelReader;
import de.javagl.jgltf.model.v2.MaterialModelV2;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Loader {
    private static final float[] MATRIX_ARRAY = new float[4 * 4];
    private static final Matrix4f MATRIX = new Matrix4f();

    private final Device device;
    private final CommandList commands;

    private final GltfModel model;

    private final Map<MeshPrimitiveModel, MeshBuffers> meshBuffers;
    private final Map<MaterialModel, Image> images;

    private Loader(Device device, GltfModel model) {
        this.device = device;
        this.commands = device.createCommandList();

        this.model = model;

        this.meshBuffers = new HashMap<>();
        this.images = new HashMap<>();
    }

    private List<ModelViewer.Mesh> load() {
        List<ModelViewer.Mesh> meshes = new ArrayList<>();
        Matrix4fStack matrices = new Matrix4fStack(32);

        for (var node : model.getSceneModels().get(0).getNodeModels()) {
            loadNode(meshes, matrices, node);
        }

        commands.submit();
        return meshes;
    }

    private void loadNode(List<ModelViewer.Mesh> meshes, Matrix4fStack matrices, NodeModel node) {
        node.computeLocalTransform(MATRIX_ARRAY);
        MATRIX.set(MATRIX_ARRAY);

        matrices.pushMatrix();
        matrices.mul(MATRIX);

        for (var mesh : node.getMeshModels()) {
            for (var primitive : mesh.getMeshPrimitiveModels()) {
                var buffers = getMeshBuffers(mesh.getName(), primitive);
                var image = getImage(primitive.getMaterialModel());

                meshes.add(new ModelViewer.Mesh(buffers.indices, buffers.vertices, matrices, image));
            }
        }

        for (var child : node.getChildren()) {
            loadNode(meshes, matrices, child);
        }

        matrices.popMatrix();
    }

    private MeshBuffers getMeshBuffers(String name, MeshPrimitiveModel primitive) {
        MeshBuffers buffers = meshBuffers.get(primitive);

        if (buffers == null) {
            Buffer indices = createIndices(name, primitive.getIndices());
            Buffer vertices = createVertices(name, primitive.getAttributes().get("POSITION"), primitive.getAttributes().get("TEXCOORD_0"));

            buffers = new MeshBuffers(indices, vertices);
            meshBuffers.put(primitive, buffers);
        }

        return buffers;
    }

    private Buffer createIndices(String name, AccessorModel accessor) {
        boolean fullInt;

        if (accessor.getComponentDataType() == short.class) {
            fullInt = false;
        } else if (accessor.getComponentDataType() == int.class) {
            fullInt = true;
        } else {
            throw new RuntimeException();
        }

        ByteBuffer data =  accessor.getAccessorData().createByteBuffer();
        ByteBuffer buffer = BufferUtils.createByteBuffer(accessor.getCount() * 4);

        for (int i = 0; i < accessor.getCount(); i++) {
            int index;

            if (fullInt) {
                index = data.getInt();
            } else {
                index = data.getShort();
            }

            buffer.putInt(index);
        }

        Buffer indices = device.createBuffer(BufferType.INDEX, buffer.capacity(), name + " - Indices");
        commands.uploadToBuffer(buffer.rewind(), indices);

        return indices;
    }

    private Buffer createVertices(String name, AccessorModel positions, AccessorModel uvs) {
        ByteBuffer positionData = positions.getAccessorData().createByteBuffer();
        ByteBuffer uvData = uvs.getAccessorData().createByteBuffer();

        ByteBuffer buffer = BufferUtils.createByteBuffer(positions.getCount() * (3 + 2) * 4);

        for (int i = 0; i < positions.getCount(); i++) {
            float x = positionData.getFloat();
            float y = positionData.getFloat();
            float z = positionData.getFloat();

            buffer.putFloat(x);
            buffer.putFloat(y);
            buffer.putFloat(z);

            buffer.putFloat(uvData.getFloat());
            buffer.putFloat(uvData.getFloat());
        }

        Buffer vertices = device.createBuffer(BufferType.VERTEX, buffer.capacity(), name + " - Vertices");
        commands.uploadToBuffer(buffer.rewind(), vertices);

        return vertices;
    }

    private Image getImage(MaterialModel material) {
        MaterialModelV2 mat = (MaterialModelV2) material;

        Image image = images.get(material);

        if (image == null) {
            TextureModel texture = mat.getBaseColorTexture();

            if (texture == null && mat.getExtensions() != null) {
                Map<String, Object> pbr = (Map<String, Object>) mat.getExtensions().get("KHR_materials_pbrSpecularGlossiness");

                if (pbr != null) {
                    Map<String, Object> diffuse = (Map<String, Object>) pbr.get("diffuseTexture");

                    if (diffuse != null) {
                        texture = this.model.getTextureModels().get((int) diffuse.get("index"));
                    }
                }
            }

            int width;
            int height;
            ByteBuffer pixels;

            if (texture == null) {
                width = 1;
                height = 1;

                pixels = BufferUtils.createByteBuffer(4);
                pixels.putInt(0, 0xFFFFFFFF);
            } else {
                ByteBuffer dataBad = texture.getImageModel().getImageData();

                ByteBuffer data = BufferUtils.createByteBuffer(dataBad.limit());
                data.put(dataBad).rewind();

                try (MemoryStack stack = MemoryStack.stackPush()) {
                    IntBuffer widthB = stack.callocInt(1);
                    IntBuffer heightB = stack.callocInt(1);
                    IntBuffer channels = stack.callocInt(1);

                    STBImage.stbi_set_flip_vertically_on_load(false);
                    pixels = STBImage.stbi_load_from_memory(data, widthB, heightB, channels, 4);

                    width = widthB.get(0);
                    height = heightB.get(0);
                }
            }

            image = device.createImage(width, height, ImageFormat.RGBA, mat.getName() + " - Base Color");
            commands.uploadToImage(pixels, image);

            if (texture != null) {
                STBImage.stbi_image_free(pixels);
            }

            images.put(material, image);
        }

        return image;
    }

    public static List<ModelViewer.Mesh> load(Device device, String path) {
        try {
            var model = new GltfModelReader().read(Loader.class.getResource(path).toURI());
            var loader = new Loader(device, model);

            return loader.load();
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private record MeshBuffers(Buffer indices, Buffer vertices) {}
}
