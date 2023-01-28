package org.meteordev.juno.mc.mixin;

import net.minecraft.client.render.GameRenderer;
import org.meteordev.juno.mc.Omg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(CallbackInfo info) {
        Omg.render();
    }
}
