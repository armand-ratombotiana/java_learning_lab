package com.capstone.spark;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ShuffleManagerTest {
    private ShuffleManager manager;

    @BeforeEach
    void setUp() { manager = new ShuffleManager(3); }

    @Test void testPartitionByKey() {
        var data = Map.of("apple", 1, "banana", 2, "cherry", 3, "date", 4);
        var partitioned = manager.partitionByKey(data);
        assertEquals(3, partitioned.size());
        int total = partitioned.values().stream().mapToInt(List::size).sum();
        assertEquals(4, total);
    }

    @Test void testWriteAndReadShuffle() {
        manager.writeShuffle(1, 0, Map.of("a", 1, "b", 2));
        manager.writeShuffle(1, 1, Map.of("c", 3));
        var result = manager.readShuffle(1, manager.getPartition("a"));
        assertFalse(result.isEmpty());
    }

    @Test void testGetPartition() {
        int p = manager.getPartition("key");
        assertTrue(p >= 0 && p < 3);
    }

    @Test void testClear() {
        manager.writeShuffle(1, 0, Map.of("a", 1));
        manager.clear();
        var result = manager.readShuffle(1, 0);
        assertTrue(result.isEmpty());
    }
}
