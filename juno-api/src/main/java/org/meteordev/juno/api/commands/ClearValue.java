package org.meteordev.juno.api.commands;

public record ClearValue(float r, float g, float b, float a) {
    public ClearValue(float v) {
        this(v, v, v, v);
    }
}
