package com.algorithms.mst;

import java.util.*;

/**
 * Custom: MST Algorithms (Kruskal, Prim) and Variants
 * Minimum spanning tree for weighted undirected graphs.
 *
 * Time Complexity: O(E log E) for Kruskal, O(V^2) for Prim
 * Space Complexity: O(V + E)
 */
public class MSTVariants {

    public static class UnionFind {
        int[] parent;
        UnionFind(int n) { parent = new int[n]; for (int i = 0; i < n; i++) parent[i] = i; }
        int find(int x) { return parent[x] == x ? x : (parent[x] = find(parent[x])); }
        boolean union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return false;
            parent[ry] = rx;
            return true;
        }
    }

    // Kruskal's MST
    public int kruskalMST(int n, int[][] edges) {
        Arrays.sort(edges, (a, b) -> a[2] - b[2]);
        UnionFind uf = new UnionFind(n);
        int mstWeight = 0, edgesUsed = 0;
        for (int[] e : edges) {
            if (uf.union(e[0], e[1])) {
                mstWeight += e[2];
                edgesUsed++;
                if (edgesUsed == n - 1) break;
            }
        }
        return edgesUsed == n - 1 ? mstWeight : -1;
    }

    // Prim's MST
    public int primMST(int n, int[][] edges) {
        List<int[]>[] graph = new ArrayList[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] e : edges) {
            graph[e[0]].add(new int[]{e[1], e[2]});
            graph[e[1]].add(new int[]{e[0], e[2]});
        }
        boolean[] visited = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{0, 0});
        int mstWeight = 0, edgesUsed = 0;

        while (!pq.isEmpty() && edgesUsed < n) {
            int[] cur = pq.poll();
            if (visited[cur[0]]) continue;
            visited[cur[0]] = true;
            mstWeight += cur[1];
            edgesUsed++;
            for (int[] nb : graph[cur[0]]) {
                if (!visited[nb[0]]) pq.offer(new int[]{nb[0], nb[1]});
            }
        }
        return edgesUsed == n ? mstWeight : -1;
    }

    public static void main(String[] args) {
        MSTVariants mst = new MSTVariants();
        int[][] edges = {
            {0, 1, 4}, {0, 2, 3}, {1, 2, 1}, {1, 3, 2}, {2, 3, 4}
        };
        System.out.println("Kruskal MST weight: " + mst.kruskalMST(4, edges) + " (expected: 7)");
        System.out.println("Prim MST weight: " + mst.primMST(4, edges) + " (expected: 7)");
    }
}
