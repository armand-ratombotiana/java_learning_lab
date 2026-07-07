package com.algo.lab30;

public class Simplex {

    private static final double EPS = 1e-9;
    private int m, n;
    private double[][] tableau;
    private int[] basis;

    public Simplex(double[][] A, double[] b, double[] c) {
        m = b.length;
        n = c.length;
        tableau = new double[m + 1][n + 1];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, tableau[i], 0, n);
            tableau[i][n] = b[i];
        }
        for (int j = 0; j < n; j++) tableau[m][j] = -c[j];
        basis = new int[m];
        for (int i = 0; i < m; i++) basis[i] = n + i;
    }

    public double[] solve() {
        while (true) {
            int q = -1;
            for (int j = 0; j < n; j++) {
                if (tableau[m][j] < -EPS) {
                    q = j;
                    break;
                }
            }
            if (q == -1) break;

            int p = -1;
            for (int i = 0; i < m; i++) {
                if (tableau[i][q] > EPS) {
                    if (p == -1 || tableau[i][n] / tableau[i][q] < tableau[p][n] / tableau[p][q]) {
                        p = i;
                    }
                }
            }
            if (p == -1) return null;
            pivot(p, q);
        }
        double[] x = new double[n];
        for (int i = 0; i < m; i++) {
            if (basis[i] < n) x[basis[i]] = tableau[i][n];
        }
        return x;
    }

    private void pivot(int p, int q) {
        for (int j = 0; j <= n; j++) tableau[p][j] /= tableau[p][q];
        for (int i = 0; i <= m; i++) {
            if (i != p) {
                double factor = tableau[i][q];
                for (int j = 0; j <= n; j++) tableau[i][j] -= factor * tableau[p][j];
            }
        }
        basis[p] = q;
    }

    public double getOptimalValue() {
        return tableau[m][n];
    }
}
