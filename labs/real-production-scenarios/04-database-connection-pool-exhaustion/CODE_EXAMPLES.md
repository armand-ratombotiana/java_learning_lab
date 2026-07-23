# Lab 04 — Connection Pool Exhaustion: Code Examples

## Reproducing the Bug

```java
import java.sql.*;
import com.zaxxer.hikari.*;

public class ConnectionLeakDemo {

    private static final HikariDataSource dataSource = createDataSource();

    static HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(3000);
        config.setLeakDetectionThreshold(10000);
        config.setPoolName("LeakDemoPool");
        config.setRegisterMbeans(true);
        return new HikariDataSource(config);
    }

    // BUG: Connection not returned in case of exception
    public static void processOrder(int orderId) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM orders WHERE id = ?");
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            // Business logic that may throw exception
            if (orderId % 3 == 0) {
                throw new RuntimeException("Processing error for order " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            // BUG: Missing finally block — conn.close() never called on exception!
        }
        // BUG: Connections leak when exception thrown above
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting connection leak demo...");
        System.out.println("Pool size: 5, Timeout: 3s");

        // Leak connections quickly
        for (int i = 0; i < 20; i++) {
            int orderId = i;
            new Thread(() -> processOrder(orderId)).start();
            Thread.sleep(100);
        }

        Thread.sleep(2000);
        System.out.println("\nPool status:");
        System.out.println("Active: " + dataSource.getHikariPoolMXBean().getActiveConnections());
        System.out.println("Idle: " + dataSource.getHikariPoolMXBean().getIdleConnections());
        System.out.println("Pending: " + dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());

        if (dataSource.getHikariPoolMXBean().getActiveConnections() >= 5) {
            System.out.println("\nPOOL EXHAUSTED! Connections leaked: " +
                    dataSource.getHikariPoolMXBean().getActiveConnections());
        }

        dataSource.close();
    }
}
```

## Fixing the Bug

### Fixed: try-with-resources

```java
private static final HikariDataSource dataSource = createDataSource();

public static void processOrderFixed(int orderId) {
    // FIX: try-with-resources ensures connection is always returned!
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM orders WHERE id = ?")) {

        stmt.setInt(1, orderId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Order " + rs.getInt("id") + ": " + rs.getString("status"));
            }
        }
        // stmt and conn are auto-closed here (reverse order)
    } catch (SQLException e) {
        System.err.println("DB error: " + e.getMessage());
        // Connection still returned due to try-with-resources!
    }
}
```

### HikariCP Production Configuration

```properties
# Production HikariCP configuration
spring.datasource.hikari.pool-name=OrderServicePool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=5000
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=600000
spring.datasource.hikari.leak-detection-threshold=5000
spring.datasource.hikari.validation-timeout=1000
spring.datasource.hikari.register-mbeans=true
spring.datasource.hikari.auto-commit=false
spring.datasource.hikari.connection-test-query=SELECT 1
```

### Pool Health Check Service

```java
import com.zaxxer.hikari.*;

public class PoolHealthChecker {
    private final HikariDataSource dataSource;
    private final int warningThreshold; // % of max

    public PoolHealthChecker(HikariDataSource dataSource, int warningThreshold) {
        this.dataSource = dataSource;
        this.warningThreshold = warningThreshold;
    }

    public PoolHealthStatus check() {
        HikariPoolMXBean pool = dataSource.getHikariPoolMXBean();
        int active = pool.getActiveConnections();
        int idle = pool.getIdleConnections();
        int total = active + idle;
        int pending = pool.getThreadsAwaitingConnection();
        int timeouts = pool.getTimeoutCount();
        double utilization = (double) active / total * 100;

        return new PoolHealthStatus(active, idle, total, pending, timeouts, utilization);
    }

    public boolean isHealthy() {
        PoolHealthStatus status = check();
        return status.pending == 0 && status.timeouts == 0 && status.utilization < warningThreshold;
    }

    record PoolHealthStatus(int active, int idle, int total, int pending,
                            int timeouts, double utilization) {}
}
```

### Unit Tests

```java
import org.junit.jupiter.api.*;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolTest {

    private static HikariDataSource dataSource;

    @BeforeAll
    static void setup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testpool;DB_CLOSE_DELAY=-1");
        config.setMaximumPoolSize(5);
        config.setConnectionTimeout(3000);
        config.setLeakDetectionThreshold(5000);
        dataSource = new HikariDataSource(config);
    }

    @Test
    void testConnectionReturnsAfterNormalUse() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
        }
        assertEquals(0, dataSource.getHikariPoolMXBean().getActiveConnections(),
                "Connection should be returned to pool");
    }

    @Test
    void testConnectionReturnsAfterException() {
        assertThrows(SQLException.class, () -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("INVALID SQL");
            }
        });
        assertEquals(0, dataSource.getHikariPoolMXBean().getActiveConnections(),
                "Connection should be returned after exception");
    }

    @AfterAll
    static void cleanup() {
        dataSource.close();
    }
}
```
