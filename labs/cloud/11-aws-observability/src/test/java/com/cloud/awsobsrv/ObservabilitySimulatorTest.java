package com.cloud.awsobsrv;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class ObservabilitySimulatorTest {
    private ObservabilitySimulator.MetricsRegistry registry;

    @BeforeEach void setUp() { registry = new ObservabilitySimulator.MetricsRegistry(); }

    @Test void testRecordAndAverage() {
        registry.record("test_metric", 10.0, Map.of("env", "test"));
        registry.record("test_metric", 20.0, Map.of("env", "test"));
        registry.record("test_metric", 30.0, Map.of("env", "test"));
        assertEquals(20.0, registry.average("test_metric", Map.of("env", "test")), 0.001);
    }

    @Test void testPercentile() {
        for (int i = 1; i <= 100; i++) registry.record("latency", (double) i, Map.of());
        assertEquals(50.0, registry.percentile("latency", Map.of(), 50), 1.0);
        assertEquals(95.0, registry.percentile("latency", Map.of(), 95), 1.0);
    }

    @Test void testEmptyMetrics() {
        assertEquals(0, registry.average("nonexistent", Map.of()), 0.001);
        assertEquals(0, registry.percentile("nonexistent", Map.of(), 50), 0.001);
    }

    @Test void testTraceSegment() {
        var trace = new ObservabilitySimulator.TraceSegment("trace-1", "Service");
        trace.putAnnotation("key", "value");
        trace.end();
        assertTrue(trace.getDurationMs() >= 0);
        assertEquals("trace-1", trace.getTraceId());
        assertEquals("value", trace.getAnnotations().get("key"));
        assertFalse(trace.isFault());
    }
}
