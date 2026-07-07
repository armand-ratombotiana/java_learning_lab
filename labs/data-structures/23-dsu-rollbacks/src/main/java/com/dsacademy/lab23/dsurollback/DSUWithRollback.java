package com.dsacademy.lab23.dsurollback;

import java.util.ArrayDeque;
import java.util.Deque;

public class DSUWithRollback {

    private final int[] parent;
    private final int[] size;
    private int sets;
    private final Deque<Change> history;

    private static class Change {
        final int x, prevParent, y, prevSize;
        final int prevSets;
        Change(int x, int prevParent, int y, int prevSize, int prevSets) {
            this.x = x; this.prevParent = prevParent;
            this.y = y; this.prevSize = prevSize;
            this.prevSets = prevSets;
        }
    }

    public DSUWithRollback(int n) {
        parent = new int[n];
        size = new int[n];
        sets = n;
        history = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public int find(int x) {
        while (parent[x] != x) {
            x = parent[x];
        }
        return x;
    }

    public boolean union(int a, int b) {
        int ra = find(a);
        int rb = find(b);
        if (ra == rb) {
            history.push(new Change(-1, -1, -1, -1, sets));
            return false;
        }
        if (size[ra] < size[rb]) {
            int tmp = ra; ra = rb; rb = tmp;
        }
        history.push(new Change(rb, parent[rb], ra, size[ra], sets));
        parent[rb] = ra;
        size[ra] += size[rb];
        sets--;
        return true;
    }

    public void snapshot() {
        history.push(new Change(-2, -1, -2, -1, sets));
    }

    public void rollback() {
        while (!history.isEmpty()) {
            Change c = history.pop();
            if (c.x == -2) break;
            if (c.x == -1) continue;
            parent[c.x] = c.prevParent;
            size[c.y] = c.prevSize;
            sets = c.prevSets;
        }
    }

    public void rollbackToSnapshot() {
        rollback();
    }

    public int getSets() { return sets; }

    public boolean connected(int a, int b) {
        return find(a) == find(b);
    }
}
