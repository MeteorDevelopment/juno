package org.meteordev.juno.opengl.buffer;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.opengl.GLJuno;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL45C.glCreateBuffers;
import static org.lwjgl.opengl.GL45C.nglNamedBufferData;

public class GLBuffer implements Buffer {
    public final BufferType type;
    public final int id;

    private boolean valid;

    public GLBuffer(BufferType type) {
        this.type = type;

        if (GLJuno.DSA) this.id = glCreateBuffers();
        else this.id = glGenBuffers();

        this.valid = true;
    }

    @Override
    public BufferType getType() {
        return type;
    }

    @Override
    public void write(long data, long size) {
        if (size <= 0) return;

        if (GLJuno.DSA) nglNamedBufferData(id, size, data, GL_DYNAMIC_DRAW);
        else nglBufferData(BufferBindings.bind(this), size, data, GL_DYNAMIC_DRAW);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid buffer");

        glDeleteBuffers(id);
        valid = false;
    }
}
