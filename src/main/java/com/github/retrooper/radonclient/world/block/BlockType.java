package com.github.retrooper.radonclient.world.block;

import com.github.retrooper.radonclient.texture.Texture;
import org.jetbrains.annotations.Nullable;

public interface BlockType {
    int id();
    String name();
    @Nullable Texture texture();
}
