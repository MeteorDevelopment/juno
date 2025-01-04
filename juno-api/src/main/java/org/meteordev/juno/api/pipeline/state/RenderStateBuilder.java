package org.meteordev.juno.api.pipeline.state;

import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;

/**
 * Creates a {@link RenderState} with some defaults.
 */
public class RenderStateBuilder {
    public VertexFormat vertexFormat = null;
    public PrimitiveType primitiveType = PrimitiveType.TRIANGLES;
    public BlendFunc blendFunc = null;
    public DepthFunc depthFunc = null;
    public CullFace cullFace = null;
    public WriteMask writeMask = WriteMask.COLOR_DEPTH;

    /**
     * The vertex format for the vertex buffer.
     */
    public RenderStateBuilder setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        return this;
    }

    /**
     * The type of primitives to render.
     */
    public RenderStateBuilder setPrimitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
        return this;
    }

    /**
     * The blending function to use, null for none.
     */
    public RenderStateBuilder setBlendFunc(BlendFunc blendFunc) {
        this.blendFunc = blendFunc;
        return this;
    }

    /**
     * The depth function to use, null for none.
     */
    public RenderStateBuilder setDepthFunc(DepthFunc depthFunc) {
        this.depthFunc = depthFunc;
        return this;
    }

    /**
     * The culling face to use, null for none.
     */
    public RenderStateBuilder setCullFace(CullFace cullFace) {
        this.cullFace = cullFace;
        return this;
    }

    /**
     * The write mask.
     * For example if it is set to {@link WriteMask#COLOR} then only the color output of shaders will be saved and the depth will be discarded.
     */
    public RenderStateBuilder setWriteMask(WriteMask writeMask) {
        this.writeMask = writeMask;
        return this;
    }

    /**
     * @return the new {@link RenderState} with the current values.
     */
    public RenderState build() {
        return new RenderState(vertexFormat, primitiveType, blendFunc, depthFunc, cullFace, writeMask);
    }
}
