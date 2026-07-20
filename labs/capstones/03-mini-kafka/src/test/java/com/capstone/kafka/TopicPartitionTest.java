package com.capstone.kafka;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class TopicPartitionTest {
    private TopicPartition tp;

    @BeforeEach
    void setUp() { tp = new TopicPartition("test-topic", 0); }

    @Test void testAppendAndRead() {
        long offset = tp.append("key1".getBytes(), "value1".getBytes());
        assertEquals(0, offset);
        var messages = tp.read(0, 10);
        assertEquals(1, messages.size());
        assertEquals("value1", new String(messages.get(0).value()));
    }

    @Test void testReadWithOffset() {
        tp.append("k1".getBytes(), "v1".getBytes());
        tp.append("k2".getBytes(), "v2".getBytes());
        var messages = tp.read(1, 10);
        assertEquals(1, messages.size());
        assertEquals("v2", new String(messages.get(0).value()));
    }

    @Test void testHighWatermark() {
        assertEquals(0, tp.getHighWatermark());
        tp.append("k".getBytes(), "v".getBytes());
        assertEquals(1, tp.getHighWatermark());
    }

    @Test void testCompaction() {
        tp.append("k1".getBytes(), "v1".getBytes());
        tp.append("k2".getBytes(), "v2".getBytes());
        tp.append("k1".getBytes(), "v3".getBytes());
        tp.compact();
        var messages = tp.read(0, 10);
        assertEquals(2, messages.size());
        assertEquals("v3", new String(messages.get(1).value()));
    }

    @Test void testClear() {
        tp.append("k".getBytes(), "v".getBytes());
        tp.clear();
        assertEquals(0, tp.messageCount());
    }

    @Test void testReadPastEnd() {
        var messages = tp.read(100, 10);
        assertTrue(messages.isEmpty());
    }
}
