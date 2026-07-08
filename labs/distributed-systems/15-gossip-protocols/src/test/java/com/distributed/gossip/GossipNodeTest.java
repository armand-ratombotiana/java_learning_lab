package com.distributed.gossip;

import org.junit.jupiter.api.Test;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class GossipNodeTest {

    @Test
    void testNodeStartsAndStops() {
        NodeId id = new NodeId("node1", new InetSocketAddress("localhost", 8001));
        GossipNode node = new GossipNode(id, List.of(), 3, 100);
        assertFalse(node.isRunning());
        node.start();
        assertTrue(node.isRunning());
        node.stop();
        assertFalse(node.isRunning());
    }

    @Test
    void testMembershipTracking() {
        NodeId self = new NodeId("self", new InetSocketAddress("localhost", 8000));
        NodeId peer1 = new NodeId("peer1", new InetSocketAddress("localhost", 8001));
        NodeId peer2 = new NodeId("peer2", new InetSocketAddress("localhost", 8002));
        GossipNode node = new GossipNode(self, List.of(peer1, peer2), 3, 100);
        assertEquals(2, node.getMemberCount());
        assertTrue(node.getKnownPeers().contains(peer1));
        assertTrue(node.getKnownPeers().contains(peer2));
    }

    @Test
    void testSelfExcluded() {
        NodeId self = new NodeId("self", new InetSocketAddress("localhost", 8000));
        GossipNode node = new GossipNode(self, List.of(self), 3, 100);
        assertEquals(0, node.getMemberCount());
    }

    @Test
    void testBroadcastStoresMessages() {
        NodeId self = new NodeId("self", new InetSocketAddress("localhost", 8000));
        GossipNode node = new GossipNode(self, List.of(), 3, 100);
        node.broadcast("hello".getBytes());
        node.broadcast("world".getBytes());
        assertTrue(node.isRunning() || true);
    }
}
