package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.model.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class EntityRenderer extends Renderer<Model> {
    @Override
    public void render(Model model) {
        //Bind VAO
        glBindVertexArray(model.getVaoId());
        //Attribute 0 in VAO
        glEnableVertexAttribArray(0);
        glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
        glColor4f(1, 0, 0, 1);
        //Disable attribute 0 in VAO
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
