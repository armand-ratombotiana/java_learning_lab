package com.databases.plsqladv;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class PlSqlProfilerTest {
    private PlSqlProfiler profiler;
    @BeforeEach void setUp() { profiler = PlSqlProfiler.createWithSampleData(); }

    @Test void shouldRecordSamples() {
        var samples = profiler.getSamples("EMP_PKG.GET_SALARY");
        assertFalse(samples.isEmpty());
    }

    @Test void shouldGenerateReport() {
        var report = profiler.generateReport();
        assertTrue(report.contains("EMP_PKG.GET_SALARY"));
        assertTrue(report.contains("EMP_PKG.CALC_BONUS"));
    }

    @Test void shouldGetUnitTotals() {
        var totals = profiler.getUnitTotals();
        assertTrue(totals.containsKey("EMP_PKG.GET_SALARY"));
        assertTrue(totals.get("EMP_PKG.GET_SALARY") > 0);
    }

    @Test void shouldMeasureTimer() {
        profiler.startTimer("test_op");
        var elapsed = profiler.stopTimer("test_op");
        assertTrue(elapsed >= 0);
    }

    @Test void shouldClear() {
        profiler.clear();
        assertEquals(0, profiler.getUnitTotals().size());
    }
}