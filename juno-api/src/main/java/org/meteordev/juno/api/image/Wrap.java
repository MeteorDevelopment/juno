package org.meteordev.juno.api.image;

/**
 * Determines how a color value is selected when the UV coordinate is out of the 0 - 1 range.
 */
public enum Wrap {
    REPEAT,
    MIRRORED_REPEAT,
    CLAMP_TO_EDGE,
    CLAMP_TO_BORDER
}
