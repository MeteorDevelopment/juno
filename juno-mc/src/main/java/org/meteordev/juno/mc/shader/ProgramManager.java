package org.meteordev.juno.mc.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL20;
import org.meteordev.juno.api.shader.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramManager {
    public static Program create(ShaderInfo[] shaderInfos) {
        // Validate
        validate(shaderInfos);

        // Compile shaders
        int vertexShader = -1;
        int fragmentShader = -1;

        for (ShaderInfo shaderInfo : shaderInfos) {
            int shader = GlStateManager.glCreateShader(shaderInfo.type == ShaderType.VERTEX ? GL20.GL_VERTEX_SHADER : GL20.GL_FRAGMENT_SHADER);
            GlStateManager.glShaderSource(shader, List.of(shaderInfo.getSource()));
            GlStateManager.glCompileShader(shader);

            if (GlStateManager.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
                throw new ShaderException(shaderInfo, GlStateManager.glGetShaderInfoLog(shader, 1024));
            }

            if (shaderInfo.type == ShaderType.VERTEX) vertexShader = shader;
            else fragmentShader = shader;
        }

        // Create program
        int id = GlStateManager.glCreateProgram();

        GlStateManager.glAttachShader(id, vertexShader);
        GlStateManager.glAttachShader(id, fragmentShader);
        GlStateManager.glLinkProgram(id);

        if (GlStateManager.glGetProgrami(id, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            throw new ProgramException("Failed to link program: " + GlStateManager.glGetProgramInfoLog(id, 1024));
        }

        GL20.glDetachShader(id, vertexShader);
        GlStateManager.glDeleteShader(vertexShader);

        GL20.glDetachShader(id, fragmentShader);
        GlStateManager.glDeleteShader(fragmentShader);

        return new MCProgram(id);
    }

    // TODO: Somehow cache this at least for uniform bindings
    public static void bind(Program program) {
        GlStateManager._glUseProgram(((MCProgram) program).id);
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
