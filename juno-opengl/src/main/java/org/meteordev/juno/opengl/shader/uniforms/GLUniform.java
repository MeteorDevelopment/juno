package org.meteordev.juno.opengl.shader.uniforms;

import org.meteordev.juno.api.shader.uniforms.Uniform;
import org.meteordev.juno.opengl.shader.GLProgram;

public abstract class GLUniform implements Uniform {
    public GLProgram program;

    public String name;
    public int location;

    @Override
    public String getName() {
        return name;
    }

    public abstract void apply();
}
