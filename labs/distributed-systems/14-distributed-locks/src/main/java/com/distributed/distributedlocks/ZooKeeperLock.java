package com.distributed.distributedlocks;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class ZooKeeperLock implements DistributedLock {
    private final ConcurrentHashMap<String, ZkLockEntry> locks = new ConcurrentHashMap<>();
    private final AtomicLong fencingToken = new AtomicLong(0);

    private static class ZkLockEntry {
        final String path;
        final String owner;
        final long token;
        final long created;
        volatile boolean isAcquired;

        ZkLockEntry(String path, String owner, long token) {
            this.path = path;
            this.owner = owner;
            this.token = token;
            this.created = System.currentTimeMillis();
            this.isAcquired = false;
        }
    }

    @Override
    public boolean tryLock(String key, Duration timeout) {
        String path = "/locks/" + key;
        String owner = Thread.currentThread().getName();
        long token = fencingToken.incrementAndGet();
        ZkLockEntry entry = new ZkLockEntry(path, owner, token);
        ZkLockEntry existing = locks.putIfAbsent(key, entry);
        if (existing == null) {
            entry.isAcquired = true;
            return true;
        }
        if (System.currentTimeMillis() - existing.created > timeout.toMillis()) {
            if (locks.replace(key, existing, entry)) {
                entry.isAcquired = true;
                return true;
            }
        }
        return false;
    }

    @Override
    public void unlock(String key) {
        ZkLockEntry entry = locks.get(key);
        if (entry != null && entry.owner.equals(Thread.currentThread().getName())) {
            locks.remove(key, entry);
        }
    }

    @Override
    public long getFencingToken(String key) {
        ZkLockEntry entry = locks.get(key);
        return entry != null ? entry.token : -1;
    }
}
