package com.algo.lab14;

import java.util.*;

/**
 * Approximation algorithms for NP-hard problems.
 *
 * Vertex Cover (approx): O(V + E) time, O(V) space, 2-approximation
 * TSP (approx - MST based): O(V^2) time, O(V) space, 2-approximation
 * Set Cover (greedy): O(n * m) time, O(n + m) space, ln(n)-approximation
 */
public class ApproximationAlgorithms {

    private ApproximationAlgorithms() {}

    public static Set<Integer> vertexCoverApprox(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        Set<Integer> cover = new HashSet<>();
        for (int u = 0; u < n; u++) {
            if (!visited[u]) {
                for (int v = 0; v < n; v++) {
                    if (graph[u][v] == 1 && !visited[v]) {
                        visited[u] = true;
                        visited[v] = true;
                        cover.add(u);
                        cover.add(v);
                        break;
                    }
                }
            }
        }
        return cover;
    }

    public static List<Integer> tspApprox(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>();
        visited[0] = true;
        tour.add(0);
        int current = 0;
        for (int step = 1; step < n; step++) {
            int nearest = -1;
            int minDist = Integer.MAX_VALUE;
            for (int v = 0; v < n; v++) {
                if (!visited[v] && graph[current][v] > 0 && graph[current][v] < minDist) {
                    minDist = graph[current][v];
                    nearest = v;
                }
            }
            if (nearest == -1) return List.of();
            visited[nearest] = true;
            tour.add(nearest);
            current = nearest;
        }
        if (graph[current][0] > 0) tour.add(0);
        return tour;
    }

    public static Set<Integer> setCoverGreedy(Set<Integer> universe, List<Set<Integer>> sets) {
        Set<Integer> remaining = new HashSet<>(universe);
        Set<Integer> cover = new HashSet<>();
        boolean[] used = new boolean[sets.size()];
        while (!remaining.isEmpty()) {
            int bestIdx = -1;
            int bestCover = 0;
            for (int i = 0; i < sets.size(); i++) {
                if (used[i]) continue;
                int count = 0;
                for (int elem : sets.get(i)) {
                    if (remaining.contains(elem)) count++;
                }
                if (count > bestCover) {
                    bestCover = count;
                    bestIdx = i;
                }
            }
            if (bestIdx == -1) return cover;
            used[bestIdx] = true;
            cover.add(bestIdx);
            remaining.removeAll(sets.get(bestIdx));
        }
        return cover;
    }
}