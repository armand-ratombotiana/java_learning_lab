package com.oracleebs.customization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class WorkflowSimulatorTest {
    private WorkflowSimulator sim;

    @BeforeEach
    void setUp() {
        sim = WorkflowSimulator.createDefault();
    }

    @Test
    void testStartWorkflow() {
        var item = sim.startWorkflow("ITEM001", "PO_APPROVAL", "Alice");
        assertNotNull(item);
        assertEquals(WorkflowSimulator.WorkflowStatus.IN_PROGRESS, item.getStatus());
    }

    @Test
    void testAdvanceWorkflow() {
        sim.startWorkflow("ITEM001", "PO_APPROVAL", "Alice");
        assertTrue(sim.advanceWorkflow("ITEM001", "MANAGER_APPROVAL"));
    }

    @Test
    void testWorkflowCompletes() {
        sim.startWorkflow("ITEM001", "PO_APPROVAL", "Alice");
        sim.advanceWorkflow("ITEM001", "MANAGER_APPROVAL");
        sim.advanceWorkflow("ITEM001", "BUDGET_CHECK");
        sim.advanceWorkflow("ITEM001", "FINANCE_APPROVAL");
        sim.advanceWorkflow("ITEM001", "END");
        var item = sim.getItem("ITEM001");
        assertTrue(item.isPresent());
        assertEquals(WorkflowSimulator.WorkflowStatus.COMPLETED, item.get().getStatus());
    }

    @Test
    void testSuspendWorkflow() {
        sim.startWorkflow("ITEM001", "PO_APPROVAL", "Alice");
        assertTrue(sim.suspendWorkflow("ITEM001"));
        var item = sim.getItem("ITEM001");
        assertEquals(WorkflowSimulator.WorkflowStatus.SUSPENDED, item.get().getStatus());
    }

    @Test
    void testInvalidWorkflow() {
        assertThrows(IllegalArgumentException.class, () -> sim.startWorkflow("X", "NONEXISTENT", "User"));
    }

    @Test
    void testActivityLogging() {
        sim.startWorkflow("ITEM001", "PO_APPROVAL", "Alice");
        sim.advanceWorkflow("ITEM001", "MANAGER_APPROVAL");
        var item = sim.getItem("ITEM001").get();
        assertEquals(2, item.getActivityLog().size());
    }
}
