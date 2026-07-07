package com.algo.lab28;

public class CapacityScaling {

    private int n;
    private long[][] cap, flow;

    public CapacityScaling(int n) {
        this.n = n;
        cap = new long[n][n];
        flow = new long[n][n];
    }

    public void addEdge(int from, int to, long capacity) {
        cap[from][to] += capacity;
    }

    public long maxFlow(int s, int t) {
        long maxCap = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                maxCap = Math.max(maxCap, cap[i][j]);
            }
        }
        long delta = Long.highestOneBit(maxCap);
        long totalFlow = 0;
        while (delta > 0) {
            totalFlow += augment(s, t, delta);
            delta >>= 1;
        }
        return totalFlow;
    }

    private long augment(int s, int t, long delta) {
        long total = 0;
        boolean[] visited = new boolean[n];
        while (true) {
            Arrays.fill(visited, false);
            long f = dfs(s, t, Long.MAX_VALUE, delta, visited);
            if (f == 0) break;
            total += f;
        }
        return total;
    }

    private long dfs(int v, int t, long f, long delta, boolean[] visited) {
        if (v == t) return f;
        visited[v] = true;
        for (int u = 0; u < n; u++) {
            long residual = cap[v][u] - flow[v][u];
            if (!visited[u] && residual >= delta) {
                long pushed = dfs(u, t, Math.min(f, residual), delta, visited);
                if (pushed > 0) {
                    flow[v][u] += pushed;
                    flow[u][v] -= pushed;
                    return pushed;
                }
            }
        }
        return 0;
    }
}
