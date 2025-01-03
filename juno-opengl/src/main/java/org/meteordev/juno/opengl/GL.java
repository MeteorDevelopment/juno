package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL33C;
import org.lwjgl.opengl.GL43C;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.BlendFunc;
import org.meteordev.juno.api.pipeline.state.CullFace;
import org.meteordev.juno.api.pipeline.state.DepthFunc;
import org.meteordev.juno.api.pipeline.state.PrimitiveType;
import org.meteordev.juno.api.pipeline.vertexformat.VertexType;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Wrap;

public class GL {
    public static boolean objectLabelAvailable = false;

    public static void setName(GLObjectType type, int handle, String name) {
        if (objectLabelAvailable && !name.isEmpty()) {
            GL43C.glObjectLabel(type.gl, handle, name);
        }
    }

    // Enums

    public static int convert(ShaderType type) {
        return switch (type) {
            case VERTEX -> GL33C.GL_VERTEX_SHADER;
            case FRAGMENT -> GL33C.GL_FRAGMENT_SHADER;
        };
    }

    public static int convert(BufferType type) {
        return switch (type) {
            case INDEX -> GL33C.GL_ELEMENT_ARRAY_BUFFER;
            case VERTEX -> GL33C.GL_ARRAY_BUFFER;
        };
    }

    public static int convert(VertexType type) {
        return switch (type) {
            case UNSIGNED_BYTE -> GL33C.GL_UNSIGNED_BYTE;
            case FLOAT -> GL33C.GL_FLOAT;
        };
    }

    public static int convertInternal(ImageFormat format) {
        return switch (format) {
            case R -> GL33C.GL_R8;
            case RGB -> GL33C.GL_RGB8;
            case RGBA -> GL33C.GL_RGBA8;
        };
    }

    public static int convert(ImageFormat format) {
        return switch (format) {
            case R -> GL33C.GL_RED;
            case RGB -> GL33C.GL_RGB;
            case RGBA -> GL33C.GL_RGBA;
        };
    }

    public static int convertType(ImageFormat format) {
        return switch (format) {
            case R, RGB, RGBA -> GL33C.GL_UNSIGNED_BYTE;
        };
    }

    public static int convert(Filter filter) {
        return switch (filter) {
            case NEAREST -> GL33C.GL_NEAREST;
            case LINEAR -> GL33C.GL_LINEAR;
        };
    }

    public static int convert(Wrap wrap) {
        return switch (wrap) {
            case REPEAT -> GL33C.GL_REPEAT;
            case MIRRORED_REPEAT -> GL33C.GL_MIRRORED_REPEAT;
            case CLAMP_TO_EDGE -> GL33C.GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER -> GL33C.GL_CLAMP_TO_BORDER;
        };
    }

    public static int convert(PrimitiveType type) {
        return switch (type) {
            case TRIANGLES -> GL33C.GL_TRIANGLES;
            case LINES -> GL33C.GL_LINES;
        };
    }

    public static int convert(CullFace mode) {
        return switch (mode) {
            case FRONT -> GL33C.GL_FRONT;
            case BACK -> GL33C.GL_BACK;
        };
    }

    public static int convert(BlendFunc.Factor factor) {
        return switch (factor) {
            case ZERO -> GL33C.GL_ZERO;
            case ONE -> GL33C.GL_ONE;
            case SRC_COLOR -> GL33C.GL_SRC_COLOR;
            case ONE_MINUS_SRC_COLOR -> GL33C.GL_ONE_MINUS_SRC_COLOR;
            case DST_COLOR -> GL33C.GL_DST_COLOR;
            case ONE_MINUS_DST_COLOR -> GL33C.GL_ONE_MINUS_DST_COLOR;
            case SRC_ALPHA -> GL33C.GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA -> GL33C.GL_ONE_MINUS_SRC_ALPHA;
            case DST_ALPHA -> GL33C.GL_DST_ALPHA;
            case ONE_MINUS_DST_ALPHA -> GL33C.GL_ONE_MINUS_DST_ALPHA;
        };
    }

    public static int convert(DepthFunc func) {
        return switch (func) {
            case NEVER -> GL33C.GL_NEVER;
            case LESS -> GL33C.GL_LESS;
            case LESS_THAN_OR_EQUAL -> GL33C.GL_LEQUAL;
            case EQUAL -> GL33C.GL_EQUAL;
            case NOT_EQUAL -> GL33C.GL_NOTEQUAL;
            case GREATER -> GL33C.GL_GREATER;
            case GREATER_THAN_OR_EQUAL -> GL33C.GL_GEQUAL;
            case ALWAYS -> GL33C.GL_ALWAYS;
        };
    }
}
