package com.leetcode.minhash;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Custom: MinHash for Jaccard Similarity
 * Estimates similarity between sets using random hash functions.
 *
 * Time Complexity: O(k * |set|) where k is number of hash functions
 * Space Complexity: O(k * n) for signatures
 */
public class MinHashSketch {

    private final int[] hashSeeds;
    private static final int K = 100;

    public MinHashSketch() {
        hashSeeds = new int[K];
        Random r = new Random(42);
        for (int i = 0; i < K; i++) hashSeeds[i] = r.nextInt();
    }

    public int[] computeSignature(Set<Integer> set) {
        int[] signature = new int[K];
        for (int i = 0; i < K; i++) {
            int minHash = Integer.MAX_VALUE;
            for (int val : set) {
                int hash = (val ^ hashSeeds[i]) * hashSeeds[i];
                hash ^= (hash >>> 13);
                hash = Math.abs(hash);
                minHash = Math.min(minHash, hash);
            }
            signature[i] = minHash;
        }
        return signature;
    }

    public double estimateSimilarity(int[] sig1, int[] sig2) {
        int matches = 0;
        for (int i = 0; i < K; i++) if (sig1[i] == sig2[i]) matches++;
        return (double) matches / K;
    }

    public static void main(String[] args) {
        MinHashSketch mh = new MinHashSketch();

        Set<Integer> setA = new HashSet<>();
        for (int i = 0; i < 100; i++) setA.add(i);

        Set<Integer> setB = new HashSet<>();
        for (int i = 50; i < 150; i++) setB.add(i);

        int[] sigA = mh.computeSignature(setA);
        int[] sigB = mh.computeSignature(setB);
        double sim = mh.estimateSimilarity(sigA, sigB);

        double actualJaccard = (double) 50 / 150;
        System.out.println("Estimated Jaccard: " + sim + " (actual: " + actualJaccard + ")");

        // Identical sets
        Set<Integer> setC = new HashSet<>(setA);
        int[] sigC = mh.computeSignature(setC);
        System.out.println("Identical sets similarity: " + mh.estimateSimilarity(sigA, sigC) + " (expected: ~1.0)");
    }
}
