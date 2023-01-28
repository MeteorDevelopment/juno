package org.meteordev.juno.opengl.shader;

import org.meteordev.juno.api.shader.Program;
import org.meteordev.juno.api.shader.uniforms.FloatUniform;
import org.meteordev.juno.api.shader.uniforms.Matrix4Uniform;
import org.meteordev.juno.api.shader.uniforms.TextureUniform;
import org.meteordev.juno.api.shader.uniforms.Uniform;
import org.meteordev.juno.opengl.shader.uniforms.GLFloatUniform;
import org.meteordev.juno.opengl.shader.uniforms.GLMatrix4Uniform;
import org.meteordev.juno.opengl.shader.uniforms.GLTextureUniform;
import org.meteordev.juno.opengl.shader.uniforms.GLUniform;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glGetUniformLocation;

public class GLProgram implements Program {
    public final int id;

    public final Map<String, GLUniform> uniforms = new HashMap<>();
    public boolean dirty;

    public GLProgram(int id) {
        this.id = id;
    }

    @Override
    public FloatUniform.Single getFloat1Uniform(String name) {
        return getUniform(name, () -> new GLFloatUniform(1));
    }

    @Override
    public FloatUniform.Double getFloat2Uniform(String name) {
        return getUniform(name, () -> new GLFloatUniform(2));
    }

    @Override
    public FloatUniform.Triple getFloat3Uniform(String name) {
        return getUniform(name, () -> new GLFloatUniform(3));
    }

    @Override
    public FloatUniform.Quadruple getFloat4Uniform(String name) {
        return getUniform(name, () -> new GLFloatUniform(4));
    }

    @Override
    public Matrix4Uniform getMatrix4Uniform(String name) {
        return getUniform(name, GLMatrix4Uniform::new);
    }

    @Override
    public TextureUniform getTextureUniform(String name) {
        return getUniform(name, GLTextureUniform::new);
    }

    public void applyUniforms() {
        if (!dirty) return;

        for (GLUniform uniform : uniforms.values()) {
            uniform.apply();
        }

        dirty = false;
    }

    @SuppressWarnings("unchecked")
    private <T extends Uniform> T getUniform(String name, Supplier<T> factory) {
        GLUniform uniform = uniforms.get(name);
        if (uniform != null) return (T) uniform;

        int location = glGetUniformLocation(id, name);
        uniform = (GLUniform) factory.get();

        uniform.program = this;
        uniform.name = name;
        uniform.location = location;

        uniforms.put(name, uniform);
        return (T) uniform;
    }
}
