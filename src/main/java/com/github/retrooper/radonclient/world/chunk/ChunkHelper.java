package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.util.MathUtil;
import com.github.retrooper.radonclient.world.block.Block;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkHelper {
    private static final Map<Long, Chunk> CHUNKS = new ConcurrentHashMap<>();

    public static Map<Long, Chunk> getChunks() {
        return CHUNKS;
    }

    public static long serializeChunkXZ(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    public static Chunk getChunkAt(int x, int z) {
        return CHUNKS.get(serializeChunkXZ(x >> 4, z >> 4));
    }

    public static ChunkSection getChunkSectionAt(int x, int y, int z) {
        Chunk column = getChunkAt(x, z);
        return column.getChunks()[y >> 4];
    }

    public static Block getBlockAt(Vector3f position) {
        return getBlockAt(MathUtil.floor(position.x), MathUtil.floor(position.y), MathUtil.floor(position.z));
    }

    public static Block getBlockAt(int x, int y, int z) {
        ChunkSection chunkSection = getChunkSectionAt(x, y, z);
        return chunkSection.getBlock(x & 15, y & 15, z & 15);
    }

    public static void setBlockAt(int x, int y, int z, Block block) {
        ChunkSection chunkSection = getChunkSectionAt(x, y, z);
        chunkSection.setBlock(x & 15, y & 15, z & 15, block);
    }

    public static void setBlockAt(Vector3f position, Block block) {
        setBlockAt(MathUtil.floor(position.x), MathUtil.floor(position.y), MathUtil.floor(position.z), block);
    }
}
