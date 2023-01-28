package org.meteordev.juno.api.pipeline;

public enum PrimitiveType {
    TRIANGLES(3),
    LINES(2);

    public final int vertexCount;

    PrimitiveType(int vertexCount) {
        this.vertexCount = vertexCount;
    }
}
