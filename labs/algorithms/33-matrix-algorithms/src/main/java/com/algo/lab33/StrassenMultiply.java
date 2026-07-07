package com.algo.lab33;

public class StrassenMultiply {

    private static final int THRESHOLD = 64;

    public static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        if (n <= THRESHOLD) {
            for (int i = 0; i < n; i++)
                for (int k = 0; k < n; k++)
                    for (int j = 0; j < n; j++)
                        C[i][j] += A[i][k] * B[k][j];
            return C;
        }
        int h = n / 2;
        double[][] A11 = sub(A, 0, 0, h);
        double[][] A12 = sub(A, 0, h, h);
        double[][] A21 = sub(A, h, 0, h);
        double[][] A22 = sub(A, h, h, h);
        double[][] B11 = sub(B, 0, 0, h);
        double[][] B12 = sub(B, 0, h, h);
        double[][] B21 = sub(B, h, 0, h);
        double[][] B22 = sub(B, h, h, h);

        double[][] M1 = multiply(add(A11, A22), add(B11, B22));
        double[][] M2 = multiply(add(A21, A22), B11);
        double[][] M3 = multiply(A11, sub(B12, B22));
        double[][] M4 = multiply(A22, sub(B21, B11));
        double[][] M5 = multiply(add(A11, A12), B22);
        double[][] M6 = multiply(sub(A21, A11), add(B11, B12));
        double[][] M7 = multiply(sub(A12, A22), add(B21, B22));

        double[][] C11 = add(sub(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(sub(add(M1, M3), M2), M6);

        set(C, C11, 0, 0);
        set(C, C12, 0, h);
        set(C, C21, h, 0);
        set(C, C22, h, h);
        return C;
    }

    private static double[][] sub(double[][] M, int r, int c, int h) {
        double[][] res = new double[h][h];
        for (int i = 0; i < h; i++)
            System.arraycopy(M[r + i], c, res[i], 0, h);
        return res;
    }

    private static double[][] add(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] + B[i][j];
        return C;
    }

    private static double[][] sub(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                C[i][j] = A[i][j] - B[i][j];
        return C;
    }

    private static void set(double[][] M, double[][] block, int r, int c) {
        int h = block.length;
        for (int i = 0; i < h; i++)
            System.arraycopy(block[i], 0, M[r + i], c, h);
    }
}
