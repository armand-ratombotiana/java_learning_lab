package com.dsacademy.lab12.segmenttree;

public class IterativeSegmentTree {

    private final int[] tree;
    private final int size;
    private final int n;

    public IterativeSegmentTree(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Array cannot be null");
        this.n = arr.length;
        this.size = 1;
        while (this.size < n) {
            this.size <<= 1;
        }
        this.tree = new int[2 * size];
        for (int i = 0; i < n; i++) {
            tree[size + i] = arr[i];
        }
        for (int i = size - 1; i > 0; i--) {
            tree[i] = tree[2 * i] + tree[2 * i + 1];
        }
    }

    public int rangeSum(int l, int r) {
        if (n == 0) return 0;
        validateRange(l, r);
        l += size;
        r += size;
        int sum = 0;
        while (l <= r) {
            if ((l & 1) == 1) sum += tree[l++];
            if ((r & 1) == 0) sum += tree[r--];
            l /= 2;
            r /= 2;
        }
        return sum;
    }

    public void update(int pos, int val) {
        if (n == 0) return;
        validateIndex(pos);
        int i = size + pos;
        tree[i] = val;
        i /= 2;
        while (i > 0) {
            tree[i] = tree[2 * i] + tree[2 * i + 1];
            i /= 2;
        }
    }

    private void validateRange(int l, int r) {
        if (l > r) throw new IllegalArgumentException("l must be <= r");
        if (l < 0 || r >= n) throw new IndexOutOfBoundsException();
    }

    private void validateIndex(int pos) {
        if (pos < 0 || pos >= n) throw new IndexOutOfBoundsException();
    }

    public int size() { return n; }
}
