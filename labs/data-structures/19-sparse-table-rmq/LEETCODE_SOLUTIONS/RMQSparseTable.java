package com.leetcode.sparse;

/**
 * Custom: Range Minimum Query using Sparse Table
 * Preprocess array for O(1) range minimum queries.
 *
 * Time Complexity: O(n log n) preprocessing, O(1) query
 * Space Complexity: O(n log n)
 */
public class RMQSparseTable {

    private final int[][] st;
    private final int[] log;

    public RMQSparseTable(int[] arr) {
        int n = arr.length;
        int k = (int) (Math.log(n) / Math.log(2)) + 1;
        st = new int[n][k];
        log = new int[n + 1];

        for (int i = 2; i <= n; i++) log[i] = log[i / 2] + 1;
        for (int i = 0; i < n; i++) st[i][0] = arr[i];

        for (int j = 1; j < k; j++) {
            for (int i = 0; i + (1 << j) <= n; i++) {
                st[i][j] = Math.min(st[i][j - 1], st[i + (1 << (j - 1))][j - 1]);
            }
        }
    }

    public int query(int l, int r) {
        int j = log[r - l + 1];
        return Math.min(st[l][j], st[r - (1 << j) + 1][j]);
    }

    public static void main(String[] args) {
        RMQSparseTable rmq = new RMQSparseTable(new int[] { 4, 6, 1, 5, 7, 3 });
        System.out.println("Min [1,3]: " + rmq.query(1, 3) + " (expected: 1)");
        System.out.println("Min [0,5]: " + rmq.query(0, 5) + " (expected: 1)");
        System.out.println("Min [2,2]: " + rmq.query(2, 2) + " (expected: 1)");
        System.out.println("Min [4,5]: " + rmq.query(4, 5) + " (expected: 3)");
    }
}
