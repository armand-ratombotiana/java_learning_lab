import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LineageBuilderTest {
    @Test
    void testBuildLineage() {
        var lb = new LineageBuilder()
            .addSource("orders_db.public.orders")
            .addTransformation("dbt_etl_job")
            .addTarget("analytics.fact_orders");
        assertTrue(lb.isValid());
        String json = lb.build();
        assertTrue(json.contains("orders_db.public.orders"));
        assertTrue(json.contains("analytics.fact_orders"));
    }

    @Test
    void testInvalidLineage() {
        var lb = new LineageBuilder();
        assertFalse(lb.isValid());
    }

    @Test
    void testMultipleSources() {
        var lb = new LineageBuilder()
            .addSource("db1.orders")
            .addSource("db2.customers")
            .addTransformation("etl_join")
            .addTarget("analytics.orders_with_customers");
        String json = lb.build();
        assertTrue(json.contains("db1.orders"));
        assertTrue(json.contains("db2.customers"));
    }
}
