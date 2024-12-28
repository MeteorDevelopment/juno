package org.meteordev.juno.api.commands;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.sampler.Sampler;

import java.nio.ByteBuffer;

public interface RenderPass {
    CommandList getCommandList();

    void bindPipeline(Pipeline pipeline);

    void bindImage(Image image, Sampler sampler, int slot);

    void setUniforms(ByteBuffer data, int slot);

    void draw(Buffer indexBuffer, Buffer vertexBuffer, int count);

    void end();
}
