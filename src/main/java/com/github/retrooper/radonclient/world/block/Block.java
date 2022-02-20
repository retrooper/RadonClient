package com.github.retrooper.radonclient.world.block;

import com.github.retrooper.radonclient.entity.Entity;
import com.github.retrooper.radonclient.entity.collision.BoxCollision;
import com.github.retrooper.radonclient.model.Model;
import org.joml.Vector3f;

import static com.github.retrooper.radonclient.RadonClient.DIRT_MODEL;
import static com.github.retrooper.radonclient.RadonClient.GRASS_MODEL;

public class Block {
    private static final byte ALL_VISIBLE_MASK;

    static {
        byte temp = 0x00;
        for (BlockFace face : BlockFace.values()) {
            temp |= face.getBit();
        }

        ALL_VISIBLE_MASK = temp;

    }

    private byte faceMask = 0x00;
    private BlockType type;
    private int x;
    private int y;
    private int z;

    public Block(final BlockType type, int x, int y, int z) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockType getType() {
        return this.type;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setFaceVisible(BlockFace face) {
        faceMask |= face.getBit();
    }

    public void setAllFacesVisible() {
        faceMask = ALL_VISIBLE_MASK;
    }

    public void setAllFacesInvisible() {
        faceMask = 0x00;
    }

    public void setFaceInvisible(BlockFace face) {
        faceMask &= ~face.getBit();
    }

    public boolean isFaceVisible(BlockFace face) {
        return (faceMask & face.getBit()) != 0;
    }

    public boolean isOneFaceVisible() {
        for (BlockFace face : BlockFace.values()) {
            if (isFaceVisible(face)) {
                return true;
            }
        }
        return false;
    }

    public BoxCollision getCollision() {
        return new BoxCollision(new Vector3f(x, y, z), 1.0f, 1.0f);
    }

    public Model asModel() {
        return toModel(type);
    }

    public static Model toModel(BlockType type) {
        return type.equals(BlockTypes.DIRT) ? DIRT_MODEL : GRASS_MODEL;
    }

    public Entity asEntity() {
        return new Entity(asModel(),
                x, y, z,
                new Vector3f(), 1.0f);
    }
}
