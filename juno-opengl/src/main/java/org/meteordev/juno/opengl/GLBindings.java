package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.Sampler;

import java.util.Arrays;

public class GLBindings {
    public final int[] textures = new int[4];
    public int activeTexture;

    public final int[] samplers = new int[4];

    public GLBindings() {
        Arrays.fill(textures, -1);
        activeTexture = -1;

        Arrays.fill(samplers, -1);
    }

    // Bind image

    public void bind(Image image, int slot) {
        if (slot != -1 && activeTexture != slot) {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + slot);
            activeTexture = slot;
        }

        if (activeTexture == -1) {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0);
            activeTexture = 0;
        }

        int handle = ((GLResource) image).getHandle();

        if (textures[activeTexture] != handle) {
            GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, handle);
            textures[activeTexture] = handle;
        }
    }

    // Bind sampler

    public void bind(Sampler sampler, int slot) {
        int handle = ((GLResource) sampler).getHandle();

        if (samplers[slot] != handle) {
            GL33C.glBindSampler(slot, handle);
            samplers[slot] = handle;
        }
    }

    // Set

    public void setTo(GLBindings other) {
        System.arraycopy(other.textures, 0, textures, 0, textures.length);
        activeTexture = other.activeTexture;

        System.arraycopy(other.samplers, 0, samplers, 0, samplers.length);
    }

    // Sync

    public void syncWith(GLBindings other) {
        for (int i = 0; i < textures.length; i++) {
            if (textures[i] != other.textures[i] && other.textures[i] != -1) {
                GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + i);
                GL33C.glBindTexture(GL33C.GL_TEXTURE_2D, other.textures[i]);

                textures[i] = other.textures[i];
                activeTexture = i;
            }
        }

        for (int i = 0; i < samplers.length; i++) {
            if (samplers[i] != other.samplers[i] && other.samplers[i] != -1) {
                GL33C.glBindSampler(i, other.samplers[i]);
                samplers[i] = other.samplers[i];
            }
        }

        if (activeTexture != other.activeTexture && other.activeTexture != -1) {
            GL33C.glActiveTexture(GL33C.GL_TEXTURE0 + other.activeTexture);
            activeTexture = other.activeTexture;
        }
    }
}
