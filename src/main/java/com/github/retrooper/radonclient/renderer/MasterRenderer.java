package com.github.retrooper.radonclient.renderer;

import static org.lwjgl.opengl.GL11.*;

public class MasterRenderer {
    public void prepare() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.7f, 1.0f);
    }

    public void render() {
        glBegin(4);
        glVertex3f(-0.5f, -0.5f, 0);
        glVertex3f(0.5f, -0.5f, 0);
        glVertex3f(0, 0.5f, 0);
        glColor4f(1, 0, 0, 1);
        glEnd();
        glFlush();
    }
}
