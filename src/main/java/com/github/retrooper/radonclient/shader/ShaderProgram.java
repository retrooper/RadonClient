package com.github.retrooper.radonclient.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram {
    private String vertexShaderFile;
    private String fragmentShaderFile;
    private int programId;
    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram(String vertexShaderFile, String fragmentShaderFile) {
        this.vertexShaderFile = vertexShaderFile;
        this.fragmentShaderFile = fragmentShaderFile;
    }

    public void init() {
        programId = glCreateProgram();
        vertexShaderId = loadShader(vertexShaderFile, GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentShaderFile, GL_FRAGMENT_SHADER);
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        bindAttributes();
        glLinkProgram(programId);
        glValidateProgram(programId);
    }

    protected abstract void bindAttributes();

    protected final void bindAttribute(String varName, int attributeIndex) {
        glBindAttribLocation(programId, attributeIndex, varName);
    }

    public int loadShader(String file, int type) {
        StringBuilder sb = new StringBuilder();
        InputStream in = ShaderProgram.class.getClassLoader().getResourceAsStream(file);
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
            System.err.println(glGetShaderInfoLog(shaderId, 1024));
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
