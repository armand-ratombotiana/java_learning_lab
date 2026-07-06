package com.dsacademy.lab12.segmenttree;

public class LazySegmentTree {

    private final int[] tree;
    private final int[] lazy;
    private final int n;

    public LazySegmentTree(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Array cannot be null");
        this.n = arr.length;
        this.tree = new int[4 * n];
        this.lazy = new int[4 * n];
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

    public void rangeAdd(int ql, int qr, int val) {
        if (n == 0) return;
        validateRange(ql, qr);
        rangeAdd(1, 0, n - 1, ql, qr, val);
    }

    private void rangeAdd(int node, int l, int r, int ql, int qr, int val) {
        if (ql <= l && r <= qr) {
            tree[node] += (r - l + 1) * val;
            lazy[node] += val;
            return;
        }
        push(node, l, r);
        int mid = (l + r) / 2;
        if (ql <= mid) rangeAdd(node * 2, l, mid, ql, qr, val);
        if (qr > mid) rangeAdd(node * 2 + 1, mid + 1, r, ql, qr, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public int rangeSum(int ql, int qr) {
        if (n == 0) return 0;
        validateRange(ql, qr);
        return rangeSum(1, 0, n - 1, ql, qr);
    }

    private int rangeSum(int node, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) return tree[node];
        if (r < ql || l > qr) return 0;
        push(node, l, r);
        int mid = (l + r) / 2;
        return rangeSum(node * 2, l, mid, ql, qr) +
               rangeSum(node * 2 + 1, mid + 1, r, ql, qr);
    }

    private void push(int node, int l, int r) {
        if (lazy[node] != 0) {
            int mid = (l + r) / 2;
            tree[node * 2] += (mid - l + 1) * lazy[node];
            tree[node * 2 + 1] += (r - mid) * lazy[node];
            lazy[node * 2] += lazy[node];
            lazy[node * 2 + 1] += lazy[node];
            lazy[node] = 0;
        }
    }

    public void pointUpdate(int pos, int val) {
        if (n == 0) return;
        validateIndex(pos);
        pointUpdate(1, 0, n - 1, pos, val);
    }

    private void pointUpdate(int node, int l, int r, int pos, int val) {
        if (l == r) {
            tree[node] = val;
            return;
        }
        push(node, l, r);
        int mid = (l + r) / 2;
        if (pos <= mid) pointUpdate(node * 2, l, mid, pos, val);
        else pointUpdate(node * 2 + 1, mid + 1, r, pos, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
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
