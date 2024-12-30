package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.LoadOp;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;

import java.nio.ByteBuffer;

public class ValidationCommandList implements CommandList {
    private final ValidationDevice layer;
    private final CommandList commands;

    ValidationRenderPass pass;

    ValidationCommandList(ValidationDevice layer, CommandList commands) {
        this.layer = layer;
        this.commands = commands;
    }

    @Override
    public Device getDevice() {
        return layer;
    }

    @Override
    public void uploadToBuffer(ByteBuffer src, Buffer dst) {
        if (src.remaining() > dst.getSize())
            throw new ValidationException("buffer is too small, src: " + src.remaining() + ", dst: " + dst.getSize());

        commands.uploadToBuffer(src, dst);
    }

    @Override
    public void uploadToImage(ByteBuffer src, Image dst) {
        int dstSize = dst.getWidth() * dst.getHeight() * dst.getFormat().size;

        if (src.remaining() > dstSize)
            throw new ValidationException("image is too small, src: " + src.remaining() + ", dst: " + dstSize);

        commands.uploadToImage(src, dst);
    }

    @Override
    public RenderPass beginRenderPass(Attachment color, Attachment depth) {
        if (pass != null)
            throw new ValidationException("previous render pass was not yet ended, there can only be one active render pass per command list");

        if (color == null && depth == null)
            throw new ValidationException("render pass needs to have at least one attachment");

        if (color != null && color.loadOp() == LoadOp.CLEAR && color.clearValue() == null)
            throw new ValidationException("color attachment has loadOp set to CLEAR but doesn't have a clear value");

        if (depth != null && depth.loadOp() == LoadOp.CLEAR && depth.clearValue() == null)
            throw new ValidationException("depth attachment has loadOp set to CLEAR but doesn't have a clear value");

        pass = new ValidationRenderPass(this, commands.beginRenderPass(color, depth));
        return pass;
    }

    @Override
    public void submit() {
        if (pass != null)
            throw new ValidationException("current render pass was not yet ended, all render passes need to end before submitting a command list");

        commands.submit();
    }
}
