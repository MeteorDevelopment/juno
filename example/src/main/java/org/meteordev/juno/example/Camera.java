package org.meteordev.juno.example;

import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Camera {
    public final Matrix4f matrix = new Matrix4f();

    public final Vector3f position;
    public float yaw, pitch;

    private boolean pressedW;
    private boolean pressedA;
    private boolean pressedS;
    private boolean pressedD;

    private boolean pressedUp;
    private boolean pressedLeft;
    private boolean pressedDown;
    private boolean pressedRight;

    private boolean pressedCtrl;
    private boolean pressedSpace;
    private boolean pressedShift;

    public Camera(Window window, Vector3f position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;

        GLFW.glfwSetKeyCallback(window.handle, this::onKey);
    }

    private void onKey(long window, int key, int scancode, int action, int mods) {
        switch (key) {
            case GLFW.GLFW_KEY_W -> pressedW = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_A -> pressedA = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_S -> pressedS = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_D -> pressedD = action != GLFW.GLFW_RELEASE;

            case GLFW.GLFW_KEY_UP -> pressedUp = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_LEFT -> pressedLeft = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_DOWN -> pressedDown = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_RIGHT -> pressedRight = action != GLFW.GLFW_RELEASE;

            case GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL -> pressedCtrl = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT -> pressedShift = action != GLFW.GLFW_RELEASE;
            case GLFW.GLFW_KEY_SPACE -> pressedSpace = action != GLFW.GLFW_RELEASE;
        }
    }

    private Vector3f getDirection(boolean applyPitch) {
        if (applyPitch) {
            return new Vector3f(
                    Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)),
                    Math.sin(Math.toRadians(pitch)),
                    Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch))
            ).normalize();
        }

        return new Vector3f(
                Math.cos(Math.toRadians(yaw)),
                0,
                Math.sin(Math.toRadians(yaw))
        ).normalize();
    }

    public void update(float delta) {
        // Rotation

        if (pressedUp) pitch += 90 * delta;
        if (pressedLeft) yaw -= 90 * delta;
        if (pressedDown) pitch -= 90 * delta;
        if (pressedRight) yaw += 90 * delta;

        pitch = Math.clamp(pitch, -89.95f, 89.95f);

        // Position

        float speed = 20 * delta;
        if (pressedCtrl) speed *= 15;

        Vector3f forward = getDirection(false);
        Vector3f right = new Vector3f(forward).cross(new Vector3f(0, -1, 0)).normalize();

        forward.mul(speed);
        right.mul(speed);

        if (pressedW) position.add(forward);
        if (pressedA) position.add(right);
        if (pressedS) position.sub(forward);
        if (pressedD) position.sub(right);

        if (pressedSpace) position.y += speed;
        if (pressedShift) position.y -= speed;

        // Matrix

        matrix.identity().lookAt(position, getDirection(true).add(position), new Vector3f(0, 1, 0));
    }
}
