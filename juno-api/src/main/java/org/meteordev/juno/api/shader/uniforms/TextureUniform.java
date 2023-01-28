package org.meteordev.juno.api.shader.uniforms;

import org.meteordev.juno.api.texture.TextureBinding;

public interface TextureUniform extends Uniform {
    void set(TextureBinding binding);
}
