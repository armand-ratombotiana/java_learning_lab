package com.distributed.monitoring;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class AlertEvaluatorTest {

    @Test
    void testThresholdAlert() {
        AlertEvaluator evaluator = new AlertEvaluator();
        evaluator.addRule(new AlertEvaluator.AlertRule(
            "high_cpu", "cpu_usage", 90.0,
            AlertEvaluator.AlertRule.Comparison.GREATER_THAN,
            AlertEvaluator.Duration.ofMillis(100), "critical"
        ));

        List<AlertEvaluator.AlertEvent> events = evaluator.evaluate(() -> Map.of("cpu_usage", 95));
        assertTrue(events.isEmpty());

        events = evaluator.evaluate(() -> Map.of("cpu_usage", 95));
        assertFalse(events.isEmpty());
        assertEquals("high_cpu", events.get(0).ruleName());
    }

    @Test
    void testNoAlertBelowThreshold() {
        AlertEvaluator evaluator = new AlertEvaluator();
        evaluator.addRule(new AlertEvaluator.AlertRule(
            "low_memory", "memory_free", 100,
            AlertEvaluator.AlertRule.Comparison.LESS_THAN,
            AlertEvaluator.Duration.ofMillis(100), "warning"
        ));

        List<AlertEvaluator.AlertEvent> events = evaluator.evaluate(() -> Map.of("memory_free", 500));
        assertTrue(events.isEmpty());
    }

    @Test
    void testAlertResolves() {
        AlertEvaluator evaluator = new AlertEvaluator();
        evaluator.addRule(new AlertEvaluator.AlertRule(
            "disk_full", "disk_usage", 95.0,
            AlertEvaluator.AlertRule.Comparison.GREATER_THAN,
            AlertEvaluator.Duration.ofMillis(100), "critical"
        ));

        evaluator.evaluate(() -> Map.of("disk_usage", 98));
        List<AlertEvaluator.AlertEvent> events = evaluator.evaluate(() -> Map.of("disk_usage", 98));
        assertEquals(1, events.size());

        events = evaluator.evaluate(() -> Map.of("disk_usage", 50));
        assertEquals(0, events.size());
    }
}
