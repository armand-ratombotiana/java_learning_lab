package com.algo.lab20;

import java.util.random.RandomGenerator;

/**
 * Freivalds' algorithm for checking matrix multiplication.
 * Probabilistically verifies if A * B = C using random vectors.
 * Time: O(k * n^2) for k iterations, Space: O(n)
 */
public class FreivaldsChecker {

    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private FreivaldsChecker() {}

    public static boolean check(int[][] A, int[][] B, int[][] C, int iterations) {
        int n = A.length;
        if (n == 0) return true;
        for (int iter = 0; iter < iterations; iter++) {
            int[] r = new int[n];
            for (int i = 0; i < n; i++) {
                r[i] = RNG.nextInt(2);
            }
            int[] Br = multiplyMatrixVector(B, r);
            int[] ABr = multiplyMatrixVector(A, Br);
            int[] Cr = multiplyMatrixVector(C, r);
            for (int i = 0; i < n; i++) {
                if (ABr[i] != Cr[i]) return false;
            }
        }
        return true;
    }

    private static int[] multiplyMatrixVector(int[][] matrix, int[] vector) {
        int n = matrix.length;
        int[] result = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i] += matrix[i][j] * vector[j];
            }
        }
        return result;
    }
}
