package org.meteordev.juno.mc.backend;

import net.minecraft.client.render.BufferRenderer;
import org.jetbrains.annotations.Nullable;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.opengl.GLBindings;
import org.meteordev.juno.opengl.GLState;

import java.nio.ByteBuffer;

public class MCCommandList implements CommandList {
    private final MCDevice device;
    private final CommandList commands;

    MCCommandList(MCDevice device, CommandList commands) {
        this.device = device;
        this.commands = commands;
    }

    @Override
    public Device getDevice() {
        return device;
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
    public RenderPass beginRenderPass(@Nullable Attachment depth, Attachment... color) {
        return commands.beginRenderPass(depth, color);
    }

    @Override
    public void submit() {
        // Set state
        GLState mcState = device.getMcState();
        device.getState().setTo(mcState);

        GLBindings mcBindings = device.getMcBindings();
        device.getBindings().setTo(mcBindings);

        BufferRenderer.reset();

        // Submit
        commands.submit();

        // Sync state
        device.getState().syncWith(mcState);

        device.getBindings().syncWith(mcBindings);
    }
}
