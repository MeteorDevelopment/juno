package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.CullMode;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.WriteMask;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.shader.ShaderInfo;

import java.util.Arrays;
import java.util.Objects;

public class PipelineInfo {
    public PrimitiveType primitiveType = PrimitiveType.TRIANGLES;
    public VertexFormat vertexFormat;
    public ShaderInfo[] shaderInfos;

    public CullMode cullMode = CullMode.DISABLE;
    public BlendFunc blendFunc;
    public DepthFunc depthFunc;
    public WriteMask writeMask;

    public PipelineInfo setPrimitiveType(PrimitiveType primitiveType) {
        this.primitiveType = primitiveType;
        return this;
    }

    public PipelineInfo setVertexFormat(VertexFormat vertexFormat) {
        this.vertexFormat = vertexFormat;
        return this;
    }

    public PipelineInfo setShaders(ShaderInfo... shaderInfos) {
        this.shaderInfos = shaderInfos;
        return this;
    }

    public PipelineInfo setCullMode(CullMode cullMode) {
        this.cullMode = cullMode;
        return this;
    }

    public PipelineInfo setBlendFunc(BlendFunc blendFunc) {
        this.blendFunc = blendFunc;
        return this;
    }

    public PipelineInfo setDepthFunc(DepthFunc depthFunc) {
        this.depthFunc = depthFunc;
        return this;
    }

    public PipelineInfo setWriteMask(WriteMask writeMask) {
        this.writeMask = writeMask;
        return this;
    }

    public void validate() {
        assert primitiveType != null : "PipelineInfo.primitiveType cannot be null";
        assert vertexFormat != null : "PipelineInfo.vertexFormat cannot be null";
        assert shaderInfos != null : "PipelineInfo.program cannot be null";

        assert cullMode != null : "PipelineInfo.cullMode cannot be null";
        assert writeMask != null : "PipelineInfo.writeMask cannot be null";
    }

    public PipelineInfo copy() {
        return new PipelineInfo()
                .setVertexFormat(vertexFormat)
                .setPrimitiveType(primitiveType)
                .setShaders(shaderInfos)
                .setCullMode(cullMode)
                .setBlendFunc(blendFunc)
                .setDepthFunc(depthFunc)
                .setWriteMask(writeMask);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PipelineInfo that = (PipelineInfo) o;

        if (primitiveType != that.primitiveType) return false;
        if (!vertexFormat.equals(that.vertexFormat)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(shaderInfos, that.shaderInfos)) return false;
        if (cullMode != that.cullMode) return false;
        if (!Objects.equals(blendFunc, that.blendFunc)) return false;
        if (depthFunc != that.depthFunc) return false;
        return writeMask == that.writeMask;
    }

    @Override
    public int hashCode() {
        int result = primitiveType.hashCode();
        result = 31 * result + vertexFormat.hashCode();
        result = 31 * result + Arrays.hashCode(shaderInfos);
        result = 31 * result + cullMode.hashCode();
        result = 31 * result + (blendFunc != null ? blendFunc.hashCode() : 0);
        result = 31 * result + (depthFunc != null ? depthFunc.hashCode() : 0);
        result = 31 * result + writeMask.hashCode();
        return result;
    }
}
