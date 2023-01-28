package org.meteordev.juno.opengl.texture;

import org.meteordev.juno.api.texture.Filter;
import org.meteordev.juno.api.texture.Format;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.Wrap;
import org.meteordev.juno.opengl.GLJuno;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12C.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13C.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14C.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL45C.*;

public class GLTexture implements Texture {
    public final int id;

    private final int width, height;
    private final Format format;

    private boolean valid;

    public GLTexture(int width, int height, Format format, Filter min, Filter mag, Wrap wrap) {
        if (GLJuno.DSA) this.id = glCreateTextures(GL_TEXTURE_2D);
        else this.id = glGenTextures();

        this.width = width;
        this.height = height;
        this.format = format;

        if (GLJuno.DSA) {
            glTextureStorage2D(id, 1, getGL(format, true), width, height);

            glTextureParameteri(id, GL_TEXTURE_MIN_FILTER, getGL(min));
            glTextureParameteri(id, GL_TEXTURE_MAG_FILTER, getGL(mag));
            glTextureParameteri(id, GL_TEXTURE_WRAP_S, getGL(wrap));
            glTextureParameteri(id, GL_TEXTURE_WRAP_T, getGL(wrap));
            glTextureParameteri(id, GL_TEXTURE_WRAP_R, getGL(wrap));
        }
        else {
            TextureManager.bind(this);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, getGL(min));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, getGL(mag));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, getGL(wrap));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, getGL(wrap));
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, getGL(wrap));
        }

        this.valid = true;
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
    public void write(ByteBuffer data) {
        if (GLJuno.DSA) {
            glTextureSubImage2D(id, 0, 0, 0, width, height, getGL(format, false), GL_UNSIGNED_BYTE, data);
        }
        else {
            TextureManager.bind(this);
            glTexImage2D(GL_TEXTURE_2D, 0, getGL(format, true), width, height, 0, getGL(format, false), GL_UNSIGNED_BYTE, data);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid texture");

        glDeleteTextures(id);
        valid = false;
    }

    private static int getGL(Filter filter) {
        return filter == Filter.NEAREST ? GL_NEAREST : GL_LINEAR;
    }

    private static int getGL(Wrap wrap) {
        switch (wrap) {
            case REPEAT:            return GL_REPEAT;
            case MIRRORED_REPEAT:   return GL_MIRRORED_REPEAT;
            case CLAMP_TO_EDGE:     return GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER:   return GL_CLAMP_TO_BORDER;
            default:                throw new UnsupportedOperationException();
        }
    }

    private static int getGL(Format format, boolean internal) {
        switch (format) {
            case R:     return internal ? GL_R8 : GL_RED;
            case RGB:   return internal ? GL_RGB8 : GL_RGB;
            case RGBA:  return internal ? GL_RGBA8 : GL_RGBA;
            default:    throw new UnsupportedOperationException();
        }
    }
}
