package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;

import java.nio.ByteBuffer;

public class ValidationCommandList implements CommandList {
    private final CommandList commands;

    ValidationRenderPass pass;

    ValidationCommandList(CommandList commands) {
        this.commands = commands;
    }

    @Override
    public void uploadToBuffer(ByteBuffer src, Buffer dst) {
        commands.uploadToBuffer(src, dst);
    }

    @Override
    public void uploadToImage(ByteBuffer src, Image dst) {
        commands.uploadToImage(src, dst);
    }

    @Override
    public RenderPass beginRenderPass(Attachment color, Attachment depth) {
        if (pass != null)
            throw new RuntimeException();

        pass = new ValidationRenderPass(this, commands.beginRenderPass(color, depth));
        return pass;
    }

    @Override
    public void submit() {
        if (pass != null)
            throw new RuntimeException();

        commands.submit();
    }
}
