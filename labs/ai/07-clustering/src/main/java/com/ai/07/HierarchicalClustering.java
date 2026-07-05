package com.ai07;

import java.util.*;

public class HierarchicalClustering {
    private int numClusters;
    private int[] assignments;

    public HierarchicalClustering(int numClusters) {
        this.numClusters = numClusters;
    }

    public void fit(double[][] data) {
        int n = data.length;
        List<List<Integer>> clusters = new ArrayList<>();
        double[][] distMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            List<Integer> cluster = new ArrayList<>();
            cluster.add(i);
            clusters.add(cluster);
            for (int j = 0; j < n; j++)
                distMatrix[i][j] = euclidean(data[i], data[j]);
        }
        while (clusters.size() > numClusters) {
            int bestI = -1, bestJ = -1;
            double minDist = Double.MAX_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double d = averageLinkage(data, clusters.get(i), clusters.get(j));
                    if (d < minDist) {
                        minDist = d;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
            clusters.get(bestI).addAll(clusters.get(bestJ));
            clusters.remove(bestJ);
        }
        assignments = new int[n];
        for (int c = 0; c < clusters.size(); c++)
            for (int idx : clusters.get(c))
                assignments[idx] = c;
    }

    private double euclidean(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    private double averageLinkage(double[][] data, List<Integer> c1, List<Integer> c2) {
        double sum = 0;
        for (int i : c1)
            for (int j : c2)
                sum += euclidean(data[i], data[j]);
        return sum / (c1.size() * c2.size());
    }

    public int[] getAssignments() { return assignments; }

    public static void main(String[] args) {
        System.out.println("=== Hierarchical Clustering Demo ===");
        double[][] data = {
            {1.0, 1.0}, {1.5, 1.5}, {1.2, 1.3},
            {5.0, 5.0}, {5.5, 5.5}, {9.0, 9.0}, {9.5, 9.3}
        };
        HierarchicalClustering hc = new HierarchicalClustering(3);
        hc.fit(data);
        System.out.println("Assignments: " + Arrays.toString(hc.getAssignments()));
    }
}
