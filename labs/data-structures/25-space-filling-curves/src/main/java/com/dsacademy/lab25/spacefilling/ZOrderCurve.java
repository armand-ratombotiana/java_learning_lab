package com.dsacademy.lab25.spacefilling;

public class ZOrderCurve {

    public static long encode2D(int x, int y) {
        long result = 0;
        for (int i = 0; i < 32; i++) {
            result |= ((long)(x >> i) & 1) << (2 * i);
            result |= ((long)(y >> i) & 1) << (2 * i + 1);
        }
        return result;
    }

    public static int[] decode2D(long z) {
        int x = 0, y = 0;
        for (int i = 0; i < 32; i++) {
            x |= (int)((z >> (2 * i)) & 1) << i;
            y |= (int)((z >> (2 * i + 1)) & 1) << i;
        }
        return new int[]{x, y};
    }

    public static long encode3D(int x, int y, int z) {
        long result = 0;
        for (int i = 0; i < 21; i++) {
            result |= ((long)(x >> i) & 1) << (3 * i);
            result |= ((long)(y >> i) & 1) << (3 * i + 1);
            result |= ((long)(z >> i) & 1) << (3 * i + 2);
        }
        return result;
    }

    public static int[] decode3D(long morton) {
        int x = 0, y = 0, z = 0;
        for (int i = 0; i < 21; i++) {
            x |= (int)((morton >> (3 * i)) & 1) << i;
            y |= (int)((morton >> (3 * i + 1)) & 1) << i;
            z |= (int)((morton >> (3 * i + 2)) & 1) << i;
        }
        return new int[]{x, y, z};
    }

    public static double distance2D(long a, long b) {
        int[] p1 = decode2D(a);
        int[] p2 = decode2D(b);
        return Math.sqrt(Math.pow(p1[0] - p2[0], 2) + Math.pow(p1[1] - p2[1], 2));
    }
}
