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
     * Invalidates this resource, making it impossible to use in future commands.
     * <p>
     * Note: It is safe to invalidate a resource in the middle of recording a command list, for example (if you are not planning to use it anymore).
     *       The underlying GPU resource won't be destroyed immediately, only when it is safe to do so.
     */
    void invalidate();
}
