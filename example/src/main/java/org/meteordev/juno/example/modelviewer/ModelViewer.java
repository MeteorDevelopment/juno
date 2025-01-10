package org.meteordev.juno.example.modelviewer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.image.Filter;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.Sampler;
import org.meteordev.juno.api.image.Wrap;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.RenderStateBuilder;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.example.Camera;
import org.meteordev.juno.example.Window;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.utils.uniforms.UniformStruct;
import org.meteordev.juno.utils.uniforms.Uniforms;
import org.meteordev.juno.utils.validation.ValidationDevice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class ModelViewer {
    public record Mesh(Buffer indices, Buffer vertices, Matrix4f transform, Image image) {}

    @UniformStruct
    public record MyUniforms(Matrix4f projectionView, Matrix4f model) {}

    public static void main(String[] args) {
        Window window = new Window("Model Viewer", 1280, 720);
        Device device = ValidationDevice.wrap(GLDevice.create());

        GraphicsPipeline pipeline = device.createGraphicsPipeline(
                new RenderStateBuilder()
                        .setVertexFormat(StandardFormats.POSITION_3D_UV)
                        .setDepthFunc(DepthFunc.LESS)
                        .build(),
                loadShader(device, ShaderType.VERTEX, "/shaders/model.vert"),
                loadShader(device, ShaderType.FRAGMENT, "/shaders/model.frag")
        );

        Sampler sampler = device.createSampler(Filter.LINEAR, Filter.LINEAR, Wrap.CLAMP_TO_EDGE);

        ByteBuffer uniforms = BufferUtils.createByteBuffer(Uniforms.getSize(MyUniforms.class));

        Matrix4f projection = new Matrix4f().perspective(70, 1280f / 720f, 0.01f, 1000);
        Camera camera = new Camera(window, new Vector3f(0, 0, 10), 0, 0);

        List<Mesh> meshes = Loader.load(device, "/models/fantasy_game_inn.glb");

        long prev = System.nanoTime();

        while (!window.shouldClose()) {
            long now = System.nanoTime();
            float delta = (now - prev) / 1000000000f;
            prev = now;

            window.pollEvents();
            camera.update(delta);

            CommandList commands = device.createCommandList();

            RenderPass pass = commands.beginRenderPass(
                    new Attachment(device.getBackBufferDepth(), LoadOp.CLEAR, new ClearValue(1), StoreOp.STORE),
                    new Attachment(device.getBackBufferColor(), LoadOp.CLEAR, new ClearValue(0.9f, 0.9f, 0.9f, 1), StoreOp.STORE)
            );

            pass.bindPipeline(pipeline);

            for (Mesh mesh : meshes) {
                Uniforms.write(new MyUniforms(
                        new Matrix4f(projection).mul(camera.matrix),
                        mesh.transform
                ), uniforms).rewind();

                pass.bindImage(mesh.image, sampler, 0);
                pass.setUniforms(uniforms, 0);
                pass.draw(mesh.indices, mesh.vertices, (int) (mesh.indices.getSize() / 4));
            }

            pass.end();

            commands.submit();

            window.swapBuffers();
        }
    }

    private static Shader loadShader(Device device, ShaderType type, String path) {
        try {
            byte[] bytes = ModelViewer.class.getResourceAsStream(path).readAllBytes();
            return device.createShader(type, new String(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
