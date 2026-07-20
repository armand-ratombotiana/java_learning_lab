package com.capstone.cache;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class CacheClientTest {
    private ConsistentHashRing ring;
    private CacheClient client;

    @BeforeEach
    void setUp() {
        ring = new ConsistentHashRing(10, String::hashCode);
        ring.addNode(new ConsistentHashRing.CacheNode("node1", "localhost", 8081, true));
        client = new CacheClient(ring);
    }

    @Test void testPutAndGet() {
        client.put("key1", "value1".getBytes());
        var val = client.get("key1");
        assertTrue(val.isPresent());
        assertEquals("value1", new String(val.get()));
    }

    @Test void testGetMissing() {
        var val = client.get("nonexistent");
        assertFalse(val.isPresent());
    }

    @Test void testDelete() {
        client.put("key1", "value1".getBytes());
        assertTrue(client.delete("key1"));
        assertFalse(client.get("key1").isPresent());
    }

    @Test void testStats() {
        client.get("miss1"); client.get("miss2");
        client.put("k1", "v1".getBytes());
        client.get("k1");
        var stats = client.getStats();
        assertEquals(1, stats.hits());
        assertEquals(2, stats.misses());
    }

    @Test void testClear() {
        client.put("k1", "v1".getBytes());
        client.clear();
        assertEquals(0, client.getStats().totalOps());
    }
}
