package org.meteordev.juno.opengl;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class GrowableByteBuffer {
    private final int alignment;

    private ByteBuffer buffer;

    public GrowableByteBuffer(int alignment, int initialCapacity) {
        this.alignment = alignment;

        buffer = MemoryUtil.memAlloc(initialCapacity);
    }

    public void reset() {
        buffer.rewind();
        buffer.limit(buffer.capacity());
    }

    public long add(ByteBuffer data) {
        int offset = buffer.position();
        offset = (int) Math.ceil((double) offset / alignment) * alignment;

        if (offset + data.remaining() > buffer.capacity()) {
            int size = Math.max((int) (buffer.capacity() * 1.75), offset + data.remaining());
            buffer = MemoryUtil.memRealloc(buffer, size);
        }

        data.mark();
        buffer.position(offset);
        buffer.put(data);
        data.reset();

        return offset;
    }

    public ByteBuffer getBuffer() {
        int size = buffer.position();
        return buffer.rewind().limit(size);
    }

    public void delete() {
        MemoryUtil.memFree(buffer);
    }
}
