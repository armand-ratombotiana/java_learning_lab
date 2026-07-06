package com.dsacademy.lab11.disjointsetunion;

public class DisjointSetUnionBySize {

    private final int[] parent;
    private final int[] size;
    private int sets;

    public DisjointSetUnionBySize(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Size must be non-negative: " + n);
        }
        parent = new int[n];
        size = new int[n];
        sets = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        validateIndex(x);
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        if (rootX == rootY) {
            return false;
        }
        if (size[rootX] < size[rootY]) {
            parent[rootX] = rootY;
            size[rootY] += size[rootX];
        } else {
            parent[rootY] = rootX;
            size[rootX] += size[rootY];
        }
        sets--;
        return true;
    }

    public int componentSize(int x) {
        return size[find(x)];
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
