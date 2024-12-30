package org.meteordev.juno.api.pipeline.vertexformat;

/**
 * Represents the format of individual vertices inside a vertex {@link org.meteordev.juno.api.buffer.Buffer}.
 * @param attributes the attributes each vertex consists of.
 */
public record VertexFormat(VertexAttribute... attributes) {
    public int getStride() {
        int stride = 0;

        for (VertexAttribute attribute : attributes) {
            stride += attribute.getSize();
        }

        return stride;
    }
}
