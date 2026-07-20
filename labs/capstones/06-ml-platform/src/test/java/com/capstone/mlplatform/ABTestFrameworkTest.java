package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ABTestFrameworkTest {
    private ABTestFramework framework;

    @BeforeEach
    void setUp() { framework = new ABTestFramework(); }

    @Test void testCreateExperiment() {
        var variants = List.of(
            new ABTestFramework.Variant("v1", "Control", "model-a", 0.5),
            new ABTestFramework.Variant("v2", "Treatment", "model-b", 0.5));
        var exp = framework.createExperiment("test-ab", variants, 1.0, "conversion");
        assertNotNull(exp.experimentId());
        assertEquals(2, exp.variants().size());
    }

    @Test void testAssignVariant() {
        var variants = List.of(
            new ABTestFramework.Variant("v1", "Control", "model-a", 0.5),
            new ABTestFramework.Variant("v2", "Treatment", "model-b", 0.5));
        framework.createExperiment("test-ab", variants, 1.0, "conversion");
        var v = framework.assignVariant("test-ab", "user-1");
        assertNotNull(v);
    }

    @Test void testRecordAndGetMetrics() {
        var variants = List.of(
            new ABTestFramework.Variant("v1", "Control", "model-a", 0.5),
            new ABTestFramework.Variant("v2", "Treatment", "model-b", 0.5));
        var exp = framework.createExperiment("test-ab", variants, 1.0, "conversion");
        framework.recordResult(exp.experimentId(), "v1", "u1", 0.8);
        framework.recordResult(exp.experimentId(), "v1", "u2", 0.9);
        framework.recordResult(exp.experimentId(), "v2", "u3", 0.95);
        var metrics = framework.getVariantMetrics(exp.experimentId());
        assertEquals(2, metrics.size());
        assertEquals(0.85, metrics.get("v1"), 0.01);
    }

    @Test void testWinningVariant() {
        var variants = List.of(
            new ABTestFramework.Variant("v1", "Control", "model-a", 0.5),
            new ABTestFramework.Variant("v2", "Treatment", "model-b", 0.5));
        var exp = framework.createExperiment("test-ab", variants, 1.0, "conversion");
        framework.recordResult(exp.experimentId(), "v1", "u1", 0.5);
        framework.recordResult(exp.experimentId(), "v2", "u2", 0.9);
        var winner = framework.getWinningVariant(exp.experimentId());
        assertTrue(winner.isPresent());
        assertEquals("v2", winner.get().id());
    }
}
