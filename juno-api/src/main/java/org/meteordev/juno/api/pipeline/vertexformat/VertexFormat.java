package org.meteordev.juno.api.pipeline.vertexformat;

public record VertexFormat(VertexAttribute... attributes) {
    public int getStride() {
        int stride = 0;

        for (VertexAttribute attribute : attributes) {
            stride += attribute.getSize();
        }

        return stride;
    }
}
