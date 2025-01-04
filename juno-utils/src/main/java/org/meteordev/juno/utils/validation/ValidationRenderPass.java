package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.sampler.Sampler;

import java.nio.ByteBuffer;

public class ValidationRenderPass implements RenderPass {
    private final ValidationCommandList commands;
    private final RenderPass pass;

    GraphicsPipeline pipeline;
    boolean ended;

    ValidationRenderPass(ValidationCommandList commands, RenderPass pass) {
        this.commands = commands;
        this.pass = pass;
    }

    @Override
    public CommandList getCommandList() {
        return commands;
    }

    @Override
    public void bindPipeline(GraphicsPipeline pipeline) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (!pipeline.isValid())
            throw new InvalidResourceException(pipeline);

        this.pipeline = pipeline;
        pass.bindPipeline(pipeline);
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (!image.isValid())
            throw new InvalidResourceException(image);

        if (!sampler.isValid())
            throw new InvalidResourceException(sampler);

        if (slot < 0 || slot >= 4)
            throw new ValidationException("can only use image slots 0 - 3, got: " + slot);

        pass.bindImage(image, sampler, slot);
    }

    @Override
    public void setUniforms(ByteBuffer data, int slot) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (slot < 0 || slot >= 4)
            throw new ValidationException("can only use uniform slots 0 - 3, got: " + slot);

        pass.setUniforms(data, slot);
    }

    @Override
    public void draw(Buffer indexBuffer, Buffer vertexBuffer, int count) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (pipeline == null)
            throw new ValidationException("no pipeline bound");

        if (!indexBuffer.isValid())
            throw new InvalidResourceException(indexBuffer);

        if (!vertexBuffer.isValid())
            throw new InvalidResourceException(vertexBuffer);

        if (indexBuffer.getType() != BufferType.INDEX)
            throw new ValidationException("invalid indexBuffer type, needs to be BufferType.INDEX but got BufferType." + indexBuffer.getType());

        if (vertexBuffer.getType() != BufferType.VERTEX)
            throw new ValidationException("invalid vertexBuffer type, needs to be BufferType.VERTEX but got BufferType." + indexBuffer.getType());

        if (count < 0)
            throw new ValidationException("invalid primitive count, needs to be larger than 0, got: " + count);

        pass.draw(indexBuffer, vertexBuffer, count);
    }

    @Override
    public void end() {
        if (ended)
            throw new ValidationException("render pass ended");

        pass.end();
        ended = true;

        commands.pass = null;
    }
}
