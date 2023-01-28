package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GLCapabilities;
import org.meteordev.juno.api.Juno;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.PrimitiveType;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.texture.*;
import org.meteordev.juno.opengl.buffer.BufferBindings;
import org.meteordev.juno.opengl.buffer.GLBuffer;
import org.meteordev.juno.opengl.pipeline.GLDrawableBuffers;
import org.meteordev.juno.opengl.pipeline.PipelineCache;
import org.meteordev.juno.opengl.pipeline.StateTracker;
import org.meteordev.juno.opengl.shader.ProgramManager;
import org.meteordev.juno.opengl.texture.GLTexture;
import org.meteordev.juno.opengl.texture.TextureManager;

import static org.lwjgl.opengl.GL11C.*;

public class GLJuno implements Juno {
    public static boolean DSA = false;

    private final PipelineCache pipelineCache = new PipelineCache();
    private Pipeline lastBoundPipeline, boundPipeline;

    public GLJuno() {
        StateTracker.init();
        TextureManager.init();

        // Check DSA support
        GLCapabilities caps = GL.getCapabilities();
        if (caps.OpenGL45 || caps.GL_ARB_direct_state_access) DSA = true;
    }

    @Override
    public Pipeline findPipeline(PipelineInfo info) {
        return pipelineCache.find(info);
    }

    @Override
    public Buffer createBuffer(BufferType type) {
        return new GLBuffer(type);
    }

    @Override
    public DrawableBuffers createDrawable(VertexFormat vertexFormat, Buffer vbo, Buffer ibo) {
        return new GLDrawableBuffers(vertexFormat, vbo, ibo);
    }

    @Override
    public Texture createTexture(int width, int height, Format format, Filter min, Filter mag, Wrap wrap) {
        return new GLTexture(width, height, format, min, mag, wrap);
    }

    @Override
    public void bind(Pipeline pipeline) {
        boundPipeline = pipeline;
    }

    @Override
    public TextureBinding bind(Texture texture, int slot) {
        TextureManager.setSlot(slot);
        return TextureManager.bind(texture);
    }

    @Override
    public void enableScissor(int x, int y, int width, int height) {
        StateTracker.setScissor(true);
        GL33C.glScissor(x, y, width, height);
    }

    @Override
    public void disableScissor() {
        StateTracker.setScissor(false);
    }

    @Override
    public void draw(DrawableBuffers buffers, int indices) {
        assert boundPipeline != null && boundPipeline.isValid() : "You need to bind a valid pipeline before drawing";

        if (indices <= 0) return;

        // Bind pipeline
        if (lastBoundPipeline != boundPipeline) {
            ProgramManager.bind(boundPipeline.getProgram());
            StateTracker.bind(boundPipeline.getInfo());

            lastBoundPipeline = boundPipeline;
        }

        // Draw
        BufferBindings.bindDrawable((GLDrawableBuffers) buffers);
        glDrawElements(boundPipeline.getInfo().primitiveType == PrimitiveType.TRIANGLES ? GL_TRIANGLES : GL_LINES, indices, GL_UNSIGNED_INT, 0);
        BufferBindings.bindDrawable(null);
    }
}
