package org.meteordev.juno.mc.backend;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.font.GlyphAtlasTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.mc.mixin.GlyphAtlasTextureAccessor;
import org.meteordev.juno.opengl.GLResource;

public class MCWrappedImage implements GLResource, Image {
    private final AbstractTexture texture;
    private final ImageFormat format;

    public MCWrappedImage(AbstractTexture texture) {
        this.texture = texture;

        this.format = switch (texture) {
            case NativeImageBackedTexture nativeTexture -> switch (nativeTexture.getImage().getFormat()) {
                case RGBA -> ImageFormat.RGBA;
                case RGB -> ImageFormat.RGB;
                case LUMINANCE_ALPHA -> ImageFormat.RG;
                case LUMINANCE -> ImageFormat.R;
            };
            case GlyphAtlasTexture glyphTexture -> ((GlyphAtlasTextureAccessor) glyphTexture).getHasColor() ? ImageFormat.RGBA : ImageFormat.R;
            default -> ImageFormat.RGBA;
        };
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
        return format;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void invalidate() {
        throw new RuntimeException();
    }
}
