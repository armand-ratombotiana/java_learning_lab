package com.dsacademy.lab13.fenwicktree;

public class InversionCount {

    public static long countInversions(int[] arr) {
        if (arr == null || arr.length == 0) return 0;

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int v : arr) {
            if (v < min) min = v;
            if (v > max) max = v;
        }

        int range = max - min + 1;
        FenwickTree bit = new FenwickTree(range);

        long inversions = 0;
        for (int i = arr.length - 1; i >= 0; i--) {
            int idx = arr[i] - min;
            inversions += bit.sum(idx - 1);
            bit.add(idx, 1);
        }
        return inversions;
    }

    public static long countInversionsWithCompression(int[] arr) {
        if (arr == null || arr.length == 0) return 0;

        int[] sorted = arr.clone();
        java.util.Arrays.sort(sorted);

        java.util.Map<Integer, Integer> rank = new java.util.HashMap<>();
        for (int i = 0; i < sorted.length; i++) {
            rank.putIfAbsent(sorted[i], rank.size());
        }

        FenwickTree bit = new FenwickTree(rank.size());
        long inversions = 0;

        for (int i = arr.length - 1; i >= 0; i--) {
            int r = rank.get(arr[i]);
            inversions += bit.sum(r - 1);
            bit.add(r, 1);
        }
        return inversions;
    }
}
