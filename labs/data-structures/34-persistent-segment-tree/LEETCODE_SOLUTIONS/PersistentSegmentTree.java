package com.leetcode.persistent;

/**
 * Custom: Persistent Segment Tree
 * Stores multiple versions of a segment tree for range queries on any version.
 * Used for K-th smallest in range problems.
 *
 * Time Complexity: O(log n) per operation
 * Space Complexity: O(n log n)
 */
public class PersistentSegmentTree {

    public static class Node {
        int sum;
        Node left, right;
        Node(int sum) { this.sum = sum; }
    }

    private final Node[] roots;
    private final int n;
    private int version;

    public PersistentSegmentTree(int[] arr) {
        this.n = arr.length;
        this.roots = new Node[n + 1];
        this.version = 0;
        roots[0] = build(0, n - 1);
        for (int v : arr) {
            roots[version + 1] = update(roots[version], 0, n - 1, v, 1);
            version++;
        }
    }

    private Node build(int l, int r) {
        if (l == r) return new Node(0);
        int mid = l + (r - l) / 2;
        Node node = new Node(0);
        node.left = build(l, mid);
        node.right = build(mid + 1, r);
        return node;
    }

    private Node update(Node prev, int l, int r, int pos, int val) {
        Node node = new Node(prev.sum + val);
        if (l == r) return node;
        int mid = l + (r - l) / 2;
        if (pos <= mid) {
            node.left = update(prev.left, l, mid, pos, val);
            node.right = prev.right;
        } else {
            node.left = prev.left;
            node.right = update(prev.right, mid + 1, r, pos, val);
        }
        return node;
    }

    public int query(int v, int l, int r) {
        return query(roots[v], 0, n - 1, l, r);
    }

    private int query(Node node, int l, int r, int ql, int qr) {
        if (ql > r || qr < l) return 0;
        if (ql <= l && r <= qr) return node.sum;
        int mid = l + (r - l) / 2;
        return query(node.left, l, mid, ql, qr) + query(node.right, mid + 1, r, ql, qr);
    }

    public int kthSmallest(int l, int r, int k) {
        return kthSmallest(roots[l - 1], roots[r], 0, n - 1, k);
    }

    private int kthSmallest(Node left, Node right, int l, int r, int k) {
        if (l == r) return l;
        int mid = l + (r - l) / 2;
        int leftCount = (right.left != null ? right.left.sum : 0) - (left.left != null ? left.left.sum : 0);
        if (k <= leftCount) return kthSmallest(left.left, right.left, l, mid, k);
        return kthSmallest(left.right, right.right, mid + 1, r, k - leftCount);
    }

    public static void main(String[] args) {
        PersistentSegmentTree pst = new PersistentSegmentTree(new int[]{1, 5, 2, 6, 3, 7, 4});

        System.out.println("Version 0 sum [0,6]: " + pst.query(0, 0, 6) + " (expected: 1)");
        System.out.println("Version 1 sum [0,6]: " + pst.query(1, 0, 6) + " (expected: 2)");

        int k1 = pst.kthSmallest(1, 4, 2);
        System.out.println("K=2nd smallest in [1,4]: " + k1 + " (expected: 2)");

        int k2 = pst.kthSmallest(3, 6, 3);
        System.out.println("K=3rd smallest in [3,6]: " + k2);

        int k3 = pst.kthSmallest(2, 5, 2);
        System.out.println("K=2nd smallest in [2,5]: " + k3);
    }
}
