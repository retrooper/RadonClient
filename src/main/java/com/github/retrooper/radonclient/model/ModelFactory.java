package com.github.retrooper.radonclient.model;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class ModelFactory {
    public static final List<Integer> VAOS = new ArrayList<>();
    public static final List<Integer> VBOS = new ArrayList<>();
    public static final List<Integer> TEXTURES = new ArrayList<>();

    public static Model createTexturedModel(float[] textureIndices, float[] vertices, int[] indices, float[] uv) {
        int vaoId = glGenVertexArrays();
        VAOS.add(vaoId);
        glBindVertexArray(vaoId);
        //Attribute 0 in VAO (vertices)
        storeFloatsInVAO(vertices, 0, 3);
        storeIndicesInVAO(indices);
        //Attribute 1 in VAO (texture coordinates)
        storeFloatsInVAO(uv, 1, 2);
        //Attribute 2 in VAO (texture index)
        storeFloatsInVAO(textureIndices, 2, 1);
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
        glVertexAttribPointer(attributeIndex, dimensions, GL_UNSIGNED_BYTE, false, 0, 0);
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

        for (int textureId : TEXTURES) {
            glDeleteTextures(textureId);
        }
    }
}
