package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.model.Model;
import com.github.retrooper.radonclient.model.ModelFactory;
import com.github.retrooper.radonclient.renderer.EntityRenderer;
import com.github.retrooper.radonclient.window.Resolution;
import com.github.retrooper.radonclient.window.Window;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final Window WINDOW = new Window("RadonClient", new Resolution(1280, 720), false);
    public static final EntityRenderer RENDERER = new EntityRenderer();

    public static void main(String[] args) {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        WINDOW.create();
        WINDOW.show();
        int frames = 0;
        int fps;
        double lastSecondTime = 0.0;
        float[] vertices = new float[]{
                -0.5f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        int[] indices = new int[] {
                0, 1, 2, 2, 1, 3
        };
        Model model = ModelFactory.create(vertices, indices);
        while (WINDOW.isOpen()) {
            RENDERER.prepare();
            RENDERER.render(model);
            //RENDERER.renderTriangle();
            WINDOW.update();
            frames++;
            double currentFrameTime = glfwGetTime();
            double deltaTime = currentFrameTime - lastSecondTime;
            if (deltaTime > 1.0) {
                fps = frames;
                System.out.println("FPS: " + fps);
                frames = 0;
                lastSecondTime = currentFrameTime;
            }
        }
        ModelFactory.cleanup();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
