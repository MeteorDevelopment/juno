package org.meteordev.juno.mc.mixin;

import net.minecraft.client.font.GlyphAtlasTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GlyphAtlasTexture.class)
public interface GlyphAtlasTextureAccessor {
    @Accessor
    boolean getHasColor();
}
