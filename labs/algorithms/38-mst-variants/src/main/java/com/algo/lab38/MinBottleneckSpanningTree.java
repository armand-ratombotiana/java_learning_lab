package com.algo.lab38;

import java.util.*;

/**
 * Minimum Bottleneck Spanning Tree (MBST) using binary search + BFS.
 * A minimum bottleneck spanning tree minimizes the maximum edge weight.
 * Every MST is an MBST but not vice versa. Uses binary search on edge
 * weights and connectivity checks to find the minimal threshold.
 */
public class MinBottleneckSpanningTree {
    private final int n;
    private final List<Edge> edges;

    public static class Edge {
        public final int u, v;
        public final double weight;
        public Edge(int u, int v, double w) { this.u = u; this.v = v; this.weight = w; }
    }

    public MinBottleneckSpanningTree(int n) {
        this.n = n;
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, double w) { edges.add(new Edge(u, v, w)); }

    public double findMinBottleneck() {
        double lo = 0, hi = 0;
        for (Edge e : edges) hi = Math.max(hi, e.weight);
        double ans = hi;
        while (hi - lo > 1e-9) {
            double mid = (lo + hi) / 2;
            if (canConnect(mid)) {
                ans = mid;
                hi = mid;
            } else {
                lo = mid;
            }
        }
        return ans;
    }

    private boolean canConnect(double threshold) {
        List<Integer>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        for (Edge e : edges) {
            if (e.weight <= threshold) {
                g[e.u].add(e.v);
                g[e.v].add(e.u);
            }
        }
        boolean[] visited = new boolean[n];
        Queue<Integer> q = new LinkedList<>();
        q.add(0); visited[0] = true;
        int count = 1;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v : g[u]) {
                if (!visited[v]) { visited[v] = true; q.add(v); count++; }
            }
        }
        return count == n;
    }
}
