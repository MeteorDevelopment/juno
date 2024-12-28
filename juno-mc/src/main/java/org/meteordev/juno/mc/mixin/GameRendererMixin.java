package org.meteordev.juno.mc.mixin;

import net.minecraft.client.render.GameRenderer;
import org.meteordev.juno.mc.Juno;
import org.meteordev.juno.mc.events.Render2DCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void juno$render2d(CallbackInfo info) {
        Render2DCallback.EVENT.invoker().render(Juno.getDevice());
    }
}
