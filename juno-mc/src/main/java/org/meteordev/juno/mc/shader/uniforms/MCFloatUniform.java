package org.meteordev.juno.mc.shader.uniforms;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.uniforms.FloatUniform;

import java.nio.FloatBuffer;

public class MCFloatUniform extends MCUniform implements FloatUniform.Single, FloatUniform.Double, FloatUniform.Triple, FloatUniform.Quadruple {
    private final int size;

    private final FloatBuffer data;
    private boolean dirty;

    public MCFloatUniform(int size) {
        this.size = size;
        this.data = MemoryUtil.memAllocFloat(size);
    }

    @Override
    public void set(float v) {
        if (data.get(0) == v) return;

        data.put(0, v);

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2) {
        if (data.get(0) == v1 && data.get(1) == v2) return;

        data.put(0, v1);
        data.put(1, v2);

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2, float v3) {
        if (data.get(0) == v1 && data.get(1) == v2 && data.get(2) == v3) return;

        data.put(0, v1);
        data.put(1, v2);
        data.put(2, v3);

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void set(float v1, float v2, float v3, float v4) {
        if (data.get(0) == v1 && data.get(1) == v2 && data.get(2) == v3 && data.get(3) == v4) return;

        data.put(0, v1);
        data.put(1, v2);
        data.put(2, v3);
        data.put(3, v4);

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void apply() {
        if (!dirty) return;

        switch (size) {
            case 0 -> GlStateManager._glUniform1(location, data);
            case 1 -> GlStateManager._glUniform2(location, data);
            case 2 -> GlStateManager._glUniform3(location, data);
            case 3 -> GlStateManager._glUniform4(location, data);
        }

        dirty = false;
    }
}
