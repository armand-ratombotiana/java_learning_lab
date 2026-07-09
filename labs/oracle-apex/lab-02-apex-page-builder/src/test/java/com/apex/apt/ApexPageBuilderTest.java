package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexPageBuilderTest {
    private ApexPageBuilder builder;
    @BeforeEach void setUp() { builder = ApexPageBuilder.createSample(); }

    @Test void shouldHaveProcesses() { assertEquals(1, builder.getProcessCount()); }
    @Test void shouldHaveActions() { assertEquals(1, builder.getActionCount()); }
    @Test void shouldHaveBranches() { assertEquals(1, builder.getBranchCount()); }
    @Test void shouldHaveValidations() { assertEquals(1, builder.getValidationCount()); }

    @Test void shouldExecuteProcesses() {
        var results = builder.executeProcesses();
        assertEquals(1, results.size());
        assertTrue(results.get(0).contains("Save Employee"));
    }

    @Test void shouldHandleAction() {
        var results = builder.handleAction("Refresh Grid", Map.of());
        assertFalse(results.isEmpty());
        assertTrue(results.get(0).contains("REFRESH"));
    }

    @Test void shouldExecuteValidations() {
        var results = builder.executeValidations(Map.of("P1_SALARY", "5000"));
        assertFalse(results.isEmpty());
    }
}