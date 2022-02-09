package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.entity.player.Camera;
import com.github.retrooper.radonclient.entity.player.MoveDirection;
import com.github.retrooper.radonclient.input.InputUtil;
import com.github.retrooper.radonclient.model.ModelFactory;
import com.github.retrooper.radonclient.model.TexturedModel;
import com.github.retrooper.radonclient.renderer.BatchRenderer;
import com.github.retrooper.radonclient.renderer.EntityRenderer;
import com.github.retrooper.radonclient.renderer.Renderer;
import com.github.retrooper.radonclient.shader.StaticShader;
import com.github.retrooper.radonclient.texture.Texture;
import com.github.retrooper.radonclient.texture.TextureFactory;
import com.github.retrooper.radonclient.window.Resolution;
import com.github.retrooper.radonclient.window.Window;
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import com.github.retrooper.radonclient.world.chunk.ChunkColumn;
import com.github.retrooper.radonclient.world.chunk.ChunkHelper;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.retrooper.radonclient.util.MathUtil.floor;
import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(800, 600), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final BatchRenderer batchRenderer = new BatchRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;
    private final Map<Long, ChunkColumn> chunkColumns = new ConcurrentHashMap<>();
    private final Map<Long, List<Entity>> renderedColumns = new ConcurrentHashMap<>();
    public static Texture DIRT_TEXTURE;
    public static Texture GRASS_TEXTURE_ATLAS;
    public static TexturedModel DIRT_MODEL;
    public static TexturedModel GRASS_MODEL;

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

        float[] atlasUV = {
                1.01f / 3.0f, 1.01f / 3.0f,
                1.01f / 3.0f, 1.99f / 3.0f,
                1.99f /  3.0f, 1.99f / 3.0f,
                1.99f / 3.0f, 1.01f / 3.0f,

                1.01f / 3.0f, 1.01f / 3.0f,
                1.01f / 3.0f, 1.99f / 3.0f,
                1.99f /  3.0f, 1.99f / 3.0f,
                1.99f / 3.0f, 1.01f / 3.0f,

                1.01f / 3.0f, 1.01f / 3.0f,
                1.01f / 3.0f, 1.99f / 3.0f,
                1.99f /  3.0f, 1.99f / 3.0f,
                1.99f / 3.0f, 1.01f / 3.0f,

                1.01f / 3.0f, 1.01f / 3.0f,
                1.01f / 3.0f, 1.99f / 3.0f,
                1.99f /  3.0f, 1.99f / 3.0f,
                1.99f / 3.0f, 1.01f / 3.0f,

                1.0f / 3.0f, 2.01f / 3.0f,
                1.01f / 3.0f, 0.99f,
                1.99f / 3.0f, 0.99f,
                1.99f / 3.0f, 2.1f / 3.0f,

                0.01f, 1.0f / 3.0f,
                0.01f, 1.99f / 3.0f,
                0.99f / 3.0f, 1.99f / 3.0f,
                0.99f / 3.0f, 1.01f / 3.0f
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

        DIRT_TEXTURE = TextureFactory.loadTexture("textures/dirt.png");
        GRASS_TEXTURE_ATLAS = TextureFactory.loadTexture("textures/grassTextureAtlas.png");
        DIRT_MODEL = ModelFactory.createTexturedModel(DIRT_TEXTURE, vertices, indices, uv);
        GRASS_MODEL = ModelFactory.createTexturedModel(GRASS_TEXTURE_ATLAS, vertices, indices, atlasUV);

        Camera camera = new Camera(window);
        shader.start();
        shader.updateProjectionMatrix(camera.createProjectionMatrix());
        shader.stop();
        InputUtil.init(window);

        float lastFrameTime = (float) glfwGetTime();
        int frameCount = 0;
        int fps = 0;
        float lastSecondTime = lastFrameTime;
        int chunkRenderDistance = 1;
        Thread terrainCleanupThread = new Thread(() -> {
            long lastCleanupTime = System.currentTimeMillis();
            while (window.isOpen()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCleanupTime > 500L) {
                    List<Long> toRemove = new ArrayList<>();
                    for (long columnId : renderedColumns.keySet()) {
                        ChunkColumn column = chunkColumns.get(columnId);
                        if (column != null) {
                            int columnDistX = Math.abs(column.getX() - (floor(camera.getPosition().x) >> 4));
                            int columnDistZ = Math.abs(column.getZ() - (floor(camera.getPosition().z) >> 4));
                            if (columnDistX > chunkRenderDistance || columnDistZ > chunkRenderDistance) {
                                toRemove.add(columnId);
                            }
                        }
                    }
                    for (long columnId : toRemove) {
                        renderedColumns.remove(columnId);
                    }
                    lastCleanupTime = System.currentTimeMillis();
                }
            }
        });
        Thread generateTerrainThread = new Thread(() -> {
            long lastTime = System.currentTimeMillis();
            while (window.isOpen()) {
                long now = System.currentTimeMillis();
                if (now - lastTime >= 500L) {
                    int minX = (floor(camera.getPosition().x) >> 4) - chunkRenderDistance;
                    int minZ = (floor(camera.getPosition().z) >> 4) - chunkRenderDistance;
                    int maxX = (floor(camera.getPosition().x) >> 4) + chunkRenderDistance;
                    int maxZ = (floor(camera.getPosition().z) >> 4) + chunkRenderDistance;
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            long columnId = ChunkHelper.serializeChunkXZ(x, z);
                            ChunkColumn chunkColumn = chunkColumns.get(columnId);
                            if (chunkColumn == null || !renderedColumns.containsKey(columnId)) {
                                //Make it since it does not exist
                                chunkColumn = new ChunkColumn(x, z, 256);
                                List<Entity> entities = new ArrayList<>();
                                for (Block block : chunkColumn.getBlocks()) {
                                    if (block.getPosition().y == 0) {
                                        TexturedModel model = (x == 0 && z == 0) ? DIRT_MODEL : GRASS_MODEL;
                                        entities.add(
                                                new Entity(
                                                        model,
                                                        block.getPosition(),
                                                        new Vector3f(0, 0, 0),
                                                        1.0f));
                                        block.setType(BlockTypes.DIRT);
                                    } else {
                                        block.setType(BlockTypes.AIR);
                                    }
                                }
                                chunkColumns.put(columnId, chunkColumn);
                                //Cache block entities
                                renderedColumns.put(columnId, entities);
                                System.out.println("Created chunk column at " + x + ", " + z);
                            }
                        }
                    }
                    lastTime = System.currentTimeMillis();
                }
            }
        });
        generateTerrainThread.start();
        terrainCleanupThread.start();
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
            Renderer.prepare();
            shader.start();
            shader.updateViewMatrix(camera.createViewMatrix());
            //List<Entity> entities = new ArrayList<>();
            for (List<Entity> columnEntities : renderedColumns.values()) {
                for (Entity e : columnEntities) {
                    renderer.render(shader, e);
                }
            }
            //batchRenderer.render(shader, entities);
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

    public BatchRenderer getBatchRenderer() {
        return batchRenderer;
    }

    public StaticShader getShader() {
        return shader;
    }

    public static RadonClient getInstance() {
        return INSTANCE;
    }

}
