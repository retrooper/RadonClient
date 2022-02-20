package com.github.retrooper.radonclient.util;

import org.joml.Vector3f;

public class MathUtil {
    public static int floor(double value) {
        int temp = (int) value;
        return value < (double) temp ? temp - 1 : temp;
    }

    public static int floor(float value) {
        int temp = (int) value;
        return value < (float) temp ? temp - 1 : temp;
    }


    public static float lerpFloat(float start, float end, float time) {
        return start * (1 - time) + end * time;
    }

    public static Vector3f lerpVector3f(Vector3f start, Vector3f end, float time) {
        final float x = lerpFloat(start.x, end.x, time);
        final float y = lerpFloat(start.y, end.y, time);
        final float z = lerpFloat(start.z, end.z, time);
        return new Vector3f(x, y, z);
    }
}
