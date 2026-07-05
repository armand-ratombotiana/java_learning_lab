package com.ai07;

import java.util.*;

public class DBSCAN {
    private double eps;
    private int minPts;
    private int[] labels;
    private static final int NOISE = -1;
    private static final int UNCLASSIFIED = 0;

    public DBSCAN(double eps, int minPts) {
        this.eps = eps;
        this.minPts = minPts;
    }

    public void fit(double[][] data) {
        int n = data.length;
        labels = new int[n];
        Arrays.fill(labels, UNCLASSIFIED);
        int clusterId = 0;
        for (int i = 0; i < n; i++) {
            if (labels[i] != UNCLASSIFIED) continue;
            List<Integer> neighbors = regionQuery(data, i);
            if (neighbors.size() < minPts) {
                labels[i] = NOISE;
            } else {
                clusterId++;
                expandCluster(data, i, neighbors, clusterId);
            }
        }
    }

    private void expandCluster(double[][] data, int pointIdx, List<Integer> neighbors, int clusterId) {
        labels[pointIdx] = clusterId;
        Queue<Integer> queue = new LinkedList<>(neighbors);
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            if (labels[curr] == NOISE) {
                labels[curr] = clusterId;
            }
            if (labels[curr] != UNCLASSIFIED) continue;
            labels[curr] = clusterId;
            List<Integer> currNeighbors = regionQuery(data, curr);
            if (currNeighbors.size() >= minPts)
                queue.addAll(currNeighbors);
        }
    }

    private List<Integer> regionQuery(double[][] data, int idx) {
        List<Integer> neighbors = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            double dist = 0;
            for (int d = 0; d < data[0].length; d++) {
                double diff = data[idx][d] - data[i][d];
                dist += diff * diff;
            }
            if (Math.sqrt(dist) <= eps) neighbors.add(i);
        }
        return neighbors;
    }

    public int[] getLabels() { return labels; }

    public static void main(String[] args) {
        System.out.println("=== DBSCAN Demo ===");
        double[][] data = {
            {1.0, 1.0}, {1.1, 1.1}, {1.2, 1.0},
            {5.0, 5.0}, {5.1, 5.1}, {5.2, 5.0},
            {9.0, 9.0}, {9.1, 9.1},
            {15.0, 15.0}
        };
        DBSCAN dbscan = new DBSCAN(0.5, 2);
        dbscan.fit(data);
        System.out.println("Labels: " + Arrays.toString(dbscan.getLabels()));
    }
}
