package com.databases.sharding;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashRingTest {
    private ConsistentHashRing<String> ring;
    @BeforeEach void setUp() { ring = new ConsistentHashRing<>(3); }

    @Test void shouldRouteKeyToExistingNode() {
        ring.addNode("a"); ring.addNode("b"); ring.addNode("c");
        assertNotNull(ring.getNode("test-key"));
    }

    @Test void shouldThrowWhenEmpty() {
        assertThrows(IllegalStateException.class, () -> ring.getNode("x"));
    }

    @Test void shouldDistributeKeysEvenly() {
        ring.addNode("1"); ring.addNode("2"); ring.addNode("3");
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 1000; i++) keys.add("key-" + i);
        var dist = ring.getDistribution(keys);
        assertEquals(3, dist.size());
        double avg = 1000.0 / 3;
        for (int c : dist.values()) assertTrue(Math.abs(c - avg) / avg < 0.5);
    }

    @Test void shouldMaintainAfterRemoval() {
        ring.addNode("a"); ring.addNode("b"); ring.addNode("c"); ring.addNode("d");
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < 500; i++) keys.add("k-" + i);
        ring.removeNode("b");
        assertFalse(ring.getDistribution(keys).containsKey("b"));
    }

    @Test void shouldBeDeterministic() {
        ring.addNode("x"); ring.addNode("y");
        assertEquals(ring.getNode("hello"), ring.getNode("hello"));
    }

    @Test void shouldAddRemoveMultiple() {
        ring.addNode("n1"); ring.addNode("n2");
        assertEquals(2, ring.getNodeCount());
        ring.removeNode("n1");
        assertEquals(1, ring.getNodeCount());
        ring.addNode("n3");
        assertEquals(2, ring.getNodeCount());
    }
}
