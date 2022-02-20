package com.github.retrooper.radonclient.entity.collision;

import org.joml.Vector3f;

public class BoxCollision {
    private final float minX, maxX;
    private final float minY, maxY;
    private final float minZ, maxZ;

    public BoxCollision(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }

    public BoxCollision(Vector3f position, float width, float height) {
        this(position.x, width, position.y, height, position.z, width);
    }

    public boolean isCollidingVertically(BoxCollision other) {
        return other.maxX > this.minX && other.minX < this.maxX
                && other.maxY >= this.minY && other.minY <= this.maxY
                && other.maxZ > this.minZ && other.minZ < this.maxZ;
    }

    public boolean isCollided(BoxCollision other) {
        return other.maxX >= this.minX && other.minX <= this.maxX
                && other.maxY >= this.minY && other.minY <= this.maxY
                && other.maxZ >= this.minZ && other.minZ <= this.maxZ;    }

    public boolean isIntersected(BoxCollision other) {
        return other.maxX > this.minX && other.minX < this.maxX
                && other.maxY > this.minY && other.minY < this.maxY
                && other.maxZ > this.minZ && other.minZ < this.maxZ;
    }

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxZ() {
        return maxZ;
    }
}
