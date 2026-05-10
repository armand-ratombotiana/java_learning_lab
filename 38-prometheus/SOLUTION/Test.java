package com.learning.prometheus;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PrometheusSolutionTest {

    private MeterRegistry registry;
    private PrometheusSolution solution;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        solution = new PrometheusSolution(registry);
    }

    @Test
    void testIncrementCounter() {
        solution.incrementCounter();
        Counter counter = registry.find("http_requests_total").counter();
        assertNotNull(counter);
        assertEquals(1.0, counter.count());
    }

    @Test
    void testRecordTimer() {
        solution.recordTimer(100, TimeUnit.MILLISECONDS);
        var timer = registry.find("http_request_duration").timer();
        assertNotNull(timer);
    }

    @Test
    void testGetCounter() {
        Counter counter = solution.getCounter("custom_counter");
        assertNotNull(counter);
    }

    @Test
    void testRegisterGauge() {
        solution.registerGauge("test_gauge", () -> 42.0);
        var gauge = registry.find("test_gauge").gauge();
        assertNotNull(gauge);
        assertEquals(42.0, gauge.value());
    }

    @Test
    void testGetRegistry() {
        assertEquals(registry, solution.getRegistry());
    }
}