package org.meteordev.juno.api.buffer;

import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Resource;

public interface Buffer extends Resource {
    BufferType getType();

    void write(long data, long size);

    default void write(java.nio.Buffer data, long size) {
        write(MemoryUtil.memAddress(data), size);
    }
}
