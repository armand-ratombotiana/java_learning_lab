package com.algo.lab20;

import java.util.*;
import java.util.random.RandomGenerator;

/**
 * Karger's randomized algorithm for minimum cut.
 * Repeatedly contracts random edges until 2 vertices remain.
 * Time: O(n^2 * log n) with repeated runs, Space: O(n^2)
 */
public class KargerMinCut {

    private static final RandomGenerator RNG = RandomGenerator.getDefault();

    private KargerMinCut() {}

    public static int minCut(int[][] graph) {
        int n = graph.length;
        int minCut = Integer.MAX_VALUE;
        int trials = (int) (n * n * Math.log(n));
        for (int t = 0; t < Math.max(trials, 100); t++) {
            int cut = singleRun(graph);
            if (cut < minCut) minCut = cut;
        }
        return minCut;
    }

    private static int singleRun(int[][] original) {
        int n = original.length;
        int[] parent = new int[n];
        int[] size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
        int remaining = n;
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (original[i][j] > 0) {
                    for (int k = 0; k < original[i][j]; k++) {
                        edges.add(new Edge(i, j));
                    }
                }
            }
        }
        while (remaining > 2) {
            Edge e = edges.get(RNG.nextInt(edges.size()));
            int u = find(parent, e.u);
            int v = find(parent, e.v);
            if (u == v) continue;
            if (size[u] < size[v]) {
                parent[u] = v;
                size[v] += size[u];
            } else {
                parent[v] = u;
                size[u] += size[v];
            }
            remaining--;
        }
        int cutWeight = 0;
        for (Edge e : edges) {
            if (find(parent, e.u) != find(parent, e.v)) {
                cutWeight++;
            }
        }
        return cutWeight;
    }

    private static int find(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private record Edge(int u, int v) {}
}
