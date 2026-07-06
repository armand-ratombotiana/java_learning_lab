package com.dsacademy.lab19.sparsetable;

public class DisjointSparseTable {
    private final int[][] st;
    private final int[] log;
    private final int n;

    public DisjointSparseTable(int[] arr) {
        if (arr == null) throw new IllegalArgumentException();
        this.n = arr.length;
        if (n == 0) { st = new int[0][0]; log = new int[0]; return; }
        int k = 32 - Integer.numberOfLeadingZeros(n - 1);
        st = new int[k][n];
        log = new int[n + 1];
        for (int i = 0; i < n; i++) st[0][i] = arr[i];
        for (int level = 1; level < k; level++) {
            int block = 1 << level;
            int half = block >> 1;
            for (int start = 0; start < n; start += block) {
                int mid = Math.min(start + half, n);
                int end = Math.min(start + block, n);
                if (mid > 0) st[level][mid - 1] = arr[mid - 1];
                for (int i = mid - 2; i >= start; i--) st[level][i] = arr[i] + st[level][i + 1];
                if (mid < n) st[level][mid] = arr[mid];
                for (int i = mid + 1; i < end; i++) st[level][i] = st[level][i - 1] + arr[i];
            }
        }
        for (int i = 2; i <= n; i++) log[i] = log[i / 2] + 1;
    }

    public int rangeSum(int l, int r) {
        if (n == 0) throw new IllegalStateException("Empty table");
        if (l == r) return st[0][l];
        int level = log[l ^ r];
        return st[level][l] + st[level][r];
    }
}
