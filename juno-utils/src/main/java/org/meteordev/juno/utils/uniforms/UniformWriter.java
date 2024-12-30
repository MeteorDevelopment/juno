package org.meteordev.juno.utils.uniforms;

import java.nio.ByteBuffer;

interface UniformWriter {
    int getSize();

    void write(Object uniforms, ByteBuffer buffer);
}
