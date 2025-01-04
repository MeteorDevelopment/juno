package org.meteordev.juno.mc.example;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.commands.*;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.PrimitiveType;
import org.meteordev.juno.api.pipeline.state.RenderStateBuilder;
import org.meteordev.juno.api.pipeline.vertexformat.StandardFormats;
import org.meteordev.juno.utils.MeshBuilder;
import org.meteordev.juno.utils.uniforms.UniformStruct;
import org.meteordev.juno.utils.uniforms.Uniforms;

import java.nio.ByteBuffer;

public class Example3D {
    private static final String VERTEX_SHADER = """
            #version 330 core
            
            layout (location = 0) in vec3 pos;
            
            layout (std140, binding = 0) uniform Uniforms {
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

    @UniformStruct
    public record MyUniforms(Matrix4f projection, Matrix4f view) {}

    private static GraphicsPipeline pipeline;
    private static ByteBuffer uniforms;
    private static MeshBuilder mesh;

    public static void init(Device device) {
        pipeline = device.createGraphicsPipeline(
                new RenderStateBuilder()
                        .setVertexFormat(StandardFormats.POSITION_3D)
                        .setPrimitiveType(PrimitiveType.LINES)
                        .setDepthFunc(DepthFunc.LESS)
                        .build(),
                device.createShader(ShaderType.VERTEX, VERTEX_SHADER),
                device.createShader(ShaderType.FRAGMENT, FRAGMENT_SHADER)
        );

        uniforms = BufferUtils.createByteBuffer(Uniforms.getSize(MyUniforms.class));

        mesh = new MeshBuilder(pipeline.getState());
    }

    public static void render(Device device, Matrix4f projection, Matrix4f view) {
        if (!buildMesh()) {
            return;
        }

        CommandList commands = device.createCommandList();

        Uniforms.write(new MyUniforms(projection, view), uniforms).rewind();

        RenderPass pass = commands.beginRenderPass(
                new Attachment(device.getBackBufferDepth(), LoadOp.LOAD, null, StoreOp.STORE),
                new Attachment(device.getBackBufferColor(), LoadOp.LOAD, null, StoreOp.STORE)
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
