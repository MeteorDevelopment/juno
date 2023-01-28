package org.meteordev.juno.mc.shader.uniforms;

import com.mojang.blaze3d.platform.GlStateManager;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.uniforms.Matrix4Uniform;

import java.nio.FloatBuffer;

public class MCMatrix4Uniform extends MCUniform implements Matrix4Uniform {
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
        GlStateManager._glUniformMatrix4(location, false, BUFFER);

        dirty = false;
    }
}
