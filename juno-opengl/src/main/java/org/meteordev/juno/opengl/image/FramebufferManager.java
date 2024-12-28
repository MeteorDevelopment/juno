package org.meteordev.juno.opengl.image;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.commands.Attachment;

import java.util.HashMap;
import java.util.Map;

// TODO: Bad
public class FramebufferManager {
    private final Map<Key, Integer> framebuffers = new HashMap<>();

    public int get(Attachment color, Attachment depth) {
        Key key = new Key((GLImage) color.image(), depth != null ? (GLImage) depth.image() : null);
        Integer framebuffer = framebuffers.get(key);

        if (framebuffer == null) {
            framebuffer = create(color, depth);
            framebuffers.put(key, framebuffer);
        }

        return framebuffer;
    }

    public void put(GLImage color, GLImage depth, int framebuffer) {
        Key key = new Key(color, depth);
        framebuffers.put(key, framebuffer);
    }

    private int create(Attachment color, Attachment depth) {
        int handle = GL33C.glGenFramebuffers();
        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, handle);

        GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_COLOR_ATTACHMENT0, GL33C.GL_TEXTURE_2D, ((GLImage) color.image()).handle, 0);

        if (depth != null) {
            GL33C.glFramebufferTexture2D(GL33C.GL_FRAMEBUFFER, GL33C.GL_DEPTH_ATTACHMENT, GL33C.GL_TEXTURE_2D, ((GLImage) depth.image()).handle, 0);
        }

        GL33C.glBindFramebuffer(GL33C.GL_FRAMEBUFFER, 0);
        return handle;
    }

    private record Key(GLImage color, GLImage depth) {}
}
