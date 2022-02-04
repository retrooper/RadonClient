package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.renderer.MasterRenderer;
import com.github.retrooper.radonclient.window.Resolution;
import com.github.retrooper.radonclient.window.Window;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final Window WINDOW = new Window("RadonClient", new Resolution(1280, 720), false);
    public static final MasterRenderer RENDERER = new MasterRenderer();

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
        while (WINDOW.isOpen()) {
            RENDERER.prepare();
            RENDERER.render();
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
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }
}
