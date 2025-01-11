package org.meteordev.juno.api.image;

public enum ImageFormat {
    R(1),
    RG(2),
    RGB(3),
    RGBA(4);

    public final int size;

    ImageFormat(int size) {
        this.size = size;
    }
}
