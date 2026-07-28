package com.ds.advanced.lab03;

public class FenwickTree {
    private final int n;
    private final int[] bit;

    public FenwickTree(int n) {
        this.n = n;
        this.bit = new int[n + 1];
    }

    public void pointUpdate(int idx, int delta) {
        for (int i = idx; i <= n; i += i & -i) bit[i] += delta;
    }

    public int prefixSum(int idx) {
        int sum = 0;
        for (int i = idx; i > 0; i -= i & -i) sum += bit[i];
        return sum;
    }

    public int rangeSum(int l, int r) {
        return prefixSum(r) - prefixSum(l - 1);
    }
}