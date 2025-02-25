package org.meteordev.juno.opengl.commands;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.Sampler;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLResource;
import org.meteordev.juno.opengl.GLState;

import java.nio.ByteBuffer;

public class GLRenderPass implements RenderPass {
    private final GLCommandList commands;

    private final int width;
    private final int height;

    private GraphicsPipeline pipeline;

    GLRenderPass(GLCommandList commands, int width, int height) {
        this.commands = commands;
        this.width = width;
        this.height = height;
    }

    @Override
    public CommandList getCommandList() {
        return commands;
    }

    @Override
    public void bindPipeline(GraphicsPipeline pipeline) {
        this.pipeline = pipeline;

        commands.add(() -> {
            GLState state = commands.getDevice().getState();

            state.applyRenderState(pipeline.getState());
            state.disableScissor();

            GL33C.glUseProgram(((GLResource) pipeline).getHandle());
        });

        commands.addResource(pipeline);
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        commands.add(() -> {
            commands.getDevice().getBindings().bind(image, slot);
            commands.getDevice().getBindings().bind(sampler, slot);
        });

        commands.addResource(image);
        commands.addResource(sampler);
    }

    @Override
    public void setUniforms(ByteBuffer data, int slot) {
        long offset = commands.getUniforms().add(data);
        long size = data.remaining();

        commands.add(() -> GL33C.glBindBufferRange(GL33C.GL_UNIFORM_BUFFER, slot, commands.getDevice().getUniformBuffer(), offset, size));
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        if (x == 0 && y == 0 && width == this.width && height == this.height) {
            commands.add(() -> commands.getDevice().getState().disableScissor());
        } else {
            commands.add(() -> {
                commands.getDevice().getState().enableScissor();
                GL33C.glScissor(x, y, width, height);
            });
        }
    }

    @Override
    public void draw(Buffer indexBuffer, Buffer vertexBuffer, int count) {
        GraphicsPipeline pipeline = this.pipeline;

        commands.add(() -> {
            int vao = commands.getDevice().getVaoManager().get(pipeline, indexBuffer, vertexBuffer);

            GL33C.glBindVertexArray(vao);
            GL33C.glDrawElements(GL.convert(pipeline.getState().primitiveType()), count, GL33C.GL_UNSIGNED_INT, 0);
            GL33C.glBindVertexArray(0);
        });

        commands.addResource(indexBuffer);
        commands.addResource(vertexBuffer);
    }

    @Override
    public void end() {
    }
}
