package org.meteordev.juno.api.pipeline.vertexformat;

/**
 * A single type of data for a vertex.
 * @param type the data type.
 * @param count the number elements inside this attribute, 1 corresponds to a single value (e.g. float), 2 to a 2-dimensional vector (e.g. vec2), and so on.
 * @param normalized whenever an integer data type should be automatically normalized to the 0 - 1 float range.
 *                   For example if the data type is an unsigned byte then value range 0 - 255 is converted to a float 0 - 1 and accessed as a float in a shader.
 */
public record VertexAttribute(VertexType type, int count, boolean normalized) {
    public int getSize() {
        return type.size * count;
    }
}
