package com.ds05;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Undirected Graph Demo ===");
        GraphAdjList<String> graph = new GraphAdjList<>();
        graph.addEdge("A", "B");
        graph.addEdge("A", "C");
        graph.addEdge("B", "D");
        graph.addEdge("C", "E");
        graph.addEdge("D", "E");
        graph.addEdge("D", "F");
        graph.addEdge("E", "F");

        System.out.println("Graph structure:");
        System.out.println(graph);
        System.out.println("Vertices: " + graph.vertexCount() + ", Edges: " + graph.edgeCount());

        System.out.println("BFS from A: " + graph.bfs("A"));
        System.out.println("DFS from A: " + graph.dfs("A"));
        System.out.println("Has edge A-B: " + graph.hasEdge("A", "B"));
        System.out.println("Has edge A-D: " + graph.hasEdge("A", "D"));
        graph.removeEdge("A", "B");
        System.out.println("After remove A-B, has edge: " + graph.hasEdge("A", "B"));

        System.out.println("\n=== Directed Graph Demo ===");
        GraphAdjList<String> dag = new GraphAdjList<>(true);
        dag.addEdge("A", "B");
        dag.addEdge("A", "C");
        dag.addEdge("B", "D");
        dag.addEdge("C", "D");
        dag.addEdge("D", "E");
        System.out.println("Topological sort: " + dag.topologicalSort());

        System.out.println("\n=== Dijkstra Shortest Path Demo ===");
        GraphAdjList<String> weighted = new GraphAdjList<>();
        weighted.addEdge("A", "B", 4);
        weighted.addEdge("A", "C", 2);
        weighted.addEdge("B", "C", 1);
        weighted.addEdge("B", "D", 5);
        weighted.addEdge("C", "D", 8);
        weighted.addEdge("C", "E", 10);
        weighted.addEdge("D", "E", 2);
        weighted.addEdge("D", "F", 6);
        weighted.addEdge("E", "F", 3);

        Dijkstra<String> dijkstra = new Dijkstra<>(weighted);
        System.out.println("Shortest distances from A:");
        Map<String, Integer> dists = dijkstra.shortestDistances("A");
        for (Map.Entry<String, Integer> e : dists.entrySet()) {
            System.out.println("  A -> " + e.getKey() + " = " + e.getValue());
        }
        System.out.println("Shortest path A -> F: " + dijkstra.shortestPath("A", "F"));
    }
}
