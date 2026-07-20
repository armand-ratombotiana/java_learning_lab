package com.capstone.mlplatform;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DriftDetectorTest {
    private DriftDetector detector;

    @BeforeEach
    void setUp() {
        detector = new DriftDetector();
        detector.setBaseline("feature1", new DriftDetector.Distribution(0.5, 0.1, 1000, new double[]{0.1, 0.5, 0.9}));
    }

    @Test void testDetectNoDrift() {
        double[] values = new double[100];
        for (int i = 0; i < 100; i++) values[i] = 0.5 + Math.random() * 0.1;
        var report = detector.detectDrift("feature1", values);
        assertNotNull(report);
    }

    @Test void testBatchDetection() {
        double[] vals = new double[100];
        for (int i = 0; i < 100; i++) vals[i] = Math.random();
        var reports = detector.detectBatch(Map.of("feature1", vals));
        assertEquals(1, reports.size());
    }

    @Test void testNoBaseline() {
        var report = detector.detectDrift("unknown", new double[]{1, 2, 3});
        assertEquals(DriftDetector.DriftLevel.NONE, report.level());
    }

    @Test void testAlerts() {
        double[] vals = new double[100];
        for (int i = 0; i < 100; i++) vals[i] = Math.random() * 10;
        detector.detectDrift("feature1", vals);
        var alerts = detector.getRecentAlerts(DriftDetector.DriftLevel.WARNING);
        assertNotNull(alerts);
    }
}
