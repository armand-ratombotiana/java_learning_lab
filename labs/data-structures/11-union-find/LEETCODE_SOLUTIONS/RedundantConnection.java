package com.leetcode.unionfind;

import java.util.Arrays;

/**
 * LeetCode 684: Redundant Connection
 * https://leetcode.com/problems/redundant-connection/
 *
 * Find an edge that can be removed to make the graph a tree (no cycles).
 * The graph has n nodes and n edges (one extra edge creates a cycle).
 *
 * Time Complexity: O(n * alpha(n))
 * Space Complexity: O(n)
 */
public class RedundantConnection {

    public static class UnionFind {
        int[] parent, rank;
        UnionFind(int n) {
            parent = new int[n + 1];
            rank = new int[n + 1];
            for (int i = 1; i <= n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        boolean union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return false;
            if (rank[rx] < rank[ry]) parent[rx] = ry;
            else if (rank[rx] > rank[ry]) parent[ry] = rx;
            else { parent[ry] = rx; rank[rx]++; }
            return true;
        }
    }

    /**
     * Approach: Union-Find
     * Process edges in order. If an edge connects two already-connected nodes, it's redundant.
     * Time: O(n * alpha(n)), Space: O(n)
     */
    public int[] findRedundantConnection(int[][] edges) {
        UnionFind uf = new UnionFind(edges.length);
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) return edge;
        }
        return new int[0];
    }

    public static void main(String[] args) {
        RedundantConnection rc = new RedundantConnection();

        int[] r1 = rc.findRedundantConnection(new int[][] { { 1, 2 }, { 1, 3 }, { 2, 3 } });
        System.out.println("Test 1: " + Arrays.toString(r1) + " (expected: [2, 3])");

        int[] r2 = rc.findRedundantConnection(new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 4 }, { 1, 5 } });
        System.out.println("Test 2: " + Arrays.toString(r2) + " (expected: [1, 4])");

        int[] r3 = rc.findRedundantConnection(new int[][] { { 1, 2 }, { 1, 3 } });
        System.out.println("Test 3: " + Arrays.toString(r3) + " (expected: [])");
    }
}
