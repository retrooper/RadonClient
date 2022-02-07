package com.github.retrooper.radonclient.entity.player;

import com.github.retrooper.radonclient.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.joml.Math.tan;
import static org.joml.Math.toRadians;

public class Camera {
    private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
    private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
    private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
    private float aspectRatio;
    private float fov;
    private float farPlane;
    private float nearPlane;
    private Vector3f position;
    private Vector3f rotation;
    public Camera(Window window, Vector3f position, Vector3f rotation, float fov, float farPlane, float nearPlane) {
        this.position = position;
        this.rotation = rotation;
        this.aspectRatio = window.getResolution().getAspectRatio();
        this.fov = fov;
        this.farPlane = farPlane;
        this.nearPlane = nearPlane;
    }

    public Camera(Window window, Vector3f position, Vector3f rotation) {
        //FOV: Field of view
        //Far-pane: Anything farther than this will not be rendered.
        //Near-pane: Anything closer than this will not be rendered.
        this(window, position, rotation, 70.0f, 1000.0f, 0.1f);
    }

    public Camera(Window window) {
        this(window, new Vector3f(), new Vector3f());
    }

    public void move(MoveDirection direction, float amount) {
        switch (direction) {
            case FORWARD:
                position.z -= amount;
                break;
                case BACKWARD:
                position.z += amount;
                    break;
            case RIGHT:
                position.x += amount;
                break;
            case LEFT:
                position.x -= amount;
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

    public Matrix4f createViewMatrix() {
        return new Matrix4f().identity()
                .rotate(toRadians(rotation.x), X_AXIS)
                .rotate(toRadians(rotation.y), Y_AXIS)
                .rotate(toRadians(rotation.z), Z_AXIS)
                .translate(-position.x, -position.y, -position.z);
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
