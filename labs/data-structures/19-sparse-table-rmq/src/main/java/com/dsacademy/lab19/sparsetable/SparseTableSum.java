package com.dsacademy.lab19.sparsetable;

public class SparseTableSum {
    private final int[][] st;
    private final int[] log;
    private final int n;

    public SparseTableSum(int[] arr) {
        if (arr == null) throw new IllegalArgumentException();
        this.n = arr.length;
        if (n == 0) { st = new int[0][0]; log = new int[0]; return; }
        int k = (int) (Math.log(n) / Math.log(2)) + 1;
        st = new int[n][k];
        log = new int[n + 1];
        for (int i = 0; i < n; i++) st[i][0] = arr[i];
        for (int j = 1; j < k; j++) {
            for (int i = 0; i + (1 << j) <= n; i++) {
                st[i][j] = st[i][j - 1] + st[i + (1 << (j - 1))][j - 1];
            }
        }
        for (int i = 2; i <= n; i++) log[i] = log[i / 2] + 1;
    }

    public int rangeSum(int l, int r) {
        if (n == 0) throw new IllegalStateException("Empty table");
        if (l > r) return 0;
        int length = r - l + 1;
        int result = 0;
        int pos = l;
        for (int j = st[0].length - 1; j >= 0; j--) {
            if ((length & (1 << j)) != 0) {
                result += st[pos][j];
                pos += (1 << j);
            }
        }
        return result;
    }
}
