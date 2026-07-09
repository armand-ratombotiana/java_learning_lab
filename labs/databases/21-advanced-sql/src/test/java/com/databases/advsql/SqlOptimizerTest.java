package com.databases.advsql;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class SqlOptimizerTest {
    private SqlOptimizer optimizer;
    @BeforeEach void setUp() { optimizer = SqlOptimizer.sampleOptimizer(); }

    @Test void shouldEstimateFullScanCost() {
        double cost = optimizer.estimateFullScanCost("EMPLOYEES");
        assertTrue(cost > 0);
        assertTrue(cost < 10000);
    }

    @Test void shouldGeneratePlan() {
        var plan = optimizer.generatePlan("SELECT * FROM EMPLOYEES WHERE dept_id = 10");
        assertNotNull(plan);
        assertEquals("EMPLOYEES", plan.object());
        assertTrue(plan.estCost() > 0);
    }

    @Test void shouldEstimateJoin() {
        double cost = optimizer.estimateJoinCost("EMPLOYEES", "DEPARTMENTS", "dept_id", 0.02);
        assertTrue(cost > 0);
    }

    @Test void shouldReturnStats() {
        assertNotNull(optimizer.getTableStats("EMPLOYEES"));
        assertNull(optimizer.getTableStats("NONEXISTENT"));
        assertNotNull(optimizer.getColumnStats("dept_id"));
    }
}