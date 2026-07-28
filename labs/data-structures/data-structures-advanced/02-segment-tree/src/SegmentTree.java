package com.ds.advanced.lab02;

public class SegmentTree {
    private final int n;
    private final int[] tree;
    private final int[] lazy;

    public SegmentTree(int[] arr) {
        this.n = arr.length;
        tree = new int[4 * n];
        lazy = new int[4 * n];
        build(arr, 1, 0, n - 1);
    }

    private void build(int[] arr, int node, int l, int r) {
        if (l == r) { tree[node] = arr[l]; return; }
        int mid = (l + r) / 2;
        build(arr, node * 2, l, mid);
        build(arr, node * 2 + 1, mid + 1, r);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public void rangeUpdate(int ul, int ur, int val) {
        rangeUpdate(1, 0, n - 1, ul, ur, val);
    }

    private void rangeUpdate(int node, int l, int r, int ul, int ur, int val) {
        applyLazy(node, l, r);
        if (l > ur || r < ul) return;
        if (ul <= l && r <= ur) {
            tree[node] += (r - l + 1) * val;
            if (l != r) { lazy[node * 2] += val; lazy[node * 2 + 1] += val; }
            return;
        }
        int mid = (l + r) / 2;
        rangeUpdate(node * 2, l, mid, ul, ur, val);
        rangeUpdate(node * 2 + 1, mid + 1, r, ul, ur, val);
        tree[node] = tree[node * 2] + tree[node * 2 + 1];
    }

    public int rangeQuery(int ql, int qr) {
        return rangeQuery(1, 0, n - 1, ql, qr);
    }

    private int rangeQuery(int node, int l, int r, int ql, int qr) {
        applyLazy(node, l, r);
        if (l > qr || r < ql) return 0;
        if (ql <= l && r <= qr) return tree[node];
        int mid = (l + r) / 2;
        return rangeQuery(node * 2, l, mid, ql, qr)
             + rangeQuery(node * 2 + 1, mid + 1, r, ql, qr);
    }

    private void applyLazy(int node, int l, int r) {
        if (lazy[node] != 0) {
            tree[node] += (r - l + 1) * lazy[node];
            if (l != r) { lazy[node * 2] += lazy[node]; lazy[node * 2 + 1] += lazy[node]; }
            lazy[node] = 0;
        }
    }
}