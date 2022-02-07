package com.github.retrooper.radonclient.entity.player;

public enum MoveDirection {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT;

    MoveDirection getOpposite() {
        return switch (this) {
            case FORWARD -> BACKWARD;
            case BACKWARD -> FORWARD;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }
}
