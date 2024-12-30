package org.meteordev.juno.api;

/**
 * Base interface for all GPU resources.
 */
public interface Resource {
    /**
     * @return true if the resource is valid for use.
     */
    boolean isValid();

    /**
     * Destroys the resource.
     */
    void destroy();
}
