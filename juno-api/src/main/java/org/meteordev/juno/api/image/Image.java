package org.meteordev.juno.api.image;

import org.meteordev.juno.api.Resource;

public interface Image extends Resource {
    int getWidth();

    int getHeight();

    ImageFormat getFormat();
}
