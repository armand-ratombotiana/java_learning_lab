package com.ds06;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HashMapTest {

    private HashMapChaining<String, Integer> chainMap;
    private HashMapOpenAddressing<String, String> openMap;

    @BeforeEach
    void setUp() {
        chainMap = new HashMapChaining<>();
        openMap = new HashMapOpenAddressing<>();
    }

    @Test
    void chainPutAndGet() {
        chainMap.put("a", 1);
        chainMap.put("b", 2);
        assertEquals(1, chainMap.get("a"));
        assertEquals(2, chainMap.get("b"));
    }

    @Test
    void chainUpdateValue() {
        chainMap.put("k", 100);
        chainMap.put("k", 200);
        assertEquals(200, chainMap.get("k"));
    }

    @Test
    void chainRemove() {
        chainMap.put("x", 10);
        chainMap.put("y", 20);
        assertEquals(10, chainMap.remove("x"));
        assertNull(chainMap.get("x"));
        assertNull(chainMap.remove("z"));
    }

    @Test
    void chainContainsKey() {
        chainMap.put("key1", 1);
        assertTrue(chainMap.containsKey("key1"));
        assertFalse(chainMap.containsKey("key2"));
    }

    @Test
    void chainSize() {
        chainMap.put("a", 1); chainMap.put("b", 2); chainMap.put("c", 3);
        assertEquals(3, chainMap.size());
        chainMap.remove("a");
        assertEquals(2, chainMap.size());
    }

    @Test
    void chainResize() {
        for (int i = 0; i < 100; i++) {
            chainMap.put("key-" + i, i);
        }
        assertEquals(100, chainMap.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i, chainMap.get("key-" + i));
        }
    }

    @Test
    void chainKeySet() {
        chainMap.put("a", 1); chainMap.put("b", 2);
        assertEquals(2, chainMap.keySet().size());
        assertTrue(chainMap.keySet().contains("a"));
    }

    @Test
    void chainValues() {
        chainMap.put("a", 10); chainMap.put("b", 20);
        assertTrue(chainMap.values().contains(10));
        assertTrue(chainMap.values().contains(20));
    }

    @Test
    void chainNullKey() {
        chainMap.put(null, 999);
        assertEquals(999, chainMap.get(null));
        assertTrue(chainMap.containsKey(null));
        chainMap.remove(null);
        assertNull(chainMap.get(null));
    }

    @Test
    void openPutAndGet() {
        openMap.put("city", "NYC");
        openMap.put("state", "NY");
        assertEquals("NYC", openMap.get("city"));
        assertEquals("NY", openMap.get("state"));
    }

    @Test
    void openUpdateValue() {
        openMap.put("k", "v1");
        openMap.put("k", "v2");
        assertEquals("v2", openMap.get("k"));
    }

    @Test
    void openRemove() {
        openMap.put("a", "1"); openMap.put("b", "2");
        assertEquals("1", openMap.remove("a"));
        assertNull(openMap.get("a"));
    }

    @Test
    void openContainsKey() {
        openMap.put("x", "val");
        assertTrue(openMap.containsKey("x"));
        assertFalse(openMap.containsKey("y"));
    }

    @Test
    void openResize() {
        for (int i = 0; i < 50; i++) {
            openMap.put("k" + i, "v" + i);
        }
        assertEquals(50, openMap.size());
        for (int i = 0; i < 50; i++) {
            assertEquals("v" + i, openMap.get("k" + i));
        }
    }
}
