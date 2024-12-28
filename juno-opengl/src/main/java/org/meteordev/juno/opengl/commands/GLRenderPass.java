package org.meteordev.juno.opengl.commands;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.state.CullMode;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.image.GLImage;
import org.meteordev.juno.opengl.pipeline.GLPipeline;
import org.meteordev.juno.opengl.sampler.GLSampler;

import java.nio.ByteBuffer;

public class GLRenderPass implements RenderPass {
    private final GLCommandList commands;

    private Pipeline pipeline;

    GLRenderPass(GLCommandList commands) {
        this.commands = commands;
    }

    @Override
    public CommandList getCommandList() {
        return commands;
    }

    @Override
    public void bindPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;

        commands.add(() -> {
            PipelineState state = pipeline.getState();
            
            if (state.cullMode == CullMode.DISABLE) {
                GL33C.glDisable(GL33C.GL_CULL_FACE);
            } else {
                GL33C.glEnable(GL33C.GL_CULL_FACE);
                GL33C.glCullFace(GL.convert(state.cullMode));
            }

            if (state.blendFunc == null) {
                GL33C.glDisable(GL33C.GL_BLEND);
            } else {
                GL33C.glEnable(GL33C.GL_BLEND);
                GL33C.glBlendFuncSeparate(GL.convert(state.blendFunc.srcRGB()), GL.convert(state.blendFunc.dstRGB()), GL.convert(state.blendFunc.srcAlpha()), GL.convert(state.blendFunc.dstAlpha()));
            }

            GL33C.glDepthFunc(GL.convert(state.depthFunc));

            switch (state.writeMask) {
                case NONE -> {
                    GL33C.glColorMask(false, false, false, false);

                    GL33C.glDisable(GL33C.GL_DEPTH_TEST);
                    GL33C.glDepthMask(false);
                }
                case COLOR -> {
                    GL33C.glColorMask(true, true, true, true);

                    GL33C.glDisable(GL33C.GL_DEPTH_TEST);
                    GL33C.glDepthMask(false);
                }
                case DEPTH -> {
                    GL33C.glColorMask(false, false, false, false);

                    GL33C.glEnable(GL33C.GL_DEPTH_TEST);
                    GL33C.glDepthMask(true);
                }
                case COLOR_DEPTH -> {
                    GL33C.glColorMask(true, true, true, true);

                    GL33C.glEnable(GL33C.GL_DEPTH_TEST);
                    GL33C.glDepthMask(true);
                }
            }
            
            GL33C.glUseProgram(((GLPipeline) pipeline).handle);
        });
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        commands.add(() -> {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + slot);
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, ((GLImage) image).handle);
            GL33C.glBindSampler(slot, ((GLSampler) sampler).handle);
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
