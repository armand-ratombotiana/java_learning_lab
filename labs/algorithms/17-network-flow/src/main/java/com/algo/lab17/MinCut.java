package com.algo.lab17;

import java.util.*;

/**
 * Minimum s-t cut using max-flow residual graph.
 * After computing max flow, the min cut partitions vertices reachable from source.
 * Time: O(V * E^2) via Edmonds-Karp, Space: O(V + E)
 */
public class MinCut {

    private final int vertices;
    private final int[][] capacity;

    public MinCut(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] += cap;
    }

    public int minCutValue(int source, int sink) {
        int[][] residual = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, vertices);
        }
        int[] parent = new int[vertices];
        int maxFlow = 0;
        while (bfs(residual, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }
            maxFlow += pathFlow;
        }
        return maxFlow;
    }

    public List<Integer> getSourceSide(int source, int sink) {
        int[][] residual = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, vertices);
        }
        int[] parent = new int[vertices];
        while (bfs(residual, source, sink, parent)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, residual[u][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= pathFlow;
                residual[v][u] += pathFlow;
            }
        }
        boolean[] reachable = new boolean[vertices];
        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        reachable[source] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < vertices; v++) {
                if (!reachable[v] && residual[u][v] > 0) {
                    reachable[v] = true;
                    q.add(v);
                }
            }
        }
        List<Integer> sourceSide = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            if (reachable[i]) sourceSide.add(i);
        }
        return sourceSide;
    }

    private boolean bfs(int[][] residual, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[vertices];
        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        visited[source] = true;
        parent[source] = -1;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && residual[u][v] > 0) {
                    visited[v] = true;
                    parent[v] = u;
                    if (v == sink) return true;
                    q.add(v);
                }
            }
        }
        return false;
    }
}
