package org.meteordev.juno.api.pipeline.state;

import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;

import java.util.Objects;

public class PipelineState {
    public PrimitiveType primitiveType = PrimitiveType.TRIANGLES;
    public VertexFormat vertexFormat;

    public CullMode cullMode = CullMode.DISABLE;
    public BlendFunc blendFunc;
    public DepthFunc depthFunc = DepthFunc.LESS;
    public WriteMask writeMask = WriteMask.COLOR;

    public PipelineState setPrimitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
        return this;
    }

    public PipelineState setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        return this;
    }

    public PipelineState setCullMode(CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public PipelineState setBlendFunc(BlendFunc blendFunc) {
        this.blendFunc = blendFunc;
        return this;
    }

    public PipelineState setDepthFunc(DepthFunc depthFunc) {
        this.depthFunc = depthFunc;
        return this;
    }

    public PipelineState setWriteMask(WriteMask writeMask) {
        this.writeMask = writeMask;
        return this;
    }

    public void validate() {
        assert primitiveType != null : "PipelineInfo.primitiveType cannot be null";
        assert vertexFormat != null : "PipelineInfo.vertexFormat cannot be null";

        assert cullMode != null : "PipelineInfo.cullMode cannot be null";
        assert writeMask != null : "PipelineInfo.writeMask cannot be null";
    }

    public PipelineState copy() {
        return new PipelineState()
                .setVertexFormat(vertexFormat)
                .setPrimitiveType(primitiveType)
                .setCullMode(cullMode)
                .setBlendFunc(blendFunc)
                .setDepthFunc(depthFunc)
                .setWriteMask(writeMask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipelineState that = (PipelineState) o;

        if (primitiveType != that.primitiveType) return false;
        if (!vertexFormat.equals(that.vertexFormat)) return false;
        if (cullMode != that.cullMode) return false;
        if (!Objects.equals(blendFunc, that.blendFunc)) return false;
        if (depthFunc != that.depthFunc) return false;
        return writeMask == that.writeMask;
    }

    @Override
    public int hashCode() {
        int result = primitiveType.hashCode();
        result = 31 * result + vertexFormat.hashCode();
        result = 31 * result + cullMode.hashCode();
        result = 31 * result + (blendFunc != null ? blendFunc.hashCode() : 0);
        result = 31 * result + (depthFunc != null ? depthFunc.hashCode() : 0);
        result = 31 * result + writeMask.hashCode();
        return result;
    }
}
