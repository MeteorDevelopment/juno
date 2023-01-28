package org.meteordev.juno.opengl.texture;

import org.lwjgl.opengl.GL45C;
import org.meteordev.juno.api.texture.Texture;
import org.meteordev.juno.api.texture.TextureBinding;
import org.meteordev.juno.opengl.GLJuno;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.glBindTexture;

public class GLTextureBinding implements TextureBinding {
    public final int id;

    public Texture texture;

    public GLTextureBinding(int id) {
        this.id = id;
    }

    public void bind(Texture texture) {
        if (this.texture != texture) {
            if (GLJuno.DSA) GL45C.glBindTextureUnit(id, ((GLTexture) texture).id);
            else glBindTexture(GL_TEXTURE_2D, ((GLTexture) texture).id);

            this.texture = texture;
        }
    }
}
