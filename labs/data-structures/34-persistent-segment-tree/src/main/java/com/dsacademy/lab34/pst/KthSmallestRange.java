package com.dsacademy.lab34.pst;

import java.util.*;

public final class KthSmallestRange {

    private final int[] original;
    private final PersistentSegmentTree pst;
    private final int[] sorted;
    private final int[] compressed;

    public KthSmallestRange(int[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        this.original = array.clone();
        int[] unique = Arrays.stream(array).distinct().sorted().toArray();
        this.sorted = unique;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < unique.length; i++) {
            map.put(unique[i], i);
        }
        this.compressed = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            this.compressed[i] = map.get(array[i]);
        }
        int[] freq = new int[unique.length];
        int[][] versions = new int[array.length + 1][];
        pst = new PersistentSegmentTree(freq);
        for (int i = 0; i < array.length; i++) {
            int val = pst.get(compressed[i], i);
            pst.update(compressed[i], val + 1, i);
        }
    }

    public int query(int l, int r, int k) {
        if (k < 1 || k > (r - l + 1)) {
            throw new IllegalArgumentException("k out of range for given interval");
        }
        int lo = 0;
        int hi = sorted.length - 1;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            int leftCount = pst.query(0, mid, l - 1);
            int rightCount = pst.query(0, mid, r);
            int count = rightCount - leftCount;
            if (count >= k) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return sorted[lo];
    }

    public int[] getOriginal() {
        return original.clone();
    }

    public static int[] rangeQuery(int[] arr, int[][] queries) {
        KthSmallestRange ksr = new KthSmallestRange(arr);
        int[] result = new int[queries.length];
        for (int i = 0; i < queries.length; i++) {
            result[i] = ksr.query(queries[i][0], queries[i][1], queries[i][2]);
        }
        return result;
    }
}
