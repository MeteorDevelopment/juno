package org.meteordev.juno.api.commands;

import org.meteordev.juno.api.image.Image;

public record Attachment(Image image, LoadOp loadOp, ClearValue clearValue, StoreOp storeOp) {
}
