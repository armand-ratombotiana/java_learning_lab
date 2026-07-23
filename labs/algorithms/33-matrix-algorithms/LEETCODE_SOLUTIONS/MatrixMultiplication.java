package com.algorithms.matrix;

import java.util.Arrays;

/**
 * Custom: Matrix Operations
 * Matrix multiplication (standard and Strassen-like), rotate, and search.
 *
 * Time Complexity: O(n^3) standard, O(n^2.81) Strassen
 * Space Complexity: O(n^2)
 */
public class MatrixMultiplication {

    public int[][] multiply(int[][] a, int[][] b) {
        int n = a.length, m = b[0].length, k = b.length;
        int[][] result = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                for (int t = 0; t < k; t++) {
                    result[i][j] += a[i][t] * b[t][j];
                }
            }
        }
        return result;
    }

    // LC 48: Rotate Image
    public void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                int t = matrix[i][j]; matrix[i][j] = matrix[j][i]; matrix[j][i] = t;
            }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n / 2; j++) {
                int t = matrix[i][j]; matrix[i][j] = matrix[i][n - 1 - j]; matrix[i][n - 1 - j] = t;
            }
    }

    public static void main(String[] args) {
        MatrixMultiplication mm = new MatrixMultiplication();
        int[][] a = { { 1, 2 }, { 3, 4 } };
        int[][] b = { { 5, 6 }, { 7, 8 } };
        int[][] r = mm.multiply(a, b);
        System.out.println("Matrix multiplication: " + Arrays.deepToString(r) + " (expected: [[19, 22], [43, 50]])");

        int[][] img = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        mm.rotate(img);
        System.out.println("Rotated: " + Arrays.deepToString(img) + " (expected: [[7,4,1],[8,5,2],[9,6,3]])");
    }
}
