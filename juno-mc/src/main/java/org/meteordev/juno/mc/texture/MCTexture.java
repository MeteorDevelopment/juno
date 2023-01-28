package org.meteordev.juno.mc.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL20;
import org.meteordev.juno.api.texture.Filter;
import org.meteordev.juno.api.texture.Format;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.Wrap;

import java.nio.ByteBuffer;

public class MCTexture implements Texture {
    public final int id;

    private final int width, height;
    private final Format format;

    private boolean valid;

    public MCTexture(int width, int height, Format format, Filter min, Filter mag, Wrap wrap) {
        this.id = GlStateManager._genTexture();

        this.width = width;
        this.height = height;
        this.format = format;

        TextureManager.bind(this);
        GlStateManager._pixelStore(GL20.GL_UNPACK_SWAP_BYTES, GL20.GL_FALSE);
        GlStateManager._pixelStore(GL20.GL_UNPACK_LSB_FIRST, GL20.GL_FALSE);
        GlStateManager._pixelStore(GL20.GL_UNPACK_ROW_LENGTH, 0);
        GlStateManager._pixelStore(GL20.GL_UNPACK_IMAGE_HEIGHT, 0);
        GlStateManager._pixelStore(GL20.GL_UNPACK_SKIP_ROWS, 0);
        GlStateManager._pixelStore(GL20.GL_UNPACK_SKIP_PIXELS, 0);
        GlStateManager._pixelStore(GL20.GL_UNPACK_SKIP_IMAGES, 0);
        GlStateManager._pixelStore(GL20.GL_UNPACK_ALIGNMENT, 4);

        GlStateManager._texParameter(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, getGL(min));
        GlStateManager._texParameter(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, getGL(mag));
        GlStateManager._texParameter(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, getGL(wrap));
        GlStateManager._texParameter(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, getGL(wrap));
        GlStateManager._texParameter(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_R, getGL(wrap));

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
        TextureManager.bind(this);
        GlStateManager._texImage2D(GL20.GL_TEXTURE_2D, 0, getGL(format), width, height, 0, getGL(format), GL20.GL_UNSIGNED_BYTE, data.asIntBuffer());
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid texture");

        GlStateManager._deleteTexture(id);
        valid = false;
    }

    private static int getGL(Filter filter) {
        return filter == Filter.NEAREST ? GL20.GL_NEAREST : GL20.GL_LINEAR;
    }

    private static int getGL(Wrap wrap) {
        return switch (wrap) {
            case REPEAT -> GL20.GL_REPEAT;
            case MIRRORED_REPEAT -> GL20.GL_MIRRORED_REPEAT;
            case CLAMP_TO_EDGE -> GL20.GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER -> GL20.GL_CLAMP_TO_BORDER;
        };
    }

    private static int getGL(Format format) {
        return switch (format) {
            case R -> GL20.GL_RED;
            case RGB -> GL20.GL_RGB;
            case RGBA -> GL20.GL_RGBA;
        };
    }
}
