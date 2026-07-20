package com.capstone.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class GossipProtocolTest {
    private GossipProtocol gossip;

    @BeforeEach
    void setUp() { gossip = new GossipProtocol("local-node", 1000); }

    @Test void testJoin() {
        gossip.join("remote-node");
        assertEquals(2, gossip.clusterSize());
        assertEquals(GossipProtocol.NodeStatus.ALIVE, gossip.getNodeState("remote-node").status());
    }

    @Test void testLeave() {
        gossip.leave();
        assertEquals(GossipProtocol.NodeStatus.LEFT, gossip.getNodeState("local-node").status());
    }

    @Test void testMarkSuspectedDead() {
        gossip.join("node2");
        gossip.markSuspected("node2");
        assertEquals(GossipProtocol.NodeStatus.SUSPECT, gossip.getNodeState("node2").status());
        gossip.markDead("node2");
        assertEquals(GossipProtocol.NodeStatus.DEAD, gossip.getNodeState("node2").status());
    }

    @Test void testProcessGossip() {
        gossip.join("node2");
        var msg = gossip.generateGossip("node2");
        var gossip2 = new GossipProtocol("node2", 1000);
        gossip2.processGossip(msg);
        assertEquals(2, gossip2.clusterSize());
    }

    @Test void testAliveCount() {
        gossip.join("node2");
        gossip.join("node3");
        assertEquals(3, gossip.aliveCount());
        gossip.markDead("node2");
        assertEquals(2, gossip.aliveCount());
    }
}
