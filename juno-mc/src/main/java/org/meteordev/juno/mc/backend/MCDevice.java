package org.meteordev.juno.mc.backend;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.mc.mixin.CapabilityTrackerAccessor;
import org.meteordev.juno.opengl.GLDevice;
import org.meteordev.juno.opengl.GLState;

import java.lang.reflect.Field;

public class MCDevice extends GLDevice {
    private final GlStateManager.BlendFuncState mcBlendState;
    private final GlStateManager.DepthTestState mcDepthState;
    private final GlStateManager.CullFaceState mcCullState;
    private final GlStateManager.ColorMask mcColorMask;

    private final GLState mcState;

    public MCDevice() {
        this.mcBlendState = getMcState(GlStateManager.BlendFuncState.class);
        this.mcDepthState = getMcState(GlStateManager.DepthTestState.class);
        this.mcCullState = getMcState(GlStateManager.CullFaceState.class);
        this.mcColorMask = getMcState(GlStateManager.ColorMask.class);

        this.mcState = new GLState();
    }

    GLState getMcState() {
        mcState.blendEnabled = ((CapabilityTrackerAccessor) mcBlendState.capState).getState();
        mcState.srcColor = mcBlendState.srcFactorRGB;
        mcState.dstColor = mcBlendState.dstFactorRGB;
        mcState.srcAlpha = mcBlendState.dstFactorAlpha;
        mcState.dstAlpha = mcBlendState.dstFactorAlpha;

        mcState.depthTestEnabled = ((CapabilityTrackerAccessor) mcDepthState.capState).getState();
        mcState.depthFunc = mcDepthState.func;

        mcState.cullEnabled = ((CapabilityTrackerAccessor) mcCullState.capState).getState();
        mcState.cullFace = mcCullState.mode;

        mcState.colorMaskR = mcColorMask.red;
        mcState.colorMaskG = mcColorMask.green;
        mcState.colorMaskB = mcColorMask.blue;
        mcState.colorMaskA = mcColorMask.alpha;
        mcState.depthMask = mcDepthState.mask;

        return mcState;
    }

    @Override
    protected Image createBackBufferColor() {
        return new MCFramebufferImage(MinecraftClient.getInstance().getFramebuffer(), true);
    }

    @Override
    protected Image createBackBufferDepth() {
        return new MCFramebufferImage(MinecraftClient.getInstance().getFramebuffer(), false);
    }

    @Override
    protected int getBackBufferFramebuffer() {
        return MinecraftClient.getInstance().getFramebuffer().fbo;
    }

    @Override
    public CommandList createCommandList() {
        return new MCCommandList(this, super.createCommandList());
    }

    @SuppressWarnings("unchecked")
    private static <T> T getMcState(Class<T> target) {
        try {
            Class<?> glStateManager = GlStateManager.class;

            for (Field field : glStateManager.getDeclaredFields()) {
                if (field.getType() == target) {
                    field.setAccessible(true);
                    return (T) field.get(null);
                }
            }

            throw new RuntimeException();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
