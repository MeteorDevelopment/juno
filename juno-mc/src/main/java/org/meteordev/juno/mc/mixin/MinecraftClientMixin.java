package org.meteordev.juno.mc.mixin;

import net.minecraft.client.MinecraftClient;
import org.meteordev.juno.mc.Juno;
import org.meteordev.juno.mc.events.JunoInitCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;clear()V", shift = At.Shift.AFTER))
    private void juno$init(CallbackInfo info) {
        Juno.init();
        JunoInitCallback.EVENT.invoker().init(Juno.getDevice());
    }
}
