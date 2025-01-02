package org.meteordev.juno.opengl;

import org.lwjgl.opengl.GL33C;
import org.meteordev.juno.api.BackendInfo;
import org.meteordev.juno.api.Device;
import org.meteordev.juno.api.buffer.Buffer;
import org.meteordev.juno.api.buffer.BufferType;
import org.meteordev.juno.api.commands.CommandList;
import org.meteordev.juno.api.image.Image;
import org.meteordev.juno.api.image.ImageFormat;
import org.meteordev.juno.api.pipeline.GraphicsPipeline;
import org.meteordev.juno.api.pipeline.Shader;
import org.meteordev.juno.api.pipeline.ShaderType;
import org.meteordev.juno.api.pipeline.state.RenderState;
import org.meteordev.juno.api.sampler.Filter;
import org.meteordev.juno.api.sampler.Sampler;
import org.meteordev.juno.api.sampler.Wrap;
import org.meteordev.juno.opengl.buffer.GLBuffer;
import org.meteordev.juno.opengl.buffer.VaoManager;
import org.meteordev.juno.opengl.commands.GLCommandList;
import org.meteordev.juno.opengl.image.FramebufferManager;
import org.meteordev.juno.opengl.image.GLImage;
import org.meteordev.juno.opengl.pipeline.GLGraphicsPipeline;
import org.meteordev.juno.opengl.pipeline.GLShader;
import org.meteordev.juno.opengl.sampler.GLSampler;

public class GLDevice implements Device {
    private final GLLimits limits;
    private final GLState state;

    private final VaoManager vaoManager;
    private final FramebufferManager framebufferManager;

    private final GrowableByteBuffer uniforms;
    private final int uniformBuffer;

    private final Image backBufferColor;
    private final Image backBufferDepth;

    private final BackendInfo info;

    protected GLDevice() {
        limits = new GLLimits(GL33C.glGetInteger(GL33C.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT));
        state = new GLState();

        GL.objectLabelAvailable = org.lwjgl.opengl.GL.getCapabilities().glObjectLabel != 0;
        state.load();

        vaoManager = new VaoManager();
        framebufferManager = new FramebufferManager();

        uniforms = new GrowableByteBuffer(limits.uniformBufferOffsetAlignment(), 4096 * 1024);
        uniformBuffer = GL33C.glGenBuffers();
        GL33C.glBindBuffer(GL33C.GL_UNIFORM_BUFFER, uniformBuffer);
        GL.setName(GLObjectType.BUFFER, uniformBuffer, "Uniforms");

        backBufferColor = createBackBufferColor();
        backBufferDepth = createBackBufferDepth();

        framebufferManager.put(backBufferColor, null, getBackBufferFramebuffer());
        framebufferManager.put(backBufferColor, backBufferDepth, getBackBufferFramebuffer());

        // Info
        StringBuilder detail = new StringBuilder();

        if (GL.objectLabelAvailable)
            detail.append("Object labels");

        info = new BackendInfo("OpenGL", detail.toString());
    }

    public static Device create() {
        return new GLDevice();
    }

    public GLLimits getLimits() {
        return limits;
    }

    public GLState getState() {
        return state;
    }

    public VaoManager getVaoManager() {
        return vaoManager;
    }

    public FramebufferManager getFramebufferManager() {
        return framebufferManager;
    }

    public GrowableByteBuffer getUniforms() {
        return uniforms;
    }

    public int getUniformBuffer() {
        return uniformBuffer;
    }

    protected Image createBackBufferColor() {
        return new GLImage(this, 0, 0, ImageFormat.RGB, "Back buffer - Color", 0);
    }

    protected Image createBackBufferDepth() {
        return new GLImage(this, 0, 0, ImageFormat.R, "Back buffer - Depth", 0);
    }

    protected int getBackBufferFramebuffer() {
        return 0;
    }

    // API

    @Override
    public BackendInfo getBackendInfo() {
        return info;
    }

    @Override
    public Buffer createBuffer(BufferType type, long size, String name) {
        return new GLBuffer(this, type, size, name);
    }

    @Override
    public Image createImage(int width, int height, ImageFormat format, String name) {
        int handle = GL33C.glGenTextures();
        return new GLImage(this, width, height, format, name, handle);
    }

    @Override
    public Image getBackBufferColor() {
        return backBufferColor;
    }

    @Override
    public Image getBackBufferDepth() {
        return backBufferDepth;
    }

    @Override
    public Sampler createSampler(Filter min, Filter mag, Wrap wrap) {
        return new GLSampler(min, mag, wrap);
    }

    @Override
    public Shader createShader(ShaderType type, String source, String name) {
        return new GLShader(type, source, name);
    }

    @Override
    public GraphicsPipeline createGraphicsPipeline(RenderState state, Shader vertexShader, Shader fragmentShader, String name) {
        return new GLGraphicsPipeline(state, vertexShader, fragmentShader, name);
    }

    @Override
    public void beginFrame() {
    }

    @Override
    public CommandList createCommandList() {
        return new GLCommandList(this);
    }
}
