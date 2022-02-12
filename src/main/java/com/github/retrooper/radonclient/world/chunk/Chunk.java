package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.RadonClient;
import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Chunk {
    private final int x;
    private final int z;
    private final ChunkSection[] chunkSections;

    public Chunk(int x, int z, ChunkSection[] chunkSections) {
        this.x = x;
        this.z = z;
        this.chunkSections = chunkSections;
    }

    //maxHeight must be multiple of 256
    public Chunk(int x, int z, int maxHeight) {
        this.x = x;
        this.z = z;
        this.chunkSections = new ChunkSection[maxHeight / 16];
        for (int i = 0; i < this.chunkSections.length; i++) {
            this.chunkSections[i] = new ChunkSection(x, i, z, 16, 16, 16);
        }
    }

    public ChunkSection[] getChunks() {
        return chunkSections;
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
        ChunkSection chunkSection = getChunks()[y >> 4];
        return chunkSection.getBlock(x & 15, y & 15, z & 15);
    }

    //X(0-15), Y(0-15), Z(0-15)
    public void setBlock(int x, int y, int z, Block block) {
        ChunkSection chunkSection = getChunks()[y >> 4];
        chunkSection.setBlock(x & 15, y & 15, z & 15, block);
    }

    public void handlePerChunk(Consumer<ChunkSection> consumer) {
        for (ChunkSection chunkSection : getChunks()) {
            consumer.accept(chunkSection);
        }
    }

    public void handlePerBlock(Consumer<Block> consumer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int chunkIndex = 0; chunkIndex < chunkSections.length; chunkIndex++) {
                    ChunkSection chunkSection = chunkSections[chunkIndex];
                    for (int y = 0; y < 16; y++) {
                        Block block = chunkSection.getBlock(x, y, z);
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
                for (ChunkSection chunkSection : chunkSections) {
                    for (int y = 0; y < 16; y++) {
                        Block block = chunkSection.getBlock(x, y, z);
                        if (block.getType().equals(BlockTypes.AIR))continue;
                        //TODO Make accurate model (store in block)
                        Entity entity = new Entity(RadonClient.GRASS_MODEL,
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
                for (ChunkSection chunkSection : chunkSections) {
                    for (int y = 0; y < 16; y++) {
                        blocks.add(chunkSection.getBlock(x, y, z));
                    }
                }
            }
        }
        return blocks;
    }
}
