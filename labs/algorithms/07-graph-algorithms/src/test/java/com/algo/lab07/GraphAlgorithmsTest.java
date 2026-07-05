package com.algo.lab07;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GraphAlgorithmsTest {

    @Test
    void testBFS() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(2));
        graph.put(2, List.of());
        List<Integer> bfs = GraphAlgorithms.bfs(graph, 0);
        assertEquals(3, bfs.size());
        assertTrue(bfs.containsAll(List.of(0, 1, 2)));
    }

    @Test
    void testBFSDisconnected() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of());
        graph.put(1, List.of());
        List<Integer> bfs = GraphAlgorithms.bfs(graph, 0);
        assertEquals(1, bfs.size());
    }

    @Test
    void testDFS() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(2));
        graph.put(2, List.of());
        List<Integer> dfs = GraphAlgorithms.dfs(graph, 0);
        assertEquals(3, dfs.size());
    }

    @Test
    void testDijkstra() {
        Map<Integer, List<GraphAlgorithms.Edge>> graph = new HashMap<>();
        graph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        graph.put(1, List.of(new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        graph.put(2, List.of(new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        graph.put(3, List.of());
        var dist = GraphAlgorithms.dijkstra(graph, 0);
        assertEquals(0, dist.get(0).intValue());
        assertEquals(3, dist.get(2).intValue());
        assertEquals(4, dist.get(1).intValue());
        assertEquals(6, dist.get(3).intValue());
    }

    @Test
    void testBellmanFord() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5));
        var dist = GraphAlgorithms.bellmanFord(edges, 4, 0);
        assertEquals(0, dist.get(0).intValue());
        assertEquals(4, dist.get(1).intValue());
        assertEquals(3, dist.get(2).intValue());
        assertEquals(6, dist.get(3).intValue());
    }

    @Test
    void testBellmanFordNegativeCycle() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 1), new GraphAlgorithms.Edge(1, 2, -1),
            new GraphAlgorithms.Edge(2, 0, -2));
        assertThrows(IllegalStateException.class,
            () -> GraphAlgorithms.bellmanFord(edges, 3, 0));
    }

    @Test
    void testFloydWarshall() {
        int INF = Integer.MAX_VALUE;
        int[][] graph = {{0, 3, INF, 5}, {2, 0, INF, 4}, {INF, 1, 0, INF}, {INF, INF, 2, 0}};
        int[][] result = GraphAlgorithms.floydWarshall(graph);
        assertEquals(0, result[0][0]);
        assertEquals(3, result[0][1]);
        assertTrue(result[0][2] < INF);
    }

    @Test
    void testPrimMST() {
        Map<Integer, List<GraphAlgorithms.Edge>> graph = new HashMap<>();
        graph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        graph.put(1, List.of(new GraphAlgorithms.Edge(1, 0, 4), new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        graph.put(2, List.of(new GraphAlgorithms.Edge(2, 0, 3), new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        graph.put(3, List.of(new GraphAlgorithms.Edge(3, 1, 2), new GraphAlgorithms.Edge(3, 2, 5)));
        List<GraphAlgorithms.Edge> mst = GraphAlgorithms.primMST(graph, 4);
        assertEquals(3, mst.size());
    }

    @Test
    void testKruskalMST() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5));
        List<GraphAlgorithms.Edge> mst = GraphAlgorithms.kruskalMST(edges, 4);
        assertEquals(3, mst.size());
        int totalWeight = mst.stream().mapToInt(GraphAlgorithms.Edge::weight).sum();
        assertEquals(6, totalWeight);
    }
}