package org.meteordev.juno.opengl.shader.uniforms;

import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL41C;
import org.meteordev.juno.api.shader.uniforms.FloatUniform;
import org.meteordev.juno.opengl.GLJuno;

public class GLFloatUniform extends GLUniform implements FloatUniform.Single, FloatUniform.Double, FloatUniform.Triple, FloatUniform.Quadruple {
    private final int size;

    private float v1, v2, v3, v4;
    private boolean dirty;

    public GLFloatUniform(int size) {
        this.size = size;
    }

    @Override
    public void set(float v) {
        if (this.v1 == v) return;

        this.v1 = v;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2) {
        if (this.v1 == v1 && this.v2 == v2) return;

        this.v1 = v1;
        this.v2 = v2;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2, float v3) {
        if (this.v1 == v1 && this.v2 == v2 && this.v3 == v3) return;

        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2, float v3, float v4) {
        if (this.v1 == v1 && this.v2 == v2 && this.v3 == v3 && this.v4 == v4) return;

        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void apply() {
        if (!dirty) return;

        if (GLJuno.DSA) {
            switch (size) {
                case 0: GL41C.glProgramUniform1f(program.id, location, v1); break;
                case 1: GL41C.glProgramUniform2f(program.id, location, v1, v2); break;
                case 2: GL41C.glProgramUniform3f(program.id, location, v1, v2, v3); break;
                case 3: GL41C.glProgramUniform4f(program.id, location, v1, v2, v3, v4); break;
            }
        }
        else {
            switch (size) {
                case 0: GL20C.glUniform1f(location, v1); break;
                case 1: GL20C.glUniform2f(location, v1, v2); break;
                case 2: GL20C.glUniform3f(location, v1, v2, v3); break;
                case 3: GL20C.glUniform4f(location, v1, v2, v3, v4); break;
            }
        }

        dirty = false;
    }
}
