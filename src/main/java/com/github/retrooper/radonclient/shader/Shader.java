package com.github.retrooper.radonclient.shader;

import org.joml.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader {
    private String vertexShaderFile;
    private String fragmentShaderFile;
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public Shader(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
    }

    public void init() {
        programId = glCreateProgram();
        vertexShaderId = loadShader(vertexShaderFile, GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        loadAttributes();
        glLinkProgram(programId);
        glValidateProgram(programId);
        loadUniforms();
    }

    protected abstract void loadUniforms();

    protected abstract void loadAttributes();

    protected void setUniformBoolean(int ptr, boolean value) {
        setUniformInt(ptr, value ? 1 : 0);
    }

    protected void setUniformInt(int ptr, int value) {
        glUniform1i(ptr, value);
    }

    protected void setUniformFloat(int ptr, float value) {
        glUniform1f(ptr, value);
    }

    protected void setUniformVector2f(int ptr, Vector2f vec) {
        glUniform2f(ptr, vec.x, vec.y);
    }

    protected void setUniformVector3f(int ptr, Vector3f vec) {
        glUniform3f(ptr, vec.x, vec.y, vec.z);
    }

    protected void setUniformVector4f(int ptr, Vector4f vec) {
        glUniform4f(ptr, vec.x, vec.y, vec.z, vec.w);
    }

    protected void setUniformVector2i(int ptr, Vector2i vec) {
        glUniform2i(ptr, vec.x, vec.y);
    }

    protected void setUniformVector3i(int ptr, Vector3i vec) {
        glUniform3i(ptr, vec.x, vec.y, vec.z);
    }

    protected void setUniformVector4i(int ptr, Vector4i vec) {
        glUniform4i(ptr, vec.x, vec.y, vec.z, vec.w);
    }

    protected void setUniformMatrix(int ptr, Matrix4f matrix) {
        glUniformMatrix4fv(ptr, false, matrix.get(new float[16]));
    }

    protected int getUniformPointer(String name) {
        return glGetUniformLocation(programId, name);
    }

    protected final void bindAttribute(String varName, int attributeIndex) {
        glBindAttribLocation(programId, attributeIndex, varName);
    }

    protected int loadShader(String file, int type) {
        StringBuilder sb = new StringBuilder();
        InputStream in = Shader.class.getClassLoader().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = "";
        while (true) {
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            sb.append(line).append("//\n");
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, sb.toString());
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            System.out.println("Failed to compile shader!");
            System.err.println(glGetShaderInfoLog(shaderId, 2048));
            System.exit(-1);
        }
        return shaderId;
    }

    public void start() {
        glUseProgram(programId);
    }

    public void stop() {
        glUseProgram(0);
    }

    public void destroy() {
        stop();
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        glDeleteProgram(programId);
    }

    public int getProgramId() {
        return programId;
    }

    public int getVertexShaderId() {
        return vertexShaderId;
    }

    public int getFragmentShaderId() {
        return fragmentShaderId;
    }
}
