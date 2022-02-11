package com.github.retrooper.radonclient.shader;

import org.joml.Matrix4f;

public class StaticShader extends Shader {
    private int transformationMatrixPtr;
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
    }

    @Override
    protected void loadUniforms() {
        transformationMatrixPtr = getUniformPointer("transformationMatrix");
        projectionMatrixPtr = getUniformPointer("projectionMatrix");
        viewMatrixPtr = getUniformPointer("viewMatrix");
    }

    public void updateTransformationMatrix(Matrix4f matrix) {
        setUniformMatrix(transformationMatrixPtr, matrix);
    }

    public void updateProjectionMatrix(Matrix4f matrix) {
        setUniformMatrix(projectionMatrixPtr, matrix);
    }

    public void updateViewMatrix(Matrix4f matrix) {
        setUniformMatrix(viewMatrixPtr, matrix);
    }
}
