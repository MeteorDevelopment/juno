package org.meteordev.juno.opengl.commands;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.LoadOp;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class GLCommandList implements CommandList {
    private final GLDevice device;
    private final FloatBuffer rgba = BufferUtils.createFloatBuffer(4);

    private final List<Runnable> commands = new ArrayList<>();
    private final List<BaseGLResource> resources = new ArrayList<>();

    private GrowableByteBuffer uniforms;

    private long fence;

    public GLCommandList(GLDevice device) {
        this.device = device;
    }

    public GrowableByteBuffer getUniforms() {
        if (uniforms == null) {
            uniforms = new GrowableByteBuffer(device.getLimits().uniformBufferOffsetAlignment(), 4096 * 1024);
        }

        return uniforms;
    }

    // API

    @Override
    public GLDevice getDevice() {
        return device;
    }

    @Override
    public void uploadToBuffer(ByteBuffer src, Buffer dst) {
        long size = src.remaining();
        long data = MemoryUtil.nmemAlloc(size);

        MemoryUtil.memCopy(MemoryUtil.memAddress(src), data, size);

        commands.add(() -> {
            GL33C.glBindBuffer(GL33C.GL_COPY_WRITE_BUFFER, ((GLResource) dst).getHandle());
            GL33C.nglBufferData(GL33C.GL_COPY_WRITE_BUFFER, size, data, GL33C.GL_DYNAMIC_DRAW);

            MemoryUtil.nmemFree(data);
        });
    }

    @Override
    public void uploadToImage(ByteBuffer src, Image dst) {
        long data = MemoryUtil.nmemAlloc(src.remaining());

        MemoryUtil.memCopy(MemoryUtil.memAddress(src), data, src.remaining());

        commands.add(() -> {
            device.getBindings().bind(dst, -1);

            GL33C.nglTexImage2D(
                    GL33C.GL_TEXTURE_2D,
                    0,
                    GL.convertInternal(dst.getFormat()),
                    dst.getWidth(),
                    dst.getHeight(),
                    0,
                    GL.convert(dst.getFormat()),
                    GL.convertType(dst.getFormat()),
                    data
            );

            MemoryUtil.nmemFree(data);
        });
    }

    @Override
    public RenderPass beginRenderPass(@Nullable Attachment depth, Attachment... color) {
        add(() -> {
            int framebuffer = device.getFramebufferManager().get(depth, color);
            GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, framebuffer);

            if (depth != null && depth.loadOp() == LoadOp.CLEAR) {
                GL33C.glClearBufferfi(GL33C.GL_DEPTH_STENCIL, 0, depth.clearValue().r(), 0);
            }

            for (int i = 0; i < color.length; i++) {
                Attachment attachment = color[i];

                if (attachment.loadOp() == LoadOp.CLEAR) {
                    rgba.put(0, attachment.clearValue().r());
                    rgba.put(1, attachment.clearValue().g());
                    rgba.put(2, attachment.clearValue().b());
                    rgba.put(3, attachment.clearValue().a());

                    GL33C.glClearBufferfv(GL33C.GL_COLOR, i, rgba);
                }
            }
        });

        if (depth != null)
            addResource(depth.image());

        for (Attachment attachment : color)
            addResource(attachment.image());

        return new GLRenderPass(this);
    }

    @Override
    public void submit() {
        if (uniforms != null) {
            GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, device.getUniformBuffer());
            GL33C.glBufferData(GL33C.GL_UNIFORM_BUFFER, uniforms.getBuffer(), GL33C.GL_DYNAMIC_DRAW);

            uniforms.delete();
        }

        for (Runnable command : commands) {
            command.run();
        }

        fence = GL33C.glFenceSync(GL33C.GL_SYNC_GPU_COMMANDS_COMPLETE, 0);

        // TODO: Not sure if flushing the opengl command queue is the right thing to do
        GL33C.glFlush();

        device.addPendingCommandList(this);
    }

    // Other

    void add(Runnable command) {
        commands.add(command);
    }

    void addResource(Resource resource) {
        if (resource instanceof BaseGLResource res) {
            resources.add(res);
            res.addReference();
        }
    }

    public boolean checkIfFinished() {
        if (GL33C.glGetSynci(fence, GL33C.GL_SYNC_STATUS, null) == GL33C.GL_SIGNALED) {
            for (BaseGLResource resource : resources)
                resource.dropReference();

            return true;
        }

        return false;
    }
}
