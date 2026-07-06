package com.dsacademy.lab11.disjointsetunion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KruskalMST {

    public static class Edge implements Comparable<Edge> {
        public final int u;
        public final int v;
        public final int weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Integer.compare(this.weight, other.weight);
        }
    }

    public static List<Edge> findMST(List<Edge> edges, int n) {
        List<Edge> result = new ArrayList<>();
        if (n <= 0) return result;

        Collections.sort(edges);
        DisjointSetUnion dsu = new DisjointSetUnion(n);
        int totalWeight = 0;

        for (Edge edge : edges) {
            if (edge.u < 0 || edge.u >= n || edge.v < 0 || edge.v >= n) {
                throw new IllegalArgumentException("Edge vertex out of bounds");
            }
            if (dsu.union(edge.u, edge.v)) {
                result.add(edge);
                totalWeight += edge.weight;
                if (result.size() == n - 1) {
                    break;
                }
            }
        }
        return result;
    }

    public static int mstWeight(List<Edge> edges, int n) {
        List<Edge> mst = findMST(edges, n);
        return mst.stream().mapToInt(e -> e.weight).sum();
    }

    public static boolean hasSpanningTree(List<Edge> edges, int n) {
        List<Edge> mst = findMST(edges, n);
        return mst.size() == n - 1;
    }
}
