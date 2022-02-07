package com.github.retrooper.radonclient.model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class Model {
    private final int vaoId;
    private final int indicesCount;
    private final int vaoAttributeCount;

    protected Model(int vaoAttributeCount, int vaoId, int indicesCount) {
        this.vaoAttributeCount = vaoAttributeCount;
        this.vaoId = vaoId;
        this.indicesCount = indicesCount;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void passDrawData() {
        glDrawElements(GL_TRIANGLES, getIndicesCount(),
                GL_UNSIGNED_INT, 0);
    }

    public void draw() {
        //Bind VAO (each model has its own)
        glBindVertexArray(getVaoId());
        //Enable all required VAO attributes
        for (int i = 0; i < vaoAttributeCount; i++) {
            glEnableVertexAttribArray(i);
        }
        //Enable attribute 0 in VAO
        passDrawData();
        //Disable all used VAO attributes
        for (int i = 0; i < vaoAttributeCount; i++) {
            glDisableVertexAttribArray(i);
        }
        //Unbind VAO of model
        glBindVertexArray(0);
    }
}
