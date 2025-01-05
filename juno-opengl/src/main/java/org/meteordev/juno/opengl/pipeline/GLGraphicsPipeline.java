package org.meteordev.juno.opengl.pipeline;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.pipeline.CreatePipelineException;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.state.RenderState;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;
import org.meteordev.juno.opengl.GLResource;

import java.util.Map;

public class GLGraphicsPipeline implements GLResource, GraphicsPipeline {
    private final RenderState state;
    private final String name;

    private final int handle;

    private boolean valid;

    public GLGraphicsPipeline(RenderState state, Shader vertexShader, Shader fragmentShader, String name) {
        this.state = state;
        this.name = name;

        handle = GL33C.glCreateProgram();
        GL.setName(GLObjectType.PROGRAM, handle, name);

        GL33C.glAttachShader(handle, ((GLResource) vertexShader).getHandle());
        GL33C.glAttachShader(handle, ((GLResource) fragmentShader).getHandle());

        GL33C.glLinkProgram(handle);

        if (GL33C.glGetProgrami(handle, GL33C.GL_LINK_STATUS) != GL33C.GL_TRUE) {
            String message = GL33C.glGetProgramInfoLog(handle);
            throw new CreatePipelineException(name, message);
        }

        GL33C.glUseProgram(handle);

        applyBindings((GLShader) vertexShader);
        applyBindings((GLShader) fragmentShader);

        valid = true;
    }

    private void applyBindings(GLShader shader) {
        // Uniforms blocks

        for (Map.Entry<String, Integer> binding : shader.uniformBlockBindings.entrySet()) {
            int index = GL33C.glGetUniformBlockIndex(handle, binding.getKey());
            GL33C.glUniformBlockBinding(handle, index, binding.getValue());
        }

        // Textures

        for (Map.Entry<String, Integer> binding : shader.textureBindings.entrySet()) {
            int location = GL33C.glGetUniformLocation(handle, binding.getKey());
            GL33C.glUniform1i(location, binding.getValue());
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
    public boolean isValid() {
        return valid;
    }

    @Override
    public void invalidate() {
        if (!valid)
            throw new InvalidResourceException(this);

        GL33C.glDeleteProgram(handle);
        valid = false;
    }

    @Override
    public String toString() {
        return "Pipeline '" + name + "'";
    }
}
