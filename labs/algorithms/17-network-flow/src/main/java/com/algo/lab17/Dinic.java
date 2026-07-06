package com.algo.lab17;

import java.util.*;

/**
 * Dinic's algorithm for maximum flow.
 * Uses level graph and blocking flow for efficient max flow.
 * Time: O(V^2 * E), Space: O(V + E)
 */
public class Dinic {

    private final int vertices;
    private final List<Edge>[] graph;

    @SuppressWarnings("unchecked")
    public Dinic(int vertices) {
        this.vertices = vertices;
        this.graph = new List[vertices];
        for (int i = 0; i < vertices; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    public void addEdge(int from, int to, int cap) {
        Edge forward = new Edge(to, cap);
        Edge reverse = new Edge(from, 0);
        forward.reverse = reverse;
        reverse.reverse = forward;
        graph[from].add(forward);
        graph[to].add(reverse);
    }

    public int maxFlow(int source, int sink) {
        int flow = 0;
        int[] level = new int[vertices];
        int[] it = new int[vertices];
        while (bfs(source, sink, level)) {
            Arrays.fill(it, 0);
            while (true) {
                int pushed = dfs(source, sink, Integer.MAX_VALUE, level, it);
                if (pushed == 0) break;
                flow += pushed;
            }
        }
        return flow;
    }

    private boolean bfs(int source, int sink, int[] level) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new LinkedList<>();
        q.add(source);
        level[source] = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : graph[u]) {
                if (level[e.to] < 0 && e.capacity > 0) {
                    level[e.to] = level[u] + 1;
                    q.add(e.to);
                }
            }
        }
        return level[sink] >= 0;
    }

    private int dfs(int u, int sink, int flow, int[] level, int[] it) {
        if (u == sink) return flow;
        for (; it[u] < graph[u].size(); it[u]++) {
            Edge e = graph[u].get(it[u]);
            if (e.capacity > 0 && level[u] + 1 == level[e.to]) {
                int pushed = dfs(e.to, sink, Math.min(flow, e.capacity), level, it);
                if (pushed > 0) {
                    e.capacity -= pushed;
                    e.reverse.capacity += pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }

    private static class Edge {
        final int to;
        int capacity;
        Edge reverse;

        Edge(int to, int capacity) {
            this.to = to;
            this.capacity = capacity;
        }
    }
}
