package com.github.retrooper.radonclient.world.block;

import org.joml.Vector3i;

public class Block {
    private BlockType type;
    private final Vector3i position;

    public Block(final BlockType type, final Vector3i position) {
        this.type = type;
        this.position = position;
    }

    public Block(final BlockType type, int x, int y, int z) {
        this(type, new Vector3i(x, y, z));
    }

    public BlockType getType() {
        return this.type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public Vector3i getPosition() {
        return this.position;
    }

    public void setPosition(Vector3i position) {
        this.position.set(position);
    }
}
