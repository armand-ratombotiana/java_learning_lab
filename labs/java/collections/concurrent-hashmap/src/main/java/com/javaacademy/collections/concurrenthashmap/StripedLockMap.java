package com.javaacademy.collections.concurrenthashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A concurrent hash map backed by striped locks.
 *
 * <p>The key space is divided into a fixed number of segments, each protected
 * by its own {@link ReentrantLock}. Threads only lock the segment containing
 * the key they are operating on, enabling high concurrency when different
 * threads access different segments.
 *
 * <p>This implementation demonstrates the core idea behind
 * {@code java.util.concurrent.ConcurrentHashMap} prior to Java 8:
 * <ul>
 *   <li>Lock striping reduces contention without per-bucket locking overhead</li>
 *   <li>Read operations typically acquire only the segment lock</li>
 *   <li>Writes are isolated to a single segment</li>
 * </ul>
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public class StripedLockMap<K, V> {

    private static final int DEFAULT_SEGMENTS = 16;
    private final Segment<K, V>[] segments;
    private final Lock[] locks;

    static class Segment<K, V> {
        private final Map<K, V> map = new java.util.HashMap<>();

        void put(K key, V value) {
            map.put(key, value);
        }

        V get(K key) {
            return map.get(key);
        }

        V remove(K key) {
            return map.remove(key);
        }

        boolean containsKey(K key) {
            return map.containsKey(key);
        }

        int size() {
            return map.size();
        }

        Set<java.util.Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }

        void clear() {
            map.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public StripedLockMap() {
        this(DEFAULT_SEGMENTS);
    }

    @SuppressWarnings("unchecked")
    public StripedLockMap(int segmentCount) {
        if ((segmentCount & (segmentCount - 1)) != 0) {
            throw new IllegalArgumentException("Segment count must be a power of two");
        }
        segments = new Segment[segmentCount];
        locks = new Lock[segmentCount];
        for (int i = 0; i < segmentCount; i++) {
            locks[i] = new ReentrantLock();
            segments[i] = new Segment<>();
        }
    }

    private int segmentIndex(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        h ^= (h >>> 16);
        return h & (segments.length - 1);
    }

    private Lock lockFor(K key) {
        return locks[segmentIndex(key)];
    }

    private Segment<K, V> segmentFor(K key) {
        return segments[segmentIndex(key)];
    }

    public V put(K key, V value) {
        Lock lock = lockFor(key);
        lock.lock();
        try {
            Segment<K, V> seg = segmentFor(key);
            V old = seg.get(key);
            seg.put(key, value);
            return old;
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        Lock lock = lockFor(key);
        lock.lock();
        try {
            return segmentFor(key).get(key);
        } finally {
            lock.unlock();
        }
    }

    public V remove(K key) {
        Lock lock = lockFor(key);
        lock.lock();
        try {
            return segmentFor(key).remove(key);
        } finally {
            lock.unlock();
        }
    }

    public boolean containsKey(K key) {
        Lock lock = lockFor(key);
        lock.lock();
        try {
            return segmentFor(key).containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically computes a new value for the given key if it is absent.
     * Multiple threads calling this method with the same key will block
     * and only the first will compute the value; subsequent calls return
     * the already-computed value.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        Lock lock = lockFor(key);
        lock.lock();
        try {
            Segment<K, V> seg = segmentFor(key);
            V existing = seg.get(key);
            if (existing != null) {
                return existing;
            }
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                seg.put(key, newValue);
            }
            return newValue;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Atomically computes a new value for the given key using the remapping
     * function, regardless of whether the key is already present.
     */
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Lock lock = lockFor(key);
        lock.lock();
        try {
            Segment<K, V> seg = segmentFor(key);
            V oldValue = seg.get(key);
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                seg.put(key, newValue);
            } else if (oldValue != null) {
                seg.remove(key);
            }
            return newValue;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        int total = 0;
        for (Lock lock : locks) {
            lock.lock();
        }
        try {
            for (Segment<K, V> seg : segments) {
                total += seg.size();
            }
        } finally {
            for (int i = locks.length - 1; i >= 0; i--) {
                locks[i].unlock();
            }
        }
        return total;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        for (Lock lock : locks) {
            lock.lock();
        }
        try {
            for (Segment<K, V> seg : segments) {
                seg.clear();
            }
        } finally {
            for (int i = locks.length - 1; i >= 0; i--) {
                locks[i].unlock();
            }
        }
    }

    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> all = new HashSet<>();
        for (Lock lock : locks) {
            lock.lock();
        }
        try {
            for (Segment<K, V> seg : segments) {
                all.addAll(seg.entrySet());
            }
        } finally {
            for (int i = locks.length - 1; i >= 0; i--) {
                locks[i].unlock();
            }
        }
        return all;
    }
}
