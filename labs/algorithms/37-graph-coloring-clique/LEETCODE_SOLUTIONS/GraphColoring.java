package com.algorithms.graphcoloring;

import java.util.*;

/**
 * Custom: Graph Coloring & Maximum Clique
 * Greedy graph coloring (Welsh-Powell) and Bron-Kerbosch for maximum clique.
 *
 * Time Complexity: O(V^2) for greedy coloring, O(3^(V/3)) for Bron-Kerbosch
 * Space Complexity: O(V)
 */
public class GraphColoring {

    public int greedyColoring(int n, int[][] edges) {
        List<Integer>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) { graph[e[0]].add(e[1]); graph[e[1]].add(e[0]); }

        int[] colors = new int[n];
        Arrays.fill(colors, -1);

        for (int v = 0; v < n; v++) {
            boolean[] used = new boolean[n];
            for (int nb : graph[v]) if (colors[nb] != -1) used[colors[nb]] = true;
            for (int c = 0; c < n; c++) {
                if (!used[c]) { colors[v] = c; break; }
            }
        }
        int maxColor = 0;
        for (int c : colors) maxColor = Math.max(maxColor, c);
        return maxColor + 1;
    }

    // Bron-Kerbosch for maximum clique
    private int maxCliqueSize = 0;

    public int maxClique(int n, int[][] edges) {
        boolean[][] adj = new boolean[n][n];
        for (int[] e : edges) { adj[e[0]][e[1]] = true; adj[e[1]][e[0]] = true; }
        bronKerbosch(new HashSet<>(), new HashSet<>() {{ for (int i = 0; i < n; i++) add(i); }}, new HashSet<>(), adj);
        return maxCliqueSize;
    }

    private void bronKerbosch(Set<Integer> R, Set<Integer> P, Set<Integer> X, boolean[][] adj) {
        if (P.isEmpty() && X.isEmpty()) {
            maxCliqueSize = Math.max(maxCliqueSize, R.size());
            return;
        }
        if (P.isEmpty()) return;
        int pivot = P.iterator().next();
        Set<Integer> candidates = new HashSet<>(P);
        candidates.removeIf(v -> adj[pivot][v]);
        for (int v : candidates) {
            R.add(v);
            Set<Integer> newP = new HashSet<>(P);
            Set<Integer> newX = new HashSet<>(X);
            newP.removeIf(w -> !adj[v][w]);
            newX.removeIf(w -> !adj[v][w]);
            bronKerbosch(R, newP, newX, adj);
            R.remove(v);
            P.remove(v);
            X.add(v);
        }
    }

    public static void main(String[] args) {
        GraphColoring gc = new GraphColoring();
        int[][] edges = { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 1, 3 }, { 2, 3 } };
        System.out.println("Greedy coloring (4-node graph): " + gc.greedyColoring(4, edges) + " colors needed");

        int clique = gc.maxClique(4, edges);
        System.out.println("Max clique size: " + clique + " (expected: 3)");
    }
}
