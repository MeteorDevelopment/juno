package org.meteordev.juno.api.pipeline.vertexformat;

public class VertexAttribute {
    public final VertexType type;
    public final int count;
    public final boolean normalized;

    public VertexAttribute(VertexType type, int count, boolean normalized) {
        this.type = type;
        this.count = count;
        this.normalized = normalized;
    }

    public int getSize() {
        return type.size * count;
    }
}
