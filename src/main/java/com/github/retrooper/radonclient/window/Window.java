package com.github.retrooper.radonclient.window;

import com.github.retrooper.radonclient.util.ResourceUtil;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;
import static org.lwjgl.nanovg.NanoVGGL3.NVG_ANTIALIAS;
import static org.lwjgl.nanovg.NanoVGGL3.nvgCreate;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private long handle;
    public long vgHandle;
    public int fontHandle;
    private String title;
    private Resolution resolution;
    private final boolean vSync;

    public Window(String title, Resolution resolution, boolean vSync) {
        this.title = title;
        this.resolution = resolution;
        this.vSync = vSync;
    }

    public boolean create() {
        handle = glfwCreateWindow(resolution.getWidth(), resolution.getHeight(), title, NULL, NULL);
        if (handle != NULL) {
            // Get the thread stack and push a new frame
            try (MemoryStack stack = MemoryStack.stackPush()) {
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

                glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            } // the stack frame is popped automatically
            glfwMakeContextCurrent(handle);
            glfwSwapInterval(vSync ? 1 : 0);
            GL.createCapabilities();
            vgHandle = nvgCreate(NVG_ANTIALIAS);
            if (vgHandle == NULL) {
                throw new IllegalStateException("Failed to initialize NanoVG.");
            }
            try {
                fontHandle = nvgCreateFontMem(vgHandle, "retrooper", ResourceUtil.loadResource("fonts/SecurityMeltdown.ttf", 17000), 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public long getHandle() {
        return handle;
    }

    public String getTitle() {
        return title;
    }

    public void updateTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(handle, title);
    }

    public Resolution getResolution() {
        return resolution;
    }

    public void updateResolution(Resolution resolution) {
        this.resolution = resolution;
        glfwSetWindowSize(handle, resolution.getWidth(), resolution.getHeight());
    }
}
