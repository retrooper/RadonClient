package com.github.retrooper.radonclient.world.block;

public enum BlockFace {
    TOP(0x01),
    BOTTOM(0x02),
    WEST(0x04),
    EAST(0x08),
    NORTH(0x10),
    SOUTH(0x20);

    private final byte bit;

    BlockFace(int bit) {
        this.bit = (byte) bit;
    }

    public byte getId() {
        return (byte) ordinal();
    }

    public byte getBit() {
        return bit;
    }

    public static BlockFace getById(byte id) {
        return values()[id];
    }
}
