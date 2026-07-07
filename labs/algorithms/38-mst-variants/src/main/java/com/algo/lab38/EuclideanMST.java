package com.algo.lab38;

import java.util.*;

/**
 * Simplified Euclidean MST using Delaunay-like approach.
 * Given points in 2D plane, computes MST connecting them where edge
 * weight is the Euclidean distance. Uses a grid-based neighbor approach
 * to approximate Delaunay edges for O(n log n) expected performance.
 */
public class EuclideanMST {
    private final List<double[]> points;

    public EuclideanMST() {
        this.points = new ArrayList<>();
    }

    public void addPoint(double x, double y) { points.add(new double[]{x, y}); }

    public List<int[]> computeMst() {
        int n = points.size();
        if (n == 0) return new ArrayList<>();

        List<Edge> edges = new ArrayList<>();
        // Grid-based neighbor pruning
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for (double[] p : points) {
            minX = Math.min(minX, p[0]); minY = Math.min(minY, p[1]);
            maxX = Math.max(maxX, p[0]); maxY = Math.max(maxY, p[1]);
        }
        double cellSize = Math.max(maxX - minX, maxY - minY) / Math.sqrt(n) * 2;
        Map<Long, List<Integer>> grid = new HashMap<>();

        for (int i = 0; i < n; i++) {
            double[] p = points.get(i);
            long cx = (long) Math.floor((p[0] - minX) / cellSize);
            long cy = (long) Math.floor((p[1] - minY) / cellSize);
            grid.computeIfAbsent((cx << 32) | cy, k -> new ArrayList<>()).add(i);
        }

        for (int i = 0; i < n; i++) {
            double[] p = points.get(i);
            long cx = (long) Math.floor((p[0] - minX) / cellSize);
            long cy = (long) Math.floor((p[1] - minY) / cellSize);
            for (long dx = -1; dx <= 1; dx++) {
                for (long dy = -1; dy <= 1; dy++) {
                    long key = ((cx + dx) << 32) | (cy + dy);
                    List<Integer> cell = grid.get(key);
                    if (cell != null) for (int j : cell) {
                        if (j > i) {
                            double dist = Math.hypot(points.get(j)[0] - p[0], points.get(j)[1] - p[1]);
                            edges.add(new Edge(i, j, dist));
                        }
                    }
                }
            }
        }

        edges.sort(Comparator.comparingDouble(e -> e.w));
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        List<int[]> mst = new ArrayList<>();

        for (Edge e : edges) {
            int ru = find(e.u, parent), rv = find(e.v, parent);
            if (ru != rv) {
                parent[ru] = rv;
                mst.add(new int[]{e.u, e.v});
                if (mst.size() == n - 1) break;
            }
        }
        return mst;
    }

    private int find(int x, int[] p) {
        while (p[x] != x) { p[x] = p[p[x]]; x = p[x]; }
        return x;
    }

    private static class Edge {
        final int u, v; final double w;
        Edge(int u, int v, double w) { this.u = u; this.v = v; this.w = w; }
    }
}
