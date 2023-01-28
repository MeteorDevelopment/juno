package org.meteordev.juno.api.text;

public class Glyph {
    public final float x0;
    public final float y0;
    public final float x1;
    public final float y1;
    public final float u0;
    public final float v0;
    public final float u1;
    public final float v1;
    public final float xAdvance;

    public Glyph(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1, float xAdvance) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
        this.xAdvance = xAdvance;
    }
}
