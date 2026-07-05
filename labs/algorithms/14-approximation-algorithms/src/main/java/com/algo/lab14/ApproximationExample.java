package com.algo.lab14;

import java.util.*;

public class ApproximationExample {
    public static void main(String[] args) {
        System.out.println("=== Approximation Algorithms Demo ===\n");

        System.out.println("--- Vertex Cover (2-approximation) ---");
        int[][] graph = {
            {0, 1, 1, 0, 0},
            {1, 0, 1, 1, 0},
            {1, 1, 0, 0, 1},
            {0, 1, 0, 0, 1},
            {0, 0, 1, 1, 0}
        };
        Set<Integer> cover = ApproximationAlgorithms.vertexCoverApprox(graph);
        System.out.println("Vertex cover size: " + cover.size() + " (optimal ≤ 2)");
        System.out.println("Cover set: " + cover);

        System.out.println("\n--- TSP (Nearest Neighbor approx) ---");
        int[][] tspGraph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        List<Integer> tour = ApproximationAlgorithms.tspApprox(tspGraph);
        System.out.println("TSP tour: " + tour);
        if (tour.size() > 1) {
            int cost = 0;
            for (int i = 0; i < tour.size() - 1; i++) {
                cost += tspGraph[tour.get(i)][tour.get(i + 1)];
            }
            System.out.println("Tour cost: " + cost);
        }

        System.out.println("\n--- Set Cover (Greedy approx) ---");
        Set<Integer> universe = new HashSet<>(Set.of(1, 2, 3, 4, 5, 6, 7, 8));
        List<Set<Integer>> sets = new ArrayList<>();
        sets.add(new HashSet<>(Set.of(1, 2, 3)));
        sets.add(new HashSet<>(Set.of(2, 4, 6)));
        sets.add(new HashSet<>(Set.of(5, 7, 8)));
        sets.add(new HashSet<>(Set.of(1, 4, 5, 8)));
        Set<Integer> setCover = ApproximationAlgorithms.setCoverGreedy(universe, sets);
        System.out.println("Sets used: " + setCover);
        Set<Integer> covered = new HashSet<>();
        for (int idx : setCover) covered.addAll(sets.get(idx));
        System.out.println("Covered elements: " + covered);
    }
}