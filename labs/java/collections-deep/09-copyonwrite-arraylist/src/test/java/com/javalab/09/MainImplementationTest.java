package com.javalab.09;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainImplementationTest {

    private MainImplementation<String, Integer> map;

    @BeforeEach
    void setUp() {
        map = new MainImplementation<>();
    }

    @Nested
    @DisplayName("Basic operations")
    class BasicOperations {

        @Test
        @DisplayName("put() and get() should work for new entries")
        void testPutAndGet() {
            assertNull(map.put("one", 1));
            assertNull(map.put("two", 2));
            assertEquals(1, map.get("one"));
            assertEquals(2, map.get("two"));
        }

        @Test
        @DisplayName("put() should return old value when updating existing key")
        void testUpdate() {
            map.put("key", 1);
            assertEquals(1, map.put("key", 2));
            assertEquals(2, map.get("key"));
        }

        @Test
        @DisplayName("get() should return null for non-existent key")
        void testGetNonExistent() {
            assertNull(map.get("nonexistent"));
        }

        @Test
        @DisplayName("remove() should remove entry and return value")
        void testRemove() {
            map.put("one", 1);
            assertEquals(1, map.remove("one"));
            assertNull(map.get("one"));
            assertNull(map.remove("one"));
        }

        @Test
        @DisplayName("containsKey() should return correct results")
        void testContainsKey() {
            map.put("one", 1);
            assertTrue(map.containsKey("one"));
            assertFalse(map.containsKey("two"));
        }
    }

    @Nested
    @DisplayName("Size management")
    class SizeManagement {

        @Test
        @DisplayName("size() should track number of entries")
        void testSize() {
            assertEquals(0, map.size());
            map.put("one", 1);
            assertEquals(1, map.size());
            map.put("two", 2);
            assertEquals(2, map.size());
            map.remove("one");
            assertEquals(1, map.size());
            map.clear();
            assertEquals(0, map.size());
        }

        @Test
        @DisplayName("isEmpty() should return correct state")
        void testIsEmpty() {
            assertTrue(map.isEmpty());
            map.put("one", 1);
            assertFalse(map.isEmpty());
            map.clear();
            assertTrue(map.isEmpty());
        }

        @Test
        @DisplayName("clear() should remove all entries")
        void testClear() {
            map.put("one", 1);
            map.put("two", 2);
            map.put("three", 3);
            map.clear();
            assertTrue(map.isEmpty());
            assertEquals(0, map.size());
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCases {

        @Test
        @DisplayName("should handle many entries")
        void testManyEntries() {
            for (int i = 0; i < 1000; i++) {
                map.put("key" + i, i);
            }
            assertEquals(1000, map.size());
            for (int i = 0; i < 1000; i++) {
                assertEquals(i, map.get("key" + i));
            }
        }

        @Test
        @DisplayName("should reject null key")
        void testNullKey() {
            assertThrows(NullPointerException.class, () -> map.put(null, 1));
        }

        @Test
        @DisplayName("toString() should produce expected format")
        void testToString() {
            map.put("a", 1);
            map.put("b", 2);
            String str = map.toString();
            assertTrue(str.startsWith("["));
            assertTrue(str.endsWith("]"));
            assertTrue(str.contains("a=1"));
            assertTrue(str.contains("b=2"));
        }
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should accept valid constructor parameters")
        void testValidConstruction() {
            assertDoesNotThrow(() -> new MainImplementation<String, Integer>(32, 0.5f));
        }

        @Test
        @DisplayName("should reject invalid constructor parameters")
        void testInvalidConstruction() {
            assertThrows(IllegalArgumentException.class,
                () -> new MainImplementation<String, Integer>(0, 0.75f));
            assertThrows(IllegalArgumentException.class,
                () -> new MainImplementation<String, Integer>(16, 0.0f));
            assertThrows(IllegalArgumentException.class,
                () -> new MainImplementation<String, Integer>(16, Float.NaN));
        }
    }
}
