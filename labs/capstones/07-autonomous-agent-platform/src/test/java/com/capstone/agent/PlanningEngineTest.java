package com.capstone.agent;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class PlanningEngineTest {
    private PlanningEngine planner;

    @BeforeEach
    void setUp() { planner = new PlanningEngine(ToolRegistry.createDefaultTools()); }

    @Test void testGenerateThought() {
        String thought = planner.generateThought("test observation", List.of("search result"));
        assertNotNull(thought);
        assertFalse(thought.isBlank());
    }

    @Test void testCreatePlan() {
        var plan = planner.createPlan("search for information about Java");
        assertFalse(plan.isEmpty());
    }

    @Test void testStructuredPlan() {
        var plan = planner.createStructuredPlan("search for X.calculate Y");
        assertNotNull(plan.id());
        assertEquals(PlanningEngine.PlanStatus.DRAFT, plan.status());
    }

    @Test void testExecutePlan() {
        var plan = planner.createStructuredPlan("search for data");
        var executed = planner.executePlan(plan.id());
        assertNotNull(executed);
    }

    @Test void testGetPlans() {
        planner.createStructuredPlan("goal 1");
        planner.createStructuredPlan("goal 2");
        assertEquals(2, planner.getPlans().size());
    }
}
