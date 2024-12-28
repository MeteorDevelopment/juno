package org.meteordev.juno.opengl.pipeline;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.InvalidResourceException;
import org.meteordev.juno.api.pipeline.CreateShaderException;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.opengl.GL;
import org.meteordev.juno.opengl.GLObjectType;
import org.meteordev.juno.opengl.GLResource;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GLShader implements GLResource, Shader {
    private static final Pattern UNIFORM_BUFFER_PATTERN = Pattern.compile("layout\\s*\\(std140\\)\\s*uniform\\s+(\\w+)");

    private final ShaderType type;
    private final String name;

    private final int handle;
    public final Map<String, Integer> uniformBlockBindings;

    private boolean valid;

    public GLShader(ShaderType type, String source, String name) {
        this.type = type;
        this.name = name;

        handle = GL33C.glCreateShader(toGL(type));
        GL.setName(GLObjectType.SHADER, handle, name);
        GL33C.glShaderSource(handle, source);
        GL33C.glCompileShader(handle);

        if (GL33C.glGetShaderi(handle, GL33C.GL_COMPILE_STATUS) != GL33C.GL_TRUE) {
            String message = GL33C.glGetShaderInfoLog(handle);
            throw new CreateShaderException(type, name, message);
        }

        uniformBlockBindings = new HashMap<>();
        Matcher matcher = UNIFORM_BUFFER_PATTERN.matcher(source);

        while (matcher.find()) {
            String blockName = matcher.group(1);
            Pattern bindingPattern = Pattern.compile("// " + blockName + " binding: (\\d)");
            Matcher bindingMatcher = bindingPattern.matcher(source);

            if (bindingMatcher.find()) {
                uniformBlockBindings.put(blockName, Integer.parseInt(bindingMatcher.group(1)));
            } else {
                throw new CreateShaderException(type, name, "Couldn't find an uniform buffer '" + blockName + "' binding slot");
            }
        }

        valid = true;
    }

    @Override
    public int getHandle() {
        return handle;
    }

    @Override
    public ShaderType getType() {
        return type;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void destroy() {
        if (!valid)
            throw new InvalidResourceException(this);

        GL33C.glDeleteShader(handle);
        valid = false;
    }

    @Override
    public String toString() {
        return "Shader '" + name + "'";
    }

    private static int toGL(ShaderType type) {
        return switch (type) {
            case VERTEX -> GL33C.GL_VERTEX_SHADER;
            case FRAGMENT -> GL33C.GL_FRAGMENT_SHADER;
        };
    }
}
