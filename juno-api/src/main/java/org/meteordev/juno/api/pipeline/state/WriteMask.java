package org.meteordev.juno.api.pipeline.state;

public enum WriteMask {
    NONE,
    COLOR,
    DEPTH,
    COLOR_DEPTH;

    public boolean hasColor() {
        return this == COLOR || this == COLOR_DEPTH;
    }

    public boolean hasDepth() {
        return this == DEPTH || this == COLOR_DEPTH;
    }
}
