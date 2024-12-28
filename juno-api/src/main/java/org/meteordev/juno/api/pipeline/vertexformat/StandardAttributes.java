package org.meteordev.juno.api.pipeline.vertexformat;

public class StandardAttributes {
    public static final VertexAttribute POSITION_2D = new VertexAttribute(VertexType.FLOAT, 2, false);
    public static final VertexAttribute POSITION_3D = new VertexAttribute(VertexType.FLOAT, 3, false);
    public static final VertexAttribute COLOR = new VertexAttribute(VertexType.UNSIGNED_BYTE, 4, true);
    public static final VertexAttribute UV = new VertexAttribute(VertexType.FLOAT, 2, false);

    private StandardAttributes() {}
}
