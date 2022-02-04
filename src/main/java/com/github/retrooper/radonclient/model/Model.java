package com.github.retrooper.radonclient.model;

public class Model {
    private int vaoId;
    private int indicesCount;

    protected Model(int vaoId, int indicesCount) {
        this.vaoId = vaoId;
        this.indicesCount = indicesCount;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getIndicesCount() {
        return indicesCount;
    }
}
