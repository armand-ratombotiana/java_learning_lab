package com.distributed.gossip;

import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SwimProtocolTest {

    @Test
    void testProbeTracking() {
        NodeId self = new NodeId("self", new InetSocketAddress("localhost", 8000));
        NodeId peer = new NodeId("peer", new InetSocketAddress("localhost", 8001));
        GossipNode node = new GossipNode(self, List.of(peer), 3, 100);
        SwimProtocol swim = new SwimProtocol(node, 1000, 2, 3);
        assertFalse(swim.probe(peer));
        assertFalse(swim.isSuspect(peer));
    }

    @Test
    void testSuspicionAfterThreshold() {
        NodeId self = new NodeId("self", new InetSocketAddress("localhost", 8000));
        NodeId peer = new NodeId("peer", new InetSocketAddress("localhost", 8001));
        GossipNode node = new GossipNode(self, List.of(peer), 3, 100);
        SwimProtocol swim = new SwimProtocol(node, 5000, 2, 2);
        swim.probe(peer);
        swim.probe(peer);
        assertFalse(swim.isSuspect(peer));
    }
}
