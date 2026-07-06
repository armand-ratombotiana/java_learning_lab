package com.dsacademy.lab13.fenwicktree;

public class FenwickTreeRangeQuery {

    private final long[] bit;
    private final int n;

    public FenwickTreeRangeQuery(int n) {
        if (n < 0) throw new IllegalArgumentException("Size must be non-negative: " + n);
        this.n = n;
        this.bit = new long[n + 1];
    }

    public void add(int idx, long delta) {
        validateIndex(idx);
        int i = idx + 1;
        while (i <= n) {
            bit[i] += delta;
            i += i & -i;
        }
    }

    public long sum(int idx) {
        validateIndex(idx);
        int i = idx + 1;
        long result = 0;
        while (i > 0) {
            result += bit[i];
            i -= i & -i;
        }
        return result;
    }

    public long rangeSum(int l, int r) {
        if (l > r) throw new IllegalArgumentException("l must be <= r");
        validateIndex(l);
        validateIndex(r);
        return sum(r) - sum(l - 1);
    }

    public static FenwickTreeRangeQuery fromArray(int[] arr) {
        FenwickTreeRangeQuery ft = new FenwickTreeRangeQuery(arr.length);
        for (int i = 0; i < arr.length; i++) {
            ft.add(i, arr[i]);
        }
        return ft;
    }

    public static FenwickTreeRangeQuery fromLongArray(long[] arr) {
        FenwickTreeRangeQuery ft = new FenwickTreeRangeQuery(arr.length);
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
