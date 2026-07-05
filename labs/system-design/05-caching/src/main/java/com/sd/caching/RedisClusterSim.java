package com.sd.caching;

import java.util.*;
import java.util.concurrent.*;
import java.security.MessageDigest;

public class RedisClusterSim {

    public static class RedisNode {
        private final String id;
        private final Map<String, String> data = new ConcurrentHashMap<>();
        private final int capacity;

        public RedisNode(String id, int capacity) {
            this.id = id;
            this.capacity = capacity;
        }

        public String get(String key) { return data.get(key); }

        public boolean set(String key, String value) {
            if (data.size() >= capacity && !data.containsKey(key)) {
                return false;
            }
            data.put(key, value);
            return true;
        }

        public int size() { return data.size(); }
        public String getId() { return id; }
    }

    public static class RedisCluster {
        private final List<RedisNode> nodes;
        private static final int SLOT_COUNT = 16384;

        public RedisCluster(List<RedisNode> nodes) {
            this.nodes = nodes;
            System.out.println("Redis Cluster with " + nodes.size() + " nodes, " + SLOT_COUNT + " slots");
        }

        private int getSlot(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("CRC16");
                byte[] digest = md.digest(key.getBytes());
                int crc = 0;
                for (byte b : digest) {
                    crc = (crc << 8) ^ (b & 0xFF);
                }
                return Math.abs(crc) % SLOT_COUNT;
            } catch (Exception e) {
                return Math.abs(key.hashCode()) % SLOT_COUNT;
            }
        }

        private RedisNode getNode(String key) {
            int slot = getSlot(key);
            int nodeIdx = slot % nodes.size();
            return nodes.get(nodeIdx);
        }

        public String get(String key) {
            RedisNode node = getNode(key);
            String val = node.get(key);
            System.out.println("GET " + key + " -> slot=" + getSlot(key) + " -> " + node.getId() + " = " + val);
            return val;
        }

        public boolean set(String key, String value) {
            RedisNode node = getNode(key);
            boolean ok = node.set(key, value);
            System.out.println("SET " + key + "=" + value + " -> slot=" + getSlot(key) + " -> " + node.getId()
                + " " + (ok ? "OK" : "FAILED"));
            return ok;
        }

        public void printStats() {
            System.out.println("\nCluster Stats:");
            nodes.forEach(n -> System.out.println("  " + n.getId() + ": " + n.size() + " keys"));
        }
    }

    public static void main(String[] args) {
        List<RedisNode> nodes = Arrays.asList(
            new RedisNode("redis-1", 100), new RedisNode("redis-2", 100), new RedisNode("redis-3", 100)
        );
        RedisCluster cluster = new RedisCluster(nodes);

        cluster.set("user:100", "Alice");
        cluster.set("user:200", "Bob");
        cluster.set("session:abc", "token-xyz");
        cluster.set("cart:42", "laptop");
        cluster.set("cache:homepage", "cached-html");

        cluster.get("user:100");
        cluster.get("unknown");
        cluster.printStats();
    }
}
