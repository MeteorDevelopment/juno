package org.meteordev.juno.opengl.shader.uniforms;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL41C;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.uniforms.Matrix4Uniform;
import org.meteordev.juno.opengl.GLJuno;

import java.nio.FloatBuffer;

public class GLMatrix4Uniform extends GLUniform implements Matrix4Uniform {
    private static final FloatBuffer BUFFER = MemoryUtil.memAllocFloat(4 * 4);

    private final Matrix4f matrix = new Matrix4f();
    private boolean dirty;

    @Override
    public void set(Matrix4f matrix) {
        this.matrix.set(matrix);

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void apply() {
        if (!dirty) return;

        matrix.get(BUFFER);

        if (GLJuno.DSA) GL41C.glProgramUniformMatrix4fv(program.id, location, false, BUFFER);
        else GL20C.glUniformMatrix4fv(location, false, BUFFER);

        dirty = false;
    }
}
