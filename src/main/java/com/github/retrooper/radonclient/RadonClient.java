package com.github.retrooper.radonclient;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.entity.player.Camera;
import com.github.retrooper.radonclient.entity.player.MoveDirection;
import com.github.retrooper.radonclient.input.InputUtil;
import com.github.retrooper.radonclient.model.Model;
import com.github.retrooper.radonclient.model.ModelFactory;
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
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.retrooper.radonclient.util.MathUtil.floor;
import static org.lwjgl.glfw.GLFW.*;

public class RadonClient {
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(800, 600), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;
    private final Map<Long, List<Block>> renderedColumns = new ConcurrentHashMap<>();
    public static Texture TEXTURES;
    public static Model DIRT_MODEL;
    public static Model GRASS_MODEL;

    public void run() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        window.create();
        window.show();
        shader.init();
        float[] vertices = {
                -0.5f, 0.5f, -0.5f,//0
                -0.5f, -0.5f, -0.5f,//1
                0.5f, -0.5f, -0.5f,//2
                0.5f, 0.5f, -0.5f,//3

                -0.5f, 0.5f, 0.5f,//4
                -0.5f, -0.5f, 0.5f,//5
                0.5f, -0.5f, 0.5f,//6
                0.5f, 0.5f, 0.5f,//7

                0.5f, 0.5f, -0.5f,//8
                0.5f, -0.5f, -0.5f,//9
                0.5f, -0.5f, 0.5f,//10
                0.5f, 0.5f, 0.5f,//11

                -0.5f, 0.5f, -0.5f,//12
                -0.5f, -0.5f, -0.5f,//13
                -0.5f, -0.5f, 0.5f,//14
                -0.5f, 0.5f, 0.5f,//15

                -0.5f, 0.5f, 0.5f,//16
                -0.5f, 0.5f, -0.5f, //17 - 16,17,19 - on top left triangle
                0.5f, 0.5f, -0.5f, //18 - 19, 17, 18 - on top right triangle
                0.5f, 0.5f, 0.5f, //19

                -0.5f, -0.5f, 0.5f,//20
                -0.5f, -0.5f, -0.5f,//21
                0.5f, -0.5f, -0.5f,//22
                0.5f, -0.5f, 0.5f//23
        };

        float[] uv = {
                0, 0,//0
                0, 1,//1
                1, 1,//2
                1, 0,//3
                0, 0,//4
                0, 1,//5
                1, 1,//6
                1, 0,//7
                0, 0,//8
                0, 1,//9
                1, 1,//10
                1, 0,//11
                0, 0,//12
                0, 1,//13
                1, 1,//14
                1, 0,//15
                0, 0,//16
                0, 1,//17
                1, 1,//18
                1, 0,//19
                0, 0,//20
                0, 1,//21
                1, 1,//22
                1, 0//23
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
                16, 17, 19,//top left triangle
                19, 17, 18,//top right triangle
                20, 21, 23,
                23, 21, 22
        };

        TEXTURES = TextureFactory.loadTextureArray(128, 128, "dirt.png", "grass.png");
        //Second axis is redundant here
        //We need to know it for each vertex
        float[] plainDirtTextureIndices = new float[24];
        float[] plainGrassTextureIndices = new float[24];
        plainGrassTextureIndices[17] = 1.0f;
        plainGrassTextureIndices[18] = 1.0f;
        DIRT_MODEL = ModelFactory.createTexturedModel(plainDirtTextureIndices, vertices, indices, uv);
        GRASS_MODEL = ModelFactory.createTexturedModel(plainGrassTextureIndices, vertices, indices, uv);

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
                        ChunkColumn column = ChunkHelper.getChunkColumns().get(columnId);
                        if (column != null) {
                            int columnDistX = Math.abs(column.getX() - (floor(camera.getPosition().x) >> 4));
                            int columnDistZ = Math.abs(column.getZ() - (floor(camera.getPosition().z) >> 4));
                            if (columnDistX > chunkRenderDistance || columnDistZ > chunkRenderDistance) {
                                toRemove.add(columnId);
                                //System.out.println("Removing " + column.getX() + ", " + column.getZ());
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
                            ChunkColumn chunkColumn = ChunkHelper.getChunkColumns().get(columnId);
                            if (chunkColumn == null) {
                                //Make it since it does not exist
                                chunkColumn = new ChunkColumn(x, z, 256);
                                List<Block> entities = new ArrayList<>();
                                for (Block block : chunkColumn.getBlocks()) {
                                    if (block.getPosition().y == 0) {
                                        //if (x == 0 && z == 0) {
                                          //  block.setType(BlockTypes.DIRT);
                                        //} else {
                                            block.setType(BlockTypes.GRASS);
                                        //}
                                    } else {
                                        block.setType(BlockTypes.AIR);
                                    }
                                    entities.add(block);
                                }
                                ChunkHelper.getChunkColumns().put(columnId, chunkColumn);
                                //Cache block entities
                                renderedColumns.put(columnId, entities);
                                System.out.println("Created chunk column at " + x + ", " + z);
                            }
                            //It is stored, no need to regenerate.
                            else if (!renderedColumns.containsKey(columnId)) {
                                List<Block> entities = new ArrayList<>();
                                for (Block block : chunkColumn.getBlocks()) {
                                    if (!block.getType().equals(BlockTypes.AIR)) {
                                        entities.add(block);
                                    }
                                }
                                renderedColumns.put(columnId, entities);
                                //System.out.println("Restored chunk column at " + x + ", " + z);
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

            if (InputUtil.isMouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                //System.out.println("Change 1 0 0 to grass!");
                Vector3f targetLocation = new Vector3f(camera.getPosition()).add(new Vector3f(camera.getFrontDirection()).mul(2.0f));
                if (targetLocation.y < 0) {
                    System.out.println("Y too low!");
                } else {
                    Block block = ChunkHelper.getBlockAt(targetLocation);
                    System.out.println("Currently there: " + block.getType().name());
                    block.setType(BlockTypes.AIR);
                }
            } else if (InputUtil.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
                Vector3f targetLocation = new Vector3f(camera.getPosition()).add(new Vector3f(camera.getFrontDirection()).mul(2.0f));
                if (targetLocation.y < 0) {
                    System.out.println("Y too low!");
                } else {
                    Block block = ChunkHelper.getBlockAt(targetLocation);
                    System.out.println("Currently there: " + block.getType().name());
                    block.setType(BlockTypes.GRASS);
                }
            } else if (InputUtil.isKeyDown(GLFW_KEY_P)) {
                Vector3f targetLocation = new Vector3f(camera.getPosition()).add(new Vector3f(camera.getFrontDirection()).mul(2.0f));
                camera.setPosition(targetLocation);
            }

            double mouseX = InputUtil.getMouseXPos();
            double mouseY = InputUtil.getMouseYPos();
            camera.setMousePos(mouseX, mouseY);
            camera.updateRotation();
            Renderer.prepare();
            shader.start();
            shader.updateViewMatrix(camera.createViewMatrix());
            for (List<Block> columnEntities : renderedColumns.values()) {
                for (Block e : columnEntities) {
                    if (e.getType().equals(BlockTypes.AIR)) continue;
                    Model model = e.getType().equals(BlockTypes.GRASS) ? GRASS_MODEL : DIRT_MODEL;
                    Entity entity = new Entity(model, e.getPosition(), new Vector3f(0, 0, 0), 1.0f);
                    renderer.render(TEXTURES, shader, entity);
                }
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
                //System.out.println("Cam pos: " + camera.getPosition().x + ", " + camera.getPosition().y + ", " + camera.getPosition().z);
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
