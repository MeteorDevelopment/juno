package org.meteordev.juno.api.buffer;

import org.meteordev.juno.api.Resource;

public interface Buffer extends Resource {
    BufferType getType();

    long getSize();
}
