package com.github.retrooper.radonclient.entity.player;

import com.github.retrooper.radonclient.window.Window;
import org.joml.Matrix4f;

import static org.joml.Math.tan;
import static org.joml.Math.toRadians;

public class Camera {
    private float aspectRatio;
    private float fov;
    private float farPlane;
    private float nearPlane;
    public Camera(Window window, float fov, float farPlane, float nearPlane) {
        this.aspectRatio = window.getResolution().getAspectRatio();
        this.fov = fov;
        this.farPlane = farPlane;
        this.nearPlane = nearPlane;
    }

    public Camera(Window window) {
        //FOV: Field of view
        //Far-pane: Anything farther than this will not be rendered.
        //Near-pane: Anything closer than this will not be rendered.
        this(window, 70.0f, 1000.0f, 0.1f);
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

    public Matrix4f createProjectionMatrix() {
        float yScale = 1.0f / tan(toRadians(fov) / 2.0f);
        float xScale = yScale / aspectRatio;
        float zp = farPlane + nearPlane;
        float zm = farPlane - nearPlane;
        Matrix4f projectionMatrix = new Matrix4f();
        projectionMatrix.m00(xScale);
        projectionMatrix.m11(yScale);
        projectionMatrix.m22(-zp / zm);
        projectionMatrix.m23(-1.0f);
        projectionMatrix.m32(-2.0f * farPlane * nearPlane / zm);
        projectionMatrix.m33(0.0f);
        return projectionMatrix;
    }
}
