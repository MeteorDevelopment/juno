package org.meteordev.juno.utils;

import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.commands.RenderPass;
import org.meteordev.juno.api.pipeline.state.PrimitiveType;
import org.meteordev.juno.api.pipeline.state.RenderState;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;

import java.nio.ByteBuffer;

public class MeshBuilder {
    private final int primitiveVerticesSize;
    private final int primitiveIndicesSize;

    private ByteBuffer vertices;
    private ByteBuffer indices;

    private boolean building;

    private int vertexI;
    private int indicesCount;

    private Buffer vbo;
    private Buffer ibo;
    private boolean needsUpload;

    public MeshBuilder(VertexFormat format, PrimitiveType primitive) {
        this.primitiveVerticesSize = primitive.vertexCount * format.getStride();
        this.primitiveIndicesSize = primitive.vertexCount * 4;

        vertices = MemoryUtil.memAlloc(primitiveVerticesSize * 512);
        indices = MemoryUtil.memAlloc(primitiveIndicesSize * 512);
    }

    public MeshBuilder(RenderState state) {
        this(state.vertexFormat(), state.primitiveType());
    }

    public void begin() {
        if (building)
            throw new IllegalStateException("MeshBuilder.begin() called while already building.");

        vertexI = 0;
        indicesCount = 0;

        building = true;
    }

    public MeshBuilder float1(float v0) {
        vertices.putFloat(v0);

        return this;
    }

    public MeshBuilder float2(float v0, float v1) {
        vertices.putFloat(v0);
        vertices.putFloat(v1);

        return this;
    }

    public MeshBuilder float3(float v0, float v1, float v2) {
        vertices.putFloat(v0);
        vertices.putFloat(v1);
        vertices.putFloat(v2);

        return this;
    }

    public MeshBuilder float4(float v0, float v1, float v2, float v3) {
        vertices.putFloat(v0);
        vertices.putFloat(v1);
        vertices.putFloat(v2);
        vertices.putFloat(v3);

        return this;
    }

    public MeshBuilder color(byte r, byte g, byte b, byte a) {
        vertices.put(r);
        vertices.put(g);
        vertices.put(b);
        vertices.put(a);

        return this;
    }

    public MeshBuilder color(float r, float g, float b, float a) {
        return color((byte) (r * 255), (byte) (g * 255), (byte) (b * 255), (byte) (a * 255));
    }

    public int next() {
        return vertexI++;
    }

    public void line(int i0, int i1) {
        indices.putInt(i0);
        indices.putInt(i1);

        indicesCount += 2;
        growIfNeeded();
    }

    public void triangle(int i0, int i1, int i2) {
        indices.putInt(i0);
        indices.putInt(i1);
        indices.putInt(i2);

        indicesCount += 3;
        growIfNeeded();
    }

    public void quad(int i0, int i1, int i2, int i3) {
        indices.putInt(i0);
        indices.putInt(i1);
        indices.putInt(i2);

        indices.putInt(i2);
        indices.putInt(i3);
        indices.putInt(i0);

        indicesCount += 6;
        growIfNeeded();
    }

    public void end() {
        if (!building)
            throw new IllegalStateException("MeshBuilder.end() called while not building.");

        building = false;

        needsUpload = true;
    }

    public void growIfNeeded() {
        // Vertices
        if (vertices.remaining() < primitiveVerticesSize) {
            int newSize = vertices.capacity() * 2;
            if (newSize % primitiveVerticesSize != 0) newSize += newSize % primitiveVerticesSize;

            ByteBuffer newVertices = MemoryUtil.memAlloc(newSize);
            newVertices.put(vertices.limit(vertices.position()).rewind());

            MemoryUtil.memFree(vertices);
            vertices = newVertices;
        }

        // Indices
        if (indices.remaining() < primitiveIndicesSize) {
            int newSize = indices.capacity() * 2;
            if (newSize % primitiveIndicesSize != 0) newSize += newSize % (primitiveIndicesSize * 4);

            ByteBuffer newIndices = MemoryUtil.memAlloc(newSize);
            newIndices.put(indices.limit(indices.position()).rewind());

            MemoryUtil.memFree(indices);
            indices = newIndices;
        }
    }

    public void draw(RenderPass pass) {
        if (building)
            end();

        if (indicesCount > 0) {
            if (needsUpload) {
                upload(pass.getCommandList());
                needsUpload = false;
            }

            pass.draw(ibo, vbo, indicesCount);
        }
    }

    public void delete() {
        MemoryUtil.memFree(vertices);
        MemoryUtil.memFree(indices);

        if (vbo != null)
            vbo.invalidate();

        if (ibo != null)
            ibo.invalidate();
    }

    private void upload(CommandList commands) {
        if (vbo != null && vbo.getSize() < vertices.position()) {
            vbo.invalidate();
            vbo = null;
        }

        if (ibo != null && ibo.getSize() < indices.position()) {
            ibo.invalidate();
            ibo = null;
        }

        if (vbo == null) {
            vbo = commands.getDevice().createBuffer(BufferType.VERTEX, vertices.capacity());
            ibo = commands.getDevice().createBuffer(BufferType.INDEX, indices.capacity());
        }

        commands.uploadToBuffer(vertices.limit(vertices.position()).rewind(), vbo);
        commands.uploadToBuffer(indices.limit(indices.position()).rewind(), ibo);

        vertices.limit(vertices.capacity());
        indices.limit(indices.capacity());
    }
}
