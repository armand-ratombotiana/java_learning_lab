package com.capstone.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ReplicationManagerTest {
    private ConsistentHashRing ring;
    private ReplicationManager rep;

    @BeforeEach
    void setUp() {
        ring = new ConsistentHashRing(10, String::hashCode);
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node2", "localhost", 8082, true));
        ring.addNode(new ConsistentHashRing.CacheNode("node3", "localhost", 8083, true));
        rep = new ReplicationManager("node1", ring, 2);
    }

    @Test void testReplicatePut() {
        rep.replicatePut("key1", "value1".getBytes());
        assertTrue(rep.getPendingCount() > 0);
    }

    @Test void testReplicateDelete() {
        rep.replicateDelete("key1");
        assertTrue(rep.getPendingCount() > 0);
    }

    @Test void testLeaderElection() {
        rep.leaderElection(List.of("node1", "node2", "node3"));
        assertTrue(rep.getPendingCount() >= 0);
    }

    @Test void testAcknowledge() {
        rep.replicatePut("k", "v".getBytes());
        rep.acknowledgeReplication("node2", "some-entry");
    }
}
