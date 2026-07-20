package com.capstone.cache;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public abstract class EvictionPolicy<K, V> {
    protected final Map<K, V> cache = new ConcurrentHashMap<>();
    protected final long maxSize;
    protected final long ttlMillis;

    protected EvictionPolicy(long maxSize, long ttlMillis) {
        this.maxSize = maxSize;
        this.ttlMillis = ttlMillis;
    }

    public abstract void put(K key, V value);
    public abstract Optional<V> get(K key);
    public abstract boolean remove(K key);
    public abstract void clear();
    public abstract int size();

    public static class LRUPolicy<K, V> extends EvictionPolicy<K, V> {
        private final LinkedHashMap<K, Long> accessOrder = new LinkedHashMap<>(16, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<K, Long> eldest) {
                return size() > maxSize;
            }
        };

        public LRUPolicy(long maxSize, long ttlMillis) { super(maxSize, ttlMillis); }

        @Override
        public synchronized void put(K key, V value) {
            cache.put(key, value);
            accessOrder.put(key, System.currentTimeMillis());
            if (cache.size() > maxSize) evict();
        }

        @Override
        public synchronized Optional<V> get(K key) {
            V val = cache.get(key);
            if (val == null) return Optional.empty();
            accessOrder.get(key);
            if (isExpired(key)) { remove(key); return Optional.empty(); }
            return Optional.of(val);
        }

        @Override
        public synchronized boolean remove(K key) {
            accessOrder.remove(key);
            return cache.remove(key) != null;
        }

        @Override
        public synchronized void clear() { cache.clear(); accessOrder.clear(); }

        @Override
        public int size() { return cache.size(); }

        private void evict() {
            Iterator<K> it = accessOrder.keySet().iterator();
            if (it.hasNext()) {
                K eldest = it.next();
                it.remove();
                cache.remove(eldest);
            }
        }

        private boolean isExpired(K key) { return false; }
    }

    public static class LFUPolicy<K, V> extends EvictionPolicy<K, V> {
        private final Map<K, AtomicLong> frequencies = new ConcurrentHashMap<>();

        public LFUPolicy(long maxSize, long ttlMillis) { super(maxSize, ttlMillis); }

        @Override
        public synchronized void put(K key, V value) {
            cache.put(key, value);
            frequencies.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
            if (cache.size() > maxSize) evict();
        }

        @Override
        public synchronized Optional<V> get(K key) {
            V val = cache.get(key);
            if (val == null) return Optional.empty();
            frequencies.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
            return Optional.of(val);
        }

        @Override
        public synchronized boolean remove(K key) {
            frequencies.remove(key);
            return cache.remove(key) != null;
        }

        @Override
        public synchronized void clear() { cache.clear(); frequencies.clear(); }

        @Override
        public int size() { return cache.size(); }

        private void evict() {
            K minKey = frequencies.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().get()))
                .map(Map.Entry::getKey).orElse(null);
            if (minKey != null) { cache.remove(minKey); frequencies.remove(minKey); }
        }
    }

    public static class TTLPolicy<K, V> extends EvictionPolicy<K, V> {
        private final Map<K, Instant> expiries = new ConcurrentHashMap<>();

        public TTLPolicy(long maxSize, long ttlMillis) { super(maxSize, ttlMillis); }

        @Override
        public void put(K key, V value) {
            cache.put(key, value);
            expiries.put(key, Instant.now().plusMillis(ttlMillis));
            if (cache.size() > maxSize) evict();
        }

        @Override
        public Optional<V> get(K key) {
            V val = cache.get(key);
            if (val == null) return Optional.empty();
            if (isExpired(key)) { remove(key); return Optional.empty(); }
            return Optional.of(val);
        }

        @Override
        public boolean remove(K key) {
            expiries.remove(key);
            return cache.remove(key) != null;
        }

        @Override
        public void clear() { cache.clear(); expiries.clear(); }

        @Override
        public int size() { return cache.size(); }

        private void evict() {
            K oldest = expiries.entrySet().stream()
                .min(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey).orElse(null);
            if (oldest != null) { cache.remove(oldest); expiries.remove(oldest); }
        }

        private boolean isExpired(K key) {
            Instant exp = expiries.get(key);
            return exp != null && Instant.now().isAfter(exp);
        }
    }
}
