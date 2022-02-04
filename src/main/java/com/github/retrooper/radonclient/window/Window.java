package com.github.retrooper.radonclient.window;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    private String title;
    private Resolution resolution;
    private boolean vSync;

    public Window(String title, Resolution resolution, boolean vSync) {
        this.title = title;
        this.resolution = resolution;
        this.vSync = vSync;
    }

    public boolean create() {
        handle = glfwCreateWindow(resolution.getWidth(), resolution.getHeight(), title, NULL, NULL);
        if (handle != NULL) {
            // Get the thread stack and push a new frame
            try ( MemoryStack stack = MemoryStack.stackPush() ) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(handle, pWidth, pHeight);

                // Get the resolution of the primary monitor
                GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

                // Center the window
                glfwSetWindowPos(
                        handle,
                        (vidmode.width() - pWidth.get(0)) / 2,
                        (vidmode.height() - pHeight.get(0)) / 2
                );
            } // the stack frame is popped automatically
            glfwMakeContextCurrent(handle);
            glfwSwapInterval(vSync ? 1 : 0);
            GL.createCapabilities();
            return true;
        }
        return false;
    }

    public void show() {
        glfwShowWindow(handle);
    }

    public void hide() {
        glfwHideWindow(handle);
    }

    public boolean isOpen() {
        return !glfwWindowShouldClose(handle);
    }

    public void update() {
        glfwSwapBuffers(handle);
        glfwPollEvents();
    }

    public void destroy() {
        glfwFreeCallbacks(handle);
        glfwDestroyWindow(handle);

    }
}
