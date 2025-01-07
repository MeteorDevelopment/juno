package org.meteordev.juno.opengl.buffer;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.opengl.*;

public class GLBuffer extends BaseGLResource implements GLResource, Buffer {
    private final GLDevice device;

    private final BufferType type;
    private final long size;
    private final String name;

    private int handle;

    public GLBuffer(GLDevice device, BufferType type, long size, String name) {
        this.device = device;

        this.type = type;
        this.size = size;
        this.name = name;

        this.handle = -1;
    }

    @Override
    public int getHandle() {
        if (handle == -1) {
            handle = GL33C.glGenBuffers();
            GL33C.glBindBuffer(GL.convert(type), handle);
            GL.setName(GLObjectType.BUFFER, handle, name);
        }

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
    protected void destroy() {
        if (handle != -1) {
            device.getVaoManager().destroy(this);

            GL33C.glDeleteBuffers(handle);
        }
    }

    @Override
    public String toString() {
        return "Buffer '" + name + "'";
    }
}
