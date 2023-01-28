package org.meteordev.juno.opengl.texture;

import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.opengl.GLJuno;

import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;

public class TextureManager {
    private static final GLTextureBinding[] BINDINGS = new GLTextureBinding[8];
    private static GLTextureBinding ACTIVE_BINDING;

    public static void init() {
        for (int i = 0; i < BINDINGS.length; i++) {
            BINDINGS[i] = new GLTextureBinding(i);
        }

        ACTIVE_BINDING = BINDINGS[0];
        glActiveTexture(GL_TEXTURE0);
    }

    public static void setSlot(int slot) {
        if (ACTIVE_BINDING.id != slot) {
            ACTIVE_BINDING = BINDINGS[slot];
            if (!GLJuno.DSA) glActiveTexture(GL_TEXTURE0 + slot);
        }
    }

    public static GLTextureBinding bind(Texture texture) {
        ACTIVE_BINDING.bind(texture);
        return ACTIVE_BINDING;
    }
}
