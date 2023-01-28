package org.meteordev.juno.mc.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.shader.Program;
import org.meteordev.juno.api.shader.uniforms.FloatUniform;
import org.meteordev.juno.api.shader.uniforms.Matrix4Uniform;
import org.meteordev.juno.api.shader.uniforms.TextureUniform;
import org.meteordev.juno.api.shader.uniforms.Uniform;
import org.meteordev.juno.mc.shader.uniforms.MCFloatUniform;
import org.meteordev.juno.mc.shader.uniforms.MCMatrix4Uniform;
import org.meteordev.juno.mc.shader.uniforms.MCTextureUniform;
import org.meteordev.juno.mc.shader.uniforms.MCUniform;

import java.nio.FloatBuffer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20C.glGetUniformLocation;

public class MCProgram implements Program {
    private static final FloatBuffer BUFFER = MemoryUtil.memAllocFloat(4 * 4);

    public final int id;

    public final Object2ObjectMap<String, MCUniform> uniforms = new Object2ObjectOpenHashMap<>();
    public boolean dirty;

    public MCProgram(int id) {
        this.id = id;
    }

    @Override
    public FloatUniform.Single getFloat1Uniform(String name) {
        return getUniform(name, () -> new MCFloatUniform(1));
    }

    @Override
    public FloatUniform.Double getFloat2Uniform(String name) {
        return getUniform(name, () -> new MCFloatUniform(2));
    }

    @Override
    public FloatUniform.Triple getFloat3Uniform(String name) {
        return getUniform(name, () -> new MCFloatUniform(3));
    }

    @Override
    public FloatUniform.Quadruple getFloat4Uniform(String name) {
        return getUniform(name, () -> new MCFloatUniform(4));
    }

    @Override
    public Matrix4Uniform getMatrix4Uniform(String name) {
        return getUniform(name, MCMatrix4Uniform::new);
    }

    @Override
    public TextureUniform getTextureUniform(String name) {
        return getUniform(name, MCTextureUniform::new);
    }

    public void applyUniforms() {
        if (!dirty) return;

        for (MCUniform uniform : uniforms.values()) {
            uniform.apply();
        }

        dirty = false;
    }

    @SuppressWarnings("unchecked")
    private <T extends Uniform> T getUniform(String name, Supplier<T> factory) {
        MCUniform uniform = uniforms.get(name);
        if (uniform != null) return (T) uniform;

        int location = glGetUniformLocation(id, name);
        uniform = (MCUniform) factory.get();

        uniform.program = this;
        uniform.name = name;
        uniform.location = location;

        uniforms.put(name, uniform);
        return (T) uniform;
    }
}
