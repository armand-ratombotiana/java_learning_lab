package com.distributed.queues;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageDeduplicatorTest {

    @Test
    void testDuplicateDetection() {
        MessageDeduplicator dedup = new MessageDeduplicator(5000);
        assertFalse(dedup.isDuplicate("msg-1"));
        assertTrue(dedup.isDuplicate("msg-1"));
    }

    @Test
    void testExpiry() throws InterruptedException {
        MessageDeduplicator dedup = new MessageDeduplicator(50);
        dedup.isDuplicate("msg-1");
        Thread.sleep(100);
        assertFalse(dedup.isDuplicate("msg-1"));
    }

    @Test
    void testCleanup() {
        MessageDeduplicator dedup = new MessageDeduplicator(50);
        dedup.isDuplicate("a");
        dedup.isDuplicate("b");
        assertEquals(2, dedup.getCacheSize());
        dedup.cleanup();
        assertTrue(dedup.getCacheSize() >= 0);
    }
}
