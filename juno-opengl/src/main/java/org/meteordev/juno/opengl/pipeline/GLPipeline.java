package org.meteordev.juno.opengl.pipeline;

import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.shader.Program;
import org.meteordev.juno.opengl.shader.ProgramManager;

public class GLPipeline implements Pipeline {
    private final PipelineInfo info;
    private final Program program;

    private boolean valid;

    public GLPipeline(PipelineInfo info) {
        this.info = info;
        this.program = ProgramManager.create(info.shaderInfos);

        this.valid = true;
    }

    @Override
    public PipelineInfo getInfo() {
        return info;
    }

    @Override
    public Program getProgram() {
        return program;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid) throw new IllegalStateException("Tried to destroy an invalid pipeline");

        valid = false;
    }
}
