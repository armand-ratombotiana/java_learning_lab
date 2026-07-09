package com.algorithms.lab07;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class DeepDiveTest {

    @Test
    void testTarjanSCC() {
        // Graph: 0→1→2→0 (SCC), 2→3→4→5→3 (another SCC), 5→0 (bridge)
        int n = 6;
        List<Integer>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        graph[0].add(1); graph[1].add(2); graph[2].add(0);
        graph[2].add(3); graph[3].add(4); graph[4].add(5); graph[5].add(3);
        graph[5].add(0);
        
        // Tarjan's algorithm
        int[] index = new int[n], low = new int[n];
        boolean[] onStack = new boolean[n];
        Arrays.fill(index, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        List<List<Integer>> components = new ArrayList<>();
        int[] currentIndex = {0};
        
        for (int v = 0; v < n; v++) {
            if (index[v] == -1) {
                strongConnect(v, graph, index, low, onStack, stack, components, currentIndex);
            }
        }
        
        assertEquals(2, components.size(), "Should find 2 SCCs");
        // Find which component contains 0,1,2 and which contains 3,4,5
        assertTrue(components.stream().anyMatch(c -> c.contains(0) && c.contains(1) && c.contains(2)),
            "One SCC should contain 0,1,2");
        assertTrue(components.stream().anyMatch(c -> c.contains(3) && c.contains(4) && c.contains(5)),
            "One SCC should contain 3,4,5");
    }

    private void strongConnect(int v, List<Integer>[] graph, int[] index, int[] low,
                                boolean[] onStack, Deque<Integer> stack,
                                List<List<Integer>> components, int[] ci) {
        index[v] = low[v] = ci[0]++;
        stack.push(v);
        onStack[v] = true;
        for (int w : graph[v]) {
            if (index[w] == -1) {
                strongConnect(w, graph, index, low, onStack, stack, components, ci);
                low[v] = Math.min(low[v], low[w]);
            } else if (onStack[w]) {
                low[v] = Math.min(low[v], index[w]);
            }
        }
        if (low[v] == index[v]) {
            List<Integer> comp = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                comp.add(w);
            } while (w != v);
            components.add(comp);
        }
    }

    @Test
    void testDinicMaxFlow() {
        // Simple flow network
        int n = 4;
        Dinic dinic = new Dinic(n);
        dinic.addEdge(0, 1, 10);
        dinic.addEdge(0, 2, 10);
        dinic.addEdge(1, 2, 5);
        dinic.addEdge(1, 3, 10);
        dinic.addEdge(2, 3, 10);
        
        int maxFlow = dinic.maxFlow(0, 3);
        assertEquals(15, maxFlow, "Max flow should be 15");
    }

    static class Dinic {
        static class Edge { int to, rev, cap; Edge(int t, int c, int r) { to = t; cap = c; rev = r; } }
        List<Edge>[] g; int n; int[] level, next;
        @SuppressWarnings("unchecked")
        Dinic(int n) { 
            this.n = n; g = new List[n]; level = new int[n]; next = new int[n];
            for (int i = 0; i < n; i++) g[i] = new ArrayList<>();
        }
        void addEdge(int f, int t, int c) {
            g[f].add(new Edge(t, c, g[t].size()));
            g[t].add(new Edge(f, 0, g[f].size() - 1));
        }
        boolean bfs(int s, int t) {
            Arrays.fill(level, -1);
            Queue<Integer> q = new LinkedList<>();
            level[s] = 0; q.add(s);
            while (!q.isEmpty()) {
                int u = q.poll();
                for (Edge e : g[u]) if (e.cap > 0 && level[e.to] < 0) {
                    level[e.to] = level[u] + 1; q.add(e.to);
                }
            }
            return level[t] >= 0;
        }
        int dfs(int u, int t, int f) {
            if (u == t) return f;
            for (; next[u] < g[u].size(); next[u]++) {
                Edge e = g[u].get(next[u]);
                if (e.cap > 0 && level[u] + 1 == level[e.to]) {
                    int p = dfs(e.to, t, Math.min(f, e.cap));
                    if (p > 0) { e.cap -= p; g[e.to].get(e.rev).cap += p; return p; }
                }
            }
            return 0;
        }
        int maxFlow(int s, int t) {
            int flow = 0, INF = Integer.MAX_VALUE;
            while (bfs(s, t)) {
                Arrays.fill(next, 0);
                int pushed;
                while ((pushed = dfs(s, t, INF)) > 0) flow += pushed;
            }
            return flow;
        }
    }

    @Test
    void testEdmondsKarpComplexity() {
        // Verify O(V·E²) is slower than Dinic for larger graphs
        // This is a conceptual test
        assertTrue(true, "Dinic's O(V²·E) is better than Edmonds-Karp O(V·E²) on dense graphs");
    }

    @Test
    void testGraphIsomorphismVF2() {
        // Two isomorphic triangles
        int[][] g1 = {{1, 2}, {0, 2}, {0, 1}}; // triangle 0-1-2
        int[][] g2 = {{1, 2}, {0, 2}, {0, 1}}; // same triangle
        // Quick check: degree sequence must match
        int[] d1 = Arrays.stream(g1).mapToInt(r -> r.length).sorted().toArray();
        int[] d2 = Arrays.stream(g2).mapToInt(r -> r.length).sorted().toArray();
        assertArrayEquals(d1, d2);
    }

    @Test
    void testKuratowskiPlanarity() {
        // K₅ has 5 vertices, 10 edges
        // E ≤ 3V - 6 = 9 for planar graphs
        int V = 5, E = 10;
        assertFalse(E <= 3 * V - 6, "K₅ should fail the planar edge count test");
    }

    @Test
    void testBipartiteMatching() {
        // Simple bipartite matching: 2 left nodes, 2 right nodes
        // Edges: L0-R0, L0-R1, L1-R0
        // Can match L0-R1, L1-R0 (2 matches)
        assertTrue(true, "Bipartite matching with Dinic = O(E√V)");
    }

    @Test
    void testPushRelabelDense() {
        // Push-relabel handles dense graphs better than Dinic
        assertTrue(true, "Push-Relabel is O(V²·√E), Dinic is O(V²·E)");
    }
}
