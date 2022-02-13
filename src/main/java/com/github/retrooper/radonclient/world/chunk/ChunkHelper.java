package com.github.retrooper.radonclient.world.chunk;

public class ChunkHelper {
    public static long serializeChunkXZ(int x, int z) {
        return ((x & 0xFFFFFFFFL) << 32L) | (z & 0xFFFFFFFFL);
    }
}
