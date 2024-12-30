package org.meteordev.juno.api.image;

import org.meteordev.juno.api.Resource;

import java.nio.ByteBuffer;

/**
 * Represents an image on the GPU with fixed with, height and format.
 * Can be used for textures in shaders or as {@link org.meteordev.juno.api.commands.RenderPass} attachments.
 * Data can be uploaded to an image using {@link org.meteordev.juno.api.commands.CommandList#uploadToImage(ByteBuffer, Image)}.
 */
public interface Image extends Resource {
    /**
     * @return the image width.
     */
    int getWidth();

    /**
     * @return the image height.
     */
    int getHeight();

    /**
     * @return the image format.
     */
    ImageFormat getFormat();
}
