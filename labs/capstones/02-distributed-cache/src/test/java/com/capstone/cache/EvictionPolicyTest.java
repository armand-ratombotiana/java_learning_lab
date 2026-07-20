package com.capstone.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class EvictionPolicyTest {
    @Test void testLRUBasic() {
        var lru = new EvictionPolicy.LRUPolicy<String, String>(3, 0);
        lru.put("a", "1"); lru.put("b", "2"); lru.put("c", "3");
        assertEquals(3, lru.size());
        lru.put("d", "4");
        assertEquals(3, lru.size());
        assertFalse(lru.get("a").isPresent());
        assertTrue(lru.get("b").isPresent());
    }

    @Test void testLRUAccessOrder() {
        var lru = new EvictionPolicy.LRUPolicy<String, String>(3, 0);
        lru.put("a", "1"); lru.put("b", "2"); lru.put("c", "3");
        lru.get("a");
        lru.put("d", "4");
        assertFalse(lru.get("b").isPresent());
        assertTrue(lru.get("a").isPresent());
    }

    @Test void testLFUBasic() {
        var lfu = new EvictionPolicy.LFUPolicy<String, String>(3, 0);
        lfu.put("a", "1"); lfu.put("b", "2"); lfu.put("c", "3");
        lfu.get("a"); lfu.get("a"); lfu.get("b");
        lfu.put("d", "4");
        assertEquals(3, lfu.size());
    }

    @Test void testRemove() {
        var lru = new EvictionPolicy.LRUPolicy<String, String>(10, 0);
        lru.put("a", "1");
        assertTrue(lru.remove("a"));
        assertFalse(lru.get("a").isPresent());
    }

    @Test void testClear() {
        var lru = new EvictionPolicy.LRUPolicy<String, String>(10, 0);
        lru.put("a", "1"); lru.put("b", "2");
        lru.clear();
        assertEquals(0, lru.size());
    }
}
