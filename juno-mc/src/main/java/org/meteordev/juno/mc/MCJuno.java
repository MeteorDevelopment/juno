package org.meteordev.juno.mc;

import com.mojang.blaze3d.platform.GlStateManager;
import org.meteordev.juno.api.Juno;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.PrimitiveType;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.texture.*;
import org.meteordev.juno.mc.buffer.BufferBindings;
import org.meteordev.juno.mc.buffer.MCBuffer;
import org.meteordev.juno.mc.pipeline.MCDrawableBuffers;
import org.meteordev.juno.mc.pipeline.PipelineCache;
import org.meteordev.juno.mc.pipeline.StateTracker;
import org.meteordev.juno.mc.shader.ProgramManager;
import org.meteordev.juno.mc.texture.MCTexture;
import org.meteordev.juno.mc.texture.TextureManager;

import static org.lwjgl.opengl.GL11C.*;

public class MCJuno implements Juno {
    private final PipelineCache pipelineCache = new PipelineCache();
    private Pipeline boundPipeline;

    public MCJuno() {
        TextureManager.init();
    }

    @Override
    public Pipeline findPipeline(PipelineInfo info) {
        return pipelineCache.find(info);
    }

    @Override
    public Buffer createBuffer(BufferType type) {
        return new MCBuffer(type);
    }

    @Override
    public DrawableBuffers createDrawable(VertexFormat vertexFormat, Buffer vbo, Buffer ibo) {
        return new MCDrawableBuffers(vertexFormat, vbo, ibo);
    }

    @Override
    public Texture createTexture(int width, int height, Format format, Filter min, Filter mag, Wrap wrap) {
        return new MCTexture(width, height, format, min, mag, wrap);
    }

    @Override
    public void bind(Pipeline pipeline) {
        ProgramManager.bind(pipeline.getProgram());
        StateTracker.bind(pipeline.getInfo());

        this.boundPipeline = pipeline;
    }

    @Override
    public TextureBinding bind(Texture texture, int slot) {
        TextureManager.setSlot(slot);
        return TextureManager.bind(texture);
    }

    @Override
    public void enableScissor(int x, int y, int width, int height) {
        StateTracker.setScissor(true);
        GlStateManager._scissorBox(x, y, width, height);
    }

    @Override
    public void disableScissor() {
        StateTracker.setScissor(false);
    }

    @Override
    public void draw(DrawableBuffers buffers, int indices) {
        assert boundPipeline != null && boundPipeline.isValid() : "You need to bind a valid pipeline before drawing";

        if (indices <= 0) return;

        // Draw
        BufferBindings.bindDrawable(buffers);
        glDrawElements(boundPipeline.getInfo().primitiveType == PrimitiveType.TRIANGLES ? GL_TRIANGLES : GL_LINES, indices, GL_UNSIGNED_INT, 0);
        BufferBindings.bindDrawable(null);
    }
}
