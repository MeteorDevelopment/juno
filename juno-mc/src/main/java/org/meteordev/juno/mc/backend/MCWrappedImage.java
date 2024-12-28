package org.meteordev.juno.mc.backend;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.opengl.GLResource;

public class MCWrappedImage implements GLResource, Image {
    private final AbstractTexture texture;

    public MCWrappedImage(AbstractTexture texture) {
        this.texture = texture;
    }

    @Override
    public int getHandle() {
        return texture.getGlId();
    }

    @Override
    public int getWidth() {
        GlStateManager._bindTexture(getHandle());
        return GL33C.glGetTexLevelParameteri(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_TEXTURE_WIDTH);
    }

    @Override
    public int getHeight() {
        GlStateManager._bindTexture(getHandle());
        return GL33C.glGetTexLevelParameteri(GL33C.GL_TEXTURE_2D, 0, GL33C.GL_TEXTURE_HEIGHT);
    }

    @Override
    public ImageFormat getFormat() {
        // TODO: Uhhhh
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
