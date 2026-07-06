package com.algo.lab20;

import java.util.random.RandomGenerator;

/**
 * Reservoir sampling for selecting k random elements from a stream.
 * Each element has equal probability of being selected.
 * Time: O(n), Space: O(k)
 */
public class ReservoirSampling {

    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private ReservoirSampling() {}

    public static int[] sample(int[] stream, int k) {
        if (stream == null || k <= 0 || stream.length == 0) {
            return new int[0];
        }
        int kActual = Math.min(k, stream.length);
        int[] reservoir = new int[kActual];
        for (int i = 0; i < kActual; i++) {
            reservoir[i] = stream[i];
        }
        for (int i = kActual; i < stream.length; i++) {
            int j = RNG.nextInt(i + 1);
            if (j < kActual) {
                reservoir[j] = stream[i];
            }
        }
        return reservoir;
    }

    public static int[] sampleStreaming(java.util.Iterator<Integer> stream, int k) {
        if (stream == null || k <= 0) return new int[0];
        int[] reservoir = new int[k];
        int i = 0;
        for (; i < k && stream.hasNext(); i++) {
            reservoir[i] = stream.next();
        }
        if (i < k) {
            int[] result = new int[i];
            System.arraycopy(reservoir, 0, result, 0, i);
            return result;
        }
        while (stream.hasNext()) {
            int val = stream.next();
            int j = RNG.nextInt(i + 1);
            if (j < k) {
                reservoir[j] = val;
            }
            i++;
        }
        return reservoir;
    }
}
