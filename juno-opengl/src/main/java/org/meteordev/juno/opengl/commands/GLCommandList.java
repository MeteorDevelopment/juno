package org.meteordev.juno.opengl.commands;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.LoadOp;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.opengl.buffer.GLBuffer;
import org.meteordev.juno.opengl.image.GLImage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLCommandList implements CommandList {
    private final GLDevice device;
    private final List<Runnable> commands = new ArrayList<>();

    public GLCommandList(GLDevice device) {
        this.device = device;

        device.getUniforms().reset();
    }

    // API

    @Override
    public void uploadToBuffer(ByteBuffer src, Buffer dst) {
        // TODO: Upload buffer
        GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, ((GLBuffer) dst).handle);
        GL33C.glBufferData(GL33C.GL_COPY_WRITE_BUFFER, src, GL33C.GL_DYNAMIC_DRAW);
    }

    @Override
    public void uploadToImage(ByteBuffer src, Image dst) {
        // TODO: Upload buffer
        GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, ((GLImage) dst).handle);

        GL33C.glTexImage2D(
                GL33C.GL_TEXTURE_2D,
                0,
                GL.convertInternal(dst.getFormat()),
                dst.getWidth(),
                dst.getHeight(),
                0,
                GL.convert(dst.getFormat()),
                GL.convertType(dst.getFormat()),
                src
        );
    }

    @Override
    public RenderPass beginRenderPass(Attachment color, Attachment depth) {
        int framebuffer = device.getFramebufferManager().get(color, depth);

        add(() -> {
            GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, framebuffer);

            int mask = 0;

            if (color.loadOp() == LoadOp.CLEAR) {
                mask |= GL33C.GL_COLOR_BUFFER_BIT;
                GL33C.glClearColor(color.clearValue().r(), color.clearValue().g(), color.clearValue().b(), color.clearValue().a());
            }

            if (depth != null && depth.loadOp() == LoadOp.CLEAR) {
                mask |= GL33C.GL_DEPTH_BUFFER_BIT;
                GL33C.glClearDepth(color.clearValue().r());
            }

            if (mask != 0) {
                GL33C.glClear(mask);
            }
        });

        return new GLRenderPass(this);
    }

    @Override
    public void submit() {
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, device.getUniformBuffer());
        GL33C.glBufferData(GL33C.GL_UNIFORM_BUFFER, device.getUniforms().getBuffer(), GL33C.GL_DYNAMIC_DRAW);

        for (Runnable command : commands) {
            command.run();
        }

        // TODO: Not sure if flushing the opengl command queue is the right thing to do
        GL33C.glFlush();
    }

    // Other

    public GLDevice getDevice() {
        return device;
    }

    void add(Runnable command) {
        commands.add(command);
    }
}
