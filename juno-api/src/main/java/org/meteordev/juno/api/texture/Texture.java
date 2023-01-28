package org.meteordev.juno.api.texture;

import org.meteordev.juno.api.Resource;

import java.nio.ByteBuffer;

public interface Texture extends Resource {
    int getWidth();

    int getHeight();

    void write(ByteBuffer data);
}
