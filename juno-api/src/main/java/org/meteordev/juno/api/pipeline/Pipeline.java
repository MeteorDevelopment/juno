package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.shader.Program;

public interface Pipeline extends Resource {
    PipelineInfo getInfo();

    Program getProgram();
}
