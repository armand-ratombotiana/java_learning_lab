package com.dsacademy.lab11.disjointsetunion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConnectedComponents {

    public static int countComponents(int n, int[][] edges) {
        DisjointSetUnion dsu = new DisjointSetUnion(n);
        for (int[] edge : edges) {
            dsu.union(edge[0], edge[1]);
        }
        Set<Integer> roots = new HashSet<>();
        for (int i = 0; i < n; i++) {
            roots.add(dsu.find(i));
        }
        return roots.size();
    }

    public static Map<Integer, List<Integer>> getComponents(int n, int[][] edges) {
        DisjointSetUnion dsu = new DisjointSetUnion(n);
        for (int[] edge : edges) {
            dsu.union(edge[0], edge[1]);
        }
        Map<Integer, List<Integer>> components = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = dsu.find(i);
            components.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }
        return components;
    }

    public static boolean hasCycle(int n, int[][] edges) {
        DisjointSetUnion dsu = new DisjointSetUnion(n);
        for (int[] edge : edges) {
            if (!dsu.union(edge[0], edge[1])) {
                return true;
            }
        }
        return false;
    }

    public static int islandCount(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int rows = grid.length;
        int cols = grid[0].length;
        DisjointSetUnion dsu = new DisjointSetUnion(rows * cols);
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    int id = r * cols + c;
                    for (int d = 0; d < 4; d++) {
                        int nr = r + dr[d];
                        int nc = c + dc[d];
                        if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 1) {
                            dsu.union(id, nr * cols + nc);
                        }
                    }
                }
            }
        }
        Set<Integer> roots = new HashSet<>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 1) {
                    roots.add(dsu.find(r * cols + c));
                }
            }
        }
        return roots.size();
    }
}
