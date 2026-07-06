package com.dsacademy.lab12.segmenttree;

public class SegmentTreeMinMax {

    private final int[] treeMin;
    private final int[] treeMax;
    private final int n;

    public SegmentTreeMinMax(int[] arr) {
        if (arr == null) throw new IllegalArgumentException("Array cannot be null");
        this.n = arr.length;
        this.treeMin = new int[4 * n];
        this.treeMax = new int[4 * n];
        if (n > 0) {
            build(arr, 1, 0, n - 1);
        }
    }

    private void build(int[] arr, int node, int l, int r) {
        if (l == r) {
            treeMin[node] = arr[l];
            treeMax[node] = arr[l];
        } else {
            int mid = (l + r) / 2;
            build(arr, node * 2, l, mid);
            build(arr, node * 2 + 1, mid + 1, r);
            treeMin[node] = Math.min(treeMin[node * 2], treeMin[node * 2 + 1]);
            treeMax[node] = Math.max(treeMax[node * 2], treeMax[node * 2 + 1]);
        }
    }

    public int rangeMin(int ql, int qr) {
        if (n == 0) throw new IllegalStateException("Empty tree");
        validateRange(ql, qr);
        return rangeMin(1, 0, n - 1, ql, qr);
    }

    private int rangeMin(int node, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) return treeMin[node];
        if (r < ql || l > qr) return Integer.MAX_VALUE;
        int mid = (l + r) / 2;
        return Math.min(rangeMin(node * 2, l, mid, ql, qr),
                        rangeMin(node * 2 + 1, mid + 1, r, ql, qr));
    }

    public int rangeMax(int ql, int qr) {
        if (n == 0) throw new IllegalStateException("Empty tree");
        validateRange(ql, qr);
        return rangeMax(1, 0, n - 1, ql, qr);
    }

    private int rangeMax(int node, int l, int r, int ql, int qr) {
        if (ql <= l && r <= qr) return treeMax[node];
        if (r < ql || l > qr) return Integer.MIN_VALUE;
        int mid = (l + r) / 2;
        return Math.max(rangeMax(node * 2, l, mid, ql, qr),
                        rangeMax(node * 2 + 1, mid + 1, r, ql, qr));
    }

    public void update(int pos, int val) {
        if (n == 0) return;
        validateIndex(pos);
        update(1, 0, n - 1, pos, val);
    }

    private void update(int node, int l, int r, int pos, int val) {
        if (l == r) {
            treeMin[node] = val;
            treeMax[node] = val;
        } else {
            int mid = (l + r) / 2;
            if (pos <= mid) update(node * 2, l, mid, pos, val);
            else update(node * 2 + 1, mid + 1, r, pos, val);
            treeMin[node] = Math.min(treeMin[node * 2], treeMin[node * 2 + 1]);
            treeMax[node] = Math.max(treeMax[node * 2], treeMax[node * 2 + 1]);
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
