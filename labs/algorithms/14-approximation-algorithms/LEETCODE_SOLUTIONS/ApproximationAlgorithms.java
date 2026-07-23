package com.algorithms.approximation;

import java.util.*;

/**
 * Custom: Approximation Algorithms
 * Vertex cover approximation (2-approximation), set cover.
 *
 * Time Complexity: O(V + E) for vertex cover
 * Space Complexity: O(V + E)
 */
public class ApproximationAlgorithms {

    // 2-approximation for Vertex Cover
    public Set<Integer> vertexCoverApprox(int n, int[][] edges) {
        Set<Integer> cover = new HashSet<>();
        boolean[] covered = new boolean[edges.length];
        Random rand = new Random(42);

        for (int i = 0; i < edges.length; i++) {
            if (!covered[i]) {
                cover.add(edges[i][0]);
                cover.add(edges[i][1]);
                for (int j = 0; j < edges.length; j++) {
                    if (edges[j][0] == edges[i][0] || edges[j][0] == edges[i][1] ||
                        edges[j][1] == edges[i][0] || edges[j][1] == edges[i][1]) {
                        covered[j] = true;
                    }
                }
            }
        }
        return cover;
    }

    public static void main(String[] args) {
        ApproximationAlgorithms aa = new ApproximationAlgorithms();
        int[][] edges = { {0,1}, {0,2}, {1,3}, {2,3}, {3,4} };
        Set<Integer> cover = aa.vertexCoverApprox(5, edges);
        System.out.println("Vertex cover (2-approx): " + cover);
        System.out.println("Size: " + cover.size() + " (optimal: 2)");
    }
}
