package org.meteordev.juno.opengl.pipeline;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.pipeline.CreatePipelineException;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;

import java.util.Map;

public class GLPipeline implements Pipeline {
    private final PipelineState state;
    private final String name;

    public final int handle;

    private boolean valid;

    public GLPipeline(PipelineState state, String name, Shader... shaders) {
        this.state = state.copy();
        this.name = name;

        handle = GL33C.glCreateProgram();
        GL.setName(GLObjectType.PROGRAM, handle, name);

        for (Shader shader : shaders) {
            GL33C.glAttachShader(handle, ((GLShader) shader).handle);
        }

        GL33C.glLinkProgram(handle);

        if (GL33C.glGetProgrami(handle, GL33C.GL_LINK_STATUS) != GL33C.GL_TRUE) {
            String message = GL33C.glGetProgramInfoLog(handle);
            throw new CreatePipelineException(name, message);
        }

        for (Shader shader : shaders) {
            for (Map.Entry<String, Integer> binding : ((GLShader) shader).uniformBlockBindings.entrySet()) {
                int index = GL33C.glGetUniformBlockIndex(handle, binding.getKey());
                GL33C.glUniformBlockBinding(handle, index, binding.getValue());
            }
        }

        valid = true;
    }

    @Override
    public PipelineState getState() {
        return state;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
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
