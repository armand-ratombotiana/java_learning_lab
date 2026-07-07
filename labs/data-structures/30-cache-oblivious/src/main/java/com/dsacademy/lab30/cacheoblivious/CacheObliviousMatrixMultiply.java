package com.dsacademy.lab30.cacheoblivious;

public class CacheObliviousMatrixMultiply {

    public static int[][] multiply(int[][] a, int[][] b, int n) {
        int[][] c = new int[n][n];
        multiplyRecursive(a, b, c, 0, 0, 0, 0, 0, 0, n);
        return c;
    }

    private static void multiplyRecursive(int[][] a, int[][] b, int[][] c,
                                           int aRow, int aCol,
                                           int bRow, int bCol,
                                           int cRow, int cCol,
                                           int n) {
        if (n <= 64) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int sum = 0;
                    for (int k = 0; k < n; k++) {
                        sum += a[aRow + i][aCol + k] * b[bRow + k][bCol + j];
                    }
                    c[cRow + i][cCol + j] += sum;
                }
            }
            return;
        }
        int half = n / 2;
        multiplyRecursive(a, b, c, aRow, aCol, bRow, bCol, cRow, cCol, half);
        multiplyRecursive(a, b, c, aRow, aCol + half, bRow + half, bCol, cRow, cCol, half);
        multiplyRecursive(a, b, c, aRow, aCol, bRow, bCol + half, cRow, cCol + half, half);
        multiplyRecursive(a, b, c, aRow, aCol + half, bRow + half, bCol + half, cRow, cCol + half, half);
        multiplyRecursive(a, b, c, aRow + half, aCol, bRow, bCol, cRow + half, cCol, half);
        multiplyRecursive(a, b, c, aRow + half, aCol + half, bRow + half, bCol, cRow + half, cCol, half);
        multiplyRecursive(a, b, c, aRow + half, aCol, bRow, bCol + half, cRow + half, cCol + half, half);
        multiplyRecursive(a, b, c, aRow + half, aCol + half, bRow + half, bCol + half, cRow + half, cCol + half, half);
    }

    public static int[][] multiplyNaive(int[][] a, int[][] b, int n) {
        int[][] c = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += a[i][k] * b[k][j];
                }
                c[i][j] = sum;
            }
        }
        return c;
    }
}
