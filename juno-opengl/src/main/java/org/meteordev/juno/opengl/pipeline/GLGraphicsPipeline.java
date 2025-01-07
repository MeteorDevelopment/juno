package org.meteordev.juno.opengl.pipeline;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.pipeline.CreatePipelineException;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.state.RenderState;
import org.meteordev.juno.opengl.BaseGLResource;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;
import org.meteordev.juno.opengl.GLResource;

public class GLGraphicsPipeline extends BaseGLResource implements GLResource, GraphicsPipeline {
    private final RenderState state;
    private final String name;

    public final String[] uniformBindings;
    private final boolean[] uniformBindingSlots;

    public final String[] imageBindings;
    private final boolean[] imageBindingSlots;

    private final int handle;

    public GLGraphicsPipeline(RenderState state, Shader vertex, Shader fragment, String name) {
        this.state = state;
        this.name = name;

        // Bindings
        this.uniformBindings = new String[4];
        this.uniformBindingSlots = new boolean[4];

        this.imageBindings = new String[4];
        this.imageBindingSlots = new boolean[4];

        mergeBindings((GLShader) vertex);
        mergeBindings((GLShader) fragment);

        // Handle
        this.handle = GL33C.glCreateProgram();
        GL.setName(GLObjectType.PROGRAM, handle, name);

        linkProgram((GLShader) vertex, (GLShader) fragment);
        applyBindings();
    }

    private void mergeBindings(GLShader shader) {
        for (int i = 0; i < uniformBindings.length; i++) {
            if (shader.uniformBindings[i] != null) {
                uniformBindings[i] = shader.uniformBindings[i];
                uniformBindingSlots[i] = true;
            }
        }

        for (int i = 0; i < imageBindings.length; i++) {
            if (shader.imageBindings[i] != null) {
                imageBindings[i] = shader.imageBindings[i];
                imageBindingSlots[i] = true;
            }
        }
    }

    private void linkProgram(GLShader vertex, GLShader fragment) {
        GL33C.glAttachShader(handle, vertex.getHandle());
        GL33C.glAttachShader(handle, fragment.getHandle());

        GL33C.glLinkProgram(handle);

        if (GL33C.glGetProgrami(handle, GL33C.GL_LINK_STATUS) != GL33C.GL_TRUE) {
            String message = GL33C.glGetProgramInfoLog(handle);
            throw new CreatePipelineException(name, message);
        }
    }

    private void applyBindings() {
        for (int i = 0; i < uniformBindings.length; i++) {
            if (uniformBindings[i] != null) {
                int index = GL33C.glGetUniformBlockIndex(handle, uniformBindings[i]);
                GL33C.glUniformBlockBinding(handle, index, i);
            }
        }

        for (int i = 0; i < imageBindings.length; i++) {
            if (imageBindings[i] != null) {
                int location = GL33C.glGetUniformLocation(handle, imageBindings[i]);
                GL33C.glUniform1i(location, i);
            }
        }
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public RenderState getState() {
        return state;
    }

    @Override
    public boolean[] getUniformBindings() {
        return uniformBindingSlots;
    }

    @Override
    public boolean[] getImageBindings() {
        return imageBindingSlots;
    }

    @Override
    protected void destroy() {
        GL33C.glDeleteProgram(handle);
    }

    @Override
    public String toString() {
        return "Pipeline '" + name + "'";
    }
}
