package org.meteordev.juno.api.shader;

import org.meteordev.juno.api.shader.uniforms.FloatUniform;
import org.meteordev.juno.api.shader.uniforms.Matrix4Uniform;
import org.meteordev.juno.api.shader.uniforms.TextureUniform;

public interface Program {
    FloatUniform.Single getFloat1Uniform(String name);

    FloatUniform.Double getFloat2Uniform(String name);

    FloatUniform.Triple getFloat3Uniform(String name);

    FloatUniform.Quadruple getFloat4Uniform(String name);

    Matrix4Uniform getMatrix4Uniform(String name);

    TextureUniform getTextureUniform(String name);
}
