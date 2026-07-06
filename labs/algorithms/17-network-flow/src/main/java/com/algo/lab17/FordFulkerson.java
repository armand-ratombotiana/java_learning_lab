package com.algo.lab17;

import java.util.*;

/**
 * Ford-Fulkerson algorithm for maximum flow.
 * Uses DFS to find augmenting paths in the residual graph.
 * Time: O(E * maxFlow), Space: O(V + E)
 */
public class FordFulkerson {

    private final int vertices;
    private final int[][] capacity;

    public FordFulkerson(int vertices) {
        this.vertices = vertices;
        this.capacity = new int[vertices][vertices];
    }

    public void addEdge(int from, int to, int cap) {
        capacity[from][to] = cap;
    }

    public int maxFlow(int source, int sink) {
        int[][] residual = new int[vertices][vertices];
        for (int i = 0; i < vertices; i++) {
            System.arraycopy(capacity[i], 0, residual[i], 0, vertices);
        }
        int[] parent = new int[vertices];
        int maxFlow = 0;
        while (dfs(residual, source, sink, parent)) {
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

    private boolean dfs(int[][] residual, int source, int sink, int[] parent) {
        boolean[] visited = new boolean[vertices];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(source);
        visited[source] = true;
        parent[source] = -1;
        while (!stack.isEmpty()) {
            int u = stack.pop();
            for (int v = 0; v < vertices; v++) {
                if (!visited[v] && residual[u][v] > 0) {
                    visited[v] = true;
                    parent[v] = u;
                    if (v == sink) return true;
                    stack.push(v);
                }
            }
        }
        return false;
    }
}
