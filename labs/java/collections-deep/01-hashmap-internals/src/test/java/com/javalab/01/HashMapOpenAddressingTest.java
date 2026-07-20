package com.javalab.01;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HashMapOpenAddressing")
class HashMapOpenAddressingTest {

    private HashMapOpenAddressing<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new HashMapOpenAddressing<>();
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
    @DisplayName("remove preserves probe chain for subsequent entries")
    void removePreservesProbeChain() {
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        map.remove("a");
        assertEquals(2, map.get("b"));
        assertEquals(3, map.get("c"));
        assertEquals(2, map.size());
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
    @DisplayName("throws NPE for null key")
    void nullKeyThrowsNpe() {
        assertThrows(NullPointerException.class, () -> map.put(null, 42));
        assertThrows(NullPointerException.class, () -> map.get(null));
        assertThrows(NullPointerException.class, () -> map.remove(null));
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
    @DisplayName("handles multiple insertions and rehashing")
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
    @DisplayName("handles interleaved put and remove")
    void interleavedPutRemove() {
        for (int i = 0; i < 50; i++) {
            map.put("k" + i, i);
        }
        for (int i = 0; i < 25; i++) {
            map.remove("k" + i);
        }
        assertEquals(25, map.size());
        for (int i = 0; i < 25; i++) {
            assertNull(map.get("k" + i));
        }
        for (int i = 25; i < 50; i++) {
            assertEquals(i, map.get("k" + i));
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
