package com.capstone.cache;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class CacheClient {
    private final ConsistentHashRing ring;
    private final Map<String, Map<String, byte[]>> localStore;
    private final Map<String, Long> timestamps;
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);
    private final AtomicLong ops = new AtomicLong(0);
    private EvictionPolicy<String, byte[]> evictionPolicy;

    public CacheClient(ConsistentHashRing ring) {
        this.ring = ring;
        this.localStore = new ConcurrentHashMap<>();
        this.timestamps = new ConcurrentHashMap<>();
    }

    public CacheClient(ConsistentHashRing ring, EvictionPolicy<String, byte[]> evictionPolicy) {
        this(ring);
        this.evictionPolicy = evictionPolicy;
    }

    public void put(String key, byte[] value) {
        ConsistentHashRing.CacheNode node = ring.getNode(key)
            .orElseThrow(() -> new IllegalStateException("No available nodes"));
        localStore.computeIfAbsent(node.nodeId(), k -> new ConcurrentHashMap<>()).put(key, value);
        timestamps.put(key, System.currentTimeMillis());
        ops.incrementAndGet();
        if (evictionPolicy != null) evictionPolicy.put(key, value);
    }

    public Optional<byte[]> get(String key) {
        ConsistentHashRing.CacheNode node = ring.getNode(key).orElse(null);
        if (node == null) { misses.incrementAndGet(); return Optional.empty(); }
        Map<String, byte[]> nodeStore = localStore.get(node.nodeId());
        if (nodeStore == null) { misses.incrementAndGet(); return Optional.empty(); }
        byte[] val = nodeStore.get(key);
        if (val != null) { hits.incrementAndGet(); ops.incrementAndGet(); return Optional.of(val); }
        misses.incrementAndGet();
        return Optional.empty();
    }

    public boolean delete(String key) {
        ConsistentHashRing.CacheNode node = ring.getNode(key).orElse(null);
        if (node == null) return false;
        Map<String, byte[]> nodeStore = localStore.get(node.nodeId());
        if (nodeStore == null) return false;
        ops.incrementAndGet();
        timestamps.remove(key);
        if (evictionPolicy != null) evictionPolicy.remove(key);
        return nodeStore.remove(key) != null;
    }

    public CacheStats getStats() {
        return new CacheStats(hits.get(), misses.get(), ops.get(),
            hits.get() + misses.get() > 0 ? (double) hits.get() / (hits.get() + misses.get()) * 100.0 : 0.0);
    }

    public record CacheStats(long hits, long misses, long totalOps, double hitRate) {}

    public void clear() {
        localStore.clear();
        timestamps.clear();
        hits.set(0); misses.set(0); ops.set(0);
        if (evictionPolicy != null) evictionPolicy.clear();
    }
}
