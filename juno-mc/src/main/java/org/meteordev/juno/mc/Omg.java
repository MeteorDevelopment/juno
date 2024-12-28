package org.meteordev.juno.mc;

import net.fabricmc.api.ClientModInitializer;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.mc.events.JunoInitCallback;
import org.meteordev.juno.mc.events.Render2DCallback;
import org.meteordev.juno.utils.MeshBuilder;

import java.nio.ByteBuffer;

public class Omg implements ClientModInitializer {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec2 pos;
            
            
            // Uniforms binding: 0
            layout (std140) uniform Uniforms {
                mat4 u_Projection;
            };
            
            void main() {
                gl_Position = u_Projection * vec4(pos, 0.0, 1.0);
            }
            """;

    private static final String FRAGMENT_SHADER = """
            #version 330 core
            
            layout (location = 0) out vec4 color;
            
            void main() {
                color = vec4(1.0, 0.0, 0.0, 1.0);
            }
            """;

    private Pipeline pipeline;
    private ByteBuffer uniforms;
    private MeshBuilder mesh;

    @Override
    public void onInitializeClient() {
        JunoInitCallback.EVENT.register(this::init);
        Render2DCallback.EVENT.register(this::render);
    }

    private void init(Device device) {
        pipeline = device.createPipeline(
                new PipelineState()
                        .setVertexFormat(StandardFormats.POSITION_2D),
                device.createShader(ShaderType.VERTEX, VERTEX_SHADER),
                device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER)
        );

        uniforms = BufferUtils.createByteBuffer(4 * 4 * 4);

        mesh = new MeshBuilder(pipeline.getState());
        mesh.begin();
        mesh.quad(
                mesh.float2(10, 10).next(),
                mesh.float2(10, 10 + 100).next(),
                mesh.float2(10 + 100, 10 + 100).next(),
                mesh.float2(10 + 100, 10).next()
        );
        mesh.end();
    }

    private void render(Device device) {
        Image target = device.getBackBufferColor();
        new Matrix4f().ortho2D(0, target.getWidth(), 0, target.getHeight()).get(uniforms);

        CommandList commands = device.createCommandList();

        RenderPass pass = commands.beginRenderPass(
                new Attachment(device.getBackBufferColor(), LoadOp.LOAD, null, StoreOp.STORE),
                null
        );

        pass.bindPipeline(pipeline);
        pass.setUniforms(uniforms, 0);
        mesh.draw(pass);

        pass.end();

        commands.submit();
    }
}
