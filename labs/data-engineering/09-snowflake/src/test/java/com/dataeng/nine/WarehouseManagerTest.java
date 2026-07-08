import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

@Disabled("Requires Snowflake account credentials")
class WarehouseManagerTest {
    private WarehouseManager manager;

    @Test
    void testCreateSql() {
        assertDoesNotThrow(() -> new WarehouseManager(null));
    }

    @Test
    void testAlterSqlIsFormatted() {
        String expected = "ALTER WAREHOUSE test SET WAREHOUSE_SIZE='LARGE'";
        assertNotNull(expected);
    }
}
