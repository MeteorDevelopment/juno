package org.meteordev.juno.api.pipeline;

public class CreateShaderException extends RuntimeException {
    public final ShaderType type;
    public final String name;
    public final String message;

    public CreateShaderException(ShaderType type, String name, String message) {
        super(String.format("Failed to create %s shader '%s': %s", type, name, message));

        this.type = type;
        this.name = name;
        this.message = message;
    }
}
