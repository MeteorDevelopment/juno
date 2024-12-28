package org.meteordev.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.utils.validation.ValidationLayer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Example {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec2 pos;
            layout (location = 1) in vec2 uv;
            
            out vec2 v_Uv;
            
            // Uniforms binding: 0
            layout (std140) uniform Uniforms {
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
            
            uniform sampler2D u_Texture;
            
            void main() {
                color = texture(u_Texture, v_Uv);
            }
            """;

    public static void main(String[] args) {
        Window window = new Window("Juno Example", 1280, 720);
        Device device = ValidationLayer.wrap(GLDevice.create());

        GL43C.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            String msg = MemoryUtil.memASCII(message, length);
            System.out.println(msg);
        }, 0);

        GL43C.glEnable(GL43C.GL_DEBUG_OUTPUT);

        ByteBuffer indices = MemoryUtil.memAlloc(6 * 4);
        indices.putInt(0).putInt(1).putInt(2);
        indices.putInt(2).putInt(3).putInt(0);

        ByteBuffer vertices = MemoryUtil.memAlloc(4 * 4 * 4);
        vertices.putFloat(10).putFloat(10).putFloat(0).putFloat(0);
        vertices.putFloat(10).putFloat( 10 + 400).putFloat(0).putFloat(1);
        vertices.putFloat( 10 + 400).putFloat(10 + 400).putFloat(1).putFloat(1);
        vertices.putFloat( 10 + 400).putFloat(10).putFloat(1).putFloat(0);

        Buffer ibo = device.createBuffer(BufferType.INDEX, indices.capacity(), "Vertices");
        Buffer vbo = device.createBuffer(BufferType.VERTEX, vertices.capacity(), "Indices");

        CommandList uploads = device.createCommandList();
        uploads.uploadToBuffer(indices.rewind(), ibo);
        uploads.uploadToBuffer(vertices.rewind(), vbo);
        uploads.submit();

        Shader vertex = device.createShader(ShaderType.VERTEX, VERTEX_SHADER);
        Shader fragment = device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER);

        Pipeline pipeline = device.createPipeline(
                new PipelineState()
                        .setVertexFormat(StandardFormats.POSITION_2D_UV)
                        .setBlendFunc(BlendFunc.alphaBlend()),
                vertex,
                fragment
        );

        ByteBuffer uniforms = MemoryUtil.memAlloc(4 * 4 * 4);
        new Matrix4f().ortho2D(0, 1280, 0, 720).get(uniforms).rewind();

        Image image = loadImage(device, "/meteor_logo.png");
        Sampler sampler = device.createSampler(Filter.LINEAR, Filter.LINEAR, Wrap.CLAMP_TO_EDGE);

        while (!window.shouldClose()) {
            window.pollEvents();
            device.beginFrame();

            CommandList commands = device.createCommandList();

            RenderPass pass = commands.beginRenderPass(
                    new Attachment(device.getBackBufferColor(), LoadOp.CLEAR, new ClearValue(0.9f, 0.9f, 0.9f, 1), StoreOp.STORE),
                    null
            );

            pass.bindPipeline(pipeline);
            pass.setUniforms(uniforms, 0);
            pass.bindImage(image, sampler, 0);
            pass.draw(ibo, vbo, 6);

            pass.end();

            commands.submit();

            window.swapBuffers();
        }
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
