package com.algo.lab20;

import java.util.random.RandomGenerator;

/**
 * Fisher-Yates (Knuth) shuffle for perfect random permutation.
 * Produces unbiased random permutations of an array.
 * Time: O(n), Space: O(1) in-place
 */
public class FisherYatesShuffle {

    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private FisherYatesShuffle() {}

    public static void shuffle(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static void shuffle(Object[] arr) {
        if (arr == null || arr.length <= 1) return;
        for (int i = arr.length - 1; i > 0; i--) {
            int j = RNG.nextInt(i + 1);
            Object temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    public static int[] shuffledCopy(int[] arr) {
        if (arr == null) return new int[0];
        int[] copy = arr.clone();
        shuffle(copy);
        return copy;
    }
}
