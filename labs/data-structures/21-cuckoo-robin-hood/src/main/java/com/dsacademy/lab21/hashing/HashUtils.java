package com.dsacademy.lab21.hashing;

import java.util.zip.CRC32C;
import java.nio.charset.StandardCharsets;

public final class HashUtils {

    private HashUtils() {}

    public static int murmur3(int key, int seed) {
        int c1 = 0xCC9E2D51;
        int c2 = 0x1B873593;
        int r1 = 15;
        int r2 = 13;
        int m = 5;
        int n = 0xE6546B64;
        int k = key;
        k *= c1;
        k = Integer.rotateLeft(k, r1);
        k *= c2;
        int h = seed;
        h ^= k;
        h = Integer.rotateLeft(h, r2);
        h = h * m + n;
        h ^= 4;
        h ^= h >>> 16;
        h *= 0x85EBCA6B;
        h ^= h >>> 13;
        h *= 0xC2B2AE35;
        h ^= h >>> 16;
        return h;
    }

    public static long crc32(String input) {
        CRC32C crc = new CRC32C();
        crc.update(input.getBytes(StandardCharsets.UTF_8));
        return crc.getValue();
    }

    public static int universalHash(int key, int a, int b, int p, int m) {
        return Math.floorMod(a * key + b, p) % m;
    }

    public static int nextPowerOfTwo(int n) {
        if (n <= 0) return 1;
        int highestOne = Integer.highestOneBit(n);
        return (n == highestOne) ? n : highestOne << 1;
    }
}
