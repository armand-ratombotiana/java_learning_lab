package com.dsacademy.lab30.cacheoblivious;

import java.util.Arrays;
import java.util.Random;

public class CacheObliviousArray {

    private final int[] data;
    public final int size;

    public CacheObliviousArray(int n) {
        this.size = n;
        this.data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }
    }

    public int sumRecursive(int left, int right) {
        if (left == right) return data[left];
        int mid = (left + right) / 2;
        return sumRecursive(left, mid) + sumRecursive(mid + 1, right);
    }

    public int sumIterative() {
        int sum = 0;
        for (int v : data) sum += v;
        return sum;
    }

    public void shuffle() {
        Random rand = new Random(42);
        for (int i = size - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
        }
    }

    public int max() {
        int mx = Integer.MIN_VALUE;
        for (int v : data) mx = Math.max(mx, v);
        return mx;
    }

    public void fill(int value) {
        Arrays.fill(data, value);
    }
}
