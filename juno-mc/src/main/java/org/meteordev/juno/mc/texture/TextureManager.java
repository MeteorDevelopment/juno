package org.meteordev.juno.mc.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL20;
import org.meteordev.juno.api.texture.Texture;

public class TextureManager {
    private static final MCTextureBinding[] BINDINGS = new MCTextureBinding[GlStateManager.TEXTURE_COUNT];

    public static void init() {
        for (int i = 0; i < BINDINGS.length; i++) {
            BINDINGS[i] = new MCTextureBinding(i);
        }
    }

    public static void setSlot(int slot) {
        GlStateManager._activeTexture(GL20.GL_TEXTURE0 + slot);
    }

    public static MCTextureBinding bind(Texture texture) {
        MCTextureBinding ACTIVE_BINDING = BINDINGS[GlStateManager._getActiveTexture() - GL20.GL_TEXTURE0];
        ACTIVE_BINDING.bind(texture);

        return ACTIVE_BINDING;
    }
}
