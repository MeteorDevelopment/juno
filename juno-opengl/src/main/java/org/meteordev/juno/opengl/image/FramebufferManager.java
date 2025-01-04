package org.meteordev.juno.opengl.image;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.commands.Attachment;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.opengl.GLResource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

// TODO: Bad
public class FramebufferManager {
    private final Map<Key, Integer> framebuffers = new HashMap<>();

    public int get(Attachment depth, Attachment[] color) {
        Key key = new Key(depth, color);
        Integer framebuffer = framebuffers.get(key);

        if (framebuffer == null) {
            framebuffer = create(depth, color);
            framebuffers.put(key, framebuffer);
        }

        return framebuffer;
    }

    public void put(Key key, int framebuffer) {
        framebuffers.put(key, framebuffer);
    }

    public void destroy(Image image) {
        for (Iterator<Key> it = framebuffers.keySet().iterator(); it.hasNext();) {
            Key key = it.next();

            if (key.depth == image || key.color0 == image || key.color1 == image || key.color2 == image || key.color3 == image) {
                int framebuffer = framebuffers.get(key);
                GL33C.glDeleteFramebuffers(framebuffer);

                it.remove();
            }
        }
    }

    private int create(Attachment depth, Attachment[] color) {
        int handle = GL33C.glGenFramebuffers();
        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, handle);

        if (depth != null) {
            GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, ((GLResource) depth.image()).getHandle(), 0);
        }

        for (int i = 0; i < color.length; i++) {
            if (color[i] != null)
                GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_COLOR_ATTACHMENT0 + i, GL33C.GL_TEXTURE_2D, ((GLResource) color[i].image()).getHandle(), 0);
        }

        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, 0);
        return handle;
    }

    public record Key(Image depth, Image color0, Image color1, Image color2, Image color3) {
        public Key(Attachment depth, Attachment[] color) {
            this(
                    depth != null ? depth.image() : null,
                    color.length > 0 ? color[0].image() : null,
                    color.length > 1 ? color[1].image() : null,
                    color.length > 2 ? color[2].image() : null,
                    color.length > 3 ? color[3].image() : null
            );
        }
    }
}
