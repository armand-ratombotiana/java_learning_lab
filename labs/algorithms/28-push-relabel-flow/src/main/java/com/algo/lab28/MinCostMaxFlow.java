package com.algo.lab28;

import java.util.*;

public class MinCostMaxFlow {

    static class Edge {
        int to, rev;
        long cap, cost;
        Edge(int to, int rev, long cap, long cost) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
            this.cost = cost;
        }
    }

    private List<Edge>[] graph;
    private int n;
    private long[] potential, dist;
    private int[] prevv, preve;

    public MinCostMaxFlow(int n) {
        this.n = n;
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
    }

    public void addEdge(int from, int to, long cap, long cost) {
        graph[from].add(new Edge(to, graph[to].size(), cap, cost));
        graph[to].add(new Edge(from, graph[from].size() - 1, 0, -cost));
    }

    public long[] minCostFlow(int s, int t, long maxF) {
        long flow = 0, cost = 0;
        potential = new long[n];
        dist = new long[n];
        prevv = new int[n];
        preve = new int[n];

        while (flow < maxF) {
            Arrays.fill(dist, Long.MAX_VALUE / 2);
            dist[s] = 0;
            PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
            pq.offer(new long[]{0, s});

            while (!pq.isEmpty()) {
                long[] cur = pq.poll();
                long d = cur[0];
                int v = (int) cur[1];
                if (dist[v] < d) continue;
                for (int i = 0; i < graph[v].size(); i++) {
                    Edge e = graph[v].get(i);
                    if (e.cap <= 0) continue;
                    long nd = d + e.cost + potential[v] - potential[e.to];
                    if (dist[e.to] > nd) {
                        dist[e.to] = nd;
                        prevv[e.to] = v;
                        preve[e.to] = i;
                        pq.offer(new long[]{nd, e.to});
                    }
                }
            }

            if (dist[t] == Long.MAX_VALUE / 2) break;

            for (int v = 0; v < n; v++) {
                if (dist[v] < Long.MAX_VALUE / 2) {
                    potential[v] += dist[v];
                }
            }

            long add = maxF - flow;
            for (int v = t; v != s; v = prevv[v]) {
                add = Math.min(add, graph[prevv[v]].get(preve[v]).cap);
            }
            flow += add;
            cost += add * potential[t];

            for (int v = t; v != s; v = prevv[v]) {
                Edge e = graph[prevv[v]].get(preve[v]);
                e.cap -= add;
                graph[v].get(e.rev).cap += add;
            }
        }
        return new long[]{flow, cost};
    }
}
