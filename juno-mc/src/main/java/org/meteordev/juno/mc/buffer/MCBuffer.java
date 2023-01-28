package org.meteordev.juno.mc.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;

public class MCBuffer implements Buffer {
    public final BufferType type;
    public final int id;

    private boolean valid;

    public MCBuffer(BufferType type) {
        this.type = type;
        this.id = GlStateManager._glGenBuffers();

        this.valid = true;
    }

    @Override
    public BufferType getType() {
        return type;
    }

    @Override
    public void write(long data, long size) {
        if (size <= 0) return;

        GlStateManager._glBufferData(BufferBindings.bind(this), MemoryUtil.memByteBuffer(data, (int) size), GL15C.GL_DYNAMIC_DRAW);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid buffer");

        GlStateManager._glDeleteBuffers(id);
        valid = false;
    }
}
