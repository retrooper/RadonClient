package com.github.retrooper.radonclient.shader;

import org.joml.Matrix4f;

public class StaticShader extends Shader {
    private int transformationMatrixPtr;
    private int projectionMatrixPtr;
    public StaticShader() {
        super("shaders/vertex.shader", "shaders/fragment.shader");
    }

    @Override
    protected void loadAttributes() {
        bindAttribute("position", 0);
        bindAttribute("uv", 1);
    }

    @Override
    protected void loadUniforms() {
        transformationMatrixPtr = getUniformPointer("transformationMatrix");
        projectionMatrixPtr = getUniformPointer("projectionMatrix");
    }

    public void updateTransformationMatrix(Matrix4f matrix) {
        setUniformMatrix(transformationMatrixPtr, matrix);
    }

    public void updateProjectionMatrix(Matrix4f matrix) {
        setUniformMatrix(projectionMatrixPtr, matrix);
    }
}
