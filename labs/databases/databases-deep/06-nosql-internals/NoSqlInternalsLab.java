package com.databases.deep.lab06;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * NoSqlInternalsLab — implements a minimal LSM-tree (MemTable + SSTables),
 * a simple B-tree node, and a graph adjacency-list BFS.
 */
public class NoSqlInternalsLab {

    // --- Minimal LSM-tree ---
    static class LSMTree {
        NavigableMap<String, String> memTable = new ConcurrentSkipListMap<>();
        final List<NavigableMap<String, String>> sstables = new ArrayList<>();
        final int memTableSizeLimit;

        LSMTree(int limit) { this.memTableSizeLimit = limit; }

        void put(String key, String value) {
            memTable.put(key, value);
            if (memTable.size() >= memTableSizeLimit) flush();
        }

        synchronized void flush() {
            if (memTable.isEmpty()) return;
            sstables.add(memTable);
            memTable = new ConcurrentSkipListMap<>();
        }

        String get(String key) {
            String val = memTable.get(key);
            if (val != null) return val;
            for (int i = sstables.size() - 1; i >= 0; i--) {
                val = sstables.get(i).get(key);
                if (val != null) return val;
            }
            return null;
        }

        int sstableCount() { return sstables.size() + (memTable.isEmpty() ? 0 : 1); }
    }

    // --- Simple B-tree Node ---
    static class BTreeNode {
        final List<Integer> keys = new ArrayList<>();
        final List<BTreeNode> children = new ArrayList<>();
        boolean leaf = true;

        void insert(int key) {
            int pos = Collections.binarySearch(keys, key);
            if (pos >= 0) return; // duplicate
            pos = -pos - 1;
            keys.add(pos, key);
        }
    }

    // --- Graph BFS ---
    static Map<String, List<String>> buildGraph() {
        var g = new HashMap<String, List<String>>();
        g.put("A", List.of("B", "C"));
        g.put("B", List.of("A", "D", "E"));
        g.put("C", List.of("A", "F"));
        g.put("D", List.of("B"));
        g.put("E", List.of("B", "F"));
        g.put("F", List.of("C", "E"));
        return g;
    }

    static void bfs(Map<String, List<String>> graph, String start) {
        var visited = new HashSet<String>();
        var queue = new ArrayDeque<String>();
        queue.add(start);
        visited.add(start);
        System.out.print("BFS from " + start + ": ");
        while (!queue.isEmpty()) {
            String node = queue.poll();
            System.out.print(node + " ");
            for (var neighbor : graph.getOrDefault(node, List.of())) {
                if (visited.add(neighbor)) queue.add(neighbor);
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        System.out.println("=== LSM-tree Simulation ===");
        LSMTree lsm = new LSMTree(3);
        lsm.put("a", "1"); lsm.put("b", "2"); lsm.put("c", "3");
        lsm.put("d", "4"); // triggers flush
        lsm.put("e", "5"); lsm.put("f", "6");
        lsm.flush();
        System.out.println("SSTables: " + lsm.sstableCount());
        System.out.println("get(a)=" + lsm.get("a") + " get(d)=" + lsm.get("d") + " get(z)=" + lsm.get("z"));

        System.out.println("\n=== B-tree Node ===");
        BTreeNode root = new BTreeNode();
        root.insert(10); root.insert(5); root.insert(15); root.insert(3);
        System.out.println("B-tree root keys: " + root.keys);

        System.out.println("\n=== Graph BFS ===");
        bfs(buildGraph(), "A");
    }
}