package com.github.retrooper.radonclient.input;

import com.github.retrooper.radonclient.window.Window;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;

import static org.lwjgl.glfw.GLFW.*;

public class InputUtil {
    private static long WINDOW_HANDLE;
    private static double MOUSE_X_POS, MOUSE_Y_POS, SCROLL_X, SCROLL_Y, WINDOW_WIDTH, WINDOW_HEIGHT;

    public static boolean isKeyDown(int key) {
        return glfwGetKey(WINDOW_HANDLE, key) == GLFW_PRESS;
    }

    public static boolean isMouseButtonDown(int button) {
        return glfwGetMouseButton(WINDOW_HANDLE, button) == GLFW_PRESS;
    }

    public static double getMouseXPos() {
        return MOUSE_X_POS;
    }

    public static double getMouseYPos() {
        return MOUSE_Y_POS;
    }

    public static double getScrollX() {
        return SCROLL_X;
    }

    public static double getScrollY() {
        return SCROLL_Y;
    }

    public static double getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static double getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    public static void init(Window window) {
        WINDOW_HANDLE = window.getHandle();
        GLFWCursorPosCallback mouseMoveCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                MOUSE_X_POS = xPos;
                MOUSE_Y_POS = yPos;
            }
        };

        GLFWScrollCallback scrollWheelCallback = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double deltaX, double deltaY) {
                SCROLL_X += deltaX;
                SCROLL_Y += deltaY;
            }
        };

        GLFWWindowSizeCallback windowResizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WINDOW_WIDTH = width;
                WINDOW_HEIGHT = height;
            }
        };

        glfwSetCursorPosCallback(WINDOW_HANDLE, mouseMoveCallback);
        glfwSetScrollCallback(WINDOW_HANDLE, scrollWheelCallback);
        glfwSetWindowSizeCallback(WINDOW_HANDLE, windowResizeCallback);
    }
}
