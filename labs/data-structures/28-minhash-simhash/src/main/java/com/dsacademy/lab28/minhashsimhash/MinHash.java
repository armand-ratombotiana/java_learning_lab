package com.dsacademy.lab28.minhashsimhash;

import java.util.*;

public class MinHash {

    private final int numHashes;
    private final int maxVal;
    private final int[] seeds;
    private final Map<String, int[]> signatures;

    public MinHash(int numHashes, int maxVal) {
        this.numHashes = numHashes;
        this.maxVal = maxVal;
        this.seeds = new int[numHashes];
        Random rand = new Random(42);
        for (int i = 0; i < numHashes; i++) {
            seeds[i] = rand.nextInt();
        }
        this.signatures = new HashMap<>();
    }

    public int[] computeSignature(Set<Integer> set) {
        int[] sig = new int[numHashes];
        Arrays.fill(sig, Integer.MAX_VALUE);
        for (int element : set) {
            for (int i = 0; i < numHashes; i++) {
                int hash = (element ^ seeds[i]) & 0x7fffffff;
                sig[i] = Math.min(sig[i], hash);
            }
        }
        return sig;
    }

    public void addDocument(String docId, Set<Integer> shingles) {
        signatures.put(docId, computeSignature(shingles));
    }

    public double estimateJaccard(String docA, String docB) {
        int[] sigA = signatures.get(docA);
        int[] sigB = signatures.get(docB);
        if (sigA == null || sigB == null) return 0;
        int matches = 0;
        for (int i = 0; i < numHashes; i++) {
            if (sigA[i] == sigB[i]) matches++;
        }
        return (double) matches / numHashes;
    }

    public int getNumHashes() { return numHashes; }
}
