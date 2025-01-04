package org.meteordev.juno.api.pipeline.vertexformat;

public class StandardFormats {
    public static final VertexFormat POSITION_2D = new VertexFormat(StandardAttributes.POSITION_2D);
    public static final VertexFormat POSITION_2D_COLOR = new VertexFormat(StandardAttributes.POSITION_2D, StandardAttributes.COLOR);
    public static final VertexFormat POSITION_2D_UV = new VertexFormat(StandardAttributes.POSITION_2D, StandardAttributes.UV);
    public static final VertexFormat POSITION_2D_UV_COLOR = new VertexFormat(StandardAttributes.POSITION_2D, StandardAttributes.UV, StandardAttributes.COLOR);

    public static final VertexFormat POSITION_3D = new VertexFormat(StandardAttributes.POSITION_3D);
    public static final VertexFormat POSITION_3D_COLOR = new VertexFormat(StandardAttributes.POSITION_3D, StandardAttributes.COLOR);
    public static final VertexFormat POSITION_3D_UV = new VertexFormat(StandardAttributes.POSITION_3D, StandardAttributes.UV);

    private StandardFormats() {}
}
