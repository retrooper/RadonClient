package com.github.retrooper.radonclient.input;

import com.github.retrooper.radonclient.window.Window;
import org.joml.Vector2d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class InputUtil {
    private static final byte[] KEYS = new byte[348];
    private static final byte[] MOUSE_BUTTONS = new byte[7];
    private static double MOUSE_X_POS, MOUSE_Y_POS, SCROLL_X, SCROLL_Y, WINDOW_WIDTH, WINDOW_HEIGHT;

    private static GLFWKeyCallback KEY_CALLBACK;
    private static GLFWMouseButtonCallback MOUSE_CALLBACK;
    private static GLFWCursorPosCallback MOUSE_MOVE_CALLBACK;
    private static GLFWScrollCallback SCROLL_WHEEL_CALLBACK;
    private static GLFWWindowSizeCallback WINDOW_RESIZE_CALLBACK;

    public static boolean isKeyDown(int key) {
        return KEYS[key] == 1;
    }

    public static boolean isMouseButtonDown(int button) {
        return MOUSE_BUTTONS[button] == 1;
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
        KEY_CALLBACK = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                KEYS[key] = (action != GLFW_RELEASE) ? (byte) 1 : (byte) 0;
            }
        };

        MOUSE_CALLBACK = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                MOUSE_BUTTONS[button] = (action != GLFW_RELEASE) ? (byte) 1 : (byte) 0;
            }
        };

        MOUSE_MOVE_CALLBACK = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                MOUSE_X_POS = xPos;
                MOUSE_Y_POS = yPos;
            }
        };

        SCROLL_WHEEL_CALLBACK = new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double deltaX, double deltaY) {
                SCROLL_X += deltaX;
                SCROLL_Y += deltaY;
            }
        };

        WINDOW_RESIZE_CALLBACK = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                WINDOW_WIDTH = width;
                WINDOW_HEIGHT = height;
            }
        };

        long handle = window.getHandle();
        glfwSetKeyCallback(handle, KEY_CALLBACK);
        glfwSetMouseButtonCallback(handle, MOUSE_CALLBACK);
        glfwSetCursorPosCallback(handle, MOUSE_MOVE_CALLBACK);
        glfwSetScrollCallback(handle, SCROLL_WHEEL_CALLBACK);
        glfwSetWindowSizeCallback(handle, WINDOW_RESIZE_CALLBACK);
    }
}
