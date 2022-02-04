package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.model.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer extends Renderer<Model> {
    @Override
    public void render(Model model) {
        //Bind VAO (each model has its own)
        glBindVertexArray(model.getVaoId());
        //Attribute 0 in VAO
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, model.getIndicesCount(),
                GL_UNSIGNED_INT, 0);
        glColor4f(1, 0, 0, 1);
        //Disable attribute 0 in VAO
        glDisableVertexAttribArray(0);
        //Unbind it
        glBindVertexArray(0);
    }
}
