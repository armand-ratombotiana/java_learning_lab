package com.ds05;

import java.util.*;

/*
 * Dijkstra - Shortest path algorithm using a priority queue.
 *
 * Time Complexity: O((V + E) log V)
 * Space Complexity: O(V)
 */
public class Dijkstra<T> {

    private final GraphAdjList<T> graph;

    public Dijkstra(GraphAdjList<T> graph) {
        this.graph = graph;
    }

    public Map<T, Integer> shortestDistances(T start) {
        Map<T, Integer> distances = new HashMap<>();
        Map<T, T> predecessors = new HashMap<>();
        PriorityQueue<NodeDistance<T>> pq = new PriorityQueue<>(
                Comparator.comparingInt(n -> n.distance));

        for (T vertex : graph.getVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.offer(new NodeDistance<>(start, 0));

        while (!pq.isEmpty()) {
            NodeDistance<T> current = pq.poll();
            T vertex = current.vertex;
            int currentDist = current.distance;

            if (currentDist > distances.get(vertex)) continue;

            for (GraphAdjList.Edge<T> edge : graph.getNeighbors(vertex)) {
                int newDist = currentDist + edge.weight;
                if (newDist < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDist);
                    predecessors.put(edge.destination, vertex);
                    pq.offer(new NodeDistance<>(edge.destination, newDist));
                }
            }
        }
        return distances;
    }

    public List<T> shortestPath(T start, T end) {
        Map<T, Integer> distances = new HashMap<>();
        Map<T, T> predecessors = new HashMap<>();
        PriorityQueue<NodeDistance<T>> pq = new PriorityQueue<>(
                Comparator.comparingInt(n -> n.distance));

        for (T vertex : graph.getVertices()) {
            distances.put(vertex, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.offer(new NodeDistance<>(start, 0));

        while (!pq.isEmpty()) {
            NodeDistance<T> current = pq.poll();
            T vertex = current.vertex;
            int currentDist = current.distance;

            if (vertex.equals(end)) break;
            if (currentDist > distances.get(vertex)) continue;

            for (GraphAdjList.Edge<T> edge : graph.getNeighbors(vertex)) {
                int newDist = currentDist + edge.weight;
                if (newDist < distances.get(edge.destination)) {
                    distances.put(edge.destination, newDist);
                    predecessors.put(edge.destination, vertex);
                    pq.offer(new NodeDistance<>(edge.destination, newDist));
                }
            }
        }

        List<T> path = new LinkedList<>();
        T current = end;
        while (current != null) {
            path.addFirst(current);
            current = predecessors.get(current);
        }
        if (path.size() == 1 && !path.get(0).equals(start)) return Collections.emptyList();
        return path;
    }

    private static class NodeDistance<T> {
        T vertex;
        int distance;
        NodeDistance(T vertex, int distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }
}
