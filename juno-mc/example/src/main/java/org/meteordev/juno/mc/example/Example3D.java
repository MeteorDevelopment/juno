package org.meteordev.juno.mc.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.pipeline.state.PrimitiveType;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.utils.MeshBuilder;

import java.nio.ByteBuffer;

public class Example3D {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec3 pos;
            
            // Uniforms binding: 0
            layout (std140) uniform Uniforms {
                mat4 u_Projection;
                mat4 u_View;
            };
            
            void main() {
                gl_Position = u_Projection * u_View * vec4(pos, 1.0);
            }
            """;

    private static final String FRAGMENT_SHADER = """
            #version 330 core
            
            layout (location = 0) out vec4 color;
            
            void main() {
                color = vec4(1.0, 0.0, 0.0, 1.0);
            }
            """;

    private static Pipeline pipeline;
    private static ByteBuffer uniforms;
    private static MeshBuilder mesh;

    public static void init(Device device) {
        pipeline = device.createPipeline(
                new PipelineState()
                        .setVertexFormat(StandardFormats.POSITION_3D)
                        .setPrimitiveType(PrimitiveType.LINES)
                        .setDepthFunc(DepthFunc.LESS),
                device.createShader(ShaderType.VERTEX, VERTEX_SHADER),
                device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER)
        );

        uniforms = BufferUtils.createByteBuffer(2 * 4 * 4 * 4);

        mesh = new MeshBuilder(pipeline.getState());
    }

    public static void render(Device device, Matrix4f projection, Matrix4f view) {
        if (!buildMesh()) {
            return;
        }

        CommandList commands = device.createCommandList();

        projection.get(0, uniforms);
        view.get(4 * 4 * 4, uniforms);

        RenderPass pass = commands.beginRenderPass(
                new Attachment(device.getBackBufferColor(), LoadOp.LOAD, null, StoreOp.STORE),
                new Attachment(device.getBackBufferDepth(), LoadOp.LOAD, null, StoreOp.STORE)
        );

        pass.bindPipeline(pipeline);
        pass.setUniforms(uniforms, 0);
        mesh.draw(pass);

        pass.end();

        commands.submit();
    }

    private static boolean buildMesh() {
        if (MinecraftClient.getInstance().crosshairTarget instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

            float x = (float) (hit.getBlockPos().getX() + 0.5 - camera.getPos().x);
            float y = (float) (hit.getBlockPos().getY() + 1.0 - camera.getPos().y);
            float z = (float) (hit.getBlockPos().getZ() + 0.5 - camera.getPos().z);

            mesh.begin();
            mesh.line(
                    mesh.float3(x, y, z).next(),
                    mesh.float3(x, y + 0.5f, z).next()
            );
            mesh.end();

            return true;
        }

        return false;
    }
}
