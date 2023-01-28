package org.meteordev.juno.mc.shader.uniforms;

import org.meteordev.juno.api.shader.uniforms.Uniform;
import org.meteordev.juno.mc.shader.MCProgram;

public abstract class MCUniform implements Uniform {
    public MCProgram program;

    public String name;
    public int location;

    @Override
    public String getName() {
        return name;
    }

    public abstract void apply();
}
