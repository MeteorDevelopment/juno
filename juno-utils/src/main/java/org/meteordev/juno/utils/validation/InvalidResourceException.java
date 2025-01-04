package org.meteordev.juno.utils.validation;

import org.meteordev.juno.api.Resource;

public class InvalidResourceException extends ValidationException {
    public final Resource resource;

    public InvalidResourceException(Resource resource) {
        super(resource + " is invalid");

        this.resource = resource;
    }
}
