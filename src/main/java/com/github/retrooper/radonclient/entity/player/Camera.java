package com.github.retrooper.radonclient.entity.player;

import com.github.retrooper.radonclient.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.joml.Math.*;

public class Camera {
    private float aspectRatio;
    private float fov;
    private float farPlane;
    private float nearPlane;
    private Vector3f position;
    private Vector3f rotation;
    private double mouseX, mouseY;
    private double lastMouseX, lastMouseY;
    private float sensitivity;
    private float pitch;
    private float yaw = -90;
    private Vector3f frontDirection = new Vector3f();
    private Vector3f leftDirection = new Vector3f();

    public Camera(Window window, Vector3f position, Vector3f rotation, float sensitivity, float fov, float farPlane, float nearPlane) {
        this.position = position;
        this.rotation = rotation;
        //Between 1 and 100
        this.sensitivity = Math.max(Math.min(sensitivity, 100.0f), 1.0f);
        this.aspectRatio = window.getResolution().getAspectRatio();
        this.fov = fov;
        this.farPlane = farPlane;
        this.nearPlane = nearPlane;
    }

    public Camera(Window window, Vector3f position, Vector3f rotation, float sensitivity) {
        //FOV: Field of view
        //Far-pane: Anything farther than this will not be rendered.
        //Near-pane: Anything closer than this will not be rendered.
        this(window, position, rotation, sensitivity, 70.0f, 1000.0f, 0.1f);
    }

    public Camera(Window window) {
        this(window, new Vector3f(), new Vector3f(), 100.0f);
    }

    public void setMousePos(double x, double y) {
        this.mouseX = x;
        this.mouseY = y;
    }

    public void updateRotation() {
        double mouseDeltaY = mouseY - lastMouseY;
        double mouseDeltaX = mouseX - lastMouseX;
        float sensFactor = sensitivity / 1000;
        pitch += (-mouseDeltaY) * sensFactor;
        //Must be clamped between -90 and 90
        pitch = clamp(-89.0f, 89.0f, pitch);
        yaw += mouseDeltaX * sensFactor;

        frontDirection = new Vector3f(
                cos(toRadians(yaw)) * cos(toRadians(pitch)),
                sin(toRadians(pitch)),
                sin(toRadians(yaw)) * cos(toRadians(pitch))
        ).normalize();
        leftDirection = new Vector3f(0, 1, 0)
                .cross(frontDirection).normalize();

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    public void move(MoveDirection direction, float amount) {
        switch (direction) {
            case FORWARD:
                Vector3f frontDirClone = new Vector3f(frontDirection.x, 0, frontDirection.z).normalize();
                position.add(frontDirClone.mul(amount));
                break;
            case BACKWARD:
                move(MoveDirection.FORWARD, -amount);
                break;
            case LEFT:
                Vector3f leftDirClone = new Vector3f(leftDirection.x, 0, leftDirection.z).normalize();
                position.add(leftDirClone.mul(amount));
                break;
            case RIGHT:
                move(MoveDirection.LEFT, -amount);
                break;
            case UP:
                position.y += amount;
                break;
            case DOWN:
                position.y -= amount;
                break;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = Math.max(Math.min(sensitivity, 100.0f), 1.0f);
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getFOV() {
        return fov;
    }

    public void setFOV(float fov) {
        this.fov = fov;
    }

    public float getFarPlane() {
        return farPlane;
    }

    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
    }

    public float getNearPlane() {
        return nearPlane;
    }

    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
    }

    public Vector3f getFrontDirection() {
        return frontDirection;
    }

    public Vector3f getLeftDirection() {
        return leftDirection;
    }

    public Matrix4f createViewMatrix() {
        Vector3f center = new Vector3f(position);
        center.add(frontDirection);
        return new Matrix4f()
                .identity()
                .lookAt(position, center, new Vector3f(0, 1, 0));
    }

    public Matrix4f createProjectionMatrix() {
        float yScale = 1.0f / tan(toRadians(fov) / 2.0f);
        float xScale = yScale / aspectRatio;
        float zp = farPlane + nearPlane;
        float zm = farPlane - nearPlane;
        Matrix4f projectionMatrix = new Matrix4f().identity();
        projectionMatrix.m00(xScale);
        projectionMatrix.m11(yScale);
        projectionMatrix.m22(-zp / zm);
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(-2.0f * farPlane * nearPlane / zm);
        projectionMatrix.m33(0.0f);
        return projectionMatrix;
    }
}
