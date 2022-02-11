package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.util.MathUtil;
import com.github.retrooper.radonclient.world.block.Block;
import org.joml.Vector3f;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkHelper {
    private static final Map<Long, ChunkColumn> CHUNK_COLUMNS = new ConcurrentHashMap<>();

    public static Map<Long, ChunkColumn> getChunkColumns() {
        return CHUNK_COLUMNS;
    }

    public static long serializeChunkXZ(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }

    public static ChunkColumn getChunkColumnAt(int x, int z) {
        return CHUNK_COLUMNS.get(serializeChunkXZ(x >> 4, z >> 4));
    }

    public static Chunk getChunkAt(int x, int y, int z) {
        ChunkColumn column = getChunkColumnAt(x, z);
        return column.getChunks()[y >> 4];
    }

    public static Block getBlockAt(Vector3f position) {
        return getBlockAt(MathUtil.floor(position.x), MathUtil.floor(position.y), MathUtil.floor(position.z));
    }

    public static Block getBlockAt(int x, int y, int z) {
        Chunk chunk = getChunkAt(x, y, z);
        return chunk.getBlock(x & 15, y & 15, z & 15);
    }

    public static void setBlockAt(int x, int y, int z, Block block) {
        Chunk chunk = getChunkAt(x, y, z);
        chunk.setBlock(x & 15, y & 15, z & 15, block);
    }

    public static void setBlockAt(Vector3f position, Block block) {
        setBlockAt(MathUtil.floor(position.x), MathUtil.floor(position.y), MathUtil.floor(position.z), block);
    }
}
