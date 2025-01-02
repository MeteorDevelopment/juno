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

    public int get(Attachment color, Attachment depth) {
        Key key = new Key(color.image(), depth != null ? depth.image() : null);
        Integer framebuffer = framebuffers.get(key);

        if (framebuffer == null) {
            framebuffer = create(color, depth);
            framebuffers.put(key, framebuffer);
        }

        return framebuffer;
    }

    public void put(Image color, Image depth, int framebuffer) {
        Key key = new Key(color, depth);
        framebuffers.put(key, framebuffer);
    }

    public void destroy(Image image) {
        for (Iterator<Key> it = framebuffers.keySet().iterator(); it.hasNext();) {
            Key key = it.next();

            if (key.color == image || key.depth == image) {
                int framebuffer = framebuffers.get(key);
                GL33C.glDeleteFramebuffers(framebuffer);

                it.remove();
            }
        }
    }

    private int create(Attachment color, Attachment depth) {
        int handle = GL33C.glGenFramebuffers();
        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, handle);

        GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_COLOR_ATTACHMENT0, GL33C.GL_TEXTURE_2D, ((GLResource) color.image()).getHandle(), 0);

        if (depth != null) {
            GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, ((GLResource) depth.image()).getHandle(), 0);
        }

        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, 0);
        return handle;
    }

    private record Key(Image color, Image depth) {}
}
