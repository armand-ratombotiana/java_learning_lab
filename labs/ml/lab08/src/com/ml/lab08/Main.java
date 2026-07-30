package com.ml.lab08;

import java.util.Arrays;
import java.util.Random;

/**
 * Principal Component Analysis — eigenvalue decomposition of covariance matrix.
 * <p>
 * Demonstrates dimensionality reduction by projecting 4D synthetic data
 * onto its top 2 principal components.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Center the data
    // ──────────────────────────────────────────────

    public static double[][] center(double[][] X) {
        int n = X.length, m = X[0].length;
        double[][] c = new double[n][m];
        double[] mean = new double[m];
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) mean[j] += X[i][j];
            mean[j] /= n;
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                c[i][j] = X[i][j] - mean[j];
        return c;
    }

    // ──────────────────────────────────────────────
    // Covariance matrix
    // ──────────────────────────────────────────────

    public static double[][] covariances(double[][] Xc) {
        int n = Xc.length, m = Xc[0].length;
        double[][] cov = new double[m][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j <= i; j++) {
                double s = 0;
                for (int k = 0; k < n; k++) s += Xc[k][i] * Xc[k][j];
                cov[i][j] = s / (n - 1);
                cov[j][i] = s / (n - 1);
            }
        return cov;
    }

    // ──────────────────────────────────────────────
    // Power iteration for dominant eigenvector
    // ──────────────────────────────────────────────

    public static double[] powerIterate(double[][] A, int iters) {
        int n = A.length;
        double[] v = new double[n];
        Random rng = new Random(42);
        for (int i = 0; i < n; i++) v[i] = rng.nextDouble();
        for (int iter = 0; iter < iters; iter++) {
            double[] Av = new double[n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    Av[i] += A[i][j] * v[j];
            double norm = 0;
            for (double x : Av) norm += x * x;
            norm = Math.sqrt(norm);
            if (norm < 1e-12) break;
            for (int i = 0; i < n; i++) v[i] = Av[i] / norm;
        }
        return v;
    }

    // ──────────────────────────────────────────────
    // Deflate matrix
    // ──────────────────────────────────────────────

    public static double[][] deflate(double[][] A, double[] eigenvec, double eigenval) {
        int n = A.length;
        double[][] R = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = A[i][j] - eigenval * eigenvec[i] * eigenvec[j];
        return R;
    }

    // ──────────────────────────────────────────────
    // Project
    // ──────────────────────────────────────────────

    public static double[][] project(double[][] Xc, double[][] components) {
        int n = Xc.length, k = components.length;
        double[][] proj = new double[n][k];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++)
                for (int d = 0; d < Xc[0].length; d++)
                    proj[i][j] += Xc[i][d] * components[j][d];
        return proj;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Principal Component Analysis Lab ===");

        // Synthetic 4D data
        double[][] X = {
            {2.5, 2.4, 3.1, 1.2},
            {0.5, 0.7, 1.0, 0.8},
            {2.2, 2.9, 2.8, 1.5},
            {1.9, 2.2, 3.0, 1.1},
            {3.1, 3.0, 3.5, 1.8},
            {2.3, 2.7, 3.2, 1.4},
            {2.0, 1.6, 2.4, 0.9},
            {1.0, 1.1, 1.8, 0.7},
            {1.5, 1.6, 2.2, 1.0},
            {1.1, 0.9, 1.5, 0.6}
        };

        double[][] Xc = center(X);
        double[][] cov = covariances(Xc);

        // Extract top 2 eigenvectors via power iteration + deflation
        double[][] components = new double[2][X[0].length];
        double[][] A = cov;
        for (int k = 0; k < 2; k++) {
            double[] ev = powerIterate(A, 1000);
            components[k] = ev;
            // approximate eigenvalue
            double lambda = 0;
            for (int i = 0; i < ev.length; i++) {
                double rowDot = 0;
                for (int j = 0; j < ev.length; j++) rowDot += A[i][j] * ev[j];
                lambda += ev[i] * rowDot;
            }
            A = deflate(A, ev, lambda);
        }

        double[][] proj = project(Xc, components);
        System.out.println("Projected data (10 × 2):");
        for (double[] row : proj) {
            System.out.printf("  [%.4f, %.4f]%n", row[0], row[1]);
        }
        System.out.printf("Shape: %d × %d → %d × %d%n",
                X.length, X[0].length, proj.length, proj[0].length);
    }
}
