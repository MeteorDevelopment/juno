package org.meteordev.juno.api.pipeline;

public class CreatePipelineException extends RuntimeException {
    public final String name;
    public final String message;

    public CreatePipelineException(String name, String message) {
        super(String.format("Failed to create pipeline '%s': %s", name, message));

        this.name = name;
        this.message = message;
    }
}
