package com.dsacademy.lab13.fenwicktree;

public class FenwickTree {

    private final int[] bit;
    private final int n;

    public FenwickTree(int n) {
        if (n < 0) throw new IllegalArgumentException("Size must be non-negative: " + n);
        this.n = n;
        this.bit = new int[n + 1];
    }

    public void add(int idx, int delta) {
        validateIndex(idx);
        int i = idx + 1;
        while (i <= n) {
            bit[i] += delta;
            i += i & -i;
        }
    }

    public int sum(int idx) {
        validateIndex(idx);
        int i = idx + 1;
        int result = 0;
        while (i > 0) {
            result += bit[i];
            i -= i & -i;
        }
        return result;
    }

    public int rangeSum(int l, int r) {
        if (l > r) throw new IllegalArgumentException("l must be <= r, got l=" + l + " r=" + r);
        validateIndex(l);
        validateIndex(r);
        return sum(r) - sum(l - 1);
    }

    public static FenwickTree fromArray(int[] arr) {
        FenwickTree ft = new FenwickTree(arr.length);
        for (int i = 0; i < arr.length; i++) {
            ft.add(i, arr[i]);
        }
        return ft;
    }

    private void validateIndex(int idx) {
        if (idx < 0 || idx >= n) {
            throw new IndexOutOfBoundsException("Index " + idx + " out of bounds for size " + n);
        }
    }

    public int size() { return n; }
}
