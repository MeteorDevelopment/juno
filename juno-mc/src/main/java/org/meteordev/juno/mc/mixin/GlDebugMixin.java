package org.meteordev.juno.mc.mixin;

import net.minecraft.client.gl.GlDebug;
import org.lwjgl.opengl.GL43C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlDebug.class)
public abstract class GlDebugMixin {
    @Inject(method = "info", at = @At("HEAD"), cancellable = true)
    private static void juno$cancelGroupMessages(int source, int type, int id, int severity, int messageLength, long message, long l, CallbackInfo info) {
        if (type == GL43C.GL_DEBUG_TYPE_PUSH_GROUP || type == GL43C.GL_DEBUG_TYPE_POP_GROUP) {
            info.cancel();
        }
    }
}
