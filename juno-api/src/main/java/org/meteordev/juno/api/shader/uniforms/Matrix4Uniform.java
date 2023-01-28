package org.meteordev.juno.api.shader.uniforms;

import org.joml.Matrix4f;

public interface Matrix4Uniform extends Uniform {
    void set(Matrix4f matrix);
}
