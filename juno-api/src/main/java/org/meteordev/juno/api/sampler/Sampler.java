package org.meteordev.juno.api.sampler;

import org.meteordev.juno.api.Resource;

public interface Sampler extends Resource {
    Filter getMinFilter();

    Filter getMagFilter();

    Wrap getWrap();
}
