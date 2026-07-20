package com.javalab.05;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RedBlackTreeMap")
class RedBlackTreeMapTest {

    private RedBlackTreeMap<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new RedBlackTreeMap<>();
    }

    @Test
    @DisplayName("new map is empty")
    void newMapIsEmpty() {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("put and get")
    void putAndGet() {
        map.put("one", 1);
        map.put("two", 2);
        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
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
    @DisplayName("remove missing returns null")
    void removeMissing() {
        assertNull(map.remove("ghost"));
    }

    @Test
    @DisplayName("ordered iteration via keySet")
    void keySetOrder() {
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);
        var keys = map.keySet();
        var iter = keys.iterator();
        assertEquals("a", iter.next());
        assertEquals("b", iter.next());
        assertEquals("c", iter.next());
    }

    @Test
    @DisplayName("firstKey and lastKey")
    void firstAndLastKey() {
        map.put("b", 2);
        map.put("a", 1);
        map.put("c", 3);
        assertEquals("a", map.firstKey());
        assertEquals("c", map.lastKey());
    }

    @Test
    @DisplayName("clear empties the map")
    void clear() {
        map.put("a", 1);
        map.put("b", 2);
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    @DisplayName("null key throws NPE")
    void nullKey() {
        assertThrows(NullPointerException.class, () -> map.put(null, 1));
        assertThrows(NullPointerException.class, () -> map.get(null));
    }

    @Test
    @DisplayName("handles many insertions")
    void manyInsertions() {
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, i);
        }
        assertEquals(1000, map.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, map.get("key" + i).intValue());
        }
    }

    @Test
    @DisplayName("complex remove preserves tree structure")
    void complexRemove() {
        map.put("d", 4);
        map.put("b", 2);
        map.put("f", 6);
        map.put("a", 1);
        map.put("c", 3);
        map.put("e", 5);
        map.put("g", 7);
        map.remove("b");
        map.remove("d");
        map.remove("f");
        assertEquals(4, map.size());
        assertEquals("a", map.firstKey());
        assertEquals("g", map.lastKey());
    }
}
