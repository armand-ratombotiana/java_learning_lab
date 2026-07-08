package com.databases.sharding;

import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class ShardManagerTest {
    private ShardManager m;
    @BeforeEach void setUp() { m = new ShardManager(3); }

    @Test void shouldRouteKeys() {
        m.addShard("a"); m.addShard("b");
        assertTrue(List.of("a","b").contains(m.routeKey("user-42")));
    }

    @Test void shouldTrackStats() {
        m.addShard("s"); m.routeKey("test");
        assertNotNull(m.getStats("s"));
    }

    @Test void shouldReportSkew() {
        m.addShard("s1"); m.addShard("s2");
        assertTrue(m.getSkewFactor() >= 0);
    }

    @Test void shouldAllocateKeys() {
        m.addShard("a1"); m.addShard("a2");
        var a = m.getAllocations(new HashSet<>(Arrays.asList("k1","k2","k3","k4")));
        assertEquals(4, a.stream().mapToInt(List::size).sum());
    }

    @AfterEach void tearDown() { m.shutdown(); }
}
