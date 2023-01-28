package org.meteordev.juno.mc.shader.uniforms;

import com.mojang.blaze3d.platform.GlStateManager;
import org.meteordev.juno.api.shader.uniforms.TextureUniform;
import org.meteordev.juno.api.texture.TextureBinding;
import org.meteordev.juno.mc.texture.MCTextureBinding;

public class MCTextureUniform extends MCUniform implements TextureUniform {
    private int slot;
    private boolean dirty;

    @Override
    public void set(TextureBinding binding) {
        slot = ((MCTextureBinding) binding).id;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void apply() {
        if (dirty) return;

        GlStateManager._glUniform1i(location, slot);
        dirty = false;
    }
}
