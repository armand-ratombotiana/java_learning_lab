package com.algo.lab07;

import java.util.*;

/**
 * Graph algorithms.
 *
 * BFS: O(V + E) time, O(V) space
 * DFS: O(V + E) time, O(V) space
 * Dijkstra: O((V + E) log V) time, O(V) space
 * Bellman-Ford: O(V * E) time, O(V) space
 * Floyd-Warshall: O(V^3) time, O(V^2) space
 * Prim's MST: O((V + E) log V) time, O(V) space
 * Kruskal's MST: O(E log E) time, O(V) space
 */
public class GraphAlgorithms {

    private GraphAlgorithms() {}

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(start);
        queue.offer(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);
            for (int neighbor : graph.getOrDefault(v, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfs(graph, start, visited, order);
        return order;
    }

    private static void dfs(Map<Integer, List<Integer>> graph, int v, Set<Integer> visited, List<Integer> order) {
        visited.add(v);
        order.add(v);
        for (int neighbor : graph.getOrDefault(v, List.of())) {
            if (!visited.contains(neighbor)) {
                dfs(graph, neighbor, visited, order);
            }
        }
    }

    public static Map<Integer, Integer> dijkstra(Map<Integer, List<Edge>> graph, int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
        pq.offer(new Node(start, 0));
        dist.put(start, 0);
        while (!pq.isEmpty()) {
            Node curr = pq.poll();
            int u = curr.vertex;
            if (curr.dist > dist.getOrDefault(u, Integer.MAX_VALUE)) continue;
            for (Edge e : graph.getOrDefault(u, List.of())) {
                int newDist = dist.get(u) + e.weight;
                if (newDist < dist.getOrDefault(e.to, Integer.MAX_VALUE)) {
                    dist.put(e.to, newDist);
                    pq.offer(new Node(e.to, newDist));
                }
            }
        }
        return dist;
    }

    public static Map<Integer, Integer> bellmanFord(List<Edge> edges, int V, int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int i = 0; i < V; i++) dist.put(i, Integer.MAX_VALUE);
        dist.put(start, 0);

        for (int i = 1; i < V; i++) {
            for (Edge e : edges) {
                if (dist.get(e.from) != Integer.MAX_VALUE &&
                    dist.get(e.from) + e.weight < dist.get(e.to)) {
                    dist.put(e.to, dist.get(e.from) + e.weight);
                }
            }
        }
        for (Edge e : edges) {
            if (dist.get(e.from) != Integer.MAX_VALUE &&
                dist.get(e.from) + e.weight < dist.get(e.to)) {
                throw new IllegalStateException("Graph contains negative weight cycle");
            }
        }
        return dist;
    }

    public static int[][] floydWarshall(int[][] graph) {
        int V = graph.length;
        int[][] dist = new int[V][V];
        for (int i = 0; i < V; i++) System.arraycopy(graph[i], 0, dist[i], 0, V);
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE
                        && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }

    public static List<Edge> primMST(Map<Integer, List<Edge>> graph, int V) {
        List<Edge> mst = new ArrayList<>();
        boolean[] inMST = new boolean[V];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        inMST[0] = true;
        for (Edge e : graph.getOrDefault(0, List.of())) pq.offer(e);
        while (!pq.isEmpty() && mst.size() < V - 1) {
            Edge e = pq.poll();
            if (inMST[e.to]) continue;
            inMST[e.to] = true;
            mst.add(e);
            for (Edge next : graph.getOrDefault(e.to, List.of())) {
                if (!inMST[next.to]) pq.offer(next);
            }
        }
        return mst;
    }

    public static List<Edge> kruskalMST(List<Edge> edges, int V) {
        List<Edge> sorted = new ArrayList<>(edges);
        List<Edge> mst = new ArrayList<>();
        sorted.sort(Comparator.comparingInt(e -> e.weight));
        int[] parent = new int[V];
        for (int i = 0; i < V; i++) parent[i] = i;
        for (Edge e : sorted) {
            int rootFrom = find(parent, e.from);
            int rootTo = find(parent, e.to);
            if (rootFrom != rootTo) {
                mst.add(e);
                union(parent, rootFrom, rootTo);
            }
        }
        return mst;
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private static void union(int[] parent, int x, int y) {
        parent[x] = y;
    }

    public record Edge(int from, int to, int weight) {}
    private record Node(int vertex, int dist) {}
}