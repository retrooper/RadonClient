package com.github.retrooper.radonclient.shader;

import org.joml.Matrix4f;

public class StaticShader extends Shader {
    private int projectionMatrixPtr;
    private int viewMatrixPtr;
    public StaticShader() {
        super("shaders/vertex.shader", "shaders/fragment.shader");
    }

    @Override
    protected void loadAttributes() {
        bindAttribute("position", 0);
        bindAttribute("uv", 1);
        bindAttribute("textureIndex", 2);
        bindAttribute("transformation", 3);
    }

    @Override
    protected void loadUniforms() {
        projectionMatrixPtr = getUniformPointer("projection");
        viewMatrixPtr = getUniformPointer("view");
    }

    public void updateProjectionMatrix(Matrix4f matrix) {
        setUniformMatrix(projectionMatrixPtr, matrix);
    }

    public void updateViewMatrix(Matrix4f matrix) {
        setUniformMatrix(viewMatrixPtr, matrix);
    }
}
