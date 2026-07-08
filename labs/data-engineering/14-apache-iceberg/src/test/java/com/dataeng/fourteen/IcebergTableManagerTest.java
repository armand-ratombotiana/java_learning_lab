import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class IcebergTableManagerTest {
    private final IcebergTableManager mgr = new IcebergTableManager("iceberg", "analytics");

    @Test
    void testCreateTableSql() {
        String sql = mgr.getCreateTableSql("events", "id BIGINT, ts TIMESTAMP", "days(ts)");
        assertTrue(sql.contains("CREATE TABLE"));
        assertTrue(sql.contains("iceberg.analytics.events"));
        assertTrue(sql.contains("PARTITIONED BY"));
    }

    @Test
    void testPartitionEvolutionSql() {
        String sql = mgr.getPartitionEvolutionSql("events", "months(ts)");
        assertTrue(sql.contains("SET PARTITION SPEC"));
    }

    @Test
    void testCompactionSql() {
        String sql = mgr.getCompactionSql("events");
        assertTrue(sql.contains("rewrite_data_files"));
    }

    @Test
    void testExpireSnapshotsSql() {
        String sql = mgr.getExpireSnapshotsSql("events", "2024-01-01");
        assertTrue(sql.contains("expire_snapshots"));
    }
}
