import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DashboardDataServiceTest {
    @Test
    void testServiceCreation() {
        var svc = new DashboardDataService("http://localhost:8080");
        assertNotNull(svc);
    }

    @Test
    void testMetricSnapshot() {
        var snapshot = new DashboardDataService.MetricSnapshot("2024-01-01", "revenue", 100, 250.0);
        assertEquals("revenue", snapshot.metric());
        assertEquals(100, snapshot.count());
        assertEquals(250.0, snapshot.avg());
    }

    @Test
    void testHealthCheck() {
        var svc = new DashboardDataService("http://localhost:8080");
        boolean healthy = svc.checkLatency(5000);
        // Will be false since no server running, but shouldn't throw
        assertFalse(healthy);
    }
}
