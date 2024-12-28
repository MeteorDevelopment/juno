package org.meteordev.juno.mc.backend;

import net.minecraft.client.gl.Framebuffer;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.opengl.GLResource;

public class MCFramebufferImage implements GLResource, Image {
    private final Framebuffer framebuffer;
    private final boolean color;

    MCFramebufferImage(Framebuffer framebuffer, boolean color) {
        this.framebuffer = framebuffer;
        this.color = color;
    }

    @Override
    public int getHandle() {
        if (color) {
            return framebuffer.getColorAttachment();
        }

        return framebuffer.getDepthAttachment();
    }

    @Override
    public int getWidth() {
        return framebuffer.textureWidth;
    }

    @Override
    public int getHeight() {
        return framebuffer.textureHeight;
    }

    @Override
    public ImageFormat getFormat() {
        return ImageFormat.RGBA;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void destroy() {
        throw new RuntimeException();
    }
}
