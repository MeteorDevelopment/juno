package org.meteordev.juno.opengl.shader.uniforms;

import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL41C;
import org.meteordev.juno.api.shader.uniforms.TextureUniform;
import org.meteordev.juno.api.texture.TextureBinding;
import org.meteordev.juno.opengl.GLJuno;
import org.meteordev.juno.opengl.texture.GLTextureBinding;

public class GLTextureUniform extends GLUniform implements TextureUniform {
    private int slot;
    private boolean dirty;

    @Override
    public void set(TextureBinding binding) {
        slot = ((GLTextureBinding) binding).id;

        program.dirty = true;
        dirty = true;
    }

    @Override
    public void apply() {
        if (dirty) return;

        if (GLJuno.DSA) GL41C.glProgramUniform1i(program.id, location, slot);
        else GL20C.glUniform1i(location, slot);

        dirty = false;
    }
}
