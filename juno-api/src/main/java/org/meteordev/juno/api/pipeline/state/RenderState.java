package org.meteordev.juno.api.pipeline.state;

import org.jetbrains.annotations.Nullable;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;

/**
 * Represents all the different settings for a draw call. Can be created with {@link RenderStateBuilder}.
 * @param vertexFormat the vertex format for the vertex buffer.
 * @param primitiveType the type of primitives to render.
 * @param blendFunc the blending function to use, null for none.
 * @param depthFunc the depth function to use, null for none.
 * @param cullFace the culling face to use, null for none.
 * @param writeMask the write mask.
 *                  For example if it is set to {@link WriteMask#COLOR} then only the color output of shaders will be saved and the depth will be discarded.
 */
public record RenderState(
        VertexFormat vertexFormat,
        PrimitiveType primitiveType,
        @Nullable BlendFunc blendFunc,
        @Nullable DepthFunc depthFunc,
        @Nullable CullFace cullFace,
        WriteMask writeMask
) {}
