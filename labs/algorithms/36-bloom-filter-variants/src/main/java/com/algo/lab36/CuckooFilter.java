package com.algo.lab36;

import java.util.Arrays;

/**
 * Cuckoo Filter using bucket-based fingerprint storage with cuckoo hashing
 * displacement for high space efficiency. Supports insert, lookup, and delete.
 * Each entry stores a short fingerprint and uses two candidate buckets via
 * hash of the fingerprint.
 */
public class CuckooFilter {
    private static final int FINGERPRINT_BITS = 8;
    private static final int BUCKET_SIZE = 4;
    private static final int MAX_KICKS = 500;

    private final short[][] buckets;
    private final int numBuckets;

    public CuckooFilter(int capacity) {
        this.numBuckets = nextPowerOf2(capacity / BUCKET_SIZE);
        this.buckets = new short[numBuckets][BUCKET_SIZE];
        for (int i = 0; i < numBuckets; i++) Arrays.fill(buckets[i], (short) -1);
    }

    private static int nextPowerOf2(int n) {
        int v = 1;
        while (v < n) v <<= 1;
        return v;
    }

    private short fingerprint(String item) {
        int h = item.hashCode();
        return (short) ((h & ((1 << FINGERPRINT_BITS) - 1)) + 1);
    }

    private int hash(String item) {
        return (item.hashCode() & 0x7fffffff) % numBuckets;
    }

    private int altIndex(int i, short fp) {
        return (i ^ ((fp * 0x9e3779b9) & 0x7fffffff)) % numBuckets;
    }

    public boolean insert(String item) {
        short fp = fingerprint(item);
        int i = hash(item);
        int j = altIndex(i, fp);
        for (int k = 0; k < MAX_KICKS; k++) {
            if (insertIntoBucket(i, fp)) return true;
            fp = swapFingerprint(i, fp);
            int tmp = i; i = j; j = altIndex(tmp, fp);
            i = j; j = altIndex(i, fp);
            if (insertIntoBucket(i, fp)) return true;
            fp = swapFingerprint(i, fp);
            i = j; j = altIndex(i, fp);
        }
        return false;
    }

    private boolean insertIntoBucket(int idx, short fp) {
        for (int b = 0; b < BUCKET_SIZE; b++) {
            if (buckets[idx][b] == -1) {
                buckets[idx][b] = fp;
                return true;
            }
        }
        return false;
    }

    private short swapFingerprint(int idx, short fp) {
        int slot = (int)(Math.random() * BUCKET_SIZE);
        short old = buckets[idx][slot];
        buckets[idx][slot] = fp;
        return old;
    }

    public boolean contains(String item) {
        short fp = fingerprint(item);
        int i = hash(item);
        int j = altIndex(i, fp);
        return containsInBucket(i, fp) || containsInBucket(j, fp);
    }

    private boolean containsInBucket(int idx, short fp) {
        for (int b = 0; b < BUCKET_SIZE; b++) {
            if (buckets[idx][b] == fp) return true;
        }
        return false;
    }

    public boolean delete(String item) {
        short fp = fingerprint(item);
        int i = hash(item);
        int j = altIndex(i, fp);
        for (int idx : new int[]{i, j}) {
            for (int b = 0; b < BUCKET_SIZE; b++) {
                if (buckets[idx][b] == fp) {
                    buckets[idx][b] = -1;
                    return true;
                }
            }
        }
        return false;
    }
}
