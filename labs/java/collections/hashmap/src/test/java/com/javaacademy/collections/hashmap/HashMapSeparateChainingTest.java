package com.javaacademy.collections.hashmap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashMapSeparateChaining")
class HashMapSeparateChainingTest {

    private HashMapSeparateChaining<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new HashMapSeparateChaining<>();
    }

    @Test
    @DisplayName("put and get a single entry")
    void putAndGet() {
        map.put("one", 1);
        assertEquals(1, map.get("one"));
    }

    @Test
    @DisplayName("put overwrites existing key")
    void putOverwrites() {
        map.put("k", 1);
        map.put("k", 2);
        assertEquals(2, map.get("k"));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("get returns null for missing key")
    void getMissing() {
        assertNull(map.get("nope"));
    }

    @Test
    @DisplayName("containsKey")
    void containsKey() {
        map.put("a", 10);
        assertTrue(map.containsKey("a"));
        assertFalse(map.containsKey("b"));
    }

    @Test
    @DisplayName("remove existing key")
    void remove() {
        map.put("x", 100);
        assertEquals(100, map.remove("x"));
        assertNull(map.get("x"));
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("remove missing key returns null")
    void removeMissing() {
        assertNull(map.remove("ghost"));
    }

    @Test
    @DisplayName("size tracking")
    void sizeTracking() {
        assertEquals(0, map.size());
        map.put("a", 1);
        assertEquals(1, map.size());
        map.put("b", 2);
        assertEquals(2, map.size());
        map.remove("a");
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("handles null key")
    void nullKey() {
        map.put(null, 42);
        assertEquals(42, map.get(null));
        assertTrue(map.containsKey(null));
        map.remove(null);
        assertNull(map.get(null));
    }

    @Test
    @DisplayName("clear empties the map")
    void clear() {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertNull(map.get("a"));
    }

    @Test
    @DisplayName("handles collisions correctly")
    void collisionHandling() {
        HashMapSeparateChaining<Integer, String> small = new HashMapSeparateChaining<>(4);
        for (int i = 0; i < 20; i++) {
            small.put(i, "val" + i);
        }
        assertEquals(20, small.size());
        for (int i = 0; i < 20; i++) {
            assertEquals("val" + i, small.get(i));
        }
    }

    @Test
    @DisplayName("rehash preserves all entries")
    void rehashPreservesEntries() {
        for (int i = 0; i < 100; i++) {
            map.put("key" + i, i);
        }
        assertEquals(100, map.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, map.get("key" + i));
        }
    }

    @Test
    @DisplayName("toString produces valid format")
    void testToString() {
        map.put("a", 1);
        map.put("b", 2);
        String s = map.toString();
        assertTrue(s.startsWith("{"));
        assertTrue(s.endsWith("}"));
        assertTrue(s.contains("a=1"));
        assertTrue(s.contains("b=2"));
    }
}
