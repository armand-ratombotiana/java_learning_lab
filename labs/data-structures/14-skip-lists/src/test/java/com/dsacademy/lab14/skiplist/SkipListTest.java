package com.dsacademy.lab14.skiplist;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

public class SkipListTest {

    @Test
    void testInsertAndSearch() {
        SkipList<Integer, String> sl = new SkipList<>();
        sl.insert(1, "one");
        sl.insert(2, "two");
        sl.insert(3, "three");
        assertEquals("one", sl.search(1));
        assertEquals("two", sl.search(2));
        assertEquals("three", sl.search(3));
        assertNull(sl.search(4));
    }

    @Test
    void testUpdateExistingKey() {
        SkipList<Integer, String> sl = new SkipList<>();
        sl.insert(1, "one");
        sl.insert(1, "uno");
        assertEquals("uno", sl.search(1));
        assertEquals(1, sl.size());
    }

    @Test
    void testDelete() {
        SkipList<Integer, String> sl = new SkipList<>();
        sl.insert(1, "one");
        sl.insert(2, "two");
        assertTrue(sl.delete(1));
        assertNull(sl.search(1));
        assertFalse(sl.delete(3));
        assertEquals(1, sl.size());
    }

    @Test
    void testContains() {
        SkipList<Integer, String> sl = new SkipList<>();
        sl.insert(5, "five");
        assertTrue(sl.contains(5));
        assertFalse(sl.contains(6));
    }

    @Test
    void testLargeRandomOperations() {
        SkipList<Integer, String> sl = new SkipList<>();
        Random rand = new Random(42);
        TreeMap<Integer, String> reference = new TreeMap<>();
        for (int i = 0; i < 1000; i++) {
            int key = rand.nextInt(10000);
            if (rand.nextBoolean()) {
                String val = "v" + key;
                sl.insert(key, val);
                reference.put(key, val);
            } else {
                sl.delete(key);
                reference.remove(key);
            }
        }
        for (int key : reference.keySet()) {
            assertEquals(reference.get(key), sl.search(key));
        }
        assertEquals(reference.size(), sl.size());
    }

    @Test
    void testEmptySkipList() {
        SkipList<Integer, String> sl = new SkipList<>();
        assertTrue(sl.isEmpty());
        assertEquals(0, sl.size());
        assertNull(sl.search(1));
        assertFalse(sl.delete(1));
    }

    @Test
    void testSkipListSet() {
        SkipListSet<Integer> set = new SkipListSet<>();
        assertTrue(set.add(1));
        assertTrue(set.add(2));
        assertFalse(set.add(1));
        assertTrue(set.contains(1));
        assertTrue(set.remove(1));
        assertFalse(set.contains(1));
        assertEquals(1, set.size());
    }

    @Test
    void testSkipListSetIterator() {
        SkipListSet<Integer> set = new SkipListSet<>();
        set.add(3); set.add(1); set.add(2);
        List<Integer> keys = new ArrayList<>();
        for (int k : set) keys.add(k);
        assertEquals(Arrays.asList(1, 2, 3), keys);
    }

    @Test
    void testSkipListMap() {
        SkipListMap<String, Integer> map = new SkipListMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
        assertNull(map.get("d"));
        assertEquals(Integer.valueOf(2), map.remove("b"));
        assertNull(map.get("b"));
        assertEquals(2, map.size());
    }

    @Test
    void testSkipListMapKeySet() {
        SkipListMap<Integer, String> map = new SkipListMap<>();
        map.put(3, "c");
        map.put(1, "a");
        map.put(2, "b");
        Collection<Integer> keys = map.keySet();
        assertArrayEquals(new Integer[]{1, 2, 3}, keys.toArray());
    }
}
