package com.github.retrooper.radonclient.model;

public class Model {
    private final int vaoId;
    private final int instancesVBO;
    private final int indicesCount;

    protected Model(int vaoId, int instancesVBO, int indicesCount) {
        this.vaoId = vaoId;
        this.instancesVBO = instancesVBO;
        this.indicesCount = indicesCount;
    }

    public int getInstancesVBO() {
        return instancesVBO;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Model model) {
            return vaoId == model.vaoId && indicesCount == model.indicesCount && instancesVBO == model.instancesVBO;
        }
        return false;
    }
}
