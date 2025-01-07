package org.meteordev.juno.opengl.image;

import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.opengl.BaseGLResource;
import org.meteordev.juno.opengl.GLResource;

public class GLBackBufferImage extends BaseGLResource implements GLResource, Image {
    private final String name;

    public GLBackBufferImage(String name) {
        this.name = name;
    }

    @Override
    public int getHandle() {
        throw new InvalidResourceException(this, "invalid usage of a back-buffer image");
    }

    @Override
    public ImageFormat getFormat() {
        return ImageFormat.RGB;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    protected void destroy() {
        throw new InvalidResourceException(this, "a back-buffer image cannot be destroyed");
    }

    @Override
    public String toString() {
        return "Image '" + name + "'";
    }
}
