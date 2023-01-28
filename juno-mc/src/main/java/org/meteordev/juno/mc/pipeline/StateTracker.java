package org.meteordev.juno.mc.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.CullMode;
import org.meteordev.juno.api.pipeline.state.DepthFunc;

import static org.lwjgl.opengl.GL11C.*;

@SuppressWarnings("DuplicatedCode")
public class StateTracker {
    public static void bind(PipelineInfo info) {
        // Cull mode
        if (info.cullMode == CullMode.DISABLE) GlStateManager._disableCull();
        else {
            GlStateManager._enableCull();
        }

        // Blend func
        if (info.blendFunc == null) GlStateManager._disableBlend();
        else {
            GlStateManager._enableBlend();
            GlStateManager._blendFuncSeparate(getGl(info.blendFunc.srcRGB), getGl(info.blendFunc.dstRGB), getGl(info.blendFunc.srcAlpha), getGl(info.blendFunc.dstAlpha));
        }

        // Depth func
        if (info.depthFunc == null) GlStateManager._disableDepthTest();
        else {
            GlStateManager._enableDepthTest();
            GlStateManager._depthFunc(getGl(info.depthFunc));
        }

        // Write mask
        switch (info.writeMask) {
            case NONE -> {
                GlStateManager._colorMask(false, false, false, false);
                GlStateManager._depthMask(false);
            }
            case COLOR -> {
                GlStateManager._colorMask(true, true, true, true);
                GlStateManager._depthMask(false);
            }
            case DEPTH -> {
                GlStateManager._colorMask(false, false, false, false);
                GlStateManager._depthMask(true);
            }
            case COLOR_DEPTH -> {
                GlStateManager._colorMask(true, true, true, true);
                GlStateManager._depthMask(true);
            }
        }
    }

    public static void setScissor(boolean scissor) {
        if (scissor) GlStateManager._enableScissorTest();
        else GlStateManager._disableScissorTest();
    }

    private static int getGl(BlendFunc.Factor factor) {
        return switch (factor) {
            case ZERO -> GL_ZERO;
            case ONE -> GL_ONE;
            case SRC_COLOR -> GL_SRC_COLOR;
            case ONE_MINUS_SRC_COLOR -> GL_ONE_MINUS_SRC_COLOR;
            case DST_COLOR -> GL_DST_COLOR;
            case ONE_MINUS_DST_COLOR -> GL_ONE_MINUS_DST_COLOR;
            case SRC_ALPHA -> GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA -> GL_ONE_MINUS_SRC_ALPHA;
            case DST_ALPHA -> GL_DST_ALPHA;
            case ONE_MINUS_DST_ALPHA -> GL_ONE_MINUS_DST_ALPHA;
        };
    }

    private static int getGl(DepthFunc func) {
        return switch (func) {
            case NEVER -> GL_NEVER;
            case LESS -> GL_LESS;
            case LESS_THAN_OR_EQUAL -> GL_LEQUAL;
            case EQUAL -> GL_EQUAL;
            case NOT_EQUAL -> GL_NOTEQUAL;
            case GREATER -> GL_GREATER;
            case GREATER_THAN_OR_EQUAL -> GL_GEQUAL;
            case ALWAYS -> GL_ALWAYS;
        };
    }
}
