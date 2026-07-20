package com.capstone.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashRing {
    private final TreeMap<Long, CacheNode> ring = new TreeMap<>();
    private final int virtualNodes;
    private final HashFunction hashFn;

    @FunctionalInterface
    public interface HashFunction {
        long hash(String key);
    }

    public ConsistentHashRing() { this(150, ConsistentHashRing::md5Hash); }

    public ConsistentHashRing(int virtualNodes, HashFunction hashFn) {
        this.virtualNodes = virtualNodes;
        this.hashFn = hashFn;
    }

    public record CacheNode(String nodeId, String host, int port, boolean active) {
        public CacheNode {
            if (nodeId == null || nodeId.isBlank()) throw new IllegalArgumentException("Node ID required");
        }
    }

    public void addNode(CacheNode node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hashFn.hash(node.nodeId() + "#" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(String nodeId) {
        ring.entrySet().removeIf(e -> e.getValue().nodeId().equals(nodeId));
    }

    public Optional<CacheNode> getNode(String key) {
        if (ring.isEmpty()) return Optional.empty();
        long hash = hashFn.hash(key);
        Map.Entry<Long, CacheNode> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();
        return Optional.of(entry.getValue());
    }

    public List<CacheNode> getNodes(String key, int count) {
        if (ring.isEmpty()) return List.of();
        long hash = hashFn.hash(key);
        Set<String> seen = new HashSet<>();
        List<CacheNode> result = new ArrayList<>();
        NavigableMap<Long, CacheNode> tailMap = ring.tailMap(hash, true);
        for (Map.Entry<Long, CacheNode> e : tailMap.entrySet()) {
            if (seen.add(e.getValue().nodeId())) result.add(e.getValue());
            if (result.size() >= count) break;
        }
        if (result.size() < count) {
            for (Map.Entry<Long, CacheNode> e : ring.headMap(hash, false).entrySet()) {
                if (seen.add(e.getValue().nodeId())) result.add(e.getValue());
                if (result.size() >= count) break;
            }
        }
        return result;
    }

    public int getNodeCount() {
        return (int) ring.values().stream().map(CacheNode::nodeId).distinct().count();
    }

    public int getVirtualNodeCount() { return ring.size(); }

    public void clear() { ring.clear(); }

    CacheNode getNodeAtPosition(long position) { return ring.get(position); }

    private static long md5Hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes());
            long hash = 0;
            for (int i = 0; i < 8; i++) hash = (hash << 8) | (digest[i] & 0xFF);
            return hash & Long.MAX_VALUE;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
