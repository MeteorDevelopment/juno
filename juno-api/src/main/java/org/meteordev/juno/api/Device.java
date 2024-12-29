package org.meteordev.juno.api;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;

public interface Device {
    BackendInfo getBackendInfo();

    // Buffers

    Buffer createBuffer(BufferType type, long size, String name);

    default Buffer createBuffer(BufferType type, long size) {
        return createBuffer(type, size, "");
    }

    // Images

    Image createImage(int width, int height, ImageFormat format, String name);

    default Image createImage(int width, int height, ImageFormat format) {
        return createImage(width, height, format, "");
    }

    // Back buffer

    Image getBackBufferColor();

    Image getBackBufferDepth();

    // Samplers

    Sampler createSampler(Filter min, Filter mag, Wrap wrap);

    // Shaders

    Shader createShader(ShaderType type, String source, String name);

    default Shader createShader(ShaderType type, String source) {
        return createShader(type, source, "");
    }

    // Pipelines

    Pipeline createPipeline(PipelineState state, String name, Shader... shaders);

    default Pipeline createPipeline(PipelineState state, Shader... shaders) {
        return createPipeline(state, "", shaders);
    }

    // Commands

    void beginFrame();

    CommandList createCommandList();
}
