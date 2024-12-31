package org.meteordev.juno.opengl.commands;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLResource;

import java.nio.ByteBuffer;

public class GLRenderPass implements RenderPass {
    private final GLCommandList commands;

    private GraphicsPipeline pipeline;

    GLRenderPass(GLCommandList commands) {
        this.commands = commands;
    }

    @Override
    public CommandList getCommandList() {
        return commands;
    }

    @Override
    public void bindPipeline(GraphicsPipeline pipeline) {
        this.pipeline = pipeline;

        commands.add(() -> {
            commands.getDevice().getState().applyPipelineState(pipeline.getState());
            GL33C.glUseProgram(((GLResource) pipeline).getHandle());
        });
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        commands.add(() -> {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + slot);
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, ((GLResource) image).getHandle());
            GL33C.glBindSampler(slot, ((GLResource) sampler).getHandle());
        });
    }

    @Override
    public void setUniforms(ByteBuffer data, int slot) {
        long offset = commands.getDevice().getUniforms().add(data);
        long size = data.remaining();

        commands.add(() -> GL33C.glBindBufferRange(GL33C.GL_UNIFORM_BUFFER, slot, commands.getDevice().getUniformBuffer(), offset, size));
    }

    @Override
    public void draw(Buffer indexBuffer, Buffer vertexBuffer, int count) {
        int vao = commands.getDevice().getVaoManager().get(pipeline, indexBuffer, vertexBuffer);

        commands.add(() -> {
            GL33C.glBindVertexArray(vao);
            GL33C.glDrawElements(GL.convert(pipeline.getState().primitiveType), count, GL33C.GL_UNSIGNED_INT, 0);
        });
    }

    @Override
    public void end() {
    }
}
