package com.ai09;

import java.util.*;

public class LocalOutlierFactor {
    private double[][] data;
    private int k;
    private double[] lofScores;

    public LocalOutlierFactor(int k) {
        this.k = k;
    }

    public void fit(double[][] data) {
        this.data = data;
        int n = data.length;
        lofScores = new double[n];
        for (int i = 0; i < n; i++) {
            double[] kDistances = new double[n];
            int[] indices = new int[n];
            for (int j = 0; j < n; j++) {
                kDistances[j] = euclidean(data[i], data[j]);
                indices[j] = j;
            }
            for (int a = 0; a < n - 1; a++)
                for (int b = 0; b < n - a - 1; b++)
                    if (kDistances[b] > kDistances[b + 1]) {
                        double tmpD = kDistances[b];
                        kDistances[b] = kDistances[b + 1];
                        kDistances[b + 1] = tmpD;
                        int tmpI = indices[b];
                        indices[b] = indices[b + 1];
                        indices[b + 1] = tmpI;
                    }
            double kDist = kDistances[Math.min(k, n - 1)];
            double lrdI = localReachabilityDensity(i, kDist, indices);
            double sumRatio = 0;
            int neighborCount = 0;
            for (int j = 1; j <= k && j < n; j++) {
                int neighbor = indices[j];
                double neighborKDist = getKDist(neighbor);
                double lrdNeighbor = localReachabilityDensity(neighbor, neighborKDist, null);
                sumRatio += lrdNeighbor / lrdI;
                neighborCount++;
            }
            lofScores[i] = neighborCount > 0 ? sumRatio / neighborCount : 1.0;
        }
    }

    private double euclidean(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private double getKDist(int idx) {
        int n = data.length;
        double[] dists = new double[n];
        for (int i = 0; i < n; i++)
            dists[i] = euclidean(data[idx], data[i]);
        Arrays.sort(dists);
        return dists[Math.min(k, n - 1)];
    }

    private double localReachabilityDensity(int idx, double kDist, int[] ignored) {
        int n = data.length;
        double sumReach = 0;
        int count = 0;
        for (int j = 0; j < n; j++) {
            if (j == idx) continue;
            double dist = euclidean(data[idx], data[j]);
            double reachDist = Math.max(dist, getKDist(j));
            sumReach += reachDist;
            count++;
        }
        return count > 0 ? 1 / (sumReach / count) : 0;
    }

    public double[] getScores() { return lofScores; }

    public static void main(String[] args) {
        System.out.println("=== Local Outlier Factor Demo ===");
        double[][] data = {
            {1, 1}, {1.1, 1.1}, {1.2, 1.0}, {1.1, 0.9},
            {5, 5}, {5.1, 5.1}, {5.2, 5.0},
            {10, 1}
        };
        LocalOutlierFactor lof = new LocalOutlierFactor(3);
        lof.fit(data);
        double[] scores = lof.getScores();
        for (int i = 0; i < data.length; i++)
            System.out.println("Point " + Arrays.toString(data[i]) + " LOF: " + scores[i]);
    }
}
