package com.capstone.kafka;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class OffsetManagerTest {
    private OffsetManager om;

    @BeforeEach
    void setUp() { om = new OffsetManager(); }

    @Test void testCommitAndGet() {
        om.commitOffset("g1", "t1", 0, 5);
        var meta = om.getCommittedOffset("g1", "t1", 0);
        assertEquals(5, meta.offset());
    }

    @Test void testDefaultOffset() {
        var meta = om.getCommittedOffset("g1", "t1", 0);
        assertEquals(0, meta.offset());
    }

    @Test void testNextOffset() {
        om.commitOffset("g1", "t1", 0, 10);
        assertEquals(11, om.getNextOffset("g1", "t1", 0));
    }

    @Test void testResetOffset() {
        om.commitOffset("g1", "t1", 0, 100);
        om.resetOffset("g1", "t1", 0, 50);
        assertEquals(50, om.getCommittedOffset("g1", "t1", 0).offset());
    }

    @Test void testGlobalOffset() {
        long o1 = om.generateGlobalOffset();
        long o2 = om.generateGlobalOffset();
        assertEquals(o1 + 1, o2);
    }

    @Test void testDeleteOffsets() {
        om.commitOffset("g1", "t1", 0, 5);
        om.deleteOffsets("g1", "t1");
        assertEquals(0, om.getCommittedOffset("g1", "t1", 0).offset());
    }
}
