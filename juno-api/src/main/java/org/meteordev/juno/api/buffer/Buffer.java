package org.meteordev.juno.api.buffer;

import org.meteordev.juno.api.Resource;

import java.nio.ByteBuffer;

/**
 * Represents a buffer on the GPU with a fixed capacity.
 * Data can be uploaded to a buffer using {@link org.meteordev.juno.api.commands.CommandList#uploadToBuffer(ByteBuffer, Buffer)}.
 */
public interface Buffer extends Resource {
    /**
     * @return the buffer type.
     */
    BufferType getType();

    /**
     * @return the capacity of the buffer.
     */
    long getSize();
}
