package org.meteordev.juno.api.commands;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.Sampler;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;

import java.nio.ByteBuffer;

/**
 * Represents a state of the GPU when a rendering target is ready, and the GPU can accept rendering commands.
 */
public interface RenderPass {
    /**
     * @return the {@link CommandList} associated with the render pass.
     */
    CommandList getCommandList();

    /**
     * Binds a pipeline for subsequent commands.
     * This command clears all currently bound images, set uniform data and scissor window.
     * @param pipeline the pipeline to bind.
     */
    void bindPipeline(GraphicsPipeline pipeline);

    /**
     * Binds an {@link Image} with a {@link Sampler} to a texture slot for shaders.
     * @param image the image to bind.
     * @param sampler the sampler to bind.
     * @param slot the slot to bind to, available slots are 0 - 4.
     */
    void bindImage(Image image, Sampler sampler, int slot);

    /**
     * Sets the contents of a {@link ByteBuffer} to be available as uniform data on a certain slot for shaders.
     * @param data the data to use.
     * @param slot the slot to use, available slots are 0 - 4.
     */
    void setUniforms(ByteBuffer data, int slot);

    /**
     * Sets the current scissor region to the specified coordinates. All pixels outside this region won't be rendered.
     * To disable scissor you have to set it to the same size as the image you are rendering into.
     * @param x the x coordinate starting in the lower-left corner.
     * @param y the y coordinate starting in the lower-left corner.
     * @param width the width of the region.
     * @param height the height of the region.
     */
    void setScissor(int x, int y, int width, int height);

    /**
     * Submits a draw call. Needs a pipeline to be bound and any resources it needs (images and uniforms).
     * @param indexBuffer the index buffer to use.
     * @param vertexBuffer the vertex buffer to use.
     * @param count the number of indices from the index buffer to render.
     */
    void draw(Buffer indexBuffer, Buffer vertexBuffer, int count);

    /**
     * Ends this render pass.
     */
    void end();
}
