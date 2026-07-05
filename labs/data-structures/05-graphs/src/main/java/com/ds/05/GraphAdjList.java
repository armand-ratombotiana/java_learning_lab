package com.ds05;

import java.util.*;

/*
 * GraphAdjList - Undirected/directed graph using adjacency list.
 *
 * Time Complexity:
 * - addVertex: O(1)
 * - addEdge: O(1)
 * - removeEdge: O(deg(v))
 * - BFS: O(V + E)
 * - DFS: O(V + E)
 *
 * Space Complexity: O(V + E)
 */
public class GraphAdjList<T> {

    private final Map<T, List<Edge<T>>> adjList;
    private boolean directed;

    public static class Edge<T> {
        public T destination;
        public int weight;
        public Edge(T destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }
    }

    public GraphAdjList() {
        this(false);
    }

    public GraphAdjList(boolean directed) {
        this.directed = directed;
        this.adjList = new HashMap<>();
    }

    public void addVertex(T vertex) {
        adjList.putIfAbsent(vertex, new ArrayList<>());
    }

    public void addEdge(T source, T destination) {
        addEdge(source, destination, 1);
    }

    public void addEdge(T source, T destination, int weight) {
        adjList.putIfAbsent(source, new ArrayList<>());
        adjList.putIfAbsent(destination, new ArrayList<>());
        adjList.get(source).add(new Edge<>(destination, weight));
        if (!directed) {
            adjList.get(destination).add(new Edge<>(source, weight));
        }
    }

    public void removeEdge(T source, T destination) {
        adjList.getOrDefault(source, Collections.emptyList())
                .removeIf(e -> e.destination.equals(destination));
        if (!directed) {
            adjList.getOrDefault(destination, Collections.emptyList())
                    .removeIf(e -> e.destination.equals(source));
        }
    }

    public List<Edge<T>> getNeighbors(T vertex) {
        return adjList.getOrDefault(vertex, Collections.emptyList());
    }

    public Set<T> getVertices() {
        return adjList.keySet();
    }

    public boolean hasVertex(T vertex) {
        return adjList.containsKey(vertex);
    }

    public boolean hasEdge(T source, T destination) {
        return adjList.getOrDefault(source, Collections.emptyList())
                .stream().anyMatch(e -> e.destination.equals(destination));
    }

    public int vertexCount() {
        return adjList.size();
    }

    public int edgeCount() {
        int count = 0;
        for (List<Edge<T>> edges : adjList.values()) {
            count += edges.size();
        }
        return directed ? count : count / 2;
    }

    public List<T> bfs(T start) {
        if (!adjList.containsKey(start)) return Collections.emptyList();
        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Queue<T> queue = new LinkedList<>();
        visited.add(start);
        queue.offer(start);
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            for (Edge<T> edge : adjList.getOrDefault(vertex, Collections.emptyList())) {
                if (!visited.contains(edge.destination)) {
                    visited.add(edge.destination);
                    queue.offer(edge.destination);
                }
            }
        }
        return result;
    }

    public List<T> dfs(T start) {
        if (!adjList.containsKey(start)) return Collections.emptyList();
        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        dfsRecursive(start, visited, result);
        return result;
    }

    private void dfsRecursive(T vertex, Set<T> visited, List<T> result) {
        visited.add(vertex);
        result.add(vertex);
        for (Edge<T> edge : adjList.getOrDefault(vertex, Collections.emptyList())) {
            if (!visited.contains(edge.destination)) {
                dfsRecursive(edge.destination, visited, result);
            }
        }
    }

    public List<T> topologicalSort() {
        if (!directed) throw new IllegalStateException("Topological sort requires a directed graph");
        Map<T, Integer> inDegree = new HashMap<>();
        for (T vertex : adjList.keySet()) {
            inDegree.putIfAbsent(vertex, 0);
            for (Edge<T> edge : adjList.get(vertex)) {
                inDegree.merge(edge.destination, 1, Integer::sum);
            }
        }
        Queue<T> queue = new LinkedList<>();
        for (Map.Entry<T, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) queue.offer(entry.getKey());
        }
        List<T> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            result.add(vertex);
            for (Edge<T> edge : adjList.getOrDefault(vertex, Collections.emptyList())) {
                inDegree.merge(edge.destination, -1, Integer::sum);
                if (inDegree.get(edge.destination) == 0) queue.offer(edge.destination);
            }
        }
        if (result.size() != vertexCount()) throw new IllegalStateException("Graph has a cycle");
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (T vertex : adjList.keySet()) {
            sb.append(vertex).append(" -> ");
            List<Edge<T>> neighbors = adjList.get(vertex);
            for (int i = 0; i < neighbors.size(); i++) {
                Edge<T> e = neighbors.get(i);
                sb.append(e.destination).append("(w:").append(e.weight).append(")");
                if (i < neighbors.size() - 1) sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
