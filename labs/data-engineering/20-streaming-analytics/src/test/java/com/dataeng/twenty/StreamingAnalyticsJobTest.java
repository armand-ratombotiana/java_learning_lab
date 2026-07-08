import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StreamingAnalyticsJobTest {
    @Test
    void testJobCreation() {
        var job = new StreamingAnalyticsJob("revenue", "orders", "revenue_metrics", "");
        assertEquals("revenue", job.getJobName());
    }

    @Test
    void testTumblingWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getTumblingWindowQuery("1 MINUTE");
        assertTrue(query.contains("TUMBLE_START"));
        assertTrue(query.contains("1 MINUTE"));
        assertTrue(query.contains("GROUP BY"));
    }

    @Test
    void testSlidingWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getSlidingWindowQuery("10 MINUTE", "5 MINUTE");
        assertTrue(query.contains("HOP_START"));
        assertTrue(query.contains("10 MINUTE"));
    }

    @Test
    void testSessionWindowQuery() {
        var job = new StreamingAnalyticsJob("test", "events", "sink", "");
        String query = job.getSessionWindowQuery("30 MINUTE");
        assertTrue(query.contains("SESSION_START"));
    }

    @Test
    void testFlinkSqlStatement() {
        var job = new StreamingAnalyticsJob("test", "input-topic", "output_table",
            "SELECT COUNT(*) FROM source");
        String sql = job.getFlinkSqlStatement();
        assertTrue(sql.contains("input-topic"));
        assertTrue(sql.contains("output_table"));
        assertTrue(sql.contains("WATERMARK"));
    }
}
