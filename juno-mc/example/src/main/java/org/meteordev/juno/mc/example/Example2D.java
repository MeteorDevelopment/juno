package org.meteordev.juno.mc.example;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.RenderStateBuilder;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;
import org.meteordev.juno.utils.MeshBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Example2D {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec2 pos;
            layout (location = 1) in vec2 uv;
            
            out vec2 v_Uv;
            
            layout (std140, binding = 0) uniform Uniforms {
                mat4 u_Projection;
            };
            
            void main() {
                v_Uv = uv;
            
                gl_Position = u_Projection * vec4(pos, 0.0, 1.0);
            }
            """;

    private static final String FRAGMENT_SHADER = """
            #version 330 core
            
            layout (location = 0) out vec4 color;
            
            in vec2 v_Uv;
            
            layout (binding = 0) uniform sampler2D u_Texture;
            
            void main() {
                color = texture(u_Texture, v_Uv);
            }
            """;

    private static GraphicsPipeline pipeline;
    private static ByteBuffer uniforms;
    private static Image image;
    private static Sampler sampler;
    private static MeshBuilder mesh;

    public static void init(Device device) {
        pipeline = device.createGraphicsPipeline(
                new RenderStateBuilder()
                        .setVertexFormat(StandardFormats.POSITION_2D_UV)
                        .setBlendFunc(BlendFunc.alphaBlend())
                        .build(),
                device.createShader(ShaderType.VERTEX, VERTEX_SHADER),
                device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER)
        );

        uniforms = BufferUtils.createByteBuffer(4 * 4 * 4);

        image = loadImage(device, "/meteor_logo.png");
        sampler = device.createSampler(Filter.NEAREST, Filter.NEAREST, Wrap.CLAMP_TO_EDGE);

        mesh = new MeshBuilder(pipeline.getState());
        mesh.begin();
        mesh.quad(
                mesh.float2(10, 10).float2(0, 0).next(),
                mesh.float2(10, 10 + 100).float2(0, 1).next(),
                mesh.float2(10 + 100, 10 + 100).float2(1, 1).next(),
                mesh.float2(10 + 100, 10).float2(1, 0).next()
        );
        mesh.end();
    }

    public static void render(Device device) {
        Image target = device.getBackBufferColor();
        new Matrix4f().ortho2D(0, target.getWidth(), 0, target.getHeight()).get(uniforms);

        CommandList commands = device.createCommandList();

        RenderPass pass = commands.beginRenderPass(
                null,
                new Attachment(device.getBackBufferColor(), LoadOp.LOAD, null, StoreOp.STORE)
        );

        pass.bindPipeline(pipeline);
        pass.setUniforms(uniforms, 0);
        pass.bindImage(image, sampler, 0);
        mesh.draw(pass);

        pass.end();

        commands.submit();
    }

    private static Image loadImage(Device device, String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            byte[] bytes = Example.class.getResourceAsStream(path).readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length).put(bytes).rewind();

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = STBImage.stbi_load_from_memory(buffer, width, height, channels, 4);
            STBImage.stbi_set_flip_vertically_on_load(false);

            Image image = device.createImage(width.get(0), height.get(0), ImageFormat.RGBA);

            CommandList uploads = device.createCommandList();
            uploads.uploadToImage(data, image);
            uploads.submit();

            STBImage.stbi_image_free(data);
            MemoryUtil.memFree(buffer);

            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
