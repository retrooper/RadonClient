package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.shader.StaticShader;

public class EntityRenderer extends Renderer<StaticShader, Entity> {
    @Override
    public void render(StaticShader shader, Entity entity) {
        shader.updateTransformationMatrix(entity.getTransformationMatrix());
        entity.getModel().draw();
        /*
        //Bind VAO (each model has its own)
        glBindVertexArray(model.getVaoId());
        //Enable attribute 0 in VAO
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //Enable attribute 1 in VAO
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, model.getTexture().getId());
        glDrawElements(GL_TRIANGLES, model.getIndicesCount(),
                GL_UNSIGNED_INT, 0);
        glColor4f(1, 0, 0, 1);
        //Disable attribute 0 in VAO
        glDisableVertexAttribArray(0);
        //Disable attribute 1 in VAO
        glDisableVertexAttribArray(1);
        //Unbind it
        glBindVertexArray(0);*/
    }
}
