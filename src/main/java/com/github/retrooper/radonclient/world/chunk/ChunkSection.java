package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class ChunkSection {
    private final int x;
    private final int y;
    private final int z;
    private final Block[][][] blocks;

    public ChunkSection(int x, int y, int z, int width, int length, final int height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blocks = new Block[width][length][height];
        for (int w = 0; w < width; w++) {
            for (int l = 0; l < length; l++) {
                for (int h = 0; h < height; h++) {
                    int globalX = (x << 4) | w;
                    int globalY = (y << 4) | h;
                    int globalZ = (z << 4) | l;
                    this.blocks[w][l][h] = new Block(BlockTypes.AIR, globalX, globalY, globalZ);
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    //X(0-15), Y(0-15), Z(0-15)
    @NotNull
    public Block getBlock(int x, int y, int z) {
        return this.blocks[x][z][y];
    }

    //X(0-15), Y(0-15), Z(0-15)
    public void setBlock(int x, int y, int z, @NotNull Block block) {
        this.blocks[x][z][y] = block;
    }

    public void handlePerBlock(Consumer<@NotNull Block> consumer) {
        for (Block[][] key : this.blocks) {
            for (Block[] value : key) {
                for (Block block : value) {
                    consumer.accept(block);
                }
            }
        }
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (Block[][] block : this.blocks) {
            for (Block[] value : block) {
                blocks.addAll(Arrays.asList(value));
            }
        }
        return blocks;
    }
}
