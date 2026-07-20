package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExperimentTrackerTest {
    private ExperimentTracker tracker;

    @BeforeEach
    void setUp() { tracker = new ExperimentTracker(); }

    @Test void testCreateExperiment() {
        var exp = tracker.createExperiment("test-exp", "Testing", Map.of("lr", 0.001));
        assertEquals("test-exp", exp.name());
        assertNotNull(exp.id());
    }

    @Test void testLogMetrics() {
        var exp = tracker.createExperiment("exp1", "desc", Map.of());
        tracker.logMetrics(exp.id(), "run1", Map.of("accuracy", 0.95));
        var logs = tracker.getRunLogs(exp.id());
        assertEquals(1, logs.size());
        assertEquals(0.95, logs.get(0).metrics().get("accuracy"));
    }

    @Test void testBestMetrics() {
        var exp = tracker.createExperiment("exp1", "desc", Map.of());
        tracker.logMetrics(exp.id(), "run1", Map.of("accuracy", 0.90));
        tracker.logMetrics(exp.id(), "run2", Map.of("accuracy", 0.95));
        var best = tracker.getBestMetrics(exp.id(), "accuracy");
        assertTrue(best.isPresent());
        assertEquals(0.95, best.get().get("accuracy"));
    }

    @Test void testEmptyLogs() {
        assertTrue(tracker.getRunLogs("nonexistent").isEmpty());
    }
}
