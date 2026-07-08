package com.databases.sharding;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConsistentHashRing<T> {
    private final SortedMap<Long, T> ring = new TreeMap<>();
    private final int virtualNodeCount;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final MessageDigest md;
    private final List<T> nodeList = new ArrayList<>();

    public ConsistentHashRing(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
        try { this.md = MessageDigest.getInstance("SHA-256"); }
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
    }

    public void addNode(T node) {
        lock.writeLock().lock();
        try {
            nodeList.add(node);
            for (int i = 0; i < virtualNodeCount; i++) {
                ring.put(hash(node + ":vnode:" + i), node);
            }
        } finally { lock.writeLock().unlock(); }
    }

    public void removeNode(T node) {
        lock.writeLock().lock();
        try {
            nodeList.remove(node);
            for (int i = 0; i < virtualNodeCount; i++) {
                ring.remove(hash(node + ":vnode:" + i));
            }
        } finally { lock.writeLock().unlock(); }
    }

    public T getNode(String key) {
        lock.readLock().lock();
        try {
            if (ring.isEmpty()) throw new IllegalStateException("No nodes in hash ring");
            long h = hash(key);
            SortedMap<Long, T> tail = ring.tailMap(h);
            Long k = tail.isEmpty() ? ring.firstKey() : tail.firstKey();
            return ring.get(k);
        } finally { lock.readLock().unlock(); }
    }

    public Map<T, Integer> getDistribution(Collection<String> keys) {
        Map<T, Integer> dist = new HashMap<>();
        for (String key : keys) dist.merge(getNode(key), 1, Integer::sum);
        return dist;
    }

    public int getNodeCount() {
        lock.readLock().lock();
        try { return nodeList.size(); }
        finally { lock.readLock().unlock(); }
    }

    public Set<T> getNodes() {
        lock.readLock().lock();
        try { return new HashSet<>(nodeList); }
        finally { lock.readLock().unlock(); }
    }

    private long hash(String input) {
        byte[] d = md.digest(input.getBytes(StandardCharsets.UTF_8));
        long h = 0;
        for (int i = 0; i < 8; i++) h = (h << 8) | (d[i] & 0xFF);
        return h & Long.MAX_VALUE;
    }
}
