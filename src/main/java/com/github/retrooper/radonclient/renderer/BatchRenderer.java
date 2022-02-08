package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.shader.StaticShader;

import java.util.List;

public class BatchRenderer extends Renderer<StaticShader, List<Entity>> {
    @Override
    public void render(StaticShader shader, List<Entity> entities) {
        if (!entities.isEmpty()) {
            Entity first = entities.get(0);
            first.getModel().bind();
            for (Entity entity : entities) {
                shader.updateTransformationMatrix(entity.getTransformationMatrix());
                entity.getModel().draw();
            }
            first.getModel().unbind();
        }
    }
}
