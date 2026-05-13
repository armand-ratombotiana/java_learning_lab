package com.cache.cluster;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConsistentHashRing {

    private static final int VIRTUAL_NODES = 150;
    private final SortedMap<Integer, String> ring = new java.util.concurrent.ConcurrentSkipListMap<>();

    public void addNode(String nodeId) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            int hash = hash(nodeId + "#" + i);
            ring.put(hash, nodeId);
        }
        log.info("Added node to ring: {}", nodeId);
    }

    public void removeNode(String nodeId) {
        for (int i = 0; i < VIRTUAL_NODES; i++) {
            int hash = hash(nodeId + "#" + i);
            ring.remove(hash);
        }
        log.info("Removed node from ring: {}", nodeId);
    }

    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        int hash = hash(key);
        SortedMap<Integer, String> tail = ring.tailMap(hash);
        Integer nodeHash = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
        return ring.get(nodeHash);
    }

    public List<String> getReplicas(String key, int count) {
        if (ring.isEmpty()) {
            return List.of();
        }

        int hash = hash(key);
        SortedMap<Integer, String> tail = ring.tailMap(hash);

        List<String> replicas = new java.util.ArrayList<>();
        java.util.ArrayList<Integer> keys = new java.util.ArrayList<>(ring.keySet());

        int startIndex = tail.isEmpty() ? 0 : keys.indexOf(tail.firstKey());

        for (int i = 0; i < count && i < keys.size(); i++) {
            int index = (startIndex + i) % keys.size();
            String node = ring.get(keys.get(index));
            if (!replicas.contains(node)) {
                replicas.add(node);
            }
        }

        return replicas;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode());
    }

    public int getNodeCount() {
        return (int) ring.values().stream().distinct().count();
    }
}