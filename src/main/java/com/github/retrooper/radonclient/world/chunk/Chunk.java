package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Chunk {
    private final int x;
    private final int z;
    private final Block[][][] blocks;

    public Chunk(int x, int z, int width, int length, final int height) {
        this.x = x;
        this.z = z;
        this.blocks = new Block[width][length][height];
        for (int w = 0; w < width; w++) {
            for (int l = 0; l < length; l++) {
                for (int y = 0; y < height; y++) {
                    this.blocks[w][l][y] = new Block(BlockTypes.AIR, w, y, l);
                }
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Block getBlock(int x, int y, int z) {
        return this.blocks[x][y][z];
    }

    public void setBlock(int x, int y, int z, Block block) {
        this.blocks[x][z][y] = block;
    }

    public void handlePerBlock(Consumer<Block> consumer) {
        for (int x = 0; x < this.blocks.length; x++) {
            for (int z = 0; z < this.blocks[x].length; z++) {
                for (int y = 0; y < this.blocks[x][z].length; y++) {
                    consumer.accept(this.blocks[x][z][y]);
                }
            }
        }
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>();
        for (int x = 0; x < this.blocks.length; x++) {
            for (int z = 0; z < this.blocks[x].length; z++) {
                for (int y = 0; y < this.blocks[x][z].length; y++) {
                    blocks.add(this.blocks[x][z][y]);
                }
            }
        }
        return blocks;
    }
}
