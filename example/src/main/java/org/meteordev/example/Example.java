package org.meteordev.example;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.utils.MeshBuilder;
import org.meteordev.juno.utils.uniforms.UniformStruct;
import org.meteordev.juno.utils.uniforms.Uniforms;
import org.meteordev.juno.utils.validation.ValidationDevice;

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

    @UniformStruct
    public record MyUniforms(Matrix4f projection) {}

    public static void main(String[] args) {
        Window window = new Window("Juno Example", 1280, 720);
        Device device = ValidationDevice.wrap(GLDevice.create());

        GL43C.glDebugMessageCallback((source, type, id, severity, length, message, userParam) -> {
            String msg = MemoryUtil.memASCII(message, length);
            System.out.println(msg);
        }, 0);

        GL43C.glEnable(GL43C.GL_DEBUG_OUTPUT);

        Shader vertex = device.createShader(ShaderType.VERTEX, VERTEX_SHADER);
        Shader fragment = device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER);

        GraphicsPipeline pipeline = device.createGraphicsPipeline(
                new PipelineState()
                        .setVertexFormat(StandardFormats.POSITION_2D_UV)
                        .setBlendFunc(BlendFunc.alphaBlend()),
                vertex,
                fragment
        );

        MeshBuilder mesh = new MeshBuilder(pipeline.getState());
        mesh.begin();
        mesh.quad(
                mesh.float2(10, 10).float2(0, 0).next(),
                mesh.float2(10, 10 + 400).float2(0, 1).next(),
                mesh.float2(10 + 400, 10 + 400).float2(1, 1).next(),
                mesh.float2(10 + 400, 10).float2(1, 0).next()
        );
        mesh.end();

        ByteBuffer uniforms = MemoryUtil.memAlloc(Uniforms.getSize(MyUniforms.class));
        Uniforms.write(new MyUniforms(new Matrix4f().ortho2D(0, 1280, 0, 720)), uniforms).rewind();

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
            mesh.draw(pass);

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
