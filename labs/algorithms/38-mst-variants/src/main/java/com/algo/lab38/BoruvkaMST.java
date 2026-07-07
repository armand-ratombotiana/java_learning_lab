package com.algo.lab38;

import java.util.*;

/**
 * Boruvka's algorithm for Minimum Spanning Tree (MST).
 * Parallel-friendly algorithm that repeatedly adds the cheapest edge
 * from each component to any other component, merging components.
 * Runs in O(E log V) time using Union-Find.
 */
public class BoruvkaMST {
    private final int n;
    private final List<Edge> edges;
    private double mstWeight;
    private List<Edge> mst;

    public static class Edge implements Comparable<Edge> {
        public final int u, v;
        public final double weight;
        public Edge(int u, int v, double w) { this.u = u; this.v = v; this.weight = w; }
        public int compareTo(Edge o) { return Double.compare(this.weight, o.weight); }
    }

    public BoruvkaMST(int n) {
        this.n = n;
        this.edges = new ArrayList<>();
    }

    public void addEdge(int u, int v, double w) { edges.add(new Edge(u, v, w)); }

    public void compute() {
        int[] parent = new int[n];
        int[] rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        mst = new ArrayList<>();
        mstWeight = 0;
        int numTrees = n;

        while (numTrees > 1) {
            Edge[] cheapest = new Edge[n];
            for (Edge e : edges) {
                int ru = find(e.u, parent), rv = find(e.v, parent);
                if (ru != rv) {
                    if (cheapest[ru] == null || e.weight < cheapest[ru].weight) cheapest[ru] = e;
                    if (cheapest[rv] == null || e.weight < cheapest[rv].weight) cheapest[rv] = e;
                }
            }
            boolean merged = false;
            for (int i = 0; i < n; i++) {
                if (cheapest[i] != null) {
                    Edge e = cheapest[i];
                    int ru = find(e.u, parent), rv = find(e.v, parent);
                    if (ru != rv) {
                        union(ru, rv, parent, rank);
                        mst.add(e);
                        mstWeight += e.weight;
                        numTrees--;
                        merged = true;
                    }
                }
            }
            if (!merged) break;
        }
    }

    private int find(int x, int[] parent) {
        while (parent[x] != x) { parent[x] = parent[parent[x]]; x = parent[x]; }
        return x;
    }

    private void union(int a, int b, int[] parent, int[] rank) {
        if (rank[a] < rank[b]) { parent[a] = b; }
        else if (rank[a] > rank[b]) { parent[b] = a; }
        else { parent[b] = a; rank[a]++; }
    }

    public double getMstWeight() { return mstWeight; }
    public List<Edge> getMst() { return mst; }
}
