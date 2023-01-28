package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.CullMode;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.WriteMask;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.shader.Program;
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
        assert vertexFormat != null : "PipelineInfo.vertexFormat cannot be null";
        assert primitiveType != null : "PipelineInfo.primitiveType cannot be null";
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
        return Objects.equals(vertexFormat, that.vertexFormat) && primitiveType == that.primitiveType && Arrays.equals(shaderInfos, that.shaderInfos) && cullMode == that.cullMode && Objects.equals(blendFunc, that.blendFunc) && depthFunc == that.depthFunc && writeMask == that.writeMask;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(vertexFormat, primitiveType, cullMode, blendFunc, depthFunc, writeMask);
        result = 31 * result + Arrays.hashCode(shaderInfos);
        return result;
    }
}
