import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DeltaLakeManagerTest {
    private final DeltaLakeManager dm = new DeltaLakeManager("/data/lake");

    @Test
    void testOptimizeSql() {
        String sql = dm.getOptimizeSql("/data/lake/events");
        assertTrue(sql.contains("OPTIMIZE"));
        assertTrue(sql.contains("events"));
    }

    @Test
    void testVacuumSql() {
        String sql = dm.getVacuumSql("/data/lake/events", 168);
        assertTrue(sql.contains("VACUUM"));
        assertTrue(sql.contains("168"));
    }

    @Test
    void testMergeSql() {
        String sql = dm.getMergeSql("/data/lake/events", "updates", "id");
        assertTrue(sql.contains("MERGE"));
        assertTrue(sql.contains("MATCHED"));
        assertTrue(sql.contains("id"));
    }

    @Test
    void testTimeTravelSql() {
        String sql = dm.getTimeTravelSql("/data/lake/events", 25);
        assertTrue(sql.contains("VERSION AS OF 25"));
    }

    @Test
    void testZOrderSql() {
        String sql = dm.getZOrderSql("/data/lake/events", "customer_id", "event_date");
        assertTrue(sql.contains("ZORDER BY"));
        assertTrue(sql.contains("customer_id"));
        assertTrue(sql.contains("event_date"));
    }

    @Test
    void testChangeDataFeedSql() {
        String sql = dm.getChangeDataFeedSql("/data/lake/events", 0);
        assertTrue(sql.contains("table_changes"));
    }
}
