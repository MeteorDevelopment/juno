package org.meteordev.juno.opengl;

import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.Resource;

public abstract class BaseGLResource implements Resource {
    private boolean valid;
    private int references;

    public BaseGLResource() {
        this.valid = true;
        this.references = 1;
    }

    public void addReference() {
        references++;
    }

    public void dropReference() {
        references--;

        if (references == 0)
            destroy();
    }

    protected abstract void destroy();

    // Resource

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void invalidate() {
        if (!valid)
            throw new InvalidResourceException(this);

        valid = false;
        dropReference();
    }
}
