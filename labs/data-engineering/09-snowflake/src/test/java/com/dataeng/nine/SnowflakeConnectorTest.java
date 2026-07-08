import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

@Disabled("Requires Snowflake account credentials")
class SnowflakeConnectorTest {
    private SnowflakeConnector connector;

    @BeforeEach
    void setUp() { connector = new SnowflakeConnector(); }

    @Test
    void testConnectionString() {
        assertNotNull(connector);
    }

    @Test
    void testCreateWarehouseSql() {
        assertDoesNotThrow(() -> connector.createWarehouse(null, "test_wh", "X-SMALL", 300));
    }

    @Test
    void testCloneSql() {
        assertDoesNotThrow(() -> connector.cloneTable(null, "source", "target"));
    }
}
