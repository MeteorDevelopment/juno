package org.meteordev.juno.opengl.image;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.image.Filter;
import org.meteordev.juno.api.image.Sampler;
import org.meteordev.juno.api.image.Wrap;
import org.meteordev.juno.opengl.BaseGLResource;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLResource;

public class GLSampler extends BaseGLResource implements GLResource, Sampler {
    private final Filter min;
    private final Filter mag;
    private final Wrap wrap;

    private final int handle;

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
    }

    @Override
    public int getHandle() {
        return handle;
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
    protected void destroy() {
        GL33C.glDeleteSamplers(handle);
    }

    @Override
    public String toString() {
        return String.format("Sampler %s,%s,%s", min, mag, wrap);
    }


}
