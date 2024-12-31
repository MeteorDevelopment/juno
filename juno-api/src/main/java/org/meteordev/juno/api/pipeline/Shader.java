package org.meteordev.juno.api.pipeline;

import org.meteordev.juno.api.Resource;

/**
 * Represents a compiled shader for use in a {@link GraphicsPipeline}.
 */
public interface Shader extends Resource {
    /**
     * @return the shader type.
     */
    ShaderType getType();
}
