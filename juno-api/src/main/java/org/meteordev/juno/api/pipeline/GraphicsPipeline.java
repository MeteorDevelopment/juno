package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;
import org.meteordev.juno.api.pipeline.state.RenderState;

/**
 * Represents the shaders and various state that a draw call is executed with.
 */
public interface GraphicsPipeline extends Resource {
    /**
     * @return the state this pipeline will apply.
     */
    RenderState getState();

    /**
     * @return which uniform binding slots are used by the pipeline.
     */
    boolean[] getUniformBindings();

    /**
     * @return which image binding slots are used by the pipeline.
     */
    boolean[] getImageBindings();
}
