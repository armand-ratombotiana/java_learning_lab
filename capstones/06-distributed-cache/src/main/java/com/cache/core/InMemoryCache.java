package com.cache.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class InMemoryCache {

    private final Map<String, CacheEntry> storage = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final long defaultTtlMs = 3600000;

    public void set(String key, String value) {
        set(key, value, defaultTtlMs);
    }

    public void set(String key, String value, long ttlMs) {
        lock.writeLock().lock();
        try {
            long expiry = ttlMs > 0 ? System.currentTimeMillis() + ttlMs : Long.MAX_VALUE;
            storage.put(key, new CacheEntry(value, expiry));
            log.debug("Set key: {} with TTL: {}ms", key, ttlMs);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<String> get(String key) {
        lock.readLock().lock();
        try {
            CacheEntry entry = storage.get(key);
            if (entry == null) {
                return Optional.empty();
            }
            if (entry.isExpired()) {
                storage.remove(key);
                return Optional.empty();
            }
            return Optional.of(entry.value());
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean delete(String key) {
        lock.writeLock().lock();
        try {
            return storage.remove(key) != null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean exists(String key) {
        return get(key).isPresent();
    }

    public void expire(String key, long ttlMs) {
        lock.writeLock().lock();
        try {
            CacheEntry entry = storage.get(key);
            if (entry != null) {
                storage.put(key, new CacheEntry(entry.value(), System.currentTimeMillis() + ttlMs));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long ttl(String key) {
        lock.readLock().lock();
        try {
            CacheEntry entry = storage.get(key);
            if (entry == null) return -2;
            if (entry.isExpired()) return -2;
            long remaining = entry.expiry() - System.currentTimeMillis();
            return remaining > 0 ? remaining : -1;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            storage.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public long size() {
        return storage.size();
    }

    public Set<String> keys(String pattern) {
        lock.readLock().lock();
        try {
            String regex = pattern.replace("*", ".*").replace("?", ".");
            return storage.keySet().stream()
                .filter(k -> k.matches(regex))
                .collect(java.util.stream.Collectors.toSet());
        } finally {
            lock.readLock().unlock();
        }
    }

    private record CacheEntry(String value, long expiry) {
        boolean isExpired() {
            return expiry != Long.MAX_VALUE && System.currentTimeMillis() > expiry;
        }
    }
}