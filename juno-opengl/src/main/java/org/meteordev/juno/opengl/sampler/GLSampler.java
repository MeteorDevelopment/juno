package org.meteordev.juno.opengl.sampler;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;
import org.meteordev.juno.opengl.GL;

public class GLSampler implements Sampler {
    private final Filter min;
    private final Filter mag;
    private final Wrap wrap;

    public final int handle;

    private boolean valid;

    public GLSampler(Filter min, Filter mag, Wrap wrap) {
        this.min = min;
        this.mag = mag;
        this.wrap = wrap;

        handle = GL33C.glGenSamplers();
        GL33C.glSamplerParameteri(handle, GL33C.GL_TEXTURE_MIN_FILTER, GL.convert(min));
        GL33C.glSamplerParameteri(handle, GL33C.GL_TEXTURE_MAG_FILTER, GL.convert(mag));
        GL33C.glSamplerParameteri(handle, GL33C.GL_TEXTURE_WRAP_S, GL.convert(wrap));
        GL33C.glSamplerParameteri(handle, GL33C.GL_TEXTURE_WRAP_T, GL.convert(wrap));
        GL33C.glSamplerParameteri(handle, GL33C.GL_TEXTURE_WRAP_R, GL.convert(wrap));

        valid = true;
    }

    @Override
    public Filter getMinFilter() {
        return min;
    }

    @Override
    public Filter getMagFilter() {
        return mag;
    }

    @Override
    public Wrap getWrap() {
        return wrap;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid)
            throw new InvalidResourceException(this);

        GL33C.glDeleteSamplers(handle);
        valid = false;
    }

    @Override
    public String toString() {
        return String.format("Sampler %s,%s,%s", min, mag, wrap);
    }


}
