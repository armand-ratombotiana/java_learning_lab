package com.algorithms.loadbalancing;

import java.util.*;
import java.security.MessageDigest;

/**
 * Custom: Load Balancing Algorithms
 * Round Robin, Least Connections, Consistent Hashing.
 *
 * Time Complexity: O(1) per request
 * Space Complexity: O(n) for server pool
 */
public class LoadBalancingExamples {

    // Round Robin
    public static class RoundRobin {
        private final List<String> servers;
        private int index = 0;

        public RoundRobin(List<String> servers) { this.servers = servers; }

        public String next() {
            String server = servers.get(index);
            index = (index + 1) % servers.size();
            return server;
        }
    }

    // Consistent Hashing
    public static class ConsistentHash {
        private final TreeMap<Integer, String> ring = new TreeMap<>();
        private final int replicas;

        public ConsistentHash(List<String> servers, int replicas) {
            this.replicas = replicas;
            for (String server : servers) {
                for (int i = 0; i < replicas; i++) {
                    ring.put(hash(server + "#" + i), server);
                }
            }
        }

        private int hash(String key) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(key.getBytes());
                int h = 0;
                for (int i = 0; i < 4; i++) h = (h << 8) | (digest[i] & 0xFF);
                return h;
            } catch (Exception e) { return key.hashCode(); }
        }

        public String getServer(String key) {
            if (ring.isEmpty()) return null;
            int hash = hash(key);
            Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
            if (entry == null) entry = ring.firstEntry();
            return entry.getValue();
        }
    }

    public static void main(String[] args) {
        List<String> servers = List.of("S1", "S2", "S3");
        RoundRobin rr = new RoundRobin(servers);
        System.out.println("Round Robin:");
        for (int i = 0; i < 6; i++) System.out.print(rr.next() + " ");
        System.out.println("(expected: S1 S2 S3 S1 S2 S3)");

        ConsistentHash ch = new ConsistentHash(servers, 3);
        System.out.println("\nConsistent Hashing:");
        System.out.println("key1 -> " + ch.getServer("key1"));
        System.out.println("key2 -> " + ch.getServer("key2"));
        System.out.println("key3 -> " + ch.getServer("key3"));
    }
}
