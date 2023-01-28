package org.meteordev.juno.opengl.shader;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.Program;
import org.meteordev.juno.api.texture.TextureBinding;
import org.meteordev.juno.opengl.GLJuno;
import org.meteordev.juno.opengl.texture.GLTextureBinding;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL41C.*;

public class GLProgram implements Program {
    private static final FloatBuffer BUFFER = MemoryUtil.memAllocFloat(4 * 4);

    public final int id;

    private final Map<String, Integer> uniformLocations = new HashMap<>();

    public GLProgram(int id) {
        this.id = id;
    }

    @Override
    public void setUniform(String name, float x) {
        if (GLJuno.DSA) {
            glProgramUniform1f(id, getUniformLocation(name), x);
        }
        else {
            ProgramManager.bind(this);
            glUniform1f(getUniformLocation(name), x);
        }
    }

    @Override
    public void setUniform(String name, float x, float y) {
        if (GLJuno.DSA) {
            glProgramUniform2f(id, getUniformLocation(name), x, y);
        }
        else {
            ProgramManager.bind(this);
            glUniform2f(getUniformLocation(name), x, y);
        }
    }

    @Override
    public void setUniform(String name, float x, float y, float z) {
        if (GLJuno.DSA) {
            glProgramUniform3f(id, getUniformLocation(name), x, y, z);
        }
        else {
            ProgramManager.bind(this);
            glUniform3f(getUniformLocation(name), x, y, z);
        }
    }

    @Override
    public void setUniform(String name, float x, float y, float z, float w) {
        if (GLJuno.DSA) {
            glProgramUniform4f(id, getUniformLocation(name), x, y, z, w);
        }
        else {
            ProgramManager.bind(this);
            glUniform4f(getUniformLocation(name), x, y, z, w);
        }
    }

    @Override
    public void setUniform(String name, Matrix4f matrix) {
        matrix.get(BUFFER);

        if (GLJuno.DSA) {
            glProgramUniformMatrix4fv(id, getUniformLocation(name), false, BUFFER);
        }
        else {
            ProgramManager.bind(this);
            glUniformMatrix4fv(getUniformLocation(name), false, BUFFER);
        }
    }

    @Override
    public void setUniform(String name, TextureBinding binding) {
        if (GLJuno.DSA) {
            glProgramUniform1i(id, getUniformLocation(name), ((GLTextureBinding) binding).id);
        }
        else {
            ProgramManager.bind(this);
            glUniform1i(getUniformLocation(name), ((GLTextureBinding) binding).id);
        }
    }

    private int getUniformLocation(String name) {
        Integer location = uniformLocations.get(name);
        if (location != null) return location;

        location = glGetUniformLocation(id, name);
        uniformLocations.put(name, location);

        return location;
    }
}
