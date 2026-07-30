package com.ml.lab05;

import java.util.*;

/**
 * K-Nearest Neighbors — classification with Euclidean distance.
 * <p>
 * Supports both simple majority voting and distance-weighted voting.
 * Demonstrates K selection on synthetic 2D data.
 */
public class Main {

    // ──────────────────────────────────────────────
    // Distance
    // ──────────────────────────────────────────────

    public static double euclidean(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) s += (a[i] - b[i]) * (a[i] - b[i]);
        return Math.sqrt(s);
    }

    // ──────────────────────────────────────────────
    // KNN Predict
    // ──────────────────────────────────────────────

    public static int predict(double[][] trainX, int[] trainY,
                              double[] testX, int k, boolean weighted) {
        int n = trainX.length;
        List<Neighbor> neighbors = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            neighbors.add(new Neighbor(trainY[i], euclidean(trainX[i], testX)));
        }
        neighbors.sort(Comparator.comparingDouble(a -> a.dist));
        List<Neighbor> topK = neighbors.subList(0, Math.min(k, n));

        Map<Integer, Double> votes = new HashMap<>();
        for (Neighbor nb : topK) {
            double w = weighted ? (nb.dist == 0 ? 1e6 : 1.0 / nb.dist) : 1.0;
            votes.merge(nb.label, w, Double::sum);
        }

        return Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    static class Neighbor {
        int label;
        double dist;
        Neighbor(int l, double d) { label = l; dist = d; }
    }

    // ──────────────────────────────────────────────
    // Accuracy
    // ──────────────────────────────────────────────

    public static double accuracy(double[][] trainX, int[] trainY,
                                  double[][] testX, int[] testY, int k, boolean weighted) {
        int ok = 0;
        for (int i = 0; i < testX.length; i++) {
            if (predict(trainX, trainY, testX[i], k, weighted) == testY[i]) ok++;
        }
        return (double) ok / testX.length;
    }

    // ──────────────────────────────────────────────
    // Main — test cases
    // ──────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== K-Nearest Neighbors Lab ===");

        double[][] trainX = {
            {1.0, 1.0}, {1.5, 2.0}, {2.0, 1.5},
            {5.0, 5.0}, {5.5, 6.0}, {6.0, 5.0}
        };
        int[] trainY = {0, 0, 0, 1, 1, 1};

        double[][] testX = {
            {1.2, 1.8}, {5.2, 5.5}
        };
        int[] testY = {0, 1};

        for (int k : new int[]{1, 3, 5}) {
            double acc = accuracy(trainX, trainY, testX, testY, k, false);
            double wacc = accuracy(trainX, trainY, testX, testY, k, true);
            System.out.printf("K=%d  Accuracy=%.2f  Weighted=%.2f%n", k, acc, wacc);
        }

        double[] point = {3.0, 3.0};
        int k3 = predict(trainX, trainY, point, 3, false);
        int k3w = predict(trainX, trainY, point, 3, true);
        System.out.printf("Point (3,3) → K=3 vote=%d, weighted=%d%n", k3, k3w);
    }
}
