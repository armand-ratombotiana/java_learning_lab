package com.learning.hazelcast;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class Lab {
    // In-memory data grid node
    static class HazelcastNode {
        final String name;
        final Map<String, String> localMap = new ConcurrentHashMap<>();
        final List<HazelcastNode> cluster = new CopyOnWriteArrayList<>();
        final Map<String, List<Listener>> listeners = new ConcurrentHashMap<>();

        HazelcastNode(String name) { this.name = name; }

        interface Listener { void onEvent(String key, String value, String type); }

        void join(HazelcastNode... others) {
            for (var other : others) {
                if (!cluster.contains(other)) cluster.add(other);
                if (!other.cluster.contains(this)) other.cluster.add(this);
            }
            System.out.println("  [" + name + "] Joined cluster, size=" + (cluster.size() + 1));
        }

        void put(String key, String value) {
            localMap.put(key, value);
            notifyListeners(key, value, "PUT");
            for (var node : cluster) node.replicate(key, value);
        }

        String get(String key) {
            var val = localMap.get(key);
            if (val == null) {
                for (var node : cluster) {
                    val = node.localMap.get(key);
                    if (val != null) break;
                }
            }
            return val;
        }

        void replicate(String key, String value) {
            localMap.put(key, value);
            notifyListeners(key, value, "REPLICATE");
        }

        void addListener(String pattern, Listener listener) {
            listeners.computeIfAbsent(pattern, p -> new CopyOnWriteArrayList<>()).add(listener);
        }

        void notifyListeners(String key, String value, String type) {
            listeners.forEach((pattern, list) -> {
                if (key.startsWith(pattern.replace("*", ""))) {
                    list.forEach(l -> l.onEvent(key, value, type));
                }
            });
        }

        void remove(String key) {
            localMap.remove(key);
            for (var node : cluster) node.localMap.remove(key);
        }

        int size() { return localMap.size(); }

        Map<String, String> getAll() {
            var all = new HashMap<>(localMap);
            for (var node : cluster) all.putAll(node.localMap);
            return all;
        }
    }

    static class DistributedLock {
        private final String name;
        private volatile boolean locked = false;

        DistributedLock(String name) { this.name = name; }

        boolean tryLock() {
            if (!locked) { locked = true; return true; }
            return false;
        }

        void unlock() { locked = false; }

        void runLocked(Runnable task) {
            if (tryLock()) {
                try { task.run(); } finally { unlock(); }
            } else {
                System.out.println("  [Lock:" + name + "] Failed to acquire lock");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Hazelcast: In-Memory Data Grid ===");

        // Cluster
        System.out.println("\n--- Cluster Formation ---");
        var node1 = new HazelcastNode("node-1");
        var node2 = new HazelcastNode("node-2");
        var node3 = new HazelcastNode("node-3");
        node1.join(node2, node3);

        // Distributed Map
        System.out.println("\n--- Distributed Map ---");
        node1.put("user:1001", "Alice");
        node1.put("user:1002", "Bob");
        node2.put("user:1003", "Charlie");

        System.out.println("  node1.get(user:1003) = " + node1.get("user:1003"));
        System.out.println("  node2.get(user:1001) = " + node2.get("user:1001"));
        System.out.println("  node3.get(user:1002) = " + node3.get("user:1002"));

        // Listeners
        System.out.println("\n--- Entry Listeners ---");
        node1.addListener("user:", (k, v, t) -> System.out.println("  [Listener] " + t + " " + k + " = " + v));
        node3.put("user:1004", "Diana");

        // Distributed Locking
        System.out.println("\n--- Distributed Lock ---");
        var lock = new DistributedLock("inventory-lock");
        lock.runLocked(() -> System.out.println("  Critical section executed"));
        lock.runLocked(() -> System.out.println("  This should not run"));

        // Map operations
        System.out.println("\n--- Map Operations ---");
        node1.put("config:timeout", "5000");
        node1.put("config:retries", "3");
        System.out.println("  Cluster map size: " + node1.getAll().size());
        System.out.println("  All entries: " + node1.getAll());

        // Cache near vs distributed
        System.out.println("\n--- Cache Topology ---");
        System.out.println("  Near Cache: local read-only copy on each node");
        System.out.println("  Partitioned: data split across nodes (default)");
        System.out.println("  Replicated: full copy on every node");

        // Compute
        System.out.println("\n--- Entry Processor (Simulated) ---");
        var result = node1.getAll().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toUpperCase()));
        System.out.println("  Transformed: " + result);

        // Cluster info
        System.out.println("\n--- Cluster Info ---");
        System.out.println("  Node count: " + (node1.cluster.size() + 1));
        System.out.println("  Total entries: " + node1.getAll().size());
        System.out.println("  Node1 local: " + node1.size() + " entries");
        System.out.println("  Node2 local: " + node2.size() + " entries");
    }
}
