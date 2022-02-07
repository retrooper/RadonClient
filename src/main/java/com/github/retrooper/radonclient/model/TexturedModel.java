package com.github.retrooper.radonclient.model;

import com.github.retrooper.radonclient.texture.Texture;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

public class TexturedModel extends Model {
    private final Texture texture;

    public TexturedModel(Texture texture, int vaoId, int indicesCount) {
        super(2, vaoId, indicesCount);
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

    @Override
    public void passDrawData() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, getTexture().getId());
        //Keep super call
        super.passDrawData();
    }
}
