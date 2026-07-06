package com.dsacademy.lab11.disjointsetunion;

public class DisjointSetUnion {

    private final int[] parent;
    private final int[] rank;
    private int sets;

    public DisjointSetUnion(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Size must be non-negative: " + n);
        }
        parent = new int[n];
        rank = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        validateIndex(x);
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public int findIterative(int x) {
        validateIndex(x);
        int root = x;
        while (root != parent[root]) {
            root = parent[root];
        }
        while (x != root) {
            int next = parent[x];
            parent[x] = root;
            x = next;
        }
        return root;
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        sets--;
        return true;
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    public int getSets() {
        return sets;
    }

    public int size() {
        return parent.length;
    }

    private void validateIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("Index " + x + " out of bounds for size " + parent.length);
        }
    }
}
