package org.meteordev.juno.api.shader;

public class ShaderException extends RuntimeException {
    public final ShaderInfo info;
    public final String error;

    public ShaderException(ShaderInfo info, String error) {
        super("Failed to compile " + info.type + " shader: " + error);

        this.info = info;
        this.error = error;
    }
}
