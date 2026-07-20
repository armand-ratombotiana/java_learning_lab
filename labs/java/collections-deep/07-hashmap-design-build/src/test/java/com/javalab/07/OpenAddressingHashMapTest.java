package com.javalab.07;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OpenAddressingHashMap")
class OpenAddressingHashMapTest {

    private OpenAddressingHashMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new OpenAddressingHashMap<>();
    }

    @Test
    @DisplayName("put and get")
    void putAndGet() {
        map.put("one", 1);
        assertEquals(1, map.get("one"));
    }

    @Test
    @DisplayName("put overwrites")
    void putOverwrites() {
        map.put("k", 1);
        map.put("k", 2);
        assertEquals(2, map.get("k"));
        assertEquals(1, map.size());
    }

    @Test
    @DisplayName("get returns null for missing")
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
    @DisplayName("remove and re-insert")
    void removeAndReInsert() {
        map.put("a", 1);
        map.put("b", 2);
        assertEquals(1, map.remove("a"));
        assertNull(map.get("a"));
        map.put("a", 3);
        assertEquals(3, map.get("a"));
    }

    @Test
    @DisplayName("tombstones do not break probe chain")
    void tombstoneProbing() {
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.remove("a");
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
    }

    @Test
    @DisplayName("resize doubles capacity and preserves entries")
    void resize() {
        for (int i = 0; i < 100; i++) {
            map.put("key" + i, i);
        }
        assertEquals(100, map.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, map.get("key" + i).intValue());
        }
    }

    @Test
    @DisplayName("load factor tracking")
    void loadFactor() {
        double lf = map.loadFactor();
        assertTrue(lf >= 0.0 && lf < 0.7);
    }

    @Test
    @DisplayName("clear empties map")
    void clear() {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("handles many operations with interleaved remove")
    void interleavedOperations() {
        for (int i = 0; i < 50; i++) map.put("k" + i, i);
        for (int i = 0; i < 25; i++) map.remove("k" + i);
        assertEquals(25, map.size());
        for (int i = 25; i < 50; i++) assertEquals(i, map.get("k" + i).intValue());
    }

    @Test
    @DisplayName("null key works")
    void nullKey() {
        map.put(null, 99);
        assertEquals(99, map.get(null));
        assertTrue(map.containsKey(null));
        map.remove(null);
        assertNull(map.get(null));
    }

    @Test
    @DisplayName("toString format")
    void testToString() {
        map.put("a", 1);
        map.put("b", 2);
        String s = map.toString();
        assertTrue(s.startsWith("{"));
        assertTrue(s.endsWith("}"));
    }
}
