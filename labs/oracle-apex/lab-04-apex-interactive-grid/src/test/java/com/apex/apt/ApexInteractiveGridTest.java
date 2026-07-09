package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexInteractiveGridTest {
    private ApexInteractiveGrid grid;
    @BeforeEach void setUp() { grid = ApexInteractiveGrid.createSample(); }

    @Test void shouldRegisterGrid() {
        assertNotNull(grid.getConfig("EMPLOYEES_IG"));
    }

    @Test void shouldComputeAggregations() {
        var aggs = grid.getAggregations();
        assertTrue(aggs.containsKey("SALARY_SUM"));
        assertEquals(58000.0, aggs.get("SALARY_SUM"));
    }

    @Test void shouldUpdateCell() {
        assertTrue(grid.updateCell(0, "SALARY", 25000));
        assertEquals(25000.0, grid.getData().get(0).get("SALARY"));
    }

    @Test void shouldAddRow() {
        int size = grid.addRow(Map.of("EMP_ID", 103, "ENAME", "Test", "SALARY", 5000, "DEPT_ID", 30));
        assertEquals(4, size);
    }

    @Test void shouldGroupByColumn() {
        var groups = grid.groupByColumn("DEPT_ID");
        assertTrue(groups.size() >= 2);
    }
}