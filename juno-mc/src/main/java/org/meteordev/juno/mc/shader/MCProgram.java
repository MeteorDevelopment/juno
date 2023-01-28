package org.meteordev.juno.mc.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.Program;
import org.meteordev.juno.api.texture.TextureBinding;
import org.meteordev.juno.mc.texture.MCTextureBinding;

import java.nio.FloatBuffer;

public class MCProgram implements Program {
    private static final FloatBuffer BUFFER = MemoryUtil.memAllocFloat(4 * 4);

    public final int id;

    private final Object2IntMap<String> uniformLocations = new Object2IntOpenHashMap<>();

    public MCProgram(int id) {
        this.id = id;
    }

    @Override
    public void setUniform(String name, float x) {
        BUFFER.put(0, x);
        BUFFER.limit(1);

        ProgramManager.bind(this);
        GlStateManager._glUniform1(getUniformLocation(name), BUFFER);
    }

    @Override
    public void setUniform(String name, float x, float y) {
        BUFFER.put(0, x);
        BUFFER.put(1, y);
        BUFFER.limit(2);

        ProgramManager.bind(this);
        GlStateManager._glUniform2(getUniformLocation(name), BUFFER);
    }

    @Override
    public void setUniform(String name, float x, float y, float z) {
        BUFFER.put(0, x);
        BUFFER.put(1, y);
        BUFFER.put(2, z);
        BUFFER.limit(3);

        ProgramManager.bind(this);
        GlStateManager._glUniform3(getUniformLocation(name), BUFFER);
    }

    @Override
    public void setUniform(String name, float x, float y, float z, float w) {
        BUFFER.put(0, x);
        BUFFER.put(1, y);
        BUFFER.put(2, z);
        BUFFER.put(3, w);
        BUFFER.limit(4);

        ProgramManager.bind(this);
        GlStateManager._glUniform4(getUniformLocation(name), BUFFER);
    }

    @Override
    public void setUniform(String name, Matrix4f matrix) {
        matrix.get(BUFFER);

        ProgramManager.bind(this);
        GlStateManager._glUniformMatrix4(getUniformLocation(name), false, BUFFER);
    }

    @Override
    public void setUniform(String name, TextureBinding binding) {
        ProgramManager.bind(this);
        GlStateManager._glUniform1i(getUniformLocation(name), ((MCTextureBinding) binding).id);
    }

    private int getUniformLocation(String name) {
        if (uniformLocations.containsKey(name)) return uniformLocations.getInt(name);

        int location = GlStateManager._glGetUniformLocation(id, name);
        uniformLocations.put(name, location);

        return location;
    }
}
