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
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.retrooper.radonclient.util.MathUtil.floor;
import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(800, 600), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;
    private final Map<Long, Chunk> chunks = new ConcurrentHashMap<>();

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
        Camera camera = new Camera(window);
        shader.start();
        shader.updateProjectionMatrix(camera.createProjectionMatrix());
        shader.stop();
        InputUtil.init(window);

        float lastFrameTime = (float) glfwGetTime();
        int frameCount = 0;
        int fps = 0;
        float lastSecondTime = lastFrameTime;
        Thread generateTerrainThread = new Thread(() -> {
            double lastTime = glfwGetTime();
            while (window.isOpen()) {
                double now = glfwGetTime();
                if (now - lastTime >= 0.5) {
                    int minX = (floor(camera.getPosition().x) >> 4) - 1;
                    int minZ = (floor(camera.getPosition().z) >> 4) - 1;
                    int maxX = (floor(camera.getPosition().x) >> 4) + 1;
                    int maxZ = (floor(camera.getPosition().z) >> 4) + 1;
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            Chunk chunk = chunks.get(Chunk.serialize(x, z));
                            if (chunk == null) {
                                //Make it since it does not exist
                                chunk = new Chunk(x, z, 16, 16, 255);
                                for (Block block : chunk.getBlocks()) {
                                    if (block.getPosition().y == 0) {
                                        block.setType(BlockTypes.DIRT);
                                    } else {
                                        block.setType(BlockTypes.AIR);
                                    }
                                }
                                chunks.put(chunk.serialize(), chunk);
                                System.out.println("Created chunk at " + x + ", " + z);
                            }
                        }
                    }
                    lastTime = glfwGetTime();
                }
            }
        });
        generateTerrainThread.start();
        while (window.isOpen()) {
            if (InputUtil.isKeyDown(GLFW_KEY_W)) {
                camera.move(MoveDirection.FORWARD, 10f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_S)) {
                camera.move(MoveDirection.BACKWARD, 10f * deltaTime);
            }

            if (InputUtil.isKeyDown(GLFW_KEY_A)) {
                camera.move(MoveDirection.LEFT, 10f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_D)) {
                camera.move(MoveDirection.RIGHT, 10f * deltaTime);
            }

            if (InputUtil.isKeyDown(GLFW_KEY_SPACE)) {
                camera.move(MoveDirection.UP, 5f * deltaTime);
            } else if (InputUtil.isKeyDown(GLFW_KEY_LEFT_ALT)) {
                camera.move(MoveDirection.DOWN, 5f * deltaTime);
            }

            double mouseX = InputUtil.getMouseXPos();
            double mouseY = InputUtil.getMouseYPos();
            camera.setMousePos(mouseX, mouseY);
            camera.updateRotation();
            renderer.prepare();
            shader.start();
            shader.updateViewMatrix(camera.createViewMatrix());
            AtomicInteger count = new AtomicInteger();
            for (Chunk chunk : chunks.values()) {
                chunk.handlePerBlock(block -> {
                    if (block.getType().equals(BlockTypes.AIR)) return;
                    Entity entity = new Entity(model,
                            block.getPosition(),
                            new Vector3f(),
                            1.0f);
                    renderer.render(shader, entity);
                    count.getAndIncrement();
                });
            }
            if (count.get() != 0) {
                //System.out.println("Rendered " + count.get() + " dirt blocks!");
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

    public Chunk getChunk(Vector3i blockPosition) {
        return chunks.get(Chunk.serialize(blockPosition.x >> 4, blockPosition.z >> 4));
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

    public void setBlockAt(Vector3i blockPosition, Block block) {
        Chunk chunk = getChunk(blockPosition);
        if (chunk == null) {
            return;
        }
        int secX = blockPosition.x & 15;
        int secZ = blockPosition.z & 15;
        chunk.setBlock(secX, blockPosition.y, secZ, block);
    }

    public static RadonClient getInstance() {
        return INSTANCE;
    }

}
