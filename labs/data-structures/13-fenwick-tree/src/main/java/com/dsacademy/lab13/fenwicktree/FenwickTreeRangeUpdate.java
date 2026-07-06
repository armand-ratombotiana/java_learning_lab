package com.dsacademy.lab13.fenwicktree;

public class FenwickTreeRangeUpdate {

    private final int[] bit;
    private final int n;

    public FenwickTreeRangeUpdate(int n) {
        if (n < 0) throw new IllegalArgumentException("Size must be non-negative: " + n);
        this.n = n;
        this.bit = new int[n + 1];
    }

    public void rangeAdd(int l, int r, int val) {
        add(l, val);
        add(r + 1, -val);
    }

    private void add(int idx, int delta) {
        int i = idx + 1;
        while (i <= n) {
            bit[i] += delta;
            i += i & -i;
        }
    }

    public int pointQuery(int idx) {
        validateIndex(idx);
        int i = idx + 1;
        int result = 0;
        while (i > 0) {
            result += bit[i];
            i -= i & -i;
        }
        return result;
    }

    private void validateIndex(int idx) {
        if (idx < 0 || idx >= n) {
            throw new IndexOutOfBoundsException("Index " + idx + " out of bounds for size " + n);
        }
    }

    public int size() { return n; }
}
