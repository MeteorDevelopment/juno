package org.meteordev.juno.mc.texture;

import com.mojang.blaze3d.platform.GlStateManager;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.TextureBinding;

public class MCTextureBinding implements TextureBinding {
    public final int id;

    public MCTextureBinding(int id) {
        this.id = id;
    }

    public void bind(Texture texture) {
        GlStateManager._bindTexture(((MCTexture) texture).id);
    }
}
