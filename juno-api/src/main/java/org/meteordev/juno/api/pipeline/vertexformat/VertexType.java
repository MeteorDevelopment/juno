package org.meteordev.juno.api.pipeline.vertexformat;

public enum VertexType {
    UNSIGNED_BYTE(1),
    FLOAT(4);

    public final int size;

    VertexType(int size) {
        this.size = size;
    }
}
