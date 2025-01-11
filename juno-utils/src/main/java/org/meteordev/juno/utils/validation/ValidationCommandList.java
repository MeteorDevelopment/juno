package org.meteordev.juno.utils.validation;

import org.jetbrains.annotations.Nullable;
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

    private int groupCount;

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
        if (!dst.isValid())
            throw new InvalidResourceException(dst);

        if (src.remaining() > dst.getSize())
            throw new ValidationException("buffer is too small, src: " + src.remaining() + ", dst: " + dst.getSize());

        commands.uploadToBuffer(src, dst);
    }

    @Override
    public void uploadToImage(ByteBuffer src, Image dst) {
        if (!dst.isValid())
            throw new InvalidResourceException(dst);

        int dstSize = dst.getWidth() * dst.getHeight() * dst.getFormat().size;

        if (src.remaining() > dstSize)
            throw new ValidationException("image is too small, src: " + src.remaining() + ", dst: " + dstSize);

        commands.uploadToImage(src, dst);
    }

    @Override
    public void pushGroup(String name) {
        if (name.isBlank())
            throw new ValidationException("group name is empty");

        groupCount++;

        commands.pushGroup(name);
    }

    @Override
    public void popGroup() {
        if (groupCount == 0)
            throw new ValidationException("no group pop");

        groupCount--;

        commands.popGroup();
    }

    @Override
    public RenderPass beginRenderPass(@Nullable Attachment depth, Attachment... color) {
        if (pass != null)
            throw new ValidationException("previous render pass was not yet ended, there can only be one active render pass per command list");

        if (depth == null && color.length == 0)
            throw new ValidationException("render pass needs to have at least one attachment");

        if (depth != null && depth.loadOp() == LoadOp.CLEAR && depth.clearValue() == null)
            throw new ValidationException("depth attachment has loadOp set to CLEAR but doesn't have a clear value");

        if (depth != null && !depth.image().isValid())
            throw new InvalidResourceException(depth.image());

        if (color.length > 4)
            throw new ValidationException("maximum amount of color attachments is 4, got " + color.length);

        for (int i = 0; i < color.length; i++) {
            Attachment attachment = color[i];

            if (attachment.loadOp() == LoadOp.CLEAR && attachment.clearValue() == null)
                throw new ValidationException("color attachment " + i + " has loadOp set to CLEAR but doesn't have a clear value");

            if (!attachment.image().isValid())
                throw new InvalidResourceException(attachment.image());
        }

        boolean anyBackBuffer = false;
        boolean allBackBuffer = true;

        if (depth != null) {
            if (depth.image() == getDevice().getBackBufferDepth())
                anyBackBuffer = true;
            else
                allBackBuffer = false;
        }

        for (Attachment attachment : color) {
            if (attachment.image() == getDevice().getBackBufferColor())
                anyBackBuffer = true;
            else
                allBackBuffer = false;
        }

        if (anyBackBuffer && !allBackBuffer)
            throw new ValidationException("if rendering to any back-buffer image then all attachments need to be from the back-buffer, you can't mix back-buffer and non back-buffer images");

        if (anyBackBuffer && color.length > 1)
            throw new ValidationException(("if rendering to any back-buffer image then you can't have more than 1 color attachment, got " + color.length));

        pass = new ValidationRenderPass(this, commands.beginRenderPass(depth, color));
        return pass;
    }

    @Override
    public void submit() {
        if (pass != null)
            throw new ValidationException("current render pass was not yet ended, all render passes need to end before submitting a command list");

        if (groupCount != 0)
            throw new ValidationException("all groups need to be popped before submitting a command list");

        commands.submit();
    }
}
