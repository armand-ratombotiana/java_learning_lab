package com.algo.lab23;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class LoadBalancingTest {

    @Test
    void testRoundRobinBasic() {
        RoundRobinBalancer lb = new RoundRobinBalancer();
        List<String> servers = List.of("A", "B", "C");
        assertEquals("A", lb.selectServer(servers, "r1"));
        assertEquals("B", lb.selectServer(servers, "r2"));
        assertEquals("C", lb.selectServer(servers, "r3"));
        assertEquals("A", lb.selectServer(servers, "r4"));
    }

    @Test
    void testRoundRobinEmpty() {
        RoundRobinBalancer lb = new RoundRobinBalancer();
        assertNull(lb.selectServer(List.of(), "r1"));
    }

    @Test
    void testWeightedRoundRobin() {
        Map<String, Integer> weights = new HashMap<>();
        weights.put("A", 3);
        weights.put("B", 1);
        WeightedRoundRobin lb = new WeightedRoundRobin(weights);
        List<String> servers = List.of("A", "B");
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < 8; i++) {
            String s = lb.selectServer(servers, "r" + i);
            counts.merge(s, 1, Integer::sum);
        }
        assertTrue(counts.getOrDefault("A", 0) > counts.getOrDefault("B", 0));
    }

    @Test
    void testWeightedRoundRobinEmpty() {
        WeightedRoundRobin lb = new WeightedRoundRobin(Map.of());
        assertNull(lb.selectServer(List.of(), "r1"));
    }

    @Test
    void testLeastConnections() {
        LeastConnectionsBalancer lb = new LeastConnectionsBalancer();
        List<String> servers = List.of("A", "B");
        assertEquals("A", lb.selectServer(servers, "r1"));
        assertEquals("B", lb.selectServer(servers, "r2"));
        lb.releaseConnection("A");
        assertEquals("A", lb.selectServer(servers, "r3"));
    }

    @Test
    void testLeastConnectionsCount() {
        LeastConnectionsBalancer lb = new LeastConnectionsBalancer();
        List<String> servers = List.of("A", "B");
        lb.selectServer(servers, "r1");
        lb.selectServer(servers, "r1");
        assertEquals(2, lb.getConnectionCount("A"));
    }

    @Test
    void testConsistentHash() {
        ConsistentHashBalancer ch = new ConsistentHashBalancer(3);
        ch.addServer("A");
        ch.addServer("B");
        List<String> servers = List.of("A", "B");
        String s1 = ch.selectServer(servers, "request-1");
        String s2 = ch.selectServer(servers, "request-1");
        assertEquals(s1, s2);
    }

    @Test
    void testConsistentHashAddRemove() {
        ConsistentHashBalancer ch = new ConsistentHashBalancer(3);
        ch.addServer("A");
        ch.addServer("B");
        List<String> servers = List.of("A", "B");
        String s1 = ch.selectServer(servers, "test");
        ch.removeServer("A");
        assertNotNull(ch.selectServer(List.of("B"), "test"));
    }

    @Test
    void testConsistentHashEmpty() {
        ConsistentHashBalancer ch = new ConsistentHashBalancer(3);
        assertNull(ch.selectServer(List.of(), "r1"));
    }

    @Test
    void testLoadBalancerInterface() {
        LoadBalancer lb = (servers, request) -> servers.get(0);
        assertEquals("A", lb.selectServer(List.of("A", "B"), "r1"));
    }
}
