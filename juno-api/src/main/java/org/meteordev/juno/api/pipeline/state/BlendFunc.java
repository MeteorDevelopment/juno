package org.meteordev.juno.api.pipeline.state;

public record BlendFunc(Factor srcRGB, Factor dstRGB, Factor srcAlpha, Factor dstAlpha) {
    public BlendFunc {
        assert srcRGB != null : "srcRGB cannot be null";
        assert dstRGB != null : "dstRGB cannot be null";
        assert srcAlpha != null : "srcAlpha cannot be null";
        assert dstAlpha != null : "dstAlpha cannot be null";
    }

    public static BlendFunc unified(Factor srcRGBA, Factor dstRGBA) {
        return new BlendFunc(srcRGBA, dstRGBA, srcRGBA, dstRGBA);
    }

    public static BlendFunc alphaBlend() {
        return unified(Factor.SRC_ALPHA, Factor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlendFunc blendFunc = (BlendFunc) o;
        return srcRGB == blendFunc.srcRGB && srcAlpha == blendFunc.srcAlpha && dstRGB == blendFunc.dstRGB && dstAlpha == blendFunc.dstAlpha;
    }

    public enum Factor {
        ZERO,
        ONE,
        SRC_COLOR,
        ONE_MINUS_SRC_COLOR,
        DST_COLOR,
        ONE_MINUS_DST_COLOR,
        SRC_ALPHA,
        ONE_MINUS_SRC_ALPHA,
        DST_ALPHA,
        ONE_MINUS_DST_ALPHA
    }
}
