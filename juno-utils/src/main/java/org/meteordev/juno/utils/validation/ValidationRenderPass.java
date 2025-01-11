package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.Sampler;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class ValidationRenderPass implements RenderPass {
    private final ValidationCommandList commands;
    private final RenderPass pass;

    private GraphicsPipeline pipeline;

    private final boolean[] uniformBindings;
    private final boolean[] imageBindings;

    boolean ended;

    ValidationRenderPass(ValidationCommandList commands, RenderPass pass) {
        this.commands = commands;
        this.pass = pass;

        this.uniformBindings = new boolean[4];
        this.imageBindings = new boolean[4];
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

        Arrays.fill(uniformBindings, false);
        Arrays.fill(imageBindings, false);

        pass.bindPipeline(pipeline);
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (pipeline == null)
            throw new ValidationException("no pipeline bound");

        if (!image.isValid())
            throw new InvalidResourceException(image);

        if (!sampler.isValid())
            throw new InvalidResourceException(sampler);

        if (slot < 0 || slot >= 4)
            throw new ValidationException("can only use image slots 0 - 3, got: " + slot);

        if (!pipeline.getImageBindings()[slot])
            throw new ValidationException(String.format("%s does not use an image at slot %d", pipeline, slot));

        imageBindings[slot] = true;

        pass.bindImage(image, sampler, slot);
    }

    @Override
    public void setUniforms(ByteBuffer data, int slot) {
        if (ended)
            throw new ValidationException("render pass ended");

        if (pipeline == null)
            throw new ValidationException("no pipeline bound");

        if (slot < 0 || slot >= 4)
            throw new ValidationException("can only use uniform slots 0 - 3, got: " + slot);

        if (!pipeline.getUniformBindings()[slot])
            throw new ValidationException(String.format("%s does not use uniform data at slot %d", pipeline, slot));

        uniformBindings[slot] = true;

        pass.setUniforms(data, slot);
    }

    @Override
    public void setScissor(int x, int y, int width, int height) {
        if (x < 0)
            throw new ValidationException("scissor x cannot be negative");

        if (y < 0)
            throw new ValidationException("scissor y cannot be negative");

        if (width < 1)
            throw new ValidationException("scissor width cannot be less than 1");

        if (height < 1)
            throw new ValidationException("scissor height cannot be less than 1");

        pass.setScissor(x, y, width, height);
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

        boolean[] usedUniformBindings = pipeline.getUniformBindings();
        boolean [] usedImageBindings = pipeline.getImageBindings();

        for (int i = 0; i < 4; i++) {
            if (usedUniformBindings[i] && !uniformBindings[i])
                throw new ValidationException(String.format("%s requires uniform data at slot %d but none were provided", pipeline, i));

            if (usedImageBindings[i] && !imageBindings[i])
                throw new ValidationException(String.format("%s requires a bound image at slot %d but none was bound", pipeline, i));
        }

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
