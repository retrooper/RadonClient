package com.github.retrooper.radonclient.shader;

public class StaticShader extends ShaderProgram{
    public StaticShader() {
        super("shaders/vertex.shader", "shaders/fragment.shader");
    }

    @Override
    protected void bindAttributes() {
        bindAttribute("position", 0);
    }
}
