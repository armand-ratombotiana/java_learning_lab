package com.distributed.distributedlocks;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class RedisLock implements DistributedLock {
    private final ConcurrentHashMap<String, LockEntry> locks = new ConcurrentHashMap<>();
    private final AtomicLong fencingToken = new AtomicLong(0);

    private record LockEntry(String owner, long expiry, long token) {}

    @Override
    public boolean tryLock(String key, Duration timeout) {
        String owner = Thread.currentThread().getName();
        long expiry = System.currentTimeMillis() + timeout.toMillis();
        LockEntry entry = new LockEntry(owner, expiry, fencingToken.incrementAndGet());
        LockEntry existing = locks.putIfAbsent(key, entry);
        if (existing == null) return true;
        if (System.currentTimeMillis() > existing.expiry()) {
            if (locks.replace(key, existing, entry)) return true;
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        LockEntry entry = locks.get(key);
        if (entry != null && entry.owner().equals(Thread.currentThread().getName())) {
            locks.remove(key, entry);
        }
    }

    @Override
    public long getFencingToken(String key) {
        LockEntry entry = locks.get(key);
        return entry != null ? entry.token() : -1;
    }

    public boolean isLocked(String key) {
        LockEntry entry = locks.get(key);
        if (entry == null) return false;
        if (System.currentTimeMillis() > entry.expiry()) {
            locks.remove(key, entry);
            return false;
        }
        return true;
    }
}
