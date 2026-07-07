package com.dsacademy.lab25.spacefilling;

public class HilbertCurve {

    public static long encode2D(int x, int y, int order) {
        long result = 0;
        int rx, ry, t;
        for (int s = order - 1; s >= 0; s--) {
            rx = (x >> s) & 1;
            ry = (y >> s) & 1;
            result += (long)((3 * rx) ^ ry) << (2 * s);
            if (ry == 0) {
                if (rx == 1) {
                    x = (1 << s) - 1 - x;
                    y = (1 << s) - 1 - y;
                }
                t = x; x = y; y = t;
            }
        }
        return result;
    }

    public static int[] decode2D(long d, int order) {
        int x = 0, y = 0;
        int rx, ry, t;
        long s = 1L << (2 * order - 1);
        for (int step = order - 1; step >= 0; step--) {
            rx = (int)((d >> (2 * step)) & 1);
            ry = (int)(((d >> (2 * step + 1)) & 1) ^ rx);
            if (ry == 0) {
                if (rx == 1) {
                    x = (1 << step) - 1 - x;
                    y = (1 << step) - 1 - y;
                }
                t = x; x = y; y = t;
            }
            x |= rx << step;
            y |= ry << step;
        }
        return new int[]{x, y};
    }
}
