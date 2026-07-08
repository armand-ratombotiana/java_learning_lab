package com.distributed.monitoring;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MetricRegistryTest {

    @Test
    void testCounterIncrement() {
        MetricRegistry registry = new MetricRegistry();
        MetricRegistry.Counter counter = registry.counter("requests_total");
        assertEquals(0, counter.getValue());
        counter.increment();
        assertEquals(1, counter.getValue());
        counter.increment(5);
        assertEquals(6, counter.getValue());
    }

    @Test
    void testGaugeSet() {
        MetricRegistry registry = new MetricRegistry();
        MetricRegistry.Gauge gauge = registry.gauge("memory_usage");
        gauge.set(1024.5);
        assertEquals(1024.5, gauge.getValue(), 0.01);
    }

    @Test
    void testHistogramObservations() {
        MetricRegistry registry = new MetricRegistry();
        MetricRegistry.Histogram hist = registry.histogram("request_latency", Map.of(),
            new double[]{1, 5, 10, 50, 100});
        hist.observe(3);
        hist.observe(7);
        hist.observe(20);
        assertEquals(3, hist.getCount());
        assertTrue(hist.getSum() > 0);
    }

    @Test
    void testPrometheusExport() {
        MetricRegistry registry = new MetricRegistry();
        registry.counter("test_counter").increment();
        String output = registry.exportPrometheus();
        assertTrue(output.contains("test_counter"));
        assertTrue(output.contains("counter"));
    }
}
