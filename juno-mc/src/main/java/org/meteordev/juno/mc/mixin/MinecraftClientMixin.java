package org.meteordev.juno.mc.mixin;

import net.minecraft.client.MinecraftClient;
import org.meteordev.juno.api.JunoProvider;
import org.meteordev.juno.mc.MCJuno;
import org.meteordev.juno.mc.Omg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 0)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        JunoProvider.set(new MCJuno());
        Omg.init();
    }
}
