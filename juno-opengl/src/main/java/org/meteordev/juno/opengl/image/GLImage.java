package org.meteordev.juno.opengl.image;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.opengl.*;

public class GLImage extends BaseGLResource implements GLResource, Image {
    private final GLDevice device;

    private final int width;
    private final int height;
    private final ImageFormat format;
    private final String name;

    private final int handle;

    public GLImage(GLDevice device, int width, int height, ImageFormat format, String name, int handle) {
        this.device = device;

        this.width = width;
        this.height = height;
        this.format = format;
        this.name = name;

        this.handle = handle;
        GL.setName(GLObjectType.IMAGE, handle, name);
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    protected void destroy() {
        device.getFramebufferManager().destroy(this);

        GL33C.glDeleteTextures(handle);
    }

    @Override
    public String toString() {
        return "Image '" + name + "'";
    }
}
