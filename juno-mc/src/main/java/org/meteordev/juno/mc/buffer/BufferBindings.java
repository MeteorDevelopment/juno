package org.meteordev.juno.mc.buffer;

import com.mojang.blaze3d.platform.GlStateManager;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.mc.pipeline.MCDrawableBuffers;

import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15C.GL_ELEMENT_ARRAY_BUFFER;

public class BufferBindings {
    public static int bind(Buffer buffer) {
        if (buffer == null) return 0;

        int target = buffer.getType() == BufferType.VERTEX ? GL_ARRAY_BUFFER : GL_ELEMENT_ARRAY_BUFFER;
        GlStateManager._glBindBuffer(target, ((MCBuffer) buffer).id);

        return target;
    }

    public static void bindDrawable(DrawableBuffers buffers) {
        GlStateManager._glBindVertexArray(buffers != null ? ((MCDrawableBuffers) buffers).vao : 0);
    }
}
