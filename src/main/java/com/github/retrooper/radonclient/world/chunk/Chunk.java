package com.github.retrooper.radonclient.world.chunk;

import com.github.retrooper.radonclient.world.block.Block;
import com.github.retrooper.radonclient.world.block.BlockTypes;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Chunk {
    private final int x;
    private final int y;
    private final int z;
    private final Block[][][] blocks;

    public Chunk(int x, int y, int z, int width, int length, final int height) {
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
                    Vector3i blockPos = new Vector3i(globalX, globalY, globalZ);
                    this.blocks[w][l][h] = new Block(BlockTypes.AIR, blockPos);
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
    public Block getBlock(int x, int y, int z) {
        return this.blocks[x][z][y];
    }

    //X(0-15), Y(0-15), Z(0-15)
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
        for (Block[][] block : this.blocks) {
            for (Block[] value : block) {
                blocks.addAll(Arrays.asList(value));
            }
        }
        return blocks;
    }
}
