package com.leetcode.cacheoblivious;

/**
 * Custom: Cache-Oblivious Matrix Transpose
 * Recursive block decomposition for optimal cache performance.
 *
 * Time Complexity: O(n^2)
 * Space Complexity: O(log n) for recursion
 */
public class CacheObliviousMatrix {

    public void transpose(int[][] matrix, int n) {
        transposeRec(matrix, 0, 0, n, n);
    }

    private void transposeRec(int[][] m, int r, int c, int h, int w) {
        if (h <= 1 && w <= 1) return;
        if (h <= 1 || w <= 1) {
            if (h == 1 && w == 1) return;
            if (h == 1) {
                for (int j = c; j < c + w; j++) { int t = m[r][j]; m[r][j] = m[j][r]; m[j][r] = t; }
            } else {
                for (int i = r; i < r + h; i++) { int t = m[i][c]; m[i][c] = m[c][i]; m[c][i] = t; }
            }
            return;
        }
        int hh = h / 2, hw = w / 2;
        transposeRec(m, r, c, hh, hw);
        transposeRec(m, r, c + hw, hh, w - hw);
        transposeRec(m, r + hh, c, h - hh, hw);
        transposeRec(m, r + hh, c + hw, h - hh, w - hw);
        for (int i = 0; i < hh; i++) {
            for (int j = 0; j < hw; j++) {
                int t = m[r + i][c + hw + j];
                m[r + i][c + hw + j] = m[r + hh + i][c + j];
                m[r + hh + i][c + j] = t;
            }
        }
    }

    public static void main(String[] args) {
        CacheObliviousMatrix com = new CacheObliviousMatrix();
        int n = 4;
        int[][] m = new int[n][n];
        int val = 1;
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) m[i][j] = val++;

        System.out.println("Original matrix:");
        printMatrix(m);
        com.transpose(m, n);
        System.out.println("Transposed matrix:");
        printMatrix(m);
    }

    private static void printMatrix(int[][] m) {
        for (int[] row : m) {
            for (int v : row) System.out.print(v + " ");
            System.out.println();
        }
    }
}
