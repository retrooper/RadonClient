package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.entity.player.Camera;
import com.github.retrooper.radonclient.entity.player.MoveDirection;
import com.github.retrooper.radonclient.input.InputUtil;
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
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(1280, 720), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        window.create();
        window.show();
        shader.init();
        float[] vertices = {
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f
        };

        float[] uv = {

                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23, 21, 22};



        Texture texture = TextureFactory.loadTexture("textures/dirtTex.PNG");
        TexturedModel model = ModelFactory.createTexturedModel(texture, vertices, indices, uv);
        Vector3f position = new Vector3f(0, 0, -1);
        Vector3f rotation = new Vector3f(0, 0, 0);
        Entity entity = new Entity(model, position, rotation, 1.0f);
        Camera camera = new Camera(window);
        shader.start();
        shader.updateProjectionMatrix(camera.createProjectionMatrix());
        shader.stop();
        InputUtil.init(window);
        float lastFrameTime = (float) glfwGetTime();
        int frameCount = 0;
        int fps = 0;
        float lastSecondTime = lastFrameTime;
        while (window.isOpen()) {
            if (InputUtil.isKeyDown(GLFW_KEY_W)) {
                camera.move(MoveDirection.FORWARD, 2f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_S)) {
                camera.move(MoveDirection.BACKWARD, 2f * deltaTime);
            }

            if (InputUtil.isKeyDown(GLFW_KEY_A)) {
                camera.move(MoveDirection.LEFT, 2f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_D)) {
                camera.move(MoveDirection.RIGHT, 2f * deltaTime);
            }

            if (InputUtil.isKeyDown(GLFW_KEY_SPACE)) {
                camera.move(MoveDirection.UP, 2f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_LEFT_ALT)) {
                camera.move(MoveDirection.DOWN, 2f * deltaTime);
            }

            double mouseX = InputUtil.getMouseXPos();
            double mouseY = InputUtil.getMouseYPos();
            camera.setMousePos(mouseX, mouseY);
            camera.updateRotation();
            renderer.prepare();
            shader.start();
            //entity.getRotation().add(0, 0.1f, 0.1f);
            shader.updateViewMatrix(camera.createViewMatrix());
            renderer.render(shader, entity);
            shader.stop();
            window.update();
            float currentTime = (float) glfwGetTime();

            //Calculate delta-time
            deltaTime = currentTime - lastFrameTime;
            lastFrameTime = currentTime;
            //Calculate FPS
            frameCount++;
            if (currentTime - lastSecondTime >= 1.0) {
                fps = frameCount;
                System.out.println("FPS: " + fps);
                frameCount = 0;
                lastSecondTime = currentTime;
            }
        }
        shader.destroy();
        window.destroy();
        ModelFactory.destroy();
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public Window getWindow() {
        return window;
    }

    public EntityRenderer getRenderer() {
        return renderer;
    }

    public StaticShader getShader() {
        return shader;
    }

    public static RadonClient getInstance() {
        return INSTANCE;
    }

}
