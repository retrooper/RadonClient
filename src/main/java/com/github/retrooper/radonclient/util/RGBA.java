package com.github.retrooper.radonclient.util;

public class RGBA {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public RGBA(final float red, final float green, final float blue, final float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float getRed() {
        return this.red;
    }

    public float getGreen() {
        return this.green;
    }

    public float getBlue() {
        return this.blue;
    }

    public float getAlpha() {
        return this.alpha;
    }
}
