package org.meteordev.juno.api;

import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.Pipeline;
import org.meteordev.juno.api.pipeline.PipelineInfo;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;
import org.meteordev.juno.api.texture.*;

public interface Juno {
    Pipeline findPipeline(PipelineInfo info);

    Buffer createBuffer(BufferType type);

    DrawableBuffers createDrawable(VertexFormat vertexFormat, Buffer vbo, Buffer ibo);

    Texture createTexture(int width, int height, Format format, Filter min, Filter mag, Wrap wrap);

    void bind(Pipeline pipeline);

    TextureBinding bind(Texture texture, int slot);

    void enableScissor(int x, int y, int width, int height);

    void disableScissor();

    void draw(DrawableBuffers buffers, int indices);
}
