package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.pipeline.state.RenderState;

public class GLState {
    public boolean blendEnabled;
    public int srcColor, dstColor;
    public int srcAlpha, dstAlpha;

    public boolean depthTestEnabled;
    public int depthFunc;

    public boolean cullEnabled;
    public int cullFace;

    public boolean scissorEnabled;

    public boolean colorMaskR, colorMaskG, colorMaskB, colorMaskA;
    public boolean depthMask;

    // Blend

    private void disableBlend() {
        if (blendEnabled) {
            GL33C.glDisable(GL33C.GL_BLEND);
            blendEnabled = false;
        }
    }

    private void enableBlend() {
        if (!blendEnabled) {
            GL33C.glEnable(GL33C.GL_BLEND);
            blendEnabled = true;
        }
    }

    private void setBlendFunc(int srcColor, int dstColor, int srcAlpha, int dstAlpha) {
        if (this.srcColor != srcColor || this.dstColor != dstColor || this.srcAlpha != srcAlpha || this.dstAlpha != dstAlpha) {
            GL33C.glBlendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
            this.srcColor = srcColor;
            this.dstColor = dstColor;
            this.srcAlpha = srcAlpha;
            this.dstAlpha = dstAlpha;
        }
    }

    // Depth test

    private void disableDepthTest() {
        if (depthTestEnabled) {
            GL33C.glDisable(GL33C.GL_DEPTH_TEST);
            depthTestEnabled = false;
        }
    }

    private void enableDepthTest() {
        if (!depthTestEnabled) {
            GL33C.glEnable(GL33C.GL_DEPTH_TEST);
            depthTestEnabled = true;
        }
    }

    private void setDepthFunc(int func) {
        if (depthFunc != func) {
            GL33C.glDepthFunc(func);
            depthFunc = func;
        }
    }

    // Cull

    private void disableCull() {
        if (cullEnabled) {
            GL33C.glDisable(GL33C.GL_CULL_FACE);
            cullEnabled = false;
        }
    }

    private void enableCull() {
        if (!cullEnabled) {
            GL33C.glEnable(GL33C.GL_CULL_FACE);
            cullEnabled = true;
        }
    }

    private void setCullFace(int face) {
        if (cullFace != face) {
            GL33C.glCullFace(face);
            cullFace = face;
        }
    }

    // Scissor

    public void disableScissor() {
        if (scissorEnabled) {
            GL33C.glDisable(GL33C.GL_SCISSOR_TEST);
            scissorEnabled = false;
        }
    }

    public void enableScissor() {
        if (!scissorEnabled) {
            GL33C.glEnable(GL33C.GL_SCISSOR_TEST);
            scissorEnabled = true;
        }
    }

    // Write mask

    private void setColorMask(boolean r, boolean g, boolean b, boolean a) {
        if (colorMaskR != r || colorMaskG != g || colorMaskB != b || colorMaskA != a) {
            GL33C.glColorMask(r, g, b, a);
            colorMaskR = r;
            colorMaskG = g;
            colorMaskB = b;
            colorMaskA = a;
        }
    }

    private void setDepthMask(boolean mask) {
        if (depthMask != mask) {
            GL33C.glDepthMask(mask);
            depthMask = mask;
        }
    }

    // Pipeline

    public void applyRenderState(RenderState state) {
        if (state.blendFunc() == null) {
            disableBlend();
        } else {
            enableBlend();
            setBlendFunc(GL.convert(state.blendFunc().srcRGB()), GL.convert(state.blendFunc().dstRGB()), GL.convert(state.blendFunc().srcAlpha()), GL.convert(state.blendFunc().dstAlpha()));
        }

        if (state.depthFunc() == null) {
            disableDepthTest();
        } else {
            enableDepthTest();
            setDepthFunc(GL.convert(state.depthFunc()));
        }

        if (state.cullFace() == null) {
            disableCull();
        } else {
            enableCull();
            setCullFace(GL.convert(state.cullFace()));
        }

        switch (state.writeMask()) {
            case NONE -> {
                setColorMask(false, false, false, false);
                setDepthMask(false);
            }
            case COLOR -> {
                setColorMask(true, true, true, true);
                setDepthMask(false);
            }
            case DEPTH -> {
                setColorMask(false, false, false, false);
                setDepthMask(true);
            }
            case COLOR_DEPTH -> {
                setColorMask(true, true, true, true);
                setDepthMask(true);
            }
        }
    }

    // Load

    public void load() {
        blendEnabled = GL33C.glGetBoolean(GL33C.GL_BLEND);
        srcColor = GL33C.glGetInteger(GL33C.GL_BLEND_SRC_RGB);
        dstColor = GL33C.glGetInteger(GL33C.GL_BLEND_DST_RGB);
        srcAlpha = GL33C.glGetInteger(GL33C.GL_BLEND_DST_ALPHA);
        dstAlpha = GL33C.glGetInteger(GL33C.GL_BLEND_DST_ALPHA);

        depthTestEnabled = GL33C.glGetBoolean(GL33C.GL_DEPTH_TEST);
        depthFunc = GL33C.glGetInteger(GL33C.GL_DEPTH_FUNC);

        cullEnabled = GL33C.glGetBoolean(GL33C.GL_CULL_FACE);
        cullFace = GL33C.glGetInteger(GL33C.GL_CULL_FACE_MODE);

        scissorEnabled = GL33C.glGetBoolean(GL33C.GL_SCISSOR_TEST);

        colorMaskR = GL33C.glGetBooleani(GL33C.GL_COLOR_WRITEMASK, 0);
        colorMaskG = GL33C.glGetBooleani(GL33C.GL_COLOR_WRITEMASK, 1);
        colorMaskB = GL33C.glGetBooleani(GL33C.GL_COLOR_WRITEMASK, 2);
        colorMaskA = GL33C.glGetBooleani(GL33C.GL_COLOR_WRITEMASK, 3);
        depthMask = GL33C.glGetBoolean(GL33C.GL_DEPTH_WRITEMASK);
    }

    // Set

    public void setTo(GLState other) {
        blendEnabled = other.blendEnabled;
        srcColor = other.srcColor;
        dstColor = other.dstColor;
        srcAlpha = other.srcAlpha;
        dstAlpha = other.dstAlpha;

        depthTestEnabled = other.depthTestEnabled;
        depthFunc = other.depthFunc;

        cullEnabled = other.cullEnabled;
        cullFace = other.cullFace;

        scissorEnabled = other.scissorEnabled;

        colorMaskR = other.colorMaskR;
        colorMaskG = other.colorMaskG;
        colorMaskB = other.colorMaskB;
        colorMaskA = other.colorMaskA;
        depthMask = other.depthMask;
    }

    // Sync

    public void syncWith(GLState other) {
        if (other.blendEnabled) enableBlend();
        else disableBlend();
        setBlendFunc(other.srcColor, other.dstColor, other.srcAlpha, other.dstAlpha);

        if (other.depthTestEnabled) enableDepthTest();
        else disableDepthTest();
        setDepthFunc(other.depthFunc);

        if (other.cullEnabled) enableCull();
        else disableCull();
        setCullFace(other.cullFace);

        if (other.scissorEnabled) enableScissor();
        else disableScissor();

        setColorMask(other.colorMaskR, other.colorMaskG, other.colorMaskB, other.colorMaskA);
        setDepthMask(other.depthMask);
    }
}
