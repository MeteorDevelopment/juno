package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.BackendInfo;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.RenderState;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;

public class ValidationDevice implements Device {
    private final Device device;

    private final BackendInfo info;

    private ValidationDevice(Device device) {
        this.device = device;

        BackendInfo prevInfo = device.getBackendInfo();
        info = new BackendInfo(prevInfo.name(), "Validation, " + prevInfo.detail());
    }

    public static Device wrap(Device device) {
        return new ValidationDevice(device);
    }

    @Override
    public BackendInfo getBackendInfo() {
        return info;
    }

    @Override
    public Buffer createBuffer(BufferType type, long size, String name) {
        if (size < 0)
            throw new ValidationException("invalid buffer size, needs to be larger than 0, got: " + size);

        return device.createBuffer(type, size, name);
    }

    @Override
    public Image createImage(int width, int height, ImageFormat format, String name) {
        if (width < 0)
            throw new ValidationException("invalid image width, needs to be larger than 0, got: " + width);

        if (height < 0)
            throw new ValidationException("invalid image height, needs to be larger than 0, got: " + height);

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
    public GraphicsPipeline createGraphicsPipeline(RenderState state, Shader vertexShader, Shader fragmentShader, String name) {
        return device.createGraphicsPipeline(state, vertexShader, fragmentShader, name);
    }

    @Override
    public void beginFrame() {
        device.beginFrame();
    }

    @Override
    public CommandList createCommandList() {
        return new ValidationCommandList(this, device.createCommandList());
    }
}
