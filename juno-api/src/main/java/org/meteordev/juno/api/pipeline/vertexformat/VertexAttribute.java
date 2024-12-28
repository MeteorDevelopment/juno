package org.meteordev.juno.api.pipeline.vertexformat;

public record VertexAttribute(VertexType type, int count, boolean normalized) {
    public int getSize() {
        return type.size * count;
    }
}
