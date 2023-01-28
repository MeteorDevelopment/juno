package org.meteordev.juno.api.pipeline.vertexformat;

public class VertexFormat {
    public final VertexAttribute[] attributes;

    public VertexFormat(VertexAttribute... attributes) {
        this.attributes = attributes;
    }

    public int getStride() {
        int stride = 0;

        for (VertexAttribute attribute : attributes) {
            stride += attribute.getSize();
        }

        return stride;
    }
}
