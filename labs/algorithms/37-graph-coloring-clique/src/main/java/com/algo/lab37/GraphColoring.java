package com.algo.lab37;

import java.util.*;

/**
 * Graph coloring implementations: greedy sequential, Welsh-Powell, and DSatur.
 * Assigns colors (integers) to vertices such that adjacent vertices have
 * different colors, minimizing the number of colors used.
 */
public class GraphColoring {
    private final int n;
    private final boolean[][] adj;

    public GraphColoring(int n) {
        this.n = n;
        this.adj = new boolean[n][n];
    }

    public void addEdge(int u, int v) {
        adj[u][v] = adj[v][u] = true;
    }

    public int[] greedy() {
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        colors[0] = 0;
        boolean[] used = new boolean[n];
        for (int u = 1; u < n; u++) {
            Arrays.fill(used, false);
            for (int v = 0; v < n; v++) if (adj[u][v] && colors[v] != -1) used[colors[v]] = true;
            int c = 0; while (c < n && used[c]) c++;
            colors[u] = c;
        }
        return colors;
    }

    public int[] welshPowell() {
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) order[i] = i;
        int[] deg = new int[n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (adj[i][j]) deg[i]++;
        Arrays.sort(order, (a, b) -> Integer.compare(deg[b], deg[a]));
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        for (int iter = 0; iter < n; iter++) {
            Set<Integer> used = new HashSet<>();
            for (int v = 0; v < n; v++) if (colors[v] != -1 && adj[order[iter]][v]) used.add(colors[v]);
            int c = 0; while (used.contains(c)) c++;
            colors[order[iter]] = c;
        }
        return colors;
    }

    public int[] dsatur() {
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        int[] sat = new int[n];
        boolean[] colored = new boolean[n];
        int[] degree = new int[n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (adj[i][j]) degree[i]++;

        for (int t = 0; t < n; t++) {
            int u = -1, maxSat = -1, maxDeg = -1;
            for (int i = 0; i < n; i++) {
                if (!colored[i] && (sat[i] > maxSat || (sat[i] == maxSat && degree[i] > maxDeg))) {
                    u = i; maxSat = sat[i]; maxDeg = degree[i];
                }
            }
            boolean[] used = new boolean[n];
            for (int v = 0; v < n; v++) if (adj[u][v] && colors[v] != -1) used[colors[v]] = true;
            int c = 0; while (c < n && used[c]) c++;
            colors[u] = c;
            colored[u] = true;
            for (int v = 0; v < n; v++) if (adj[u][v] && !colored[v]) {
                Set<Integer> neighborColors = new HashSet<>();
                for (int w = 0; w < n; w++) if (adj[v][w] && colored[w]) neighborColors.add(colors[w]);
                sat[v] = neighborColors.size();
            }
        }
        return colors;
    }

    public int chromaticNumber() {
        int[] colors = dsatur();
        int max = 0;
        for (int c : colors) max = Math.max(max, c);
        return max + 1;
    }
}
