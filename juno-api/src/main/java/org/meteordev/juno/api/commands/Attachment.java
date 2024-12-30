package org.meteordev.juno.api.commands;

import org.meteordev.juno.api.image.Image;

/**
 * Represents an {@link Image} ready to be used as a target for rendering.
 * @param image the image to render to.
 * @param loadOp what to do with the contents of the image before rendering.
 * @param clearValue what value to clear the image to when {@link Attachment#loadOp()} is {@link LoadOp#CLEAR}.
 * @param storeOp what to do with the contents of the image after rendering.
 */
public record Attachment(Image image, LoadOp loadOp, ClearValue clearValue, StoreOp storeOp) {
}
