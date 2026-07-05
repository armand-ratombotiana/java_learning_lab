package com.ds09;

/*
 * FenwickTree (Binary Indexed Tree) - Range sum query and point update.
 *
 * Time Complexity:
 * - update: O(log n)
 * - query: O(log n)
 *
 * Space Complexity: O(n)
 */
public class FenwickTree {

    private final int[] tree;
    private final int n;

    public FenwickTree(int size) {
        this.n = size;
        this.tree = new int[n + 1];
    }

    public FenwickTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[n + 1];
        for (int i = 0; i < n; i++) {
            update(i, arr[i]);
        }
    }

    public void update(int index, int delta) {
        int i = index + 1;
        while (i <= n) {
            tree[i] += delta;
            i += i & -i;
        }
    }

    public int query(int index) {
        int sum = 0;
        int i = index + 1;
        while (i > 0) {
            sum += tree[i];
            i -= i & -i;
        }
        return sum;
    }

    public int rangeQuery(int l, int r) {
        if (l > r) return 0;
        return query(r) - (l > 0 ? query(l - 1) : 0);
    }

    public int size() {
        return n;
    }
}
