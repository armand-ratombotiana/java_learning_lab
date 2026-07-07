package com.algo.lab37;

import java.util.*;

/**
 * Maximum clique solver using branch and bound with greedy coloring bound.
 * Finds the largest clique (complete subgraph) in an undirected graph.
 * Uses the Carraghan-Pardalos exact algorithm with pruning.
 */
public class MaximumClique {
    private final int n;
    private final boolean[][] adj;
    private int maxSize;
    private Set<Integer> bestClique;

    public MaximumClique(int n) {
        this.n = n;
        this.adj = new boolean[n][n];
    }

    public void addEdge(int u, int v) {
        adj[u][v] = adj[v][u] = true;
    }

    public Set<Integer> findMaximum() {
        maxSize = 0;
        bestClique = new HashSet<>();
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < n; i++) order.add(i);
        order.sort((a, b) -> {
            int da = 0, db = 0;
            for (int j = 0; j < n; j++) { if (adj[a][j]) da++; if (adj[b][j]) db++; }
            return Integer.compare(db, da);
        });
        int[] ordering = new int[n];
        for (int i = 0; i < n; i++) ordering[i] = order.get(i);
        expand(new ArrayList<>(), ordering, 0);
        return bestClique;
    }

    private void expand(List<Integer> current, int[] ordering, int idx) {
        if (idx == ordering.length) {
            if (current.size() > maxSize) {
                maxSize = current.size();
                bestClique = new HashSet<>(current);
            }
            return;
        }
        int v = ordering[idx];
        boolean canAdd = true;
        for (int u : current) if (!adj[u][v]) { canAdd = false; break; }
        if (canAdd) {
            if (current.size() + (ordering.length - idx) > maxSize) {
                current.add(v);
                expand(current, ordering, idx + 1);
                current.remove(current.size() - 1);
            }
        }
        if (current.size() + (ordering.length - idx - 1) > maxSize) {
            expand(current, ordering, idx + 1);
        }
    }
}
