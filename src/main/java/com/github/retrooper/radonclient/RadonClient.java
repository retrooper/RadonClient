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
import com.github.retrooper.radonclient.texture.TextureArray;
import com.github.retrooper.radonclient.texture.TextureFactory;
import com.github.retrooper.radonclient.util.MathUtil;
import com.github.retrooper.radonclient.window.Resolution;
import com.github.retrooper.radonclient.window.Window;
import com.github.retrooper.radonclient.world.World;
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockType;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import com.github.retrooper.radonclient.world.chunk.Chunk;
import com.github.retrooper.radonclient.world.chunk.ChunkHelper;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.retrooper.radonclient.util.MathUtil.floor;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVG.*;

public class RadonClient {
    public static final RadonClient INSTANCE = new RadonClient();
    private final Window window = new Window("RadonClient", new Resolution(800, 600), false);
    private final EntityRenderer renderer = new EntityRenderer();
    private final StaticShader shader = new StaticShader();
    private float deltaTime = 0.0f;
    private final World world = new World("main");
    public static TextureArray TEXTURES;
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

        TEXTURES = TextureFactory.loadTextures(128, 128, "bottom_grass.png", "top_grass.png", "side_grass.png");
        //Second axis is redundant here
        //We need to know it for each vertex
        //Default is bottom_grass, fitting for dirt
        int[] plainDirtTextureIndices = new int[24];

        int[] plainGrassTextureIndices = new int[24];
        //Set top face to index 1
        plainGrassTextureIndices[17] = 1;
        plainGrassTextureIndices[18] = 1;
        plainGrassTextureIndices[19] = 1;

        //Set bottom face to index 0
        plainDirtTextureIndices[22] = 0;
        plainDirtTextureIndices[23] = 0;

        for (int i = 0; i < plainGrassTextureIndices.length; i++) {
            if (i != 17 && i != 18 && i != 19 && i != 22 && i != 23) {
                plainGrassTextureIndices[i] = 2;
            }
        }

        DIRT_MODEL = ModelFactory.createTexturedModel(plainDirtTextureIndices, vertices, indices, uv);
        GRASS_MODEL = ModelFactory.createTexturedModel(plainGrassTextureIndices, vertices, indices, uv);

        Camera camera = new Camera(window);
        renderer.load();
        shader.start();
        shader.updateProjectionMatrix(camera.createProjectionMatrix());
        shader.stop();
        InputUtil.init(window);

        float lastFrameTime = (float) glfwGetTime();
        int frameCount = 0;
        int fps;
        float lastSecondTime = lastFrameTime;
        world.startChunkGenerationThread(window, camera, 2);
        world.startChunkCleanupThread(window, camera, 2);
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
                System.out.println("Cam pos: " + camera.getPosition().x + ", " + camera.getPosition().y + ", " + camera.getPosition().z);
                int blockReach = 3;
                Vector3f headPos = new Vector3f(camera.getPosition()).add(0, 1, 0);
                Vector3f endLocation = new Vector3f(headPos).add(new Vector3f(camera.getFrontDirection()).mul(blockReach));
                Vector3f currentLocation = new Vector3f(headPos);
                float dist = currentLocation.distance(endLocation);
                int blocksAhead = MathUtil.floor(dist);
                if (blocksAhead > 3) {
                    blocksAhead = blockReach;
                }
                Block targetBlock = null;
                for (int i = -1; i < blocksAhead; i++) {
                    currentLocation.add(new Vector3f(camera.getFrontDirection()).mul(1.0f));
                    Block block = world.getBlockAt(currentLocation.x, currentLocation.y, currentLocation.z);
                    if (block != null && !block.getType().equals(BlockTypes.AIR)) {
                        targetBlock = block;
                        break;
                    }
                }
                if (targetBlock != null && targetBlock.isOneFaceVisible()) {
                    //Its already visible, no need to add to the rendered entities list.
                    long chunkId = ChunkHelper.serializeChunkXZ(camera.getBlockPosX() >> 4, camera.getBlockPosZ() >> 4);
                    Map<Model, Set<Entity>> renderedModels = world.getRenderedChunks().get(chunkId);
                    if (renderedModels != null) {
                        Set<Entity> entities = renderedModels.get(targetBlock.asModel());
                        if (entities != null) {
                            Block finalTargetBlock = targetBlock;
                            entities.removeIf(entity -> entity.equals(finalTargetBlock.asEntity()));
                            System.out.println("Destroyed: " + targetBlock.getType().name());
                            targetBlock.setType(BlockTypes.AIR);
                        }
                    }

                }
                //TODO Add to rendered column if not visible
            } else if (InputUtil.isMouseButtonDown(GLFW_MOUSE_BUTTON_RIGHT)) {
                BlockType placedBlockType = BlockTypes.GRASS;
                Vector3f targetLocation = new Vector3f(camera.getPosition()).add(new Vector3f(camera.getFrontDirection()).mul(2.0f));
                if (targetLocation.y < 0) {
                    System.out.println("Y too low!");
                } else {
                    Block block = world.getBlockAt(targetLocation.x, targetLocation.y, targetLocation.z);
                    if (block.getType().equals(BlockTypes.AIR)) {
                        long chunkId = ChunkHelper.serializeChunkXZ(camera.getChunkX(), camera.getChunkZ());
                        Map<Model, Set<Entity>> renderedModels = world.getRenderedChunks().get(chunkId);
                        Set<Entity> entities;
                        if (renderedModels != null) {
                            block.setType(placedBlockType);
                            entities = renderedModels.get(block.asModel());
                            //Lets add ourselves to the set of blocks of our own type.
                            if (entities != null) {
                                entities.add(block.asEntity());
                            } else {
                                //No other blocks of our block type in this chunk. We are the first!
                                entities = new HashSet<>();
                                entities.add(block.asEntity());
                                renderedModels.put(block.asModel(), entities);
                            }
                        } else {
                            //No other blocks in this chunk.
                            block.setType(placedBlockType);
                            entities = new HashSet<>();
                            entities.add(block.asEntity());
                            renderedModels = new HashMap<>();
                            renderedModels.put(block.asModel(), entities);
                            world.getRenderedChunks().put(chunkId, renderedModels);
                        }
                        block.setAllFacesVisible();
                        //ChunkHelper.setBlockAt(block.getX(), block.getY(), block.getZ(), block);
                        System.out.println("Placed: " + block.getType().name());
                    }
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
            for (Map<Model, Set<Entity>> map : world.getRenderedChunks().values()) {
                for (Model model : map.keySet()) {
                    Set<Entity> entities = map.get(model);
                    if (entities.isEmpty()) continue;
                    renderer.render(TEXTURES, shader, model, entities);
                }
            }
            shader.stop();

            //Render cursor
            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetWindowSize(window.getHandle(), width, height);
            int[] frameBufferWidth = new int[1];
            int[] frameBufferHeight = new int[1];
            glfwGetFramebufferSize(window.getHandle(), frameBufferWidth, frameBufferHeight);
            float pxRatio = (float) frameBufferWidth[0] / (float) width[0];

            //glViewport(0, 0, width[0], height[0]);
            nvgBeginFrame(window.vgHandle, window.getResolution().getWidth(), window.getResolution().getHeight(), pxRatio);
            nvgBeginPath(window.vgHandle);
            nvgFontSize(window.vgHandle, 50.0f);
            nvgFontFace(window.vgHandle, "retrooper");
            nvgTextAlign(window.vgHandle, NVG_ALIGN_CENTER | NVG_ALIGN_MIDDLE);
            //nvgText(window.vgHandle, width[0] / 2, height[0] / 2, ".");
            nvgFill(window.vgHandle);
            nvgEndFrame(window.vgHandle);
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
