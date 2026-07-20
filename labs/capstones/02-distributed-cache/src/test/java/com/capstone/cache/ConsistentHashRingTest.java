package com.capstone.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashRingTest {
    private ConsistentHashRing ring;

    @BeforeEach
    void setUp() { ring = new ConsistentHashRing(10, String::hashCode); }

    @Test void testAddNode() {
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        assertEquals(1, ring.getNodeCount());
        assertEquals(10, ring.getVirtualNodeCount());
    }

    @Test void testGetNode() {
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node2", "localhost", 8082, true));
        var node = ring.getNode("my-key");
        assertTrue(node.isPresent());
        assertTrue(node.get().nodeId().equals("node1") || node.get().nodeId().equals("node2"));
    }

    @Test void testRemoveNode() {
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node2", "localhost", 8082, true));
        ring.removeNode("node1");
        assertEquals(1, ring.getNodeCount());
    }

    @Test void testMultipleNodes() {
        for (int i = 0; i < 10; i++) {
            ring.addNode(new ConsistentHashRing.CacheNode("node" + i, "localhost", 8000 + i, true));
        }
        assertEquals(10, ring.getNodeCount());
        var node = ring.getNode("test-key");
        assertTrue(node.isPresent());
    }

    @Test void testGetNodesReplication() {
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node2", "localhost", 8082, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node3", "localhost", 8083, true));
        var nodes = ring.getNodes("key", 2);
        assertEquals(2, nodes.size());
    }

    @Test void testEmptyRing() {
        assertTrue(ring.getNode("key").isEmpty());
    }
}
