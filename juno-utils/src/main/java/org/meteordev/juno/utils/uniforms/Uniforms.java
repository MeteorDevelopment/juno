package org.meteordev.juno.utils.uniforms;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows easily writing uniform data to a {@link ByteBuffer} without dealing with GLSL data alignment rules.
 * Classes or records need to be marked with {@link UniformStruct}.
 * @see UniformStruct
 */
public class Uniforms {
    private static final Map<Class<?>, UniformWriter> writers = new HashMap<>();

    /**
     * @param klass the class to get the size of.
     * @return the size in bytes required to write the class.
     */
    public static int getSize(Class<?> klass) {
        UniformWriter writer = getWriter(klass);
        return writer.getSize();
    }

    /**
     * Writes fields of a class to a {@link ByteBuffer}.
     * Writes at the current position of the buffer and increments it accordingly to the size of the data.
     * @param uniforms the instance from where to get the data to write.
     * @param buffer the buffer to write the data to.
     * @return the passed in buffer.
     */
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
