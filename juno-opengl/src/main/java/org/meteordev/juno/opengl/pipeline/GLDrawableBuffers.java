package org.meteordev.juno.opengl.pipeline;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.vertexformat.VertexAttribute;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.pipeline.vertexformat.VertexType;
import org.meteordev.juno.opengl.GLJuno;
import org.meteordev.juno.opengl.buffer.BufferBindings;
import org.meteordev.juno.opengl.buffer.GLBuffer;

import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL20C.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL45C.*;

public class GLDrawableBuffers implements DrawableBuffers {
    public final Buffer vbo, ibo;
    public final int vao;

    private boolean valid;

    public GLDrawableBuffers(VertexFormat vertexFormat, Buffer vbo, Buffer ibo) {
        this.vbo = vbo;
        this.ibo = ibo;

        int stride = vertexFormat.getStride();

        if (GLJuno.DSA) {
            this.vao = glCreateVertexArrays();

            glVertexArrayVertexBuffer(vao, 0, ((GLBuffer) vbo).id, 0, stride);
            glVertexArrayElementBuffer(vao, ((GLBuffer) ibo).id);

            int offset = 0;

            for (int i = 0; i < vertexFormat.attributes.length; i++) {
                VertexAttribute attribute = vertexFormat.attributes[i];

                glEnableVertexArrayAttrib(vao, i);
                glVertexArrayAttribFormat(vao, i, attribute.count, attribute.type == VertexType.UNSIGNED_BYTE ? GL_UNSIGNED_BYTE : GL_FLOAT, attribute.normalized, offset);
                glVertexArrayAttribBinding(vao, i, 0);

                offset += attribute.getSize();
            }
        }
        else {
            this.vao = glGenVertexArrays();
            BufferBindings.bindDrawable(this);

            BufferBindings.bind(vbo, true);
            BufferBindings.bind(ibo, true);

            int offset = 0;

            for (int i = 0; i < vertexFormat.attributes.length; i++) {
                VertexAttribute attribute = vertexFormat.attributes[i];

                glEnableVertexAttribArray(i);
                glVertexAttribPointer(i, attribute.count, attribute.type == VertexType.UNSIGNED_BYTE ? GL_UNSIGNED_BYTE : GL_FLOAT, attribute.normalized, stride, offset);

                offset += attribute.getSize();
            }

            BufferBindings.bindDrawable(null);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid drawable buffers");

        glDeleteVertexArrays(vao);
        valid = false;
    }
}
