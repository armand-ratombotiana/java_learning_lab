package com.algo.lab37;

import java.util.*;

/**
 * Bron-Kerbosch algorithm for finding all maximal cliques in an undirected graph.
 * Uses pivoting (Tomita variant) for improved performance. A clique is a subset
 * of vertices where every pair is connected by an edge.
 */
public class BronKerbosch {
    private final int n;
    private final boolean[][] adj;

    public BronKerbosch(int n) {
        this.n = n;
        this.adj = new boolean[n][n];
    }

    public void addEdge(int u, int v) {
        adj[u][v] = adj[v][u] = true;
    }

    public List<Set<Integer>> findAllMaximalCliques() {
        List<Set<Integer>> result = new ArrayList<>();
        Set<Integer> R = new HashSet<>();
        Set<Integer> P = new HashSet<>();
        Set<Integer> X = new HashSet<>();
        for (int i = 0; i < n; i++) P.add(i);
        bronKerbosch(R, P, X, result);
        return result;
    }

    private void bronKerbosch(Set<Integer> R, Set<Integer> P, Set<Integer> X, List<Set<Integer>> result) {
        if (P.isEmpty() && X.isEmpty()) {
            result.add(new HashSet<>(R));
            return;
        }
        int pivot = selectPivot(P, X);
        Set<Integer> candidates = new HashSet<>(P);
        for (int v : P) {
            if (adj[pivot][v]) candidates.remove(v);
        }
        for (int v : candidates) {
            R.add(v);
            Set<Integer> newP = new HashSet<>();
            Set<Integer> newX = new HashSet<>();
            for (int w : P) if (adj[v][w]) newP.add(w);
            for (int w : X) if (adj[v][w]) newX.add(w);
            bronKerbosch(R, newP, newX, result);
            R.remove(v);
            P.remove(v);
            X.add(v);
        }
    }

    private int selectPivot(Set<Integer> P, Set<Integer> X) {
        Set<Integer> union = new HashSet<>(P);
        union.addAll(X);
        int best = -1, bestDeg = -1;
        for (int u : union) {
            int deg = 0;
            for (int v : P) if (adj[u][v]) deg++;
            if (deg > bestDeg) { bestDeg = deg; best = u; }
        }
        return best;
    }
}
