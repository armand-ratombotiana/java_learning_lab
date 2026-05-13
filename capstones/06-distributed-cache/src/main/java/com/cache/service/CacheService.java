package com.cache.service;

import com.cache.cluster.ConsistentHashRing;
import com.cache.core.InMemoryCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final Map<String, InMemoryCache> nodeCaches = new ConcurrentHashMap<>();
    private final ConsistentHashRing hashRing = new ConsistentHashRing();

    public void registerNode(String nodeId) {
        InMemoryCache cache = new InMemoryCache();
        nodeCaches.put(nodeId, cache);
        hashRing.addNode(nodeId);
        log.info("Registered cache node: {}", nodeId);
    }

    public void set(String key, String value) {
        set(key, value, 3600000);
    }

    public void set(String key, String value, long ttlMs) {
        String nodeId = hashRing.getNode(key);
        if (nodeId != null) {
            InMemoryCache cache = nodeCaches.get(nodeId);
            if (cache != null) {
                cache.set(key, value, ttlMs);
                log.debug("Set {} on node {}", key, nodeId);
            }
        }
    }

    public Optional<String> get(String key) {
        String nodeId = hashRing.getNode(key);
        if (nodeId != null) {
            InMemoryCache cache = nodeCaches.get(nodeId);
            if (cache != null) {
                return cache.get(key);
            }
        }
        return Optional.empty();
    }

    public boolean delete(String key) {
        String nodeId = hashRing.getNode(key);
        if (nodeId != null) {
            InMemoryCache cache = nodeCaches.get(nodeId);
            if (cache != null) {
                return cache.delete(key);
            }
        }
        return false;
    }

    public boolean setIfAbsent(String key, String value, long ttlMs) {
        String nodeId = hashRing.getNode(key);
        if (nodeId != null) {
            InMemoryCache cache = nodeCaches.get(nodeId);
            if (cache != null && cache.get(key).isEmpty()) {
                cache.set(key, value, ttlMs);
                return true;
            }
        }
        return false;
    }

    public long increment(String key) {
        String nodeId = hashRing.getNode(key);
        if (nodeId != null) {
            InMemoryCache cache = nodeCaches.get(nodeId);
            if (cache != null) {
                Optional<String> current = cache.get(key);
                long newValue = current.map(v -> Long.parseLong(v) + 1).orElse(1L);
                cache.set(key, String.valueOf(newValue));
                return newValue;
            }
        }
        return -1;
    }

    public Set<String> keys(String pattern) {
        Set<String> allKeys = new java.util.HashSet<>();
        for (InMemoryCache cache : nodeCaches.values()) {
            allKeys.addAll(cache.keys(pattern));
        }
        return allKeys;
    }

    public Map<String, Long> getClusterStats() {
        Map<String, Long> stats = new ConcurrentHashMap<>();
        nodeCaches.forEach((nodeId, cache) -> {
            stats.put(nodeId + "_size", cache.size());
        });
        return stats;
    }
}