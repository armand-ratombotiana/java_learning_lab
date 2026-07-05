package com.ds09;

/*
 * SegmentTree - Segment tree for range sum queries and point updates.
 *
 * Time Complexity:
 * - build: O(n)
 * - query: O(log n)
 * - update: O(log n)
 *
 * Space Complexity: O(n)
 */
public class SegmentTree {

    private final int[] tree;
    private final int n;

    public SegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[4 * n];
        build(arr, 0, 0, n - 1);
    }

    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start];
        } else {
            int mid = start + (end - start) / 2;
            int left = node * 2 + 1;
            int right = node * 2 + 2;
            build(arr, left, start, mid);
            build(arr, right, mid + 1, end);
            tree[node] = tree[left] + tree[right];
        }
    }

    public int query(int l, int r) {
        return query(0, 0, n - 1, l, r);
    }

    private int query(int node, int start, int end, int l, int r) {
        if (r < start || l > end) return 0;
        if (l <= start && end <= r) return tree[node];
        int mid = start + (end - start) / 2;
        int left = node * 2 + 1;
        int right = node * 2 + 2;
        return query(left, start, mid, l, r) + query(right, mid + 1, end, l, r);
    }

    public void update(int index, int value) {
        update(0, 0, n - 1, index, value);
    }

    private void update(int node, int start, int end, int index, int value) {
        if (start == end) {
            tree[node] = value;
        } else {
            int mid = start + (end - start) / 2;
            int left = node * 2 + 1;
            int right = node * 2 + 2;
            if (index <= mid) {
                update(left, start, mid, index, value);
            } else {
                update(right, mid + 1, end, index, value);
            }
            tree[node] = tree[left] + tree[right];
        }
    }

    public int getTreeSize() {
        return tree.length;
    }
}
