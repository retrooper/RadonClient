package com.github.retrooper.radonclient.renderer;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.model.Model;
import com.github.retrooper.radonclient.model.ModelFactory;
import com.github.retrooper.radonclient.shader.StaticShader;
import com.github.retrooper.radonclient.texture.TextureArray;
import org.joml.Matrix4f;

import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public class EntityRenderer extends Renderer<TextureArray, StaticShader, Model, Set<Entity>> {

    private int storeModelData(int index, Matrix4f transformation, float[] instanceBuffer) {
        instanceBuffer[index++] = transformation.m00();
        instanceBuffer[index++] = transformation.m01();
        instanceBuffer[index++] = transformation.m02();
        instanceBuffer[index++] = transformation.m03();
        instanceBuffer[index++] = transformation.m10();
        instanceBuffer[index++] = transformation.m11();
        instanceBuffer[index++] = transformation.m12();
        instanceBuffer[index++] = transformation.m13();
        instanceBuffer[index++] = transformation.m20();
        instanceBuffer[index++] = transformation.m21();
        instanceBuffer[index++] = transformation.m22();
        instanceBuffer[index++] = transformation.m23();
        instanceBuffer[index++] = transformation.m30();
        instanceBuffer[index++] = transformation.m31();
        instanceBuffer[index++] = transformation.m32();
        instanceBuffer[index++] = transformation.m33();
        return index;
    }


    @Override
    public void render(TextureArray textureArray, StaticShader shader, Model model, Set<Entity> entities) {
        glBindVertexArray(model.getVaoId());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        glEnableVertexAttribArray(3);
        glEnableVertexAttribArray(4);
        glEnableVertexAttribArray(5);
        glEnableVertexAttribArray(6);

        glActiveTexture(GL_TEXTURE_2D_ARRAY);
        glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray.getId());

        float[] instanceData = new float[entities.size() * Renderer.MODEL_INSTANCE_DATA_SIZE];
        int index = 0;
        for (Entity entity : entities) {
            index = storeModelData(index, entity.getTransformationMatrix(), instanceData);
        }

        INSTANCE_BUFFER.clear();
        INSTANCE_BUFFER.put(instanceData);
        INSTANCE_BUFFER.flip();
        glBindBuffer(GL_ARRAY_BUFFER, model.getInstancesVBO());
        glBufferData(GL_ARRAY_BUFFER, INSTANCE_BUFFER.capacity(), GL_STREAM_COPY);
        glBufferSubData(GL_ARRAY_BUFFER, 0, INSTANCE_BUFFER);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glDrawElementsInstanced(GL_TRIANGLES, model.getIndicesCount(), GL_UNSIGNED_INT, 0, entities.size());
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glDisableVertexAttribArray(3);
        glDisableVertexAttribArray(4);
        glDisableVertexAttribArray(5);
        glDisableVertexAttribArray(6);
        glBindVertexArray(0);
    }
}
