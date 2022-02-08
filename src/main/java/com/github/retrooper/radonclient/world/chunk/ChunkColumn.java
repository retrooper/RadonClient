package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.RadonClient;
import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ChunkColumn {
    private final int x;
    private final int z;
    private final Chunk[] chunks;

    public ChunkColumn(int x, int z, Chunk[] chunks) {
        this.x = x;
        this.z = z;
        this.chunks = chunks;
    }

    //maxHeight must be multiple of 256
    public ChunkColumn(int x, int z, int maxHeight) {
        this.x = x;
        this.z = z;
        this.chunks = new Chunk[maxHeight / 16];
        for (int i = 0; i < this.chunks.length; i++) {
            this.chunks[i] = new Chunk(x, i, z, 16, 16, 16);
        }
    }

    public Chunk[] getChunks() {
        return chunks;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public long serialize() {
        return ChunkHelper.serializeChunkXZ(x, z);
    }

    //X(0-15), Y(0-255), Z(0-15)
    public Block getBlock(int x, int y, int z) {
        Chunk chunk = getChunks()[y >> 4];
        return chunk.getBlock(x & 15, y & 15, z & 15);
    }

    //X(0-15), Y(0-15), Z(0-15)
    public void setBlock(int x, int y, int z, Block block) {
        Chunk chunk = getChunks()[y >> 4];
        chunk.setBlock(x & 15, y & 15, z & 15, block);
    }

    public void handlePerChunk(Consumer<Chunk> consumer) {
        for (Chunk chunk : getChunks()) {
            consumer.accept(chunk);
        }
    }

    public void handlePerBlock(Consumer<Block> consumer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int chunkIndex = 0; chunkIndex < chunks.length; chunkIndex++) {
                    Chunk chunk = chunks[chunkIndex];
                    for (int y = 0; y < 16; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        consumer.accept(block);
                    }
                }
            }
        }
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (Chunk chunk : chunks) {
                    for (int y = 0; y < 16; y++) {
                        Block block = chunk.getBlock(x, y, z);
                        if (block.getType().equals(BlockTypes.AIR))continue;
                        Entity entity = new Entity(RadonClient.DIRT_MODEL,
                                block.getPosition(),
                                new Vector3f(),
                                1.0f);
                        entities.add(entity);
                    }
                }
            }
        }
        return entities;
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (Chunk chunk : chunks) {
                    for (int y = 0; y < 16; y++) {
                        blocks.add(chunk.getBlock(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }
}
