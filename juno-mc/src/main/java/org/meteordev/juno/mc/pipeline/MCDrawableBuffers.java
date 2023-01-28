package org.meteordev.juno.mc.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL20;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.vertexformat.VertexAttribute;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.pipeline.vertexformat.VertexType;
import org.meteordev.juno.mc.buffer.BufferBindings;

public class MCDrawableBuffers implements DrawableBuffers {
    public final Buffer vbo, ibo;
    public final int vao;

    private boolean valid;

    public MCDrawableBuffers(VertexFormat vertexFormat, Buffer vbo, Buffer ibo) {
        this.vbo = vbo;
        this.ibo = ibo;

        this.vao = GlStateManager._glGenVertexArrays();
        BufferBindings.bindDrawable(this);

        BufferBindings.bind(vbo);
        BufferBindings.bind(ibo);

        int stride = vertexFormat.getStride();
        int offset = 0;

        for (int i = 0; i < vertexFormat.attributes.length; i++) {
            VertexAttribute attribute = vertexFormat.attributes[i];

            GlStateManager._enableVertexAttribArray(i);
            GlStateManager._vertexAttribPointer(i, attribute.count, attribute.type == VertexType.UNSIGNED_BYTE ? GL20.GL_UNSIGNED_BYTE : GL20.GL_FLOAT, attribute.normalized, stride, offset);

            offset += attribute.getSize();
        }

        BufferBindings.bindDrawable(null);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid drawable buffers");

        GlStateManager._glDeleteVertexArrays(vao);
        valid = false;
    }
}
