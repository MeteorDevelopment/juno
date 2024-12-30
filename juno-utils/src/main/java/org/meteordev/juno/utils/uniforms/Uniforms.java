package org.meteordev.juno.utils.uniforms;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Uniforms {
    private static final Map<Class<?>, UniformWriter> writers = new HashMap<>();

    public static int getSize(Class<?> klass) {
        UniformWriter writer = getWriter(klass);
        return writer.getSize();
    }

    public static ByteBuffer write(Object uniforms, ByteBuffer buffer) {
        UniformWriter writer = getWriter(uniforms.getClass());

        if (writer.getSize() > buffer.remaining())
            throw new IllegalArgumentException("buffer is too small, uniforms: " + writer.getSize() + ", buffer: " + buffer.remaining());

        writer.write(uniforms, buffer);

        return buffer;
    }

    static UniformWriter getWriter(Class<?> klass) {
        UniformWriter writer = writers.get(klass);

        if (writer == null) {
            if (!klass.isAnnotationPresent(UniformStruct.class))
                throw new IllegalArgumentException("Class doesn't have the @UniformStruct annotation");

            writer = WriterFactory.create(klass);
            writers.put(klass, writer);
        }

        return writer;
    }

    private Uniforms() {}
}
