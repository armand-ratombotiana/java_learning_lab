package com.ml.lab07;

import java.util.*;

/**
 * K-Means Clustering — Lloyd's algorithm with inertia computation.
 * <p>
 * Demonstrates centroid initialization, assignment, update,
 * and elbow-method analysis on synthetic 2D data.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Euclidean distance
    // ──────────────────────────────────────────────

    public static double dist(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += (a[i] - b[i]) * (a[i] - b[i]);
        return Math.sqrt(s);
    }

    // ──────────────────────────────────────────────
    // K-Means
    // ──────────────────────────────────────────────

    public static int[] kMeans(double[][] data, int K, int maxIter) {
        int n = data.length, dim = data[0].length;
        double[][] centroids = new double[K][dim];
        Random rng = new Random(42);
        for (int k = 0; k < K; k++) centroids[k] = data[rng.nextInt(n)].clone();

        int[] labels = new int[n];
        for (int iter = 0; iter < maxIter; iter++) {
            // Assign
            for (int i = 0; i < n; i++) {
                int best = 0;
                double bestDist = dist(data[i], centroids[0]);
                for (int k = 1; k < K; k++) {
                    double d = dist(data[i], centroids[k]);
                    if (d < bestDist) { bestDist = d; best = k; }
                }
                labels[i] = best;
            }
            // Update
            double[][] sums = new double[K][dim];
            int[] counts = new int[K];
            for (int i = 0; i < n; i++) {
                int c = labels[i];
                for (int j = 0; j < dim; j++) sums[c][j] += data[i][j];
                counts[c]++;
            }
            for (int k = 0; k < K; k++) {
                if (counts[k] == 0) continue;
                for (int j = 0; j < dim; j++) centroids[k][j] = sums[k][j] / counts[k];
            }
        }
        return labels;
    }

    // ──────────────────────────────────────────────
    // Inertia (WCSS)
    // ──────────────────────────────────────────────

    public static double inertia(double[][] data, int[] labels, int K) {
        int dim = data[0].length;
        double[][] centroids = new double[K][dim];
        int[] counts = new int[K];
        for (int i = 0; i < data.length; i++) {
            int c = labels[i];
            for (int j = 0; j < dim; j++) centroids[c][j] += data[i][j];
            counts[c]++;
        }
        for (int k = 0; k < K; k++) {
            if (counts[k] > 0)
                for (int j = 0; j < dim; j++) centroids[k][j] /= counts[k];
        }
        double inert = 0;
        for (int i = 0; i < data.length; i++) {
            int c = labels[i];
            inert += dist(data[i], centroids[c]) * dist(data[i], centroids[c]);
        }
        return inert;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== K-Means Clustering Lab ===");

        // Synthetic 2D blobs
        double[][] data = {
            {1.0, 1.0}, {1.2, 1.1}, {0.8, 0.9}, {1.1, 0.8},
            {5.0, 5.0}, {5.2, 5.1}, {4.8, 4.9}, {5.1, 5.2},
            {9.0, 1.0}, {9.2, 0.8}, {8.8, 1.2}, {9.1, 0.9}
        };

        for (int K = 2; K <= 5; K++) {
            int[] labels = kMeans(data, K, 100);
            double inert = inertia(data, labels, K);
            System.out.printf("K=%d  Inertia=%.4f  Labels=%s%n",
                    K, inert, Arrays.toString(labels));
        }
    }
}
