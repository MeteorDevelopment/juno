package org.meteordev.juno.api;

public class InvalidResourceException extends RuntimeException {
    public final Resource resource;

    public InvalidResourceException(Resource resource) {
        super(String.format("Resource [%s] is invalid", resource));
        this.resource = resource;
    }
}
