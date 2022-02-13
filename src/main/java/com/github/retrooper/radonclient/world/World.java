package com.github.retrooper.radonclient.world;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.entity.player.Camera;
import com.github.retrooper.radonclient.model.Model;
import com.github.retrooper.radonclient.util.MathUtil;
import com.github.retrooper.radonclient.window.Window;
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import com.github.retrooper.radonclient.world.chunk.Chunk;
import com.github.retrooper.radonclient.world.chunk.ChunkHelper;
import com.github.retrooper.radonclient.world.chunk.ChunkSection;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class World {
    private final Map<Long, Chunk> chunks = new ConcurrentHashMap<>();
    private final Map<Long, Map<Model, Set<Entity>>> renderedChunks = new ConcurrentHashMap<>();

    private final String name;

    public World(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<Long, Chunk> getChunks() {
        return chunks;
    }

    public Map<Long, Map<Model, Set<Entity>>> getRenderedChunks() {
        return renderedChunks;
    }

    public Chunk getChunk(long serializedXZ) {
        return chunks.get(serializedXZ);
    }

    public void addChunk(Chunk chunk) {
        chunks.put(chunk.serialize(), chunk);
    }

    public Chunk getChunkAt(int x, int z) {
        return chunks.get(ChunkHelper.serializeChunkXZ(x >> 4, z >> 4));
    }

    public ChunkSection getChunkSectionAt(int x, int y, int z) {
        Chunk column = getChunkAt(x, z);
        if (column != null) {
            return column.getChunkSections()[y >> 4];
        }
        return null;
    }

    public Block getBlockAt(float x, float y, float z) {
        if (y < 0) {
            //TODO Support
            return null;
        }
        return getBlockAt(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z));
    }

    public Block getBlockAt(int x, int y, int z) {
        ChunkSection chunkSection = getChunkSectionAt(x, y, z);
        if (chunkSection != null) {
            return chunkSection.getBlock(x & 15, y & 15, z & 15);
        }
        return null;
    }

    public void setBlockAt(int x, int y, int z, Block block) {
        ChunkSection chunkSection = getChunkSectionAt(x, y, z);
        chunkSection.setBlock(x & 15, y & 15, z & 15, block);
    }

    public void setBlockAt(Vector3f position, Block block) {
        setBlockAt(MathUtil.floor(position.x), MathUtil.floor(position.y), MathUtil.floor(position.z), block);
    }
    //Discard the chunks that we should no longer render.
    //We still store them in our world.
    public void startChunkCleanupThread(Window window, Camera camera, int chunkRadius) {
        Thread thread = new Thread(() -> {
            long lastCleanupTime = System.currentTimeMillis();
            while (window.isOpen()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCleanupTime > 500L) {
                    List<Long> toRemove = new ArrayList<>();
                    for (long columnId : renderedChunks.keySet()) {
                        Chunk chunk = getChunk(columnId);
                        if (chunk != null) {
                            int columnDistX = Math.abs(chunk.getX() - camera.getChunkX());
                            int columnDistZ = Math.abs(chunk.getZ() - camera.getChunkZ());
                            if (columnDistX > chunkRadius || columnDistZ > chunkRadius) {
                                toRemove.add(columnId);
                                //System.out.println("Discarding " + chunk.getX() + ", " + chunk.getZ());
                            }
                        }
                    }
                    for (long columnId : toRemove) {
                        renderedChunks.remove(columnId);
                    }
                    lastCleanupTime = System.currentTimeMillis();
                }
            }
        });
        thread.start();
    }

    //Generate chunks around us. Or if they already are stored in our world, restore them.
    public void startChunkGenerationThread(Window window, Camera camera, int chunkRadius) {
        Thread thread = new Thread(() -> {
            long lastTime = System.currentTimeMillis();
            while (window.isOpen()) {
                long now = System.currentTimeMillis();
                if (now - lastTime >= 500L) {
                    int minX = camera.getChunkX() - chunkRadius;
                    int minZ = camera.getChunkZ() - chunkRadius;
                    int maxX = camera.getChunkX() + chunkRadius;
                    int maxZ = camera.getChunkZ() + chunkRadius;
                    for (int x = minX; x <= maxX; x++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            long columnId = ChunkHelper.serializeChunkXZ(x, z);
                            Chunk chunk = getChunk(columnId);
                            if (chunk == null) {
                                //Make it since it does not exist
                                chunk = new Chunk(x, z, 256);
                                int finalX = x;
                                int finalZ = z;
                                chunk.handlePerBlock(block -> {
                                    if (block.getY() == 0) {
                                        if (finalX == 0 && finalZ == 0) {
                                            block.setType(BlockTypes.DIRT);
                                        } else {
                                            block.setType(BlockTypes.GRASS);
                                        }
                                    }
                                });
                                //Cull some faces
                                //TODO Culling isn't perfect atm
                                chunk.updateBlockFaces();
                                addChunk(chunk);
                                Map<Model, Set<Entity>> renderedModels = new ConcurrentHashMap<>();
                                for (Block block : chunk.getBlocks()) {
                                    if (block.getType().equals(BlockTypes.AIR)) continue;
                                    if (!block.isOneFaceVisible()) continue;
                                    Entity entity = block.asEntity();
                                    renderedModels.computeIfAbsent(entity.getModel(), k -> new HashSet<>())
                                            .add(entity);
                                }
                                //Cache block entities
                                renderedChunks.put(columnId, renderedModels);
                            }
                            //It is stored, no need to regenerate.
                            else if (!renderedChunks.containsKey(columnId)) {
                                Map<Model, Set<Entity>> renderedModels = new ConcurrentHashMap<>();
                                for (Block block : chunk.getBlocks()) {
                                    if (block.getType().equals(BlockTypes.AIR)) continue;
                                    if (!block.isOneFaceVisible()) continue;
                                    Entity entity = block.asEntity();
                                    renderedModels.computeIfAbsent(entity.getModel(), k -> new HashSet<>())
                                            .add(entity);
                                }
                                //Cache block entities
                                renderedChunks.put(columnId, renderedModels);
                                //System.out.println("Restored chunk column at " + x + ", " + z);
                            }
                        }
                    }
                    lastTime = System.currentTimeMillis();
                }
            }
        });
        thread.start();
    }
}
