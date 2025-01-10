package org.meteordev.juno.opengl.buffer;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.vertexformat.VertexAttribute;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// TODO: Bad
public class VaoManager {
    private final Map<Key, Integer> vaos = new HashMap<>();

    @SuppressWarnings("Java8MapApi")
    public int get(GraphicsPipeline pipeline, Buffer index, Buffer vertex) {
        Key key = new Key(index, vertex);
        Integer vao = vaos.get(key);

        if (vao == null) {
            vao = create(pipeline, index, vertex);
            vaos.put(key, vao);
        }

        return vao;
    }

    public void destroy(Buffer buffer) {
        for (Iterator<Key> it = vaos.keySet().iterator(); it.hasNext();) {
            Key key = it.next();

            if (key.index == buffer || key.vertex == buffer) {
                int vao = vaos.get(key);
                GL33C.glDeleteVertexArrays(vao);

                it.remove();
            }
        }
    }

    private int create(GraphicsPipeline pipeline, Buffer index, Buffer vertex) {
        // Apparently OpenGL drivers ignore newly bound buffers after binding a VAO if there already are some buffers bound before
        // Sanest OpenGL driver behaviour
        GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, 0);

        int handle = GL33C.glGenVertexArrays();
        GL33C.glBindVertexArray(handle);

        GL33C.glBindBuffer(GL33C.GL_ELEMENT_ARRAY_BUFFER, ((GLResource) index).getHandle());
        GL33C.glBindBuffer(GL33C.GL_ARRAY_BUFFER, ((GLResource) vertex).getHandle());

        int stride = pipeline.getState().vertexFormat().getStride();
        VertexAttribute[] attributes = pipeline.getState().vertexFormat().attributes();

        long offset = 0;

        for (int i = 0; i < attributes.length; i++) {
            VertexAttribute attribute = attributes[i];

            GL33C.glEnableVertexAttribArray(i);

            if (attribute.type().floating || attribute.normalized()) {
                GL33C.glVertexAttribPointer(i, attribute.count(), GL.convert(attribute.type()), attribute.normalized(), stride, offset);
            } else {
                GL33C.glVertexAttribIPointer(i, attribute.count(), GL.convert(attribute.type()), stride, offset);
            }

            offset += attribute.getSize();
        }

        GL33C.glBindVertexArray(0);
        return handle;
    }

    private record Key(Buffer index, Buffer vertex) {}
}
