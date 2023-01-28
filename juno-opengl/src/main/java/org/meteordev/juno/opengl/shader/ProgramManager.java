package org.meteordev.juno.opengl.shader;

import org.meteordev.juno.api.shader.*;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11C.GL_FALSE;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL20C.glDeleteShader;

public class ProgramManager {
    private static Program PROGRAM;

    public static Program create(ShaderInfo[] shaderInfos) {
        // Validate
        validate(shaderInfos);

        // Compile shaders
        int vertexShader = -1;
        int fragmentShader = -1;

        for (ShaderInfo shaderInfo : shaderInfos) {
            int shader = glCreateShader(shaderInfo.type == ShaderType.VERTEX ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
            glShaderSource(shader, shaderInfo.getSource());
            glCompileShader(shader);

            if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new ShaderException(shaderInfo, glGetShaderInfoLog(shader));
            }

            if (shaderInfo.type == ShaderType.VERTEX) vertexShader = shader;
            else fragmentShader = shader;
        }

        // Create program
        int id = glCreateProgram();

        glAttachShader(id, vertexShader);
        glAttachShader(id, fragmentShader);
        glLinkProgram(id);

        if (glGetProgrami(id, GL_LINK_STATUS) == GL_FALSE) {
            throw new ProgramException("Failed to link program: " + glGetProgramInfoLog(id));
        }

        glDetachShader(id, vertexShader);
        glDeleteShader(vertexShader);

        glDetachShader(id, fragmentShader);
        glDeleteShader(fragmentShader);

        return new GLProgram(id);
    }

    public static void bind(Program program) {
        if (PROGRAM != program) {
            glUseProgram(((GLProgram) program).id);
            PROGRAM = program;
        }
    }

    private static void validate(ShaderInfo[] shaderInfos) {
        Map<ShaderType, Integer> counts = new HashMap<>();

        for (ShaderType type : ShaderType.values()) {
            counts.put(type, 0);
        }

        for (ShaderInfo shaderInfo : shaderInfos) {
            counts.computeIfPresent(shaderInfo.type, (shaderType, integer) -> integer + 1);
        }

        for (ShaderType type : counts.keySet()) {
            int count = counts.get(type);

            if (count != 1) throw new ProgramException("A Juno program needs exactly one " + type + " shader");
        }
    }
}
