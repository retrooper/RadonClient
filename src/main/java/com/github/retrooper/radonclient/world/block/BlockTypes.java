package com.github.retrooper.radonclient.world.block;

import com.github.retrooper.radonclient.texture.Texture;
import com.github.retrooper.radonclient.texture.TextureFactory;
import org.jetbrains.annotations.Nullable;

public class BlockTypes {
    private static int BLOCK_TYPE_COUNT = 0;
    public static final BlockType AIR = declare("air", null);
    public static final BlockType DIRT = declare("dirt", "dirt");





    private static BlockType declare(final String name, @Nullable String textureName) {
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
            public Texture texture() {
                if (textureName == null) {
                    return null;
                }
                return TextureFactory.loadTexture("textures/" + textureName + ".png");
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
