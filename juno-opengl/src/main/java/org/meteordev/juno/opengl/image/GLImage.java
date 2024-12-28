package org.meteordev.juno.opengl.image;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;

public class GLImage implements Image {
    private final int width;
    private final int height;
    private final ImageFormat format;
    private final String name;

    public final int handle;

    private boolean valid;

    public GLImage(int width, int height, ImageFormat format, String name, int handle) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.name = name;

        this.handle = handle;
        GL.setName(GLObjectType.IMAGE, handle, name);

        valid = true;
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
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid)
            throw new InvalidResourceException(this);

        GL33C.glDeleteTextures(handle);
        valid = false;
    }

    @Override
    public String toString() {
        return "Image '" + name + "'";
    }
}
