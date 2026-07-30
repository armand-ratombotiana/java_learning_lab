package com.systemdesign.deep.lab04;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Lab 04: Consistent Hashing — Ring-based hashing, virtual nodes,
 * replication, sharding, and dynamic cluster management.
 */
public class ConsistentHashingLab {

    // Simple hash function using FNV-1a
    static int hash(String key) {
        int hash = 0x811c9dc5;
        for (byte b : key.getBytes(StandardCharsets.UTF_8)) {
            hash ^= (b & 0xff);
            hash *= 0x01000193;
        }
        return hash & 0x7fffffff;
    }

    // ──────────────────────────────────────────────
    // 1. Consistent Hash Ring
    // ──────────────────────────────────────────────
    static class ConsistentHashRing {
        final TreeMap<Integer, String> ring = new TreeMap<>();
        final int virtualNodes;
        final Function<String, Integer> hashFn;

        ConsistentHashRing(int virtualNodes, Function<String, Integer> hashFn) {
            this.virtualNodes = virtualNodes;
            this.hashFn = hashFn;
        }

        void addNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                int h = hashFn.apply(nodeId + ":" + i);
                ring.put(h, nodeId);
            }
        }

        void removeNode(String nodeId) {
            ring.entrySet().removeIf(e -> e.getValue().equals(nodeId));
        }

        String getNode(String key) {
            if (ring.isEmpty()) return null;
            int h = hashFn.apply(key);
            var entry = ring.ceilingEntry(h);
            if (entry == null) entry = ring.firstEntry();
            return entry.getValue();
        }

        // Get all nodes responsible for a key (replication)
        List<String> getNodes(String key, int replicationFactor) {
            if (ring.isEmpty()) return List.of();
            int h = hashFn.apply(key);
            var result = new LinkedHashSet<String>();
            var entry = ring.ceilingEntry(h);
            if (entry == null) entry = ring.firstEntry();
            for (int i = 0; i < ring.size() && result.size() < replicationFactor; i++) {
                if (entry == null || entry == ring.firstEntry() && i > 0) break;
                result.add(entry.getValue());
                entry = ring.higherEntry(entry.getKey());
                if (entry == null) entry = ring.firstEntry();
            }
            return List.copyOf(result);
        }

        int nodeCount() {
            return (int) ring.values().stream().distinct().count();
        }

        Map<String, Long> distribution() {
            return ring.values().stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        }
    }

    // ──────────────────────────────────────────────
    // 2. Distribution Analysis
    // ──────────────────────────────────────────────
    static class DistributionAnalysis {

        static void analyze(String label, ConsistentHashRing ring, int totalKeys) {
            Map<String, Long> counts = new HashMap<>();
            for (int i = 0; i < totalKeys; i++) {
                String node = ring.getNode("key-" + i);
                counts.merge(node, 1L, Long::sum);
            }
            var vals = counts.values();
            double avg = vals.stream().mapToLong(Long::longValue).average().orElse(0);
            double variance = vals.stream()
                    .mapToDouble(v -> Math.pow(v - avg, 2))
                    .average().orElse(0);

            System.out.println("  " + label + ":");
            System.out.println("    Nodes: " + ring.nodeCount() + ", Keys: " + totalKeys);
            System.out.println("    Avg keys/node: " + String.format("%.1f", avg));
            System.out.println("    Std deviation: " + String.format("%.1f", Math.sqrt(variance)));
            counts.forEach((node, count) ->
                    System.out.println("    " + node + ": " + count));
        }
    }

    // ──────────────────────────────────────────────
    // 3. Replicated KV Store
    // ──────────────────────────────────────────────
    static class ReplicatedKVStore {
        final ConsistentHashRing ring;
        final int replicationFactor;
        final Map<String, String> store = new ConcurrentHashMap<>();

        ReplicatedKVStore(ConsistentHashRing ring, int replicationFactor) {
            this.ring = ring;
            this.replicationFactor = replicationFactor;
        }

        void put(String key, String value) {
            var nodes = ring.getNodes(key, replicationFactor);
            for (var node : nodes) {
                store.put(node + ":" + key, value);
            }
            System.out.println("  [PUT] " + key + " -> replicated to " + nodes);
        }

        String get(String key, int consistencyLevel) {
            var nodes = ring.getNodes(key, replicationFactor);
            List<String> responses = new ArrayList<>();
            for (var node : nodes) {
                String val = store.get(node + ":" + key);
                if (val != null) responses.add(val);
                if (responses.size() >= consistencyLevel) break;
            }
            String result = responses.isEmpty() ? null : responses.get(0);
            System.out.println("  [GET] " + key + " -> " + result + " (read from "
                    + responses.size() + "/" + nodes.size() + " nodes)");
            return result;
        }

        void simulateNodeFailure(String nodeId) {
            System.out.println("  [FAILURE] Node " + nodeId + " is down");
            ring.removeNode(nodeId);
        }

        void addNode(String nodeId) {
            System.out.println("  [ADD] Node " + nodeId + " joining the ring");
            ring.addNode(nodeId);
        }
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 04: Consistent Hashing Deep-Dive       ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // 1. Distribution: no virtual nodes vs virtual nodes
        System.out.println("1. Distribution Analysis");
        var ringNoVnodes = new ConsistentHashRing(1, ConsistentHashingLab::hash);
        for (char c = 'A'; c <= 'E'; c++) ringNoVnodes.addNode("node-" + c);

        var ringWithVnodes = new ConsistentHashRing(150, ConsistentHashingLab::hash);
        for (char c = 'A'; c <= 'E'; c++) ringWithVnodes.addNode("node-" + c);

        DistributionAnalysis.analyze("Without virtual nodes", ringNoVnodes, 1000);
        System.out.println();
        DistributionAnalysis.analyze("With virtual nodes (150/node)", ringWithVnodes, 1000);
        System.out.println();

        // 2. Node addition/removal impact
        System.out.println("2. Node Addition/Removal Impact");
        var ring = new ConsistentHashRing(100, ConsistentHashingLab::hash);
        for (char c = 'A'; c <= 'D'; c++) ring.addNode("node-" + c);

        Map<String, String> initialMapping = new HashMap<>();
        for (int i = 0; i < 500; i++) {
            initialMapping.put("key-" + i, ring.getNode("key-" + i));
        }

        // Add a node and check how many keys moved
        ring.addNode("node-E");
        int moved = 0;
        for (int i = 0; i < 500; i++) {
            String newNode = ring.getNode("key-" + i);
            if (!newNode.equals(initialMapping.get("key-" + i))) moved++;
        }
        System.out.println("  Keys remapped after adding node-E: " + moved + "/500 ("
                + String.format("%.1f", moved / 5.0) + "%)");
        System.out.println();

        // 3. Replicated KV Store
        System.out.println("3. Replicated KV Store Demo");
        var kvRing = new ConsistentHashRing(50, ConsistentHashingLab::hash);
        for (char c = 'A'; c <= 'D'; c++) kvRing.addNode("node-" + c);
        var kv = new ReplicatedKVStore(kvRing, 3);

        kv.put("user:42", "Alice");
        kv.put("user:99", "Bob");

        kv.get("user:42", 1);
        kv.get("user:99", 2);

        // Simulate node failure and read repair
        System.out.println("  --- Node failure simulation ---");
        kv.simulateNodeFailure("node-B");
        kv.get("user:42", 2); // Should degrade gracefully
        System.out.println();

        // 4. Ring statistics
        System.out.println("4. Ring Statistics");
        System.out.println("  Ring size (total entries): " + kvRing.ring.size());
        System.out.println("  Physical nodes: " + kvRing.nodeCount());
        System.out.println();

        System.out.println("All consistent hashing concepts demonstrated successfully.");
    }
}
