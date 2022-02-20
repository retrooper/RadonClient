package com.github.retrooper.radonclient.renderer;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Renderer<T, K, L, N> {
    public static final FloatBuffer INSTANCE_BUFFER = BufferUtils.createFloatBuffer(Renderer.MAX_MODEL_COUNT * Renderer.MODEL_INSTANCE_DATA_SIZE);
    public static final int MAX_MODEL_COUNT = 100000;
    public static final int MODEL_INSTANCE_DATA_SIZE = 16;

    public void load() {

    }

    public static void prepare() {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.0f, 0.0f, 0.7f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(T texture, K shader, L model, N entity) {
    }


    public void renderTriangle() {
        glBegin(GL_TRIANGLES);
        glVertex3f(-0.5f, -0.5f, 0);
        glVertex3f(0.5f, -0.5f, 0);
        glVertex3f(0, 0.5f, 0);
        glColor4f(1, 0, 0, 1);
        glEnd();
        glFlush();
    }

    public void renderSquare() {
        glBegin(GL_TRIANGLES);
        glVertex3f(-0.5f, 0.5f, 0);
        glVertex3f(0.5f, 0.5f, 0);
        glVertex3f(-0.5f, -0.5f, 0);
        glVertex3f(-0.5f, -0.5f, 0);
        glVertex3f(0.5f, 0.5f, 0);
        glVertex3f(0.5f, -0.5f, 0);
        glColor4f(1, 0, 0, 1);
        glEnd();
        glFlush();
    }
}
