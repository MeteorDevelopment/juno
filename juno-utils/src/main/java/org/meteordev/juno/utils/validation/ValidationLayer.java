package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.Device;
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

public class ValidationLayer implements Device {
    private final Device device;

    private ValidationLayer(Device device) {
        this.device = device;
    }

    public static Device wrap(Device device) {
        return new ValidationLayer(device);
    }

    @Override
    public Buffer createBuffer(BufferType type, long size, String name) {
        return device.createBuffer(type, size, name);
    }

    @Override
    public Image createImage(int width, int height, ImageFormat format, String name) {
        return device.createImage(width, height, format, name);
    }

    @Override
    public Image getBackBufferColor() {
        return device.getBackBufferColor();
    }

    @Override
    public Image getBackBufferDepth() {
        return device.getBackBufferDepth();
    }

    @Override
    public Sampler createSampler(Filter min, Filter mag, Wrap wrap) {
        return device.createSampler(min, mag, wrap);
    }

    @Override
    public Shader createShader(ShaderType type, String source, String name) {
        return device.createShader(type, source, name);
    }

    @Override
    public Pipeline createPipeline(PipelineState state, String name, Shader... shaders) {
        return device.createPipeline(state, name, shaders);
    }

    @Override
    public void beginFrame() {
        device.beginFrame();
    }

    @Override
    public CommandList createCommandList() {
        return new ValidationCommandList(device.createCommandList());
    }
}
