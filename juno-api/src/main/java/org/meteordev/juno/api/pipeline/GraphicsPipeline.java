package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.pipeline.state.PipelineState;

/**
 * Represents the shaders and various state that a draw call is executed with.
 */
public interface GraphicsPipeline extends Resource {
    /**
     * @return the state this pipeline will apply.
     */
    PipelineState getState();
}
