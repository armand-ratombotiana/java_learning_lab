package com.cloud.clcost;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class CostOptimizationEngineTest {
    private CostOptimizationEngine engine;

    @BeforeEach void setUp() { engine = new CostOptimizationEngine(); }

    @Test void testCostReport() {
        var reports = engine.generateReport("m5.large", 730);
        assertEquals(4, reports.size());
        assertTrue(reports.get(0).monthlyCost() > reports.get(1).monthlyCost());
        assertTrue(reports.get(3).savingsVsOnDemand() > 0);
    }

    @Test void testRightsizingRecommendation() {
        var rec = engine.analyzeRightsizing("m5.xlarge", 15.0, 20.0);
        assertEquals("m5.xlarge", rec.currentType());
        assertTrue(rec.monthlySavings() > 0);
    }

    @Test void testOptimalRightsizing() {
        var rec = engine.analyzeRightsizing("m5.large", 55.0, 60.0);
        assertEquals("m5.large", rec.recommendedType());
        assertEquals(0, rec.monthlySavings(), 0.001);
    }

    @Test void testSpotFleet() {
        var spot = new CostOptimizationEngine.SpotFleetManager();
        spot.requestSpotInstance("t3.medium", 0.015);
        assertEquals(1, spot.getActiveCount());
    }
}
