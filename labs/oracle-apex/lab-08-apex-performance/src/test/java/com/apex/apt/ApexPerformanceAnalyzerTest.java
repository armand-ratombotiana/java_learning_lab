package com.apex.apt;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class ApexPerformanceAnalyzerTest {
    private ApexPerformanceAnalyzer pa;
    @BeforeEach void setUp() { pa = ApexPerformanceAnalyzer.createSample(); }

    @Test void shouldRecordPageMetric() {
        assertNotNull(pa.getPageMetric(1));
        assertEquals("Dashboard", pa.getPageMetric(1).pageName());
    }

    @Test void shouldFindSlowestPage() {
        var slow = pa.getSlowestPage();
        assertEquals(3, slow.pageId());
    }

    @Test void shouldFindFastestPage() {
        var fast = pa.getFastestPage();
        assertEquals(1, fast.pageId());
    }

    @Test void shouldFilterSlowQueries() {
        var slow = pa.getSlowQueries(500);
        assertFalse(slow.isEmpty());
    }

    @Test void shouldCalculateCacheHitRatio() {
        double ratio = pa.getCacheHitRatio("Region Cache");
        assertTrue(ratio > 90);
    }

    @Test void shouldGenerateReport() {
        var report = pa.generatePerformanceReport();
        assertTrue(report.contains("APEX Performance Report"));
        assertTrue(report.contains("Dashboard"));
    }
}