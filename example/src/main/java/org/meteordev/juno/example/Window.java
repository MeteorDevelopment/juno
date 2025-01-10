package org.meteordev.juno.example;

import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    public final long handle;
    private int width, height;

    public Window(String title, int width, int height) {
        glfwInit();

        this.width = width;
        this.height = height;

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        handle = glfwCreateWindow(width, height, title, 0, 0);

        glfwMakeContextCurrent(handle);
        GL.createCapabilities();

        glfwSetWindowSizeCallback(handle, (window, width1, height1) -> {
            this.width = width1;
            this.height = height1;
        });

        glfwSwapInterval(1);
        glfwShowWindow(handle);
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(handle);
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(handle);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
