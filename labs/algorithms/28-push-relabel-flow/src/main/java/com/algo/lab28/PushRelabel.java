package com.algo.lab28;

import java.util.*;

public class PushRelabel {

    static class Edge {
        int to, rev;
        long cap;
        Edge(int to, int rev, long cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    private List<Edge>[] graph;
    private int n;
    private long[] excess;
    private int[] height, count;
    private Queue<Integer> queue;

    public PushRelabel(int n) {
        this.n = n;
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
    }

    public void addEdge(int from, int to, long cap) {
        graph[from].add(new Edge(to, graph[to].size(), cap));
        graph[to].add(new Edge(from, graph[from].size() - 1, 0));
    }

    public long maxFlow(int s, int t) {
        excess = new long[n];
        height = new int[n];
        count = new int[n + 1];
        queue = new ArrayDeque<>();

        height[s] = n;
        count[n] = 1;
        for (Edge e : graph[s]) {
            long delta = e.cap;
            if (delta > 0) {
                e.cap -= delta;
                graph[e.to].get(e.rev).cap += delta;
                excess[s] -= delta;
                excess[e.to] += delta;
                if (e.to != t && e.to != s) queue.add(e.to);
            }
        }

        while (!queue.isEmpty()) {
            int v = queue.poll();
            if (excess[v] > 0) process(v, s, t);
        }

        return excess[t];
    }

    private void process(int v, int s, int t) {
        while (excess[v] > 0) {
            int oldH = height[v];
            for (Edge e : graph[v]) {
                if (e.cap > 0 && height[v] == height[e.to] + 1) {
                    long delta = Math.min(excess[v], e.cap);
                    e.cap -= delta;
                    graph[e.to].get(e.rev).cap += delta;
                    excess[v] -= delta;
                    excess[e.to] += delta;
                    if (e.to != s && e.to != t) queue.add(e.to);
                    if (excess[v] == 0) return;
                }
            }
            int minH = n;
            for (Edge e : graph[v]) {
                if (e.cap > 0) minH = Math.min(minH, height[e.to]);
            }
            if (--count[oldH] == 0) {
                for (int i = 0; i < n; i++) {
                    if (height[i] > oldH) height[i] = n;
                }
            }
            height[v] = minH + 1;
            count[height[v]]++;
        }
    }
}
