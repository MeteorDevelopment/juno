package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.pipeline.state.PipelineState;

public interface Pipeline extends Resource {
    PipelineState getState();
}
