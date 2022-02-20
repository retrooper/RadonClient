package com.github.retrooper.radonclient.model;

import com.github.retrooper.radonclient.renderer.Renderer;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

public class ModelFactory {
    public static final List<Integer> VAOS = new ArrayList<>();
    public static final List<Integer> VBOS = new ArrayList<>();
    public static final List<Integer> TEXTURES = new ArrayList<>();


    public static Model createTexturedModel(int[] textureIndices, float[] vertices, int[] indices, float[] uv) {
        int vaoId = glGenVertexArrays();
        VAOS.add(vaoId);
        glBindVertexArray(vaoId);
        //Attribute 0 in VAO (vertices)
        storeFloatsInVAO(vertices, 0, 3);
        storeIndicesInVAO(indices);
        //Attribute 1 in VAO (texture coordinates)
        storeFloatsInVAO(uv, 1, 2);
        //Attribute 2 in VAO (texture index)
        storeIntsInVAO(textureIndices, 2, 1);
        int instancesVBO = ModelFactory.createLargeVBO(Renderer.MAX_MODEL_COUNT * Renderer.MODEL_INSTANCE_DATA_SIZE);
        ModelFactory.addInstancedAttribute(vaoId, instancesVBO, 3, 4, Renderer.MODEL_INSTANCE_DATA_SIZE, 0);
        ModelFactory.addInstancedAttribute(vaoId, instancesVBO, 4, 4, Renderer.MODEL_INSTANCE_DATA_SIZE, 4);
        ModelFactory.addInstancedAttribute(vaoId, instancesVBO, 5, 4, Renderer.MODEL_INSTANCE_DATA_SIZE, 8);
        ModelFactory.addInstancedAttribute(vaoId, instancesVBO, 6, 4, Renderer.MODEL_INSTANCE_DATA_SIZE, 12);
        //Unbind current vaoID
        glBindVertexArray(0);
        return new Model(vaoId, instancesVBO, indices.length);
    }

    public static void storeFloatsInVAO(float[] data, int attributeIndex, int dimensions) {
        int vboId = glGenBuffers();
        VBOS.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        //Never gonna change it
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        //Store VBO in VAO
        glVertexAttribPointer(attributeIndex, dimensions, GL_FLOAT, false, 0, 0);
        //Unbind current buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void storeIntsInVAO(int[] data, int attributeIndex, int dimensions) {
        int vboId = glGenBuffers();
        VBOS.add(vboId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glVertexAttribIPointer(attributeIndex, dimensions, GL_INT, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void storeIndicesInVAO(int[] indices) {
        int vboId = glGenBuffers();
        VBOS.add(vboId);
        //Element about array
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
    }

    public static void updateFloatsInVBO(int vboId, float[] data, FloatBuffer buffer) {
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, buffer.capacity(), GL_STREAM_COPY);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static void addInstancedAttribute(int vaoId, int vboId, int attributeIndex, int dataSize,
                                             int instancedDataLength, int offset) {
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBindVertexArray(vaoId);
        glVertexAttribPointer(attributeIndex, dataSize, GL_FLOAT, false, instancedDataLength * 4, offset * 4L);
        glVertexAttribDivisor(attributeIndex, 1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public static int createLargeVBO(int floatCount) {
        int vbo = glGenBuffers();
        VBOS.add(vbo);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, floatCount * 4L, GL_STREAM_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    public static void destroy() {
        for (int vaoId : VAOS) {
            glDeleteVertexArrays(vaoId);
        }

        for (int vboId : VBOS) {
            glDeleteBuffers(vboId);
        }

        for (int textureId : TEXTURES) {
            glDeleteTextures(textureId);
        }
    }
}
