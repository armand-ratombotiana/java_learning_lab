package com.algo.lab33;

public class MatrixDecomposition {

    private static final double EPS = 1e-9;

    public static class LUResult {
        public final double[][] L;
        public final double[][] U;
        public final int[] perm;
        public final int swaps;

        LUResult(double[][] L, double[][] U, int[] perm, int swaps) {
            this.L = L;
            this.U = U;
            this.perm = perm;
            this.swaps = swaps;
        }
    }

    public static LUResult lu(double[][] A) {
        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];
        int[] perm = new int[n];
        int swaps = 0;
        for (int i = 0; i < n; i++) perm[i] = i;

        for (int col = 0; col < n; col++) {
            int pivot = col;
            for (int row = col + 1; row < n; row++) {
                if (Math.abs(A[row][col]) > Math.abs(A[pivot][col])) pivot = row;
            }
            if (pivot != col) {
                int tmp = perm[col]; perm[col] = perm[pivot]; perm[pivot] = tmp;
                double[] t = A[col]; A[col] = A[pivot]; A[pivot] = t;
                swaps++;
            }
            if (Math.abs(A[col][col]) < EPS) continue;

            for (int row = col + 1; row < n; row++) {
                double factor = A[row][col] / A[col][col];
                L[row][col] = factor;
                for (int j = col; j < n; j++) A[row][j] -= factor * A[col][j];
            }
        }

        for (int i = 0; i < n; i++) {
            L[i][i] = 1;
            for (int j = i; j < n; j++) U[i][j] = A[i][j];
            for (int j = 0; j < i; j++) U[i][j] = 0;
            for (int j = i + 1; j < n; j++) L[j][i] = A[j][i];
            for (int j = 0; j <= i; j++) L[i][j] = i == j ? 1 : A[i][j];
        }

        return new LUResult(L, U, perm, swaps);
    }

    public static double determinant(double[][] A) {
        LUResult lu = lu(A);
        double det = 1;
        for (int i = 0; i < A.length; i++) det *= lu.U[i][i];
        return lu.swaps % 2 == 0 ? det : -det;
    }

    public static double[][] inverse(double[][] A) {
        int n = A.length;
        LUResult lu = lu(A);
        double[][] inv = new double[n][n];
        for (int col = 0; col < n; col++) {
            double[] e = new double[n];
            e[col] = 1;
            double[] y = new double[n];
            for (int i = 0; i < n; i++) {
                double sum = e[lu.perm[i]];
                for (int j = 0; j < i; j++) sum -= lu.L[i][j] * y[j];
                y[i] = sum;
            }
            double[] x = new double[n];
            for (int i = n - 1; i >= 0; i--) {
                double sum = y[i];
                for (int j = i + 1; j < n; j++) sum -= lu.U[i][j] * x[j];
                x[i] = sum / lu.U[i][i];
            }
            for (int i = 0; i < n; i++) inv[i][col] = x[i];
        }
        return inv;
    }
}
