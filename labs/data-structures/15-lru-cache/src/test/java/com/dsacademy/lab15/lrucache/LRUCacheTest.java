package com.dsacademy.lab15.lrucache;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LRUCacheTest {

    @Test
    void testLRUCacheBasic() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
        cache.put(4, "d");
        assertNull(cache.get(2));
        assertEquals("a", cache.get(1));
        assertEquals("c", cache.get(3));
        assertEquals("d", cache.get(4));
    }

    @Test
    void testLRUCacheUpdate() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(1, "c");
        assertEquals("c", cache.get(1));
        assertEquals(2, cache.size());
    }

    @Test
    void testLRUCacheEvictionOrder() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.put(3, "c");
        assertNotNull(cache.get(1));
        assertNull(cache.get(2));
        assertEquals("c", cache.get(3));
    }

    @Test
    void testLRUCacheCapacityOne() {
        LRUCache<Integer, String> cache = new LRUCache<>(1);
        cache.put(1, "a");
        cache.put(2, "b");
        assertNull(cache.get(1));
        assertEquals("b", cache.get(2));
    }

    @Test
    void testLRUCacheClear() {
        LRUCache<Integer, String> cache = new LRUCache<>(3);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get(1));
    }

    @Test
    void testLinkedHashMapVersion() {
        LRUCacheLinkedHashMap<Integer, String> cache = new LRUCacheLinkedHashMap<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.get(1);
        cache.put(3, "c");
        assertEquals("a", cache.get(1));
        assertNull(cache.get(2));
    }

    @Test
    void testLFUCache() {
        LFUCache<Integer, String> cache = new LFUCache<>(3);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        cache.get(1);
        cache.get(1);
        cache.get(2);
        cache.put(4, "d");
        assertNotNull(cache.get(1));
        assertNotNull(cache.get(2));
        assertNull(cache.get(3));
        assertEquals("d", cache.get(4));
    }

    @Test
    void testConcurrentLRUCache() throws InterruptedException {
        ConcurrentLRUCache<Integer, String> cache = new ConcurrentLRUCache<>(100);
        Thread t1 = new Thread(() -> { for (int i = 0; i < 50; i++) cache.put(i, "v" + i); });
        Thread t2 = new Thread(() -> { for (int i = 50; i < 100; i++) cache.put(i, "v" + i); });
        t1.start(); t2.start();
        t1.join(); t2.join();
        assertEquals(100, cache.size());
    }

    @Test
    void testInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LRUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new LFUCache<>(0));
        assertThrows(IllegalArgumentException.class, () -> new ConcurrentLRUCache<>(0));
    }

    @Test
    void testLFUEvictionWithSameFrequency() {
        LFUCache<Integer, String> cache = new LFUCache<>(2);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.put(3, "c");
        assertNull(cache.get(1));
    }

    @Test
    void testConcurrentLRUCacheClear() {
        ConcurrentLRUCache<Integer, String> cache = new ConcurrentLRUCache<>(5);
        cache.put(1, "a");
        cache.put(2, "b");
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get(1));
    }
}
