package com.github.retrooper.radonclient.world.block;

public class BlockTypes {
    private static int BLOCK_TYPE_COUNT = 0;
    public static final BlockType AIR = declare("air");
    public static final BlockType DIRT = declare("dirt");
    public static final BlockType GRASS = declare("grass");


    private static BlockType declare(final String name) {
        final int blockId = BLOCK_TYPE_COUNT++;
        return new BlockType() {
            @Override
            public int id() {
                return blockId;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof BlockType) {
                    return ((BlockType) obj).id() == id();
                }
                return false;
            }
        };
    }
}
