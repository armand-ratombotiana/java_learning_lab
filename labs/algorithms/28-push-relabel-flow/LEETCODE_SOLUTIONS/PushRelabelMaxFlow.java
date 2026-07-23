package com.algorithms.pushrelabel;

import java.util.*;

/**
 * Custom: Push-Relabel Max Flow Algorithm (Goldberg-Tarjan)
 * Alternative to Dinic for maximum flow. O(V^2 * sqrt(E)) time.
 *
 * Time Complexity: O(V^2 * sqrt(E))
 * Space Complexity: O(V + E)
 */
public class PushRelabelMaxFlow {

    private static class Edge {
        int to, rev, cap, flow;
        Edge(int to, int rev, int cap) { this.to = to; this.rev = rev; this.cap = cap; }
    }

    private final List<Edge>[] graph;
    private final int[] height, excess;
    private final boolean[] active;

    @SuppressWarnings("unchecked")
    public PushRelabelMaxFlow(int n) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        height = new int[n];
        excess = new int[n];
        active = new boolean[n];
    }

    public void addEdge(int from, int to, int cap) {
        graph[from].add(new Edge(to, graph[to].size(), cap));
        graph[to].add(new Edge(from, graph[from].size() - 1, 0));
    }

    private void push(Edge e, int v) {
        int d = Math.min(excess[v], e.cap - e.flow);
        e.flow += d;
        graph[e.to].get(e.rev).flow -= d;
        excess[v] -= d;
        excess[e.to] += d;
    }

    private void relabel(int v) {
        int min = Integer.MAX_VALUE;
        for (Edge e : graph[v]) {
            if (e.cap - e.flow > 0) min = Math.min(min, height[e.to]);
        }
        if (min < Integer.MAX_VALUE) height[v] = min + 1;
    }

    private void discharge(int v) {
        while (excess[v] > 0) {
            for (Edge e : graph[v]) {
                if (e.cap - e.flow > 0 && height[v] == height[e.to] + 1) {
                    push(e, v);
                    if (excess[v] == 0) return;
                }
            }
            relabel(v);
        }
    }

    public int maxFlow(int s, int t) {
        height[s] = graph.length;
        for (Edge e : graph[s]) {
            excess[s] += e.cap;
            push(e, s);
        }
        while (true) {
            boolean done = true;
            for (int v = 0; v < graph.length; v++) {
                if (v != s && v != t && excess[v] > 0) {
                    discharge(v);
                    done = false;
                }
            }
            if (done) break;
        }
        return excess[t];
    }

    public static void main(String[] args) {
        PushRelabelMaxFlow pr = new PushRelabelMaxFlow(6);
        pr.addEdge(0, 1, 16); pr.addEdge(0, 2, 13);
        pr.addEdge(1, 2, 10); pr.addEdge(1, 3, 12);
        pr.addEdge(2, 1, 4); pr.addEdge(2, 4, 14);
        pr.addEdge(3, 2, 9); pr.addEdge(3, 5, 20);
        pr.addEdge(4, 3, 7); pr.addEdge(4, 5, 4);
        System.out.println("Push-Relabel Max Flow: " + pr.maxFlow(0, 5) + " (expected: 23)");
    }
}
