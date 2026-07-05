package com.ai07;

import java.util.*;

public class KMeans {
    private int k;
    private int maxIterations;
    private double[][] centroids;
    private int[] assignments;

    public KMeans(int k, int maxIterations) {
        this.k = k;
        this.maxIterations = maxIterations;
    }

    public void fit(double[][] data) {
        int n = data.length;
        int dims = data[0].length;
        Random rng = new Random(42);
        centroids = new double[k][dims];
        for (int i = 0; i < k; i++)
            centroids[i] = data[rng.nextInt(n)];

        assignments = new int[n];
        boolean changed;
        for (int iter = 0; iter < maxIterations; iter++) {
            changed = false;
            for (int i = 0; i < n; i++) {
                int nearest = nearestCentroid(data[i]);
                if (nearest != assignments[i]) {
                    assignments[i] = nearest;
                    changed = true;
                }
            }
            if (!changed) break;
            double[][] newCentroids = new double[k][dims];
            int[] counts = new int[k];
            for (int i = 0; i < n; i++) {
                for (int d = 0; d < dims; d++)
                    newCentroids[assignments[i]][d] += data[i][d];
                counts[assignments[i]]++;
            }
            for (int i = 0; i < k; i++)
                if (counts[i] > 0)
                    for (int d = 0; d < dims; d++)
                        centroids[i][d] = newCentroids[i][d] / counts[i];
        }
    }

    private int nearestCentroid(double[] point) {
        int best = -1;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < k; i++) {
            double dist = 0;
            for (int d = 0; d < point.length; d++) {
                double diff = point[d] - centroids[i][d];
                dist += diff * diff;
            }
            if (dist < bestDist) {
                bestDist = dist;
                best = i;
            }
        }
        return best;
    }

    public int predict(double[] point) {
        return nearestCentroid(point);
    }

    public double[][] getCentroids() { return centroids; }
    public int[] getAssignments() { return assignments; }

    public double inertia(double[][] data) {
        double sum = 0;
        for (int i = 0; i < data.length; i++) {
            int c = assignments[i];
            for (int d = 0; d < data[0].length; d++) {
                double diff = data[i][d] - centroids[c][d];
                sum += diff * diff;
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        System.out.println("=== K-Means Clustering Demo ===");
        double[][] data = {
            {1.0, 1.0}, {1.5, 2.0}, {1.2, 1.8},
            {5.0, 5.0}, {5.5, 5.5}, {5.2, 4.8},
            {9.0, 9.0}, {9.5, 9.0}, {9.2, 9.3}
        };
        KMeans km = new KMeans(3, 100);
        km.fit(data);
        System.out.println("Centroids:");
        for (double[] c : km.getCentroids())
            System.out.println("  " + Arrays.toString(c));
        System.out.println("Assignments: " + Arrays.toString(km.getAssignments()));
        System.out.println("Inertia: " + km.inertia(data));
    }
}
