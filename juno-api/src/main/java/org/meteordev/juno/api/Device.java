package org.meteordev.juno.api;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.PipelineState;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;

/**
 * The root interface to interact with the GPU through Juno.
 */
public interface Device {
    /**
     * @return some information about the current backend.
     */
    BackendInfo getBackendInfo();

    // Buffers

    /**
     * Creates a GPU buffer.
     * @param type the usage type of the buffer.
     * @param size fixed size of the buffer in bytes.
     * @param name name of the buffer, can be seen in debugging tools such as RenderDoc.
     * @return the new buffer.
     */
    Buffer createBuffer(BufferType type, long size, String name);

    /**
     * Creates a GPU buffer without a name.
     * @see Device#createBuffer(BufferType, long, String)
     */
    default Buffer createBuffer(BufferType type, long size) {
        return createBuffer(type, size, "");
    }

    // Images

    /**
     * Create a GPU image.
     * @param width the width of the image in pixels.
     * @param height the height of the image in pixels.
     * @param format the format of individual pixels in the image.
     * @param name name of the buffer, can be seen in debugging tools such as RenderDoc.
     * @return the new image.
     */
    Image createImage(int width, int height, ImageFormat format, String name);

    /**
     * Creates a GPU image without a name.
     * @see Device#createImage(int, int, ImageFormat, String)
     */
    default Image createImage(int width, int height, ImageFormat format) {
        return createImage(width, height, format, "");
    }

    // Back buffer

    /**
     * @return the color image of the main target, usually the window.
     */
    Image getBackBufferColor();

    /**
     * @return the depth image of the main target, usually the window.
     */
    Image getBackBufferDepth();

    // Samplers

    /**
     * Creates a GPU sampler.
     * @param min the minification filter of the sampler.
     * @param mag the magnification filter of the sampler.
     * @param wrap the wrapping mode of the sampler.
     * @return the new sampler.
     */
    Sampler createSampler(Filter min, Filter mag, Wrap wrap);

    // Shaders

    /**
     * Creates and compiles a GPU shader.
     * @param type the usage type.
     * @param source the GLSL source code.
     * @param name name of the shader, can be seen in debugging tools such as RenderDoc.
     * @return the new shader.
     */
    Shader createShader(ShaderType type, String source, String name);

    /**
     * Creates and compiles a GPU shader without a name.
     * @see Device#createShader(ShaderType, String, String)
     */
    default Shader createShader(ShaderType type, String source) {
        return createShader(type, source, "");
    }

    // Pipelines

    /**
     * Creates a GPU graphics pipeline.
     * @param state the state to be applied for draw calls.
     * @param vertexShader the vertex shader to use.
     * @param fragmentShader the fragment shader to use.
     * @param name name of the pipeline, can be seen in debugging tools such as RenderDoc.
     * @return the new pipeline.
     */
    GraphicsPipeline createGraphicsPipeline(PipelineState state, Shader vertexShader, Shader fragmentShader, String name);

    /**
     * Creates a GPU pipeline without a name.
     * @see Device#createGraphicsPipeline(PipelineState, Shader, Shader, String)
     */
    default GraphicsPipeline createGraphicsPipeline(PipelineState state, Shader vertexShader, Shader fragmentShader) {
        return createGraphicsPipeline(state, vertexShader, fragmentShader, "");
    }

    // Commands

    /**
     * Needs to be called at the start of a frame.
     */
    void beginFrame();

    /**
     * Creates a GPU command list.
     * @return the new command list.
     */
    CommandList createCommandList();
}
