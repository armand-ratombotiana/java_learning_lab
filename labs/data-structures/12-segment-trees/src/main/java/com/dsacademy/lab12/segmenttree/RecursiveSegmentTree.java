package com.dsacademy.lab12.segmenttree;

public class RecursiveSegmentTree {

    private final int[] tree;
    private final int n;

    public RecursiveSegmentTree(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Array cannot be null");
        this.n = arr.length;
        this.tree = new int[4 * n];
        if (n > 0) {
            build(arr, 1, 0, n - 1);
        }
    }

    private void build(int[] arr, int node, int l, int r) {
        if (l == r) {
            tree[node] = arr[l];
        } else {
            int mid = (l + r) / 2;
            build(arr, node * 2, l, mid);
            build(arr, node * 2 + 1, mid + 1, r);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }
    }

    public int rangeSum(int ql, int qr) {
        if (n == 0) return 0;
        validateRange(ql, qr);
        return rangeSum(1, 0, n - 1, ql, qr);
    }

    private int rangeSum(int node, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) return tree[node];
        if (r < ql || l > qr) return 0;
        int mid = (l + r) / 2;
        return rangeSum(node * 2, l, mid, ql, qr) +
               rangeSum(node * 2 + 1, mid + 1, r, ql, qr);
    }

    public void update(int pos, int val) {
        if (n == 0) return;
        validateIndex(pos);
        update(1, 0, n - 1, pos, val);
    }

    private void update(int node, int l, int r, int pos, int val) {
        if (l == r) {
            tree[node] = val;
        } else {
            int mid = (l + r) / 2;
            if (pos <= mid) update(node * 2, l, mid, pos, val);
            else update(node * 2 + 1, mid + 1, r, pos, val);
            tree[node] = tree[node * 2] + tree[node * 2 + 1];
        }
    }

    private void validateRange(int l, int r) {
        if (l > r) throw new IllegalArgumentException("l must be <= r");
        if (l < 0 || r >= n) throw new IndexOutOfBoundsException("Range [" + l + ", " + r + "] out of bounds");
    }

    private void validateIndex(int pos) {
        if (pos < 0 || pos >= n) throw new IndexOutOfBoundsException("Index " + pos + " out of bounds");
    }

    public int size() { return n; }
}
