package com.algo.lab07;

import java.util.*;

public class GraphExample {
    public static void main(String[] args) {
        System.out.println("=== Graph Algorithms Demo ===\n");

        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(0, 3, 4));
        graph.put(2, List.of(0, 4));
        graph.put(3, List.of(1, 4, 5));
        graph.put(4, List.of(1, 2, 3, 5));
        graph.put(5, List.of(3, 4));

        System.out.println("BFS from 0: " + GraphAlgorithms.bfs(graph, 0));
        System.out.println("DFS from 0: " + GraphAlgorithms.dfs(graph, 0));

        Map<Integer, List<GraphAlgorithms.Edge>> weightedGraph = new HashMap<>();
        weightedGraph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        weightedGraph.put(1, List.of(new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        weightedGraph.put(2, List.of(new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        weightedGraph.put(3, List.of());

        System.out.println("\nDijkstra from 0: " + GraphAlgorithms.dijkstra(weightedGraph, 0));

        List<GraphAlgorithms.Edge> edges = new ArrayList<>(List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5)));
        System.out.println("Bellman-Ford from 0: " + GraphAlgorithms.bellmanFord(edges, 4, 0));

        int INF = Integer.MAX_VALUE;
        int[][] fwGraph = {
            {0, 3, INF, 5}, {2, 0, INF, 4}, {INF, 1, 0, INF}, {INF, INF, 2, 0}
        };
        int[][] fwResult = GraphAlgorithms.floydWarshall(fwGraph);
        System.out.println("\nFloyd-Warshall result:");
        for (int[] row : fwResult) System.out.println(Arrays.toString(row));

        System.out.println("\nPrim's MST: " + GraphAlgorithms.primMST(weightedGraph, 4));
        System.out.println("Kruskal's MST: " + GraphAlgorithms.kruskalMST(edges, 4));
    }
}