package com.github.retrooper.radonclient.util;

public class MathUtil {
    public static int floor(double value) {
        int temp = (int) value;
        return value < (double) temp ? temp - 1 : temp;
    }

    public static int floor(float value) {
        int temp = (int) value;
        return value < (float) temp ? temp - 1 : temp;
    }
}
