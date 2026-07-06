package com.algo.lab23;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Consistent Hash load balancer using a hash ring.
 * Minimizes remapping when servers are added or removed.
 * Time: O(log n) per request, Space: O(n * v) where v = virtual nodes per server
 */
public class ConsistentHashBalancer implements LoadBalancer {

    private final int virtualNodes;
    private final TreeMap<Long, String> ring = new TreeMap<>();

    public ConsistentHashBalancer(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addServer(String server) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash(server + "-vn-" + i), server);
        }
    }

    public void removeServer(String server) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash(server + "-vn-" + i));
        }
    }

    @Override
    public String selectServer(List<String> servers, String request) {
        if (ring.isEmpty() || request == null) return null;
        Long hash = hash(request);
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xFF);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            long hash = 7;
            for (char c : key.toCharArray()) {
                hash = hash * 31 + c;
            }
            return hash;
        }
    }

    public int getRingSize() {
        return ring.size();
    }
}
