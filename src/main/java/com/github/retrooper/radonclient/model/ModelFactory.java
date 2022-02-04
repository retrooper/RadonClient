package com.github.retrooper.radonclient.model;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ModelFactory {
    public static final List<Integer> VAOS = new ArrayList<>();
    public static final List<Integer> VBOS = new ArrayList<>();

    public static Model create(float[] vertices, int[] indices) {
        int vaoId = glGenVertexArrays();
        VAOS.add(vaoId);
        glBindVertexArray(vaoId);
        storeVerticesInVAO(vertices, 0, 3);
        storeIndicesInVAO(indices);
        //Unbind current vaoID
        glBindVertexArray(0);
        return new Model(vaoId, indices.length);
    }

    //The VBO is a buffer that stores the data for the model
    //VAO is an array of attributes
    //VBOS are stored in VAO
    //Create a VAO with glGenVertexArrays
    //To use it bind it glBindVertexArrays
    //Create VBO glGenBuffers
    //Bind VBO glBindBuffers
    //Store data in VBO glBufferData
    //Store VBO in VAO glVertexAttribPointer
    //Unbind VBO and VAO
    public static void storeVerticesInVAO(float[] data, int attributeIndex, int dimensions) {
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

    public static void storeIndicesInVAO(int[] indices) {
        int vboId = glGenBuffers();
        VBOS.add(vboId);
        //Element about array
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

    }

    public static void destroy() {
        for (int vaoId : VAOS) {
            glDeleteVertexArrays(vaoId);
        }

        for (int vboId : VBOS) {
            glDeleteBuffers(vboId);
        }
    }
}
