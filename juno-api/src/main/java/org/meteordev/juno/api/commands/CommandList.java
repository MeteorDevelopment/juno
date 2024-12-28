package org.meteordev.juno.api.commands;

import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.image.Image;

import java.nio.ByteBuffer;

public interface CommandList {
    Device getDevice();

    // Uploads

    void uploadToBuffer(ByteBuffer src, Buffer dst);

    void uploadToImage(ByteBuffer src, Image dst);

    // Render passes

    RenderPass beginRenderPass(Attachment color, Attachment depth);

    // Submit

    void submit();
}
