package org.meteordev.juno.api.shader;

import org.joml.Matrix4f;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.meteordev.juno.api.texture.TextureBinding;

public interface Program {
    void setUniform(String name, float x);

    void setUniform(String name, float x, float y);
    default void setUniform(String name, Vector2fc vector) {
        setUniform(name, vector.x(), vector.y());
    }

    void setUniform(String name, float x, float y, float z);
    default void setUniform(String name, Vector3fc vector) {
        setUniform(name, vector.x(), vector.y(), vector.z());
    }

    void setUniform(String name, float x, float y, float z, float w);
    default void setUniform(String name, Vector4fc vector) {
        setUniform(name, vector.x(), vector.y(), vector.z(), vector.w());
    }

    void setUniform(String name, Matrix4f matrix);

    void setUniform(String name, TextureBinding binding);
}
