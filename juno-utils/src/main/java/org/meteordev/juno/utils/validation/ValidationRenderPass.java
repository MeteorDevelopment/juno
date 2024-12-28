package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.sampler.Sampler;

import java.nio.ByteBuffer;

public class ValidationRenderPass implements RenderPass {
    private final ValidationCommandList commands;
    private final RenderPass pass;

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
    public void bindPipeline(Pipeline pipeline) {
        if (ended)
            throw new RuntimeException();

        pass.bindPipeline(pipeline);
    }

    @Override
    public void bindImage(Image image, Sampler sampler, int slot) {
        if (ended)
            throw new RuntimeException();

        pass.bindImage(image, sampler, slot);
    }

    @Override
    public void setUniforms(ByteBuffer data, int slot) {
        if (ended)
            throw new RuntimeException();

        pass.setUniforms(data, slot);
    }

    @Override
    public void draw(Buffer indexBuffer, Buffer vertexBuffer, int count) {
        if (ended)
            throw new RuntimeException();

        pass.draw(indexBuffer, vertexBuffer, count);
    }

    @Override
    public void end() {
        if (ended)
            throw new RuntimeException();

        pass.end();
        ended = true;

        commands.pass = null;
    }
}
