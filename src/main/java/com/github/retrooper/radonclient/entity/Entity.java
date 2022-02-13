package com.github.retrooper.radonclient.entity;

import com.github.retrooper.radonclient.model.Model;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import static org.joml.Math.toRadians;

public class Entity {
    private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
    private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
    private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
    private final Model model;
    private final float scale;
    private Vector3f position;
    private Vector3f rotation;

    public Entity(Model model, Vector3i position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = new Vector3f(position.x, position.y, position.z);
        this.rotation = rotation;
        this.scale = scale;
    }

    public Entity(Model model, Vector3f position, Vector3f rotation, float scale) {
        this.model = model;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Entity(Model model, int x, int y, int z, Vector3f rotation, float scale) {
        this(model, new Vector3f(x, y, z), rotation, scale);
    }

    public Entity(Model model, float x, float y, float z, Vector3f rotation, float scale) {
        this(model, new Vector3f(x, y, z), rotation, scale);
    }

    public Model getModel() {
        return model;
    }

    public float getScale() {
        return scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public Matrix4f getTransformationMatrix() {
        return new Matrix4f()
                .translate(position)
                .rotate(toRadians(rotation.x), X_AXIS)
                .rotate(toRadians(rotation.y), Y_AXIS)
                .rotate(toRadians(rotation.z), Z_AXIS)
                .scale(scale);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Entity entity) {
            return model.getVaoId() == entity.model.getVaoId() &&
                    scale == entity.scale &&
                    position.equals(entity.position) &&
                    rotation.equals(entity.rotation);
        }
        return false;
    }
}
