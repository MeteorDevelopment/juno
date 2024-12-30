package org.meteordev.juno.api.commands;

import org.jetbrains.annotations.Nullable;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.image.Image;

import java.nio.ByteBuffer;

/**
 * Represents a list of GPU commands ready to be executed.
 */
public interface CommandList {
    /**
     * @return the {@link Device} associated with the command list.
     */
    Device getDevice();

    // Uploads

    /**
     * Uploads the contents of a {@link ByteBuffer} to a {@link Buffer}.
     * @param src the data source.
     * @param dst the data destination.
     */
    void uploadToBuffer(ByteBuffer src, Buffer dst);

    /**
     * Uploads the contents of a {@link ByteBuffer} to an {@link Image}.
     * @param src the data source.
     * @param dst the data destination.
     */
    void uploadToImage(ByteBuffer src, Image dst);

    // Render passes

    /**
     * Starts a new GPU render pass.
     * @param color the color attachment to render to, optional.
     * @param depth the depth attachment to render to, optional.
     * @return the new render pass.
     */
    RenderPass beginRenderPass(@Nullable Attachment color, @Nullable Attachment depth);

    // Submit

    /**
     * Submits all the commands recorded inside this command list and all children render passes to be executed on the GPU.
     */
    void submit();
}
