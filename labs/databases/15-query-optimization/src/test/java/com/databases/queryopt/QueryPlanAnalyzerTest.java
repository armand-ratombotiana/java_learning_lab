package com.databases.queryopt;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class QueryPlanAnalyzerTest {
    @Test void shouldDetectTableScan() {
        var plan = QueryPlanAnalyzer.QueryPlan.parse(
            "SELECT * FROM users WHERE name LIKE '%test%'",
            "Seq Scan on users (cost=0.00..100.00 rows=1000)");
        assertTrue(plan.hasTableScan());
        assertFalse(plan.hasIndexScan());
    }

    @Test void shouldDetectIndexScan() {
        var plan = QueryPlanAnalyzer.QueryPlan.parse(
            "SELECT * FROM users WHERE id = 42",
            "Index Scan using users_pkey on users (cost=0.28..8.29 rows=1)");
        assertTrue(plan.hasIndexScan());
    }

    @Test void shouldGenerateWarnings() {
        var plan = QueryPlanAnalyzer.QueryPlan.parse(
            "SELECT * FROM users WHERE name LIKE '%test%'",
            "Seq Scan on users (cost=0.00..100.00 rows=1000)");
        assertFalse(plan.warnings().isEmpty());
    }

    @Test void shouldParseCost() {
        var plan = QueryPlanAnalyzer.QueryPlan.parse("SELECT 1", "cost=42.50..100.00 rows=10");
        assertEquals(42.5, plan.estimatedCost(), 0.01);
    }

    @Test void shouldGenerateSummary() {
        var plan = QueryPlanAnalyzer.QueryPlan.parse("SELECT * FROM t", "Seq Scan on t (cost=0..50 rows=100)");
        assertTrue(plan.generateSummary().contains("Seq Scan"));
    }
}
