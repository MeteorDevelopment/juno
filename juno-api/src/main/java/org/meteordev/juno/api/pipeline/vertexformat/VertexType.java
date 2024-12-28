package org.meteordev.juno.api.pipeline.vertexformat;

public enum VertexType {
    UNSIGNED_BYTE(false, 1),
    FLOAT(true, 4);

    public final boolean floating;
    public final int size;

    VertexType(boolean floating, int size) {
        this.floating = floating;
        this.size = size;
    }
}
