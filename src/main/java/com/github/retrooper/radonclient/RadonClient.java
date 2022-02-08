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
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import com.github.retrooper.radonclient.world.chunk.Chunk;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.retrooper.radonclient.util.MathUtil.floor;
import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(1280, 720), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;
    private final List<Entity> entities = new ArrayList<>();
    private final Map<Vector2i, Chunk> chunks = new HashMap<>();

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


        Texture texture = TextureFactory.loadTexture("textures/dirt.png");
        TexturedModel model = ModelFactory.createTexturedModel(texture, vertices, indices, uv);
        Vector3f rotation = new Vector3f(0, 0, 0);
        Camera camera = new Camera(window);
        shader.start();
        shader.updateProjectionMatrix(camera.createProjectionMatrix());
        shader.stop();
        InputUtil.init(window);
        Chunk c = new Chunk(0, 0, 16, 16, 16);
        for (Block block : c.getBlocks()) {
            if (block.getPosition().y == 0) {
                block.setType(BlockTypes.DIRT);
            }
        }
        chunks.put(new Vector2i(0, 0), c);

        float lastFrameTime = (float) glfwGetTime();
        int frameCount = 0;
        int fps = 0;
        float lastSecondTime = lastFrameTime;
        while (window.isOpen()) {
            System.out.println("x: " + camera.getPosition().x + ", z: " + camera.getPosition().z + ", y: " + camera.getPosition().y);
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
            shader.updateViewMatrix(camera.createViewMatrix());

            for (Chunk chunk : chunks.values()) {
                chunk.handlePerBlock(new Consumer<Block>() {
                    @Override
                    public void accept(Block block) {
                        Block clone = getBlockAt(block.getPosition());
                        System.out.println("pos: x: " + clone.getPosition().x + ", z: " + clone.getPosition().z + ", origin: x: " + block.getPosition().x + ", z: " + block.getPosition().z);

                        Entity entity = new Entity(model,
                                block.getPosition(),
                                new Vector3f(),
                                1.0f);
                        renderer.render(shader, entity);
                    }
                });
            }

            for (Entity entity : entities) {
                //entity.getRotation().add(0, 0.1f * deltaTime, 0.1f * deltaTime);
                renderer.render(shader, entity);
            }

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

    public Vector2i getChunkXZ(Vector3i blockPosition) {
        //Either floor(x/16), floor(z/16) or x>>4, z>>4
        return new Vector2i(blockPosition.x >> 4, blockPosition.z >> 4);
    }

    public Chunk getChunk(Vector3i blockPosition) {
        Vector2i chunkCoordinates = getChunkXZ(blockPosition);
        return chunks.get(chunkCoordinates);
    }

    public Block getBlockAt(Vector3i blockPosition) {
        Chunk chunk = getChunk(blockPosition);
        if (chunk == null) {
            return null;
        }
        int secX = blockPosition.x & 15;
        int secZ = blockPosition.z & 15;
        return chunk.getBlock(secX, blockPosition.y, secZ);
    }

    public static RadonClient getInstance() {
        return INSTANCE;
    }

}
