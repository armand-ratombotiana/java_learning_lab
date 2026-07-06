package com.algo.lab21;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CachingTest {

    @Test
    void testFifoCache() {
        FifoCache<Integer, String> cache = new FifoCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        assertEquals("a", cache.get(1));
        cache.put(3, "c");
        assertNull(cache.get(1));
        assertEquals("b", cache.get(2));
        assertEquals("c", cache.get(3));
    }

    @Test
    void testFifoCacheUpdate() {
        FifoCache<Integer, String> cache = new FifoCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(1, "c");
        assertEquals("c", cache.get(1));
        cache.put(3, "d");
        assertNull(cache.get(2));
    }

    @Test
    void testFifoCacheClear() {
        FifoCache<Integer, String> cache = new FifoCache<>(3);
        cache.put(1, "a");
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testLRUCache() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
        assertNull(cache.get(2));
        assertEquals("c", cache.get(3));
    }

    @Test
    void testLRUCacheOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        assertNull(cache.get(1));
    }

    @Test
    void testLFUCache() {
        LFUCache<Integer, String> cache = new LFUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.get(1);
        cache.put(3, "c");
        assertNull(cache.get(2));
        assertEquals("a", cache.get(1));
    }

    @Test
    void testLFUCacheEvictionOrder() {
        LFUCache<Integer, String> cache = new LFUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
        assertNull(cache.get(2));
    }

    @Test
    void testLFUCacheClear() {
        LFUCache<Integer, String> cache = new LFUCache<>(3);
        cache.put(1, "a");
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testARCache() {
        ARCache<Integer, String> cache = new ARCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        assertEquals("a", cache.get(1));
        cache.put(3, "c");
        assertNull(cache.get(2));
        assertEquals("a", cache.get(1));
    }

    @Test
    void testARCacheMultipleAccess() {
        ARCache<Integer, String> cache = new ARCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.get(1);
        cache.put(3, "c");
        assertNull(cache.get(2));
    }

    @Test
    void testSecondChanceCache() {
        SecondChanceCache<Integer, String> cache = new SecondChanceCache<>(3);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
        cache.put(4, "d");
        assertTrue(cache.size() <= 3);
    }

    @Test
    void testSecondChanceCacheEviction() {
        SecondChanceCache<Integer, String> cache = new SecondChanceCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
    }

    @Test
    void testSecondChanceCacheClear() {
        SecondChanceCache<Integer, String> cache = new SecondChanceCache<>(3);
        cache.put(1, "a");
        cache.clear();
        assertEquals(0, cache.size());
    }

    @Test
    void testAllCachesThrowOnZero() {
        assertThrows(IllegalArgumentException.class, () -> new FifoCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LFUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ARCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new SecondChanceCache<>(0));
    }
}
