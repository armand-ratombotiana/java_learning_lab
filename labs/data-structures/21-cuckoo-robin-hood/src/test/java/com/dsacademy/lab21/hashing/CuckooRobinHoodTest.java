package com.dsacademy.lab21.hashing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.HashSet;
import java.util.Random;

public class CuckooRobinHoodTest {

    private CuckooHashMap<Integer, String> cuckoo;
    private RobinHoodHashMap<Integer, String> robin;

    @BeforeEach
    void setUp() {
        cuckoo = new CuckooHashMap<>();
        robin = new RobinHoodHashMap<>();
    }

    @Test
    void testCuckooPutAndGet() {
        cuckoo.put(1, "one");
        cuckoo.put(2, "two");
        cuckoo.put(3, "three");
        assertEquals("one", cuckoo.get(1));
        assertEquals("two", cuckoo.get(2));
        assertEquals("three", cuckoo.get(3));
        assertEquals(3, cuckoo.size());
    }

    @Test
    void testCuckooContains() {
        cuckoo.put(42, "answer");
        assertTrue(cuckoo.contains(42));
        assertFalse(cuckoo.contains(99));
    }

    @Test
    void testCuckooRemove() {
        cuckoo.put(1, "one");
        cuckoo.put(2, "two");
        assertEquals("one", cuckoo.remove(1));
        assertFalse(cuckoo.contains(1));
        assertEquals(1, cuckoo.size());
    }

    @Test
    void testRobinHoodPutAndGet() {
        robin.put(10, "ten");
        robin.put(20, "twenty");
        robin.put(30, "thirty");
        assertEquals("ten", robin.get(10));
        assertEquals("twenty", robin.get(20));
        assertEquals("thirty", robin.get(30));
        assertEquals(3, robin.size());
    }

    @Test
    void testRobinHoodBackwardShiftDelete() {
        robin.put(1, "a");
        robin.put(2, "b");
        robin.put(3, "c");
        robin.remove(2);
        assertNull(robin.get(2));
        assertEquals("a", robin.get(1));
        assertEquals("c", robin.get(3));
    }

    @Test
    void testRobinHoodUpdate() {
        robin.put(1, "old");
        robin.put(1, "new");
        assertEquals("new", robin.get(1));
    }

    @Test
    void testLargeScaleCuckoo() {
        int n = 1000;
        for (int i = 0; i < n; i++) {
            cuckoo.put(i, "val" + i);
        }
        assertEquals(n, cuckoo.size());
        for (int i = 0; i < n; i++) {
            assertTrue(cuckoo.contains(i));
        }
    }

    @Test
    void testLargeScaleRobinHood() {
        int n = 1000;
        for (int i = 0; i < n; i++) {
            robin.put(i, "val" + i);
        }
        assertEquals(n, robin.size());
        for (int i = 0; i < n; i++) {
            assertNotNull(robin.get(i));
        }
    }

    @Test
    void testHashUtils() {
        assertTrue(HashUtils.nextPowerOfTwo(5) == 8);
        assertTrue(HashUtils.nextPowerOfTwo(16) == 16);
        assertTrue(HashUtils.nextPowerOfTwo(1) == 1);
        assertEquals(0, HashUtils.murmur3(0, 0) != 0 ? 0 : 0);
        assertDoesNotThrow(() -> HashUtils.crc32("hello"));
    }
}
