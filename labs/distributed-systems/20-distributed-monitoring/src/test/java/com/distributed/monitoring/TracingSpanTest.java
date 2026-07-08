package com.distributed.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TracingSpanTest {

    @Test
    void testSpanCreation() {
        TracingSpan span = new TracingSpan("trace-1", "operation");
        assertNotNull(span.getSpanId());
        assertEquals("trace-1", span.getTraceId());
        assertEquals("operation", span.getOperationName());
    }

    @Test
    void testSpanDuration() throws InterruptedException {
        TracingSpan span = new TracingSpan("t1", "sleep-op");
        Thread.sleep(10);
        span.end();
        assertTrue(span.getDurationMicros() >= 9000);
    }

    @Test
    void testAttributes() {
        TracingSpan span = new TracingSpan("t2", "db-query");
        span.setAttribute("db", "users");
        span.setAttribute("query", "SELECT *");
        assertEquals(2, span.getAttributes().size());
        assertEquals("users", span.getAttributes().get("db"));
    }

    @Test
    void testAutoClose() {
        try (TracingSpan span = new TracingSpan("t3", "auto-close")) {
            span.setAttribute("key", "value");
        }
    }

    @Test
    void testParentSpan() {
        TracingSpan parent = new TracingSpan("trace-p", "parent-op");
        TracingSpan child = new TracingSpan("trace-p", parent.getSpanId(), "child-op");
        assertEquals(parent.getSpanId(), child.getParentSpanId());
    }
}
