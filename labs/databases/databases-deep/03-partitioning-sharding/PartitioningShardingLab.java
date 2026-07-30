package com.databases.deep.lab03;

import java.security.MessageDigest;
import java.util.*;

/**
 * PartitioningShardingLab — implements consistent hashing with virtual nodes
 * and simulates data distribution and rebalancing.
 */
public class PartitioningShardingLab {

    static class ConsistentHashRing {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int virtualNodes;
        private final MessageDigest md5;

        ConsistentHashRing(int virtualNodes) throws Exception {
            this.virtualNodes = virtualNodes;
            this.md5 = MessageDigest.getInstance("MD5");
        }

        void addNode(String node) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(node + "#" + i);
                ring.put(hash, node);
            }
        }

        void removeNode(String node) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(node + "#" + i);
                ring.remove(hash);
            }
        }

        String getNode(String key) {
            if (ring.isEmpty()) return null;
            int hash = hash(key);
            var entry = ring.ceilingEntry(hash);
            if (entry == null) entry = ring.firstEntry();
            return entry.getValue();
        }

        private int hash(String s) {
            byte[] digest = md5.digest(s.getBytes());
            return ((digest[0] & 0xFF) << 24) | ((digest[1] & 0xFF) << 16)
                 | ((digest[2] & 0xFF) << 8) | (digest[3] & 0xFF);
        }

        Map<String, Integer> distribution() {
            Map<String, Integer> dist = new HashMap<>();
            for (var node : ring.values()) dist.merge(node, 1, Integer::sum);
            return dist;
        }
    }

    public static void main(String[] args) throws Exception {
        ConsistentHashRing ring = new ConsistentHashRing(150);
        ring.addNode("Shard-1");
        ring.addNode("Shard-2");
        ring.addNode("Shard-3");

        System.out.println("Virtual node distribution: " + ring.distribution());

        // Simulate 100,000 keys
        Map<String, Integer> keyDist = new HashMap<>();
        for (int i = 0; i < 100_000; i++) {
            String key = "key-" + i;
            String node = ring.getNode(key);
            keyDist.merge(node, 1, Integer::sum);
        }
        System.out.println("Key distribution: " + keyDist);

        // Add a new node and show rebalancing
        ring.addNode("Shard-4");
        Map<String, Integer> newDist = new HashMap<>();
        for (int i = 0; i < 100_000; i++) {
            String key = "key-" + i;
            String node = ring.getNode(key);
            newDist.merge(node, 1, Integer::sum);
        }
        System.out.println("After adding Shard-4: " + newDist);

        // Count moved keys
        int moved = 0;
        for (int i = 0; i < 100_000; i++) {
            String key = "key-" + i;
            String nodeBefore = ring.getNode(key);
            ring.removeNode("Shard-4");
            String nodeAfter = ring.getNode(key);
            if (!nodeBefore.equals(nodeAfter)) moved++;
            ring.addNode("Shard-4");
        }
        System.out.println("Keys that would move without Shard-4: " + moved + " (expected ~25,000)");
    }
}