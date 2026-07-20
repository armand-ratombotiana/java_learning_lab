package com.capstone.kafka;

import org.junit.jupiter.api.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LogSegmentTest {
    private LogSegment segment;

    @BeforeEach
    void setUp() {
        segment = new LogSegment("test", 0, 0, Path.of(System.getProperty("java.io.tmpdir")));
    }

    @Test void testAppend() {
        long offset = segment.append("k1".getBytes(), "v1".getBytes());
        assertEquals(0, offset);
        assertEquals(1, segment.entryCount());
    }

    @Test void testRead() {
        segment.append("k1".getBytes(), "v1".getBytes());
        segment.append("k2".getBytes(), "v2".getBytes());
        var entries = segment.read(0, 10);
        assertEquals(2, entries.size());
    }

    @Test void testContainsOffset() {
        segment.append("k".getBytes(), "v".getBytes());
        assertTrue(segment.containsOffset(0));
        assertFalse(segment.containsOffset(5));
    }

    @Test void testIsActive() {
        assertTrue(segment.isActive());
    }

    @Test void testFlushToDisk() {
        segment.append("k".getBytes(), "v".getBytes());
        segment.flushToDisk();
        assertEquals(1, segment.entryCount());
    }

    @Test void testClear() {
        segment.append("k".getBytes(), "v".getBytes());
        segment.clear();
        assertEquals(0, segment.entryCount());
    }
}
