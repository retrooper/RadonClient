package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockFace;
import com.github.retrooper.radonclient.world.block.BlockTypes;

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

    //maxHeight must be multiple of 16
    public Chunk(int x, int z, int maxHeight) {
        this.x = x;
        this.z = z;
        this.chunkSections = new ChunkSection[maxHeight / 16];
        for (int i = 0; i < this.chunkSections.length; i++) {
            this.chunkSections[i] = new ChunkSection(x, i, z, 16, 16, 16);
        }
    }

    public ChunkSection[] getChunkSections() {
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
        ChunkSection chunkSection = getChunkSections()[y >> 4];
        return chunkSection.getBlock(x & 15, y & 15, z & 15);
    }

    //X(0-15), Y(0-15), Z(0-15)
    public void setBlock(int x, int y, int z, Block block) {
        ChunkSection chunkSection = getChunkSections()[y >> 4];
        chunkSection.setBlock(x & 15, y & 15, z & 15, block);
    }

    public void handlePerChunk(Consumer<ChunkSection> consumer) {
        for (ChunkSection chunkSection : getChunkSections()) {
            consumer.accept(chunkSection);
        }
    }

    public void handlePerBlock(Consumer<Block> consumer) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (ChunkSection chunkSection : chunkSections) {
                    for (int y = 0; y < 16; y++) {
                        Block block = chunkSection.getBlock(x, y, z);
                        consumer.accept(block);
                    }
                }
            }
        }
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (ChunkSection chunkSection : chunkSections) {
            blocks.addAll(chunkSection.getBlocks());
        }
        return blocks;
    }

    public void updateBlockFaces() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 256; y++) {
                    Block block = getBlock(x, y, z);
                    if (block.getType().equals(BlockTypes.AIR)) continue;
                    byte faceMask = 0x00;
                    if (y == 255 || getBlock(x, y + 1, z).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.TOP.getBit();
                    }

                    if (y == 0 || getBlock(x, y - 1, z).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.BOTTOM.getBit();
                    }

                    if (x == 0 || getBlock(x - 1, y, z).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.WEST.getBit();
                    }

                    if (x == 15 || getBlock(x + 1, y, z).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.EAST.getBit();
                    }

                    if (z == 15 || getBlock(x, y, z + 1).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.NORTH.getBit();
                    }

                    if (z == 0 || getBlock(x, y, z - 1).getType().equals(BlockTypes.AIR)) {
                        faceMask |= BlockFace.SOUTH.getBit();
                    }

                    for (BlockFace face : BlockFace.values()) {
                        if ((faceMask & face.getBit()) != 0) {
                            block.setFaceVisible(face);
                        } else {
                            block.setFaceInvisible(face);
                        }
                    }
                }
            }
        }
    }
}
