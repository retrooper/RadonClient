package com.github.retrooper.radonclient.model;

public class Model {
    private int vaoId;
    private int vertexCount;

    protected Model(int vaoId, int vertexCount) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
