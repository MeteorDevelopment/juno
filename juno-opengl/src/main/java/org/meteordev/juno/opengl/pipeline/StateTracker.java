package org.meteordev.juno.opengl.pipeline;

import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.CullMode;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.WriteMask;

import java.util.Objects;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL14C.glBlendFuncSeparate;

public class StateTracker {
    private static CullMode CULL_MODE;
    private static BlendFunc BLEND_FUNC;
    private static DepthFunc DEPTH_FUNC;
    private static boolean SCISSOR;
    private static WriteMask WRITE_MASK = WriteMask.COLOR;

    public static void init() {
        glDisable(GL_CULL_FACE);
        glDisable(GL_BLEND);
        glDisable(GL_DEPTH_TEST);

        glColorMask(true, true, true, true);
        glDepthMask(false);
    }

    public static void bind(PipelineInfo info) {
        // Cull mode
        if (CULL_MODE != info.cullMode) {
            if (info.cullMode == CullMode.DISABLE) glDisable(GL_CULL_FACE);
            else {
                glEnable(GL_CULL_FACE);
                glCullFace(info.cullMode == CullMode.FRONT ? GL_FRONT : GL_BACK);
            }

            CULL_MODE = info.cullMode;
        }

        // Blend func
        if (!Objects.equals(BLEND_FUNC, info.blendFunc)) {
            if (info.blendFunc == null) glDisable(GL_BLEND);
            else {
                glEnable(GL_BLEND);
                glBlendFuncSeparate(getGl(info.blendFunc.srcRGB), getGl(info.blendFunc.dstRGB), getGl(info.blendFunc.srcAlpha), getGl(info.blendFunc.dstAlpha));
            }

            BLEND_FUNC = info.blendFunc;
        }

        // Depth func
        if (!Objects.equals(DEPTH_FUNC, info.depthFunc)) {
            if (info.depthFunc == null) glDisable(GL_DEPTH_TEST);
            else {
                glEnable(GL_DEPTH_TEST);
                glDepthFunc(getGl(info.depthFunc));
            }

            DEPTH_FUNC = info.depthFunc;
        }

        // Write mask
        if (!Objects.equals(WRITE_MASK, info.writeMask)) {
            switch (info.writeMask) {
                case NONE:
                    glColorMask(false, false, false, false);
                    glDepthMask(false);
                    break;

                case COLOR:
                    glColorMask(true, true, true, true);
                    glDepthMask(false);
                    break;

                case DEPTH:
                    glColorMask(false, false, false, false);
                    glDepthMask(true);
                    break;

                case COLOR_DEPTH:
                    glColorMask(true, true, true, true);
                    glDepthMask(true);
                    break;
            }

            WRITE_MASK = info.writeMask;
        }
    }

    public static void setScissor(boolean scissor) {
        if (SCISSOR != scissor) {
            if (scissor) glEnable(GL_SCISSOR_TEST);
            else glDisable(GL_SCISSOR_TEST);

            SCISSOR = scissor;
        }
    }

    private static int getGl(BlendFunc.Factor factor) {
        switch (factor) {
            case ZERO:                      return GL_ZERO;
            case ONE:                       return GL_ONE;
            case SRC_COLOR:                 return GL_SRC_COLOR;
            case ONE_MINUS_SRC_COLOR:       return GL_ONE_MINUS_SRC_COLOR;
            case DST_COLOR:                 return GL_DST_COLOR;
            case ONE_MINUS_DST_COLOR:       return GL_ONE_MINUS_DST_COLOR;
            case SRC_ALPHA:                 return GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA:       return GL_ONE_MINUS_SRC_ALPHA;
            case DST_ALPHA:                 return GL_DST_ALPHA;
            case ONE_MINUS_DST_ALPHA:       return GL_ONE_MINUS_DST_ALPHA;
            default:                        throw new UnsupportedOperationException();
        }
    }

    private static int getGl(DepthFunc func) {
        switch (func) {
            case NEVER:                 return GL_NEVER;
            case LESS:                  return GL_LESS;
            case LESS_THAN_OR_EQUAL:    return GL_LEQUAL;
            case EQUAL:                 return GL_EQUAL;
            case NOT_EQUAL:             return GL_NOTEQUAL;
            case GREATER:               return GL_GREATER;
            case GREATER_THAN_OR_EQUAL: return GL_GEQUAL;
            case ALWAYS:                return GL_ALWAYS;
            default:                    throw new UnsupportedOperationException();
        }
    }
}
