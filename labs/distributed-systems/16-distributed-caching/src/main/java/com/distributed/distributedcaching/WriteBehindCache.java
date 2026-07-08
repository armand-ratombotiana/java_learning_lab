package com.distributed.distributedcaching;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class WriteBehindCache<K, V> implements CacheClient<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, Long> writeTimes = new ConcurrentHashMap<>();
    private final BiConsumer<K, V> dbWriter;
    private final int batchSize;
    private final long flushIntervalMs;
    private ScheduledExecutorService scheduler;

    public WriteBehindCache(BiConsumer<K, V> dbWriter, int batchSize, long flushIntervalMs) {
        this.dbWriter = dbWriter;
        this.batchSize = batchSize;
        this.flushIntervalMs = flushIntervalMs;
    }

    public void start() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::flush, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();
            flush();
        }
    }

    @Override
    public Optional<V> get(K key) {
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public void put(K key, V value, Duration ttl) {
        cache.put(key, value);
        writeTimes.put(key, System.currentTimeMillis());
    }

    @Override
    public boolean delete(K key) {
        writeTimes.remove(key);
        return cache.remove(key) != null;
    }

    @Override
    public boolean exists(K key) {
        return cache.containsKey(key);
    }

    @Override
    public void clear() {
        cache.clear();
        writeTimes.clear();
    }

    private void flush() {
        if (cache.isEmpty()) return;
        List<Map.Entry<K, V>> batch = new ArrayList<>();
        for (Map.Entry<K, V> entry : cache.entrySet()) {
            batch.add(entry);
            if (batch.size() >= batchSize) break;
        }
        for (Map.Entry<K, V> entry : batch) {
            try {
                dbWriter.accept(entry.getKey(), entry.getValue());
                writeTimes.remove(entry.getKey());
            } catch (Exception e) {
                Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), e);
            }
        }
    }

    public int getPendingWrites() { return writeTimes.size(); }
    public int getCacheSize() { return cache.size(); }
}
