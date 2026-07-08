package com.databases.sharding;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class RangePartitionerTest {
    private RangePartitioner<Integer> p;
    @BeforeEach void setUp() { p = new RangePartitioner<>(); }

    @Test void shouldRouteToCorrectShard() {
        p.addRange(100, "s0", 1000); p.addRange(200, "s1", 1000);
        assertEquals("s0", p.getShardForKey(50));
        assertEquals("s1", p.getShardForKey(150));
    }

    @Test void shouldThrowForOutOfRange() {
        p.addRange(100, "s0", 1000);
        assertThrows(IllegalArgumentException.class, () -> p.getShardForKey(200));
    }

    @Test void shouldReportUtilization() {
        p.addRange(500, "sa", 1000); p.addRange(1000, "sb", 2000);
        p.getShardInfo("sa").addData(500);
        assertEquals(0.5, p.getShardInfo("sa").getUtilization(), 0.001);
    }

    @Test void shouldListShardsForRange() {
        p.addRange(100, "s0", 1000); p.addRange(200, "s1", 1000); p.addRange(300, "s2", 1000);
        var shards = p.getShardsForRange(50, 250);
        assertTrue(shards.containsAll(List.of("s0", "s1", "s2")));
    }
}
