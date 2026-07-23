package com.leetcode.dsu;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Custom: DSU with Rollbacks (Persistent Union-Find)
 * Supports undo operations for union. Used in offline dynamic connectivity.
 *
 * Time Complexity: O(log n) for union, O(1) for snapshot/rollback
 * Space Complexity: O(n)
 */
public class DSURollback {

    private final int[] parent, size;
    private final Deque<int[]> history;

    public DSURollback(int n) {
        parent = new int[n];
        size = new int[n];
        history = new ArrayDeque<>();
        for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
    }

    public int find(int x) {
        while (parent[x] != x) x = parent[x];
        return x;
    }

    public boolean union(int a, int b) {
        int ra = find(a), rb = find(b);
        if (ra == rb) { history.push(null); return false; }
        if (size[ra] < size[rb]) { int t = ra; ra = rb; rb = t; }
        history.push(new int[] { rb, parent[rb], ra, size[ra] });
        parent[rb] = ra;
        size[ra] += size[rb];
        return true;
    }

    public void snapshot() {
        history.push(new int[] { -1 });
    }

    public void rollback() {
        while (!history.isEmpty()) {
            int[] op = history.pop();
            if (op[0] == -1) break;
            parent[op[0]] = op[1];
            size[op[2]] = op[3];
        }
    }

    public static void main(String[] args) {
        DSURollback dsu = new DSURollback(5);
        dsu.union(0, 1);
        dsu.union(2, 3);
        System.out.println("Find(0)==Find(1): " + (dsu.find(0) == dsu.find(1)) + " (expected: true)");
        dsu.snapshot();
        dsu.union(1, 2);
        System.out.println("Find(0)==Find(3): " + (dsu.find(0) == dsu.find(3)) + " (expected: true)");
        dsu.rollback();
        System.out.println("Find(0)==Find(3) after rollback: " + (dsu.find(0) == dsu.find(3)) + " (expected: false)");
    }
}
