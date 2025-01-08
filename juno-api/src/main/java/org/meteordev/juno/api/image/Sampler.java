package org.meteordev.juno.api.image;

import org.meteordev.juno.api.Resource;

/**
 * Describes how an {@link org.meteordev.juno.api.image.Image} is sampled in a shader.
 */
public interface Sampler extends Resource {
    /**
     * @return the minification filter.
     */
    Filter getMinFilter();

    /**
     * @return the magnification filter.
     */
    Filter getMagFilter();

    /**
     * @return the wrapping mode.
     */
    Wrap getWrap();
}
