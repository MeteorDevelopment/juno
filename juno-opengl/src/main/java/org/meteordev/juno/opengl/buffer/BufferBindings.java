package org.meteordev.juno.opengl.buffer;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.opengl.pipeline.GLDrawableBuffers;

import static org.lwjgl.opengl.GL15C.*;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;

public class BufferBindings {
    private static final Buffer[] BINDINGS = new Buffer[BufferType.values().length];
    private static int VAO = -1;

    public static int bind(Buffer buffer, boolean force) {
        if (buffer == null) return 0;

        int target = buffer.getType() == BufferType.VERTEX ? GL_ARRAY_BUFFER : GL_ELEMENT_ARRAY_BUFFER;

        if (force || BINDINGS[buffer.getType().ordinal()] != buffer) {
            glBindBuffer(target, ((GLBuffer) buffer).id);
            BINDINGS[buffer.getType().ordinal()] = buffer;
        }

        return target;
    }

    public static int bind(Buffer buffer) {
        return bind(buffer, false);
    }

    public static void bindDrawable(GLDrawableBuffers buffers) {
        if (buffers != null) {
            if (VAO != buffers.vao) {
                glBindVertexArray(buffers.vao);

                BINDINGS[BufferType.VERTEX.ordinal()] = buffers.vbo;
                BINDINGS[BufferType.INDEX.ordinal()] = buffers.ibo;

                VAO = buffers.vao;
            }
        }
        else {
            if (VAO != 0) {
                glBindVertexArray(0);

                BINDINGS[BufferType.VERTEX.ordinal()] = null;
                BINDINGS[BufferType.INDEX.ordinal()] = null;

                VAO = 0;
            }
        }
    }
}
