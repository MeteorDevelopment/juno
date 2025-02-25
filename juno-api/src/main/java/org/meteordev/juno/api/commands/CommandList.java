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

    // Debug

    /**
     * Begins a new group of commands.
     * Groups do not change any behaviour, but they can be seen in debugging tools such as RenderDoc.
     * @param name the name of the group to display.
     */
    void pushGroup(String name);

    /**
     * Stops the current group of commands.
     */
    void popGroup();

    // Render passes

    /**
     * Starts a new GPU render pass.
     * Both the depth and color attachments are optional but both cannot be null at the same time.
     * @param depth the depth attachment to render to, optional.
     * @param color the color attachments to render to, optional.
     * @return the new render pass.
     */
    RenderPass beginRenderPass(@Nullable Attachment depth, Attachment... color);

    // Submit

    /**
     * Submits all the commands recorded inside this command list and all children render passes to be executed on the GPU.
     */
    void submit();
}
