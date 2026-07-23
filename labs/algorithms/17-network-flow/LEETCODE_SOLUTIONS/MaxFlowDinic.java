package com.algorithms.networkflow;

import java.util.*;

/**
 * Custom: Dinic's Max Flow Algorithm
 * Find the maximum flow in a flow network.
 *
 * Time Complexity: O(E * V^2)
 * Space Complexity: O(V + E)
 */
public class MaxFlowDinic {

    public static class Edge {
        int to, rev, flow, cap;
        Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    private final List<Edge>[] graph;
    private final int[] level, ptr;

    @SuppressWarnings("unchecked")
    public MaxFlowDinic(int n) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        level = new int[n];
        ptr = new int[n];
    }

    public void addEdge(int from, int to, int cap) {
        graph[from].add(new Edge(to, graph[to].size(), cap));
        graph[to].add(new Edge(from, graph[from].size() - 1, 0));
    }

    private boolean bfs(int s, int t) {
        Arrays.fill(level, -1);
        Queue<Integer> q = new LinkedList<>();
        level[s] = 0;
        q.offer(s);
        while (!q.isEmpty()) {
            int v = q.poll();
            for (Edge e : graph[v]) {
                if (e.cap - e.flow > 0 && level[e.to] < 0) {
                    level[e.to] = level[v] + 1;
                    q.offer(e.to);
                }
            }
        }
        return level[t] >= 0;
    }

    private int dfs(int v, int t, int f) {
        if (v == t) return f;
        for (; ptr[v] < graph[v].size(); ptr[v]++) {
            Edge e = graph[v].get(ptr[v]);
            if (e.cap - e.flow > 0 && level[v] + 1 == level[e.to]) {
                int pushed = dfs(e.to, t, Math.min(f, e.cap - e.flow));
                if (pushed > 0) {
                    e.flow += pushed;
                    graph[e.to].get(e.rev).flow -= pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }

    public int maxFlow(int s, int t) {
        int flow = 0;
        while (bfs(s, t)) {
            Arrays.fill(ptr, 0);
            int pushed;
            while ((pushed = dfs(s, t, Integer.MAX_VALUE)) > 0) {
                flow += pushed;
            }
        }
        return flow;
    }

    public static void main(String[] args) {
        MaxFlowDinic mf = new MaxFlowDinic(6);
        mf.addEdge(0, 1, 16);
        mf.addEdge(0, 2, 13);
        mf.addEdge(1, 2, 10);
        mf.addEdge(1, 3, 12);
        mf.addEdge(2, 1, 4);
        mf.addEdge(2, 4, 14);
        mf.addEdge(3, 2, 9);
        mf.addEdge(3, 5, 20);
        mf.addEdge(4, 3, 7);
        mf.addEdge(4, 5, 4);
        System.out.println("Max Flow: " + mf.maxFlow(0, 5) + " (expected: 23)");
    }
}
