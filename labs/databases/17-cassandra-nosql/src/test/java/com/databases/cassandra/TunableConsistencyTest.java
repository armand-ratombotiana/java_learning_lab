package com.databases.cassandra;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TunableConsistencyTest {
    private TunableConsistency tc;
    @BeforeEach void setUp() { tc = new TunableConsistency(3); }

    @Test void shouldSucceedWithONE() {
        tc.addNode("n1"); tc.addNode("n2"); tc.addNode("n3");
        assertTrue(tc.write("k", "v", TunableConsistency.ConsistencyLevel.ONE));
    }

    @Test void shouldFailWithALL() {
        tc.addNode("n1"); tc.addNode("n2");
        assertFalse(tc.write("k", "v", TunableConsistency.ConsistencyLevel.ALL));
    }

    @Test void shouldThrowOnInsufficientRead() {
        tc.addNode("n1");
        assertThrows(RuntimeException.class,
            () -> tc.read("k", TunableConsistency.ConsistencyLevel.QUORUM));
    }

    @Test void shouldReadWithONE() {
        tc.addNode("n1"); tc.addNode("n2"); tc.addNode("n3");
        var val = tc.read("k", TunableConsistency.ConsistencyLevel.ONE);
        assertEquals("value_for_k", val);
    }
}
