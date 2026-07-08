package com.distributed.distributedcaching;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CacheAsideStrategy<K, V> implements CacheClient<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final AtomicLong hits = new AtomicLong(0);
    private final AtomicLong misses = new AtomicLong(0);

    private record CacheEntry<V>(V value, long expiry) {
        boolean isExpired() { return System.currentTimeMillis() > expiry; }
    }

    @Override
    public Optional<V> get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            if (entry != null) cache.remove(key, entry);
            misses.incrementAndGet();
            return Optional.empty();
        }
        hits.incrementAndGet();
        return Optional.of(entry.value());
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttl.toMillis()));
    }

    @Override
    public boolean delete(K key) {
        return cache.remove(key) != null;
    }

    @Override
    public boolean exists(K key) {
        CacheEntry<V> entry = cache.get(key);
        return entry != null && !entry.isExpired();
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public double getHitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0.0 : (double) hits.get() / total;
    }

    public long getHits() { return hits.get(); }
    public long getMisses() { return misses.get(); }
}
