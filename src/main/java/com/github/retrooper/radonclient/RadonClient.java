package com.github.retrooper.radonclient;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glVertex3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glFlush;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RadonClient {
    public static void render() {
        glBegin(4);
        glVertex3f(-0.5f, -0.5f, 0);
        glVertex3f(0.5f, -0.5f, 0);
        glVertex3f(0, 0.5f, 0);
        glColor4f(1, 0, 0, 1);
        glEnd();
        glFlush();
    }

    public static void main(String[] args) {
        System.out.println("Hello " + Version.getVersion() + "!");
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        long windowHandle = glfwCreateWindow(1280, 720, "RadonClient", NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        glfwMakeContextCurrent(windowHandle);
        boolean vSync = false;
        glfwSwapInterval(vSync ? 1 : 0);
        glfwShowWindow(windowHandle);

        GL.createCapabilities();
        glClearColor(0.5f, 0.5f,
                0.5f, 1.0f);

        int frames = 0;
        int fps = 0;
        double lastSecondTime = 0.0;
        while (!glfwWindowShouldClose(windowHandle)) {
            glClear(16640);
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            render();
            glfwSwapBuffers(windowHandle);
            glfwPollEvents();
            frames++;
            double currentFrameTime = glfwGetTime();
            double deltaTime  = currentFrameTime - lastSecondTime;
            if (deltaTime > 1.0) {
                fps = frames;
                System.out.println("FPS: " + fps);
                frames = 0;
                lastSecondTime = currentFrameTime;
            }
        }
        glfwTerminate();
    }
}
