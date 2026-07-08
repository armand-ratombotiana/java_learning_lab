import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.*;

class AnomalyDetectorTest {
    private final AnomalyDetector detector = new AnomalyDetector();

    @Test
    void testNormalVolume() {
        var history = List.of(100.0, 102.0, 98.0, 101.0, 99.0);
        var result = detector.detectVolumeAnomaly(100, history);
        assertFalse(result.isAnomaly());
    }

    @Test
    void testSpikeVolume() {
        var history = List.of(100.0, 102.0, 98.0, 101.0, 99.0);
        var result = detector.detectVolumeAnomaly(500, history);
        assertTrue(result.isAnomaly());
        assertEquals("CRITICAL", result.severity());
    }

    @Test
    void testFreshnessNormal() {
        var history = List.of(1000L, 1200L, 1100L);
        var result = detector.detectFreshnessAnomaly(1500, history, 5000);
        assertFalse(result.isAnomaly());
    }

    @Test
    void testFreshnessBreach() {
        var history = List.of(1000L, 1200L, 1100L);
        var result = detector.detectFreshnessAnomaly(30000, history, 5000);
        assertTrue(result.isAnomaly());
    }

    @Test
    void testDistributionDrift() {
        var current = new double[]{0.5, 0.3, 0.2};
        var expected = new double[]{0.5, 0.3, 0.2};
        var result = detector.detectDistributionDrift(current, expected);
        assertFalse((boolean) result.get("drift_detected"));
    }

    @Test
    void testDistributionDriftDetected() {
        var current = new double[]{0.9, 0.05, 0.05};
        var expected = new double[]{0.3, 0.3, 0.4};
        var result = detector.detectDistributionDrift(current, expected);
        assertTrue((boolean) result.get("drift_detected"));
    }
}
