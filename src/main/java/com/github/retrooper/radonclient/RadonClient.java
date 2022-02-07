package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.model.ModelFactory;
import com.github.retrooper.radonclient.model.TexturedModel;
import com.github.retrooper.radonclient.renderer.EntityRenderer;
import com.github.retrooper.radonclient.shader.StaticShader;
import com.github.retrooper.radonclient.texture.Texture;
import com.github.retrooper.radonclient.texture.TextureFactory;
import com.github.retrooper.radonclient.window.Resolution;
import com.github.retrooper.radonclient.window.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final Window WINDOW = new Window("RadonClient", new Resolution(1280, 720), false);
    public static final EntityRenderer RENDERER = new EntityRenderer();
    public static final StaticShader SHADER = new StaticShader();
    public static double DELTA_TIME = 0.0;

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        WINDOW.create();
        WINDOW.show();
        SHADER.init();
        int frames = 0;
        int fps;
        double lastSecondTime = 0.0;
        float[] vertices = {
                //Higher Left corner
                -0.5f, 0.5f, 0,
                //Lower Left corner
                -0.5f, -0.5f, 0,
                //Lower right corner
                0.5f, -0.5f, 0,
                //Higher right corner
                0.5f, 0.5f, 0,

        };

        int[] indices = {
                //Top left corner
                0,
                //Bottom left corner
                1,
                //Bottom right corner
                2,
                //Bottom right corner
                2,
                //Top right corner
                3,
                //Top left corner
                0
        };

        //The corners basically (a quad) as x and y coordinates.
        //Top left is (0|0), Top right is (1|0)
        //Bottom left is (0|1), Bottom right is (1|1)
        float[] uv = new float[] {
                0, 0,
                1, 0,
                1, 1,
                0, 1
        };

        Texture texture = TextureFactory.loadTexture("textures/dirtTex.PNG");
        TexturedModel model = ModelFactory.createTexturedModel(texture, vertices, indices, uv);
        Vector3f position = new Vector3f(0, 0, 0);
        Vector3f rotation = new Vector3f(0, 0, 0);
        Entity entity = new Entity(model, position, rotation, 1.0f);
        while (WINDOW.isOpen()) {
            RENDERER.prepare();
            SHADER.start();
            entity.getRotation().add(0, 0, 0.1f);
            RENDERER.render(SHADER, entity);
            SHADER.stop();
            //RENDERER.renderTriangle();
            WINDOW.update();
            frames++;
            double currentFrameTime = glfwGetTime();
            DELTA_TIME = currentFrameTime - lastSecondTime;
            if (DELTA_TIME > 1.0) {
                fps = frames;
                System.out.println("FPS: " + fps);
                frames = 0;
                lastSecondTime = currentFrameTime;
            }
        }
        SHADER.destroy();
        ModelFactory.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
