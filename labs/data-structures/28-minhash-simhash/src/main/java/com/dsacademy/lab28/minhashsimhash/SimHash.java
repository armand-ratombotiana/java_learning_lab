package com.dsacademy.lab28.minhashsimhash;

public class SimHash {

    private final int bits;

    public SimHash(int bits) {
        this.bits = bits;
    }

    public long computeFingerprint(String text) {
        if (text == null || text.isEmpty()) return 0;
        String[] words = text.toLowerCase().split("\\s+");
        int[] v = new int[bits];
        for (String word : words) {
            long hash = wordHash(word);
            for (int i = 0; i < bits; i++) {
                if ((hash & (1L << i)) != 0) {
                    v[i]++;
                } else {
                    v[i]--;
                }
            }
        }
        long fingerprint = 0;
        for (int i = 0; i < bits; i++) {
            if (v[i] > 0) {
                fingerprint |= (1L << i);
            }
        }
        return fingerprint;
    }

    public double cosineSimilarity(long fpA, long fpB) {
        int hamming = Long.bitCount(fpA ^ fpB);
        return 1.0 - (double) hamming / bits;
    }

    private long wordHash(String word) {
        long hash = 0x9E3779B97F4A7C15L;
        for (char c : word.toCharArray()) {
            hash ^= c;
            hash *= 0xFF51AFD7ED558CCDL;
            hash ^= hash >>> 37;
        }
        return hash;
    }

    public int getBits() { return bits; }
}
