package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.model.Model;
import com.github.retrooper.radonclient.shader.StaticShader;
import com.github.retrooper.radonclient.texture.Texture;

import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer extends Renderer<Texture, StaticShader, Model, Set<Entity>> {
    @Override
    public void render(Texture texture, StaticShader shader, Model model, Set<Entity> entities) {
        glBindVertexArray(model.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glActiveTexture(GL_TEXTURE_2D_ARRAY);
        glBindTexture(GL_TEXTURE_2D_ARRAY, texture.getId());
        for (Entity entity : entities) {
            shader.updateTransformationMatrix(entity.getTransformationMatrix());
            glDrawElements(GL_TRIANGLES, model.getIndicesCount(),
                    GL_UNSIGNED_INT, 0);
        }
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }
}
