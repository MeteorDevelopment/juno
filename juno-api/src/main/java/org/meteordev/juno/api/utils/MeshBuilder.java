package org.meteordev.juno.api.utils;

import org.lwjgl.system.MemoryUtil;
import org.meteordev.juno.api.Juno;
import org.meteordev.juno.api.JunoProvider;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.pipeline.DrawableBuffers;
import org.meteordev.juno.api.pipeline.vertexformat.VertexFormat;

public class MeshBuilder {
    private final int vertexStride;

    private final Buffer vbo, ibo;
    private final DrawableBuffers buffers;

    private boolean building;

    private long vboSize, vboCapacity, vboData;
    private long iboSize, iboCapacity, iboData;

    private int vertexIndex;
    private int indicesCount, writtenIndicesCount;

    private double alpha = 1;

    public MeshBuilder(VertexFormat vertexFormat) {
        this.vertexStride = vertexFormat.getStride();

        Juno juno = JunoProvider.get();

        this.vbo = juno.createBuffer(BufferType.VERTEX);
        this.ibo = juno.createBuffer(BufferType.INDEX);
        this.buffers = juno.createDrawable(vertexFormat, vbo, ibo);

        vboCapacity = 256L * 256L * vertexStride;
        vboData = MemoryUtil.nmemAllocChecked(vboCapacity);

        iboCapacity = 256L * 256L * 4;
        iboData = MemoryUtil.nmemAllocChecked(iboCapacity);
    }

    public void begin() {
        if (building) throw new IllegalStateException("MeshBuilder.begin() called while already building");
        building = true;
    }

    // Vertices - Unsigned bytes

    public MeshBuilder uByte1(byte v1) {
        MemoryUtil.memPutByte(vboData + vboSize, v1);

        vboSize++;
        return this;
    }
    public MeshBuilder uByte1(int v1) {
        return uByte1((byte) v1);
    }

    public MeshBuilder uByte2(byte v1, byte v2) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutByte(ptr, v1);
        MemoryUtil.memPutByte(ptr + 1, v2);

        vboSize += 2;
        return this;
    }
    public MeshBuilder uByte2(int v1, int v2) {
        return uByte2((byte) v1, (byte) v2);
    }

    public MeshBuilder uByte3(byte v1, byte v2, byte v3) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutByte(ptr, v1);
        MemoryUtil.memPutByte(ptr + 1, v2);
        MemoryUtil.memPutByte(ptr + 2, v3);

        vboSize += 3;
        return this;
    }
    public MeshBuilder uByte3(int v1, int v2, int v3) {
        return uByte3((byte) v1, (byte) v2, (byte) v3);
    }

    public MeshBuilder uByte4(byte v1, byte v2, byte v3, byte v4) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutByte(ptr, v1);
        MemoryUtil.memPutByte(ptr + 1, v2);
        MemoryUtil.memPutByte(ptr + 2, v3);
        MemoryUtil.memPutByte(ptr + 3, v4);

        vboSize += 4;
        return this;
    }
    public MeshBuilder uByte4(int v1, int v2, int v3, int v4) {
        return uByte4((byte) v1, (byte) v2, (byte) v3, (byte) v4);
    }

    // Vertices - Floats

    public MeshBuilder float1(float v1) {
        MemoryUtil.memPutFloat(vboData + vboSize, v1);

        vboSize += 4;
        return this;
    }
    public MeshBuilder float1(double v1) {
        return float1((float) v1);
    }

    public MeshBuilder float2(float v1, float v2) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutFloat(ptr, v1);
        MemoryUtil.memPutFloat(ptr + 4, v2);

        vboSize += 8;
        return this;
    }
    public MeshBuilder float2(double v1, double v2) {
        return float2((float) v1, (float) v2);
    }

    public MeshBuilder float3(float v1, float v2, float v3) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutFloat(ptr, v1);
        MemoryUtil.memPutFloat(ptr + 4, v2);
        MemoryUtil.memPutFloat(ptr + 8, v3);

        vboSize += 12;
        return this;
    }
    public MeshBuilder float3(double v1, double v2, double v3) {
        return float3((float) v1, (float) v2, (float) v3);
    }

    public MeshBuilder float4(float v1, float v2, float v3, float v4) {
        long ptr = vboData + vboSize;

        MemoryUtil.memPutFloat(ptr, v1);
        MemoryUtil.memPutFloat(ptr + 4, v2);
        MemoryUtil.memPutFloat(ptr + 8, v3);
        MemoryUtil.memPutFloat(ptr + 12, v4);

        vboSize += 16;
        return this;
    }
    public MeshBuilder float4(double v1, double v2, double v3, double v4) {
        return float4((float) v1, (float) v2, (float) v3, (float) v4);
    }

    // Vertices - Color

    public MeshBuilder color(int r, int g, int b, int a) {
        return uByte4(r, g, b, (int) (a * alpha));
    }
    public MeshBuilder color(IJunoColor color) {
        return color(color.getR(), color.getG(), color.getB(), color.getA());
    }

    // Vertices

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public int next() {
        return vertexIndex++;
    }

    // IBO

    public void line(int i1, int i2) {
        ensureVboCapacity(2 * 10);
        ensureIboCapacity(2 * 10);

        long ptr = iboData + iboSize;

        MemoryUtil.memPutInt(ptr, i1);
        MemoryUtil.memPutInt(ptr + 4, i2);

        iboSize += 8;
        indicesCount += 2;
    }

    public void triangle(int i1, int i2, int i3) {
        ensureVboCapacity(3 * 10);
        ensureIboCapacity(3 * 10);

        long ptr = iboData + iboSize;

        MemoryUtil.memPutInt(ptr, i1);
        MemoryUtil.memPutInt(ptr + 4, i2);
        MemoryUtil.memPutInt(ptr + 8, i3);

        iboSize += 12;
        indicesCount += 3;
    }

    public void quad(int i1, int i2, int i3, int i4) {
        ensureVboCapacity(4 * 10);
        ensureIboCapacity(6 * 10);

        long ptr = iboData + iboSize;

        MemoryUtil.memPutInt(ptr, i1);
        MemoryUtil.memPutInt(ptr + 4, i2);
        MemoryUtil.memPutInt(ptr + 8, i3);

        MemoryUtil.memPutInt(ptr + 12, i3);
        MemoryUtil.memPutInt(ptr + 16, i4);
        MemoryUtil.memPutInt(ptr + 20, i1);

        iboSize += 24;
        indicesCount += 6;
    }

    // Other

    public void end() {
        if (!building) throw new IllegalStateException("MeshBuilder.end() called while not building");
        building = false;

        if (vboSize > 0) {
            vbo.write(vboData, vboSize);
            vboSize = 0;
        }

        if (iboSize > 0) {
            ibo.write(iboData, iboSize);
            iboSize = 0;
        }

        vertexIndex = 0;
        writtenIndicesCount = indicesCount;
        indicesCount = 0;
    }

    public void draw() {
        if (building) end();

        JunoProvider.get().draw(buffers, writtenIndicesCount);
    }

    public void destroy() {
        vbo.destroy();
        ibo.destroy();
        buffers.destroy();

        MemoryUtil.nmemFree(vboData);
        MemoryUtil.nmemFree(iboData);
    }

    private void ensureVboCapacity(int count) {
        int vboAdditional = count * vertexStride;
        long vboRequired = vboSize + vboAdditional;

        if (vboRequired > vboCapacity) {
            long vboCapacityNew = Math.max(vboRequired, (long) (vboCapacity * 1.5));
            long vboDataNew = MemoryUtil.nmemAllocChecked(vboCapacityNew);

            MemoryUtil.memCopy(vboData, vboDataNew, vboSize);
            MemoryUtil.nmemFree(vboData);

            vboCapacity = vboCapacityNew;
            vboData = vboDataNew;
        }
    }

    private void ensureIboCapacity(int count) {
        int iboAdditional = count * 4;
        long iboRequired = iboSize + iboAdditional;

        if (iboRequired > iboCapacity) {
            long iboCapacityNew = Math.max(iboRequired, (long) (iboCapacity * 1.5));
            long iboDataNew = MemoryUtil.nmemAllocChecked(iboCapacityNew);

            MemoryUtil.memCopy(iboData, iboDataNew, iboSize);
            MemoryUtil.nmemFree(iboData);

            iboCapacity = iboCapacityNew;
            iboData = iboDataNew;
        }
    }
}
