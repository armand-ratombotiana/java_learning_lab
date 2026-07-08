import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IcebergMaintenanceTest {
    private final IcebergTableManager mgr = new IcebergTableManager("iceberg", "analytics");
    private final IcebergMaintenance maint = new IcebergMaintenance(mgr);

    @Test
    void testNeedsCompaction() {
        assertTrue(maint.needsCompaction(100, 1024L * 1024 * 1024));
        assertFalse(maint.needsCompaction(10, 1024L * 1024 * 1024 * 10));
    }

    @Test
    void testOptimalFiles() {
        assertEquals(4, maint.estimateOptimalFiles(1024L * 1024 * 1024, 256 * 1024 * 1024));
    }

    @Test
    void testBuildPlan() {
        String plan = maint.buildMaintenancePlan("events", 1024L * 1024 * 1024, 500);
        assertTrue(plan.contains("compaction"));
        assertTrue(plan.contains("expire_snapshots"));
    }
}
