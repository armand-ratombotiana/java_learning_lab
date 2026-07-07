package com.algo.lab26;

public class BitTricks {

    private BitTricks() {}

    public static boolean isPowerOfTwo(int x) {
        return x > 0 && (x & (x - 1)) == 0;
    }

    public static int countBits(int x) {
        int count = 0;
        while (x != 0) {
            x &= (x - 1);
            count++;
        }
        return count;
    }

    public static int reverseBits(int x) {
        x = ((x & 0x55555555) << 1) | ((x >>> 1) & 0x55555555);
        x = ((x & 0x33333333) << 2) | ((x >>> 2) & 0x33333333);
        x = ((x & 0x0F0F0F0F) << 4) | ((x >>> 4) & 0x0F0F0F0F);
        x = ((x & 0x00FF00FF) << 8) | ((x >>> 8) & 0x00FF00FF);
        x = ((x & 0x0000FFFF) << 16) | ((x >>> 16) & 0x0000FFFF);
        return x;
    }

    public static int nextPowerOfTwo(int x) {
        if (x <= 1) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return x + 1;
    }
}
