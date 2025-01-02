package org.meteordev.juno.opengl.buffer;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.opengl.GLObjectType;
import org.meteordev.juno.opengl.GLResource;

public class GLBuffer implements GLResource, Buffer {
    private final GLDevice device;

    private final BufferType type;
    private final long size;
    private final String name;

    private final int handle;

    private boolean valid;

    public GLBuffer(GLDevice device, BufferType type, long size, String name) {
        this.device = device;

        this.type = type;
        this.size = size;
        this.name = name;

        handle = GL33C.glGenBuffers();
        GL33C.glBindBuffer(GL.convert(type), handle);
        GL.setName(GLObjectType.BUFFER, handle, name);

        valid = true;
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public BufferType getType() {
        return type;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid)
            throw new InvalidResourceException(this);

        device.getVaoManager().destroy(this);

        GL33C.glDeleteBuffers(handle);
        valid = false;
    }

    @Override
    public String toString() {
        return "Buffer '" + name + "'";
    }
}
