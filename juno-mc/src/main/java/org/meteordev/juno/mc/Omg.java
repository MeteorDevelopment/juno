package org.meteordev.juno.mc;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.joml.Matrix4f;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Juno;
import org.meteordev.juno.api.JunoProvider;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.PrimitiveType;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.WriteMask;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.api.shader.ShaderInfo;
import org.meteordev.juno.api.shader.ShaderType;
import org.meteordev.juno.api.texture.Filter;
import org.meteordev.juno.api.texture.Format;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.Wrap;
import org.meteordev.juno.api.utils.MeshBuilder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Omg {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec2 pos;
            layout (location = 1) in vec2 uv;
            
            out vec2 v_Uv;
            
            uniform mat4 u_Projection;
            
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

    private static Pipeline pipeline;
    private static Texture texture;
    private static MeshBuilder mb;

    public static void init() {
        Juno juno = JunoProvider.get();

        pipeline = juno.findPipeline(new PipelineInfo()
                .setVertexFormat(StandardFormats.POSITION_2D_UV)
                .setPrimitiveType(PrimitiveType.TRIANGLES)
                .setShaders(ShaderInfo.source(ShaderType.VERTEX, VERTEX_SHADER), ShaderInfo.source(ShaderType.FRAGMENT, FRAGMENT_SHADER))
                .setBlendFunc(BlendFunc.unified(BlendFunc.Factor.SRC_ALPHA, BlendFunc.Factor.ONE_MINUS_SRC_ALPHA))
                .setWriteMask(WriteMask.COLOR)
        );

        texture = readTexture("/meteor_logo.png");

        mb = new MeshBuilder(pipeline.getInfo().vertexFormat);
        mb.begin();
        mb.quad(
                mb.float2(0, 0).float2(0, 0).next(),
                mb.float2(0, 100).float2(0, 1).next(),
                mb.float2(100, 100).float2(1, 1).next(),
                mb.float2(100, 0).float2(1, 0).next()
        );
        mb.end();
    }

    public static void render() {
        Juno juno = JunoProvider.get();
        Window window = MinecraftClient.getInstance().getWindow();

        pipeline.getProgram().getMatrix4Uniform("u_Projection").set(new Matrix4f().ortho2D(0, window.getFramebufferWidth(), 0, window.getFramebufferHeight()));
        pipeline.getProgram().getTextureUniform("u_Texture").set(juno.bind(texture, 0));

        juno.bind(pipeline);
        mb.draw();
    }

    private static Texture readTexture(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            byte[] bytes = Omg.class.getResourceAsStream(path).readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length).put(bytes).rewind();

            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            STBImage.stbi_set_flip_vertically_on_load(true);
            ByteBuffer data = STBImage.stbi_load_from_memory(buffer, width, height, channels, 4);
            STBImage.stbi_set_flip_vertically_on_load(false);

            Texture texture = JunoProvider.get().createTexture(width.get(0), height.get(0), Format.RGBA, Filter.LINEAR, Filter.LINEAR, Wrap.CLAMP_TO_EDGE);
            texture.write(data);

            STBImage.stbi_image_free(data);
            MemoryUtil.memFree(buffer);

            return texture;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
