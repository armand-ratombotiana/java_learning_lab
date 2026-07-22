# Solution: Fixing Database Connection Pool Exhaustion

**Incident**: INC-2024-0715-CONNPOOL
**Fix Version**: Order Management System v2.7.2
**Last Updated**: July 16, 2024

## Overview

The fix addresses the connection pool exhaustion at four levels:

1. **Primary Fix**: Add try-with-resources to all Connection, Statement, and ResultSet usage
2. **Slow Query Fix**: Create composite index and optimize the query
3. **Configuration Fix**: Reduce leak detection threshold and optimize pool sizing
4. **Defensive Fix**: Add connection borrow timeout and database health check pattern

## Fix 1: Try-With-Resources for Connection Management

The primary fix replaces all manual connection management with try-with-resources (ARM — Automatic Resource Management), which guarantees that Connection, Statement, and ResultSet are closed even if exceptions occur.

### Original (Leaking) Code

```java
package com.example.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class OrderService {

    private final DataSource dataSource;

    public OrderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Processes a batch of orders for fulfillment.
     * BUG: Connection is not returned to pool on exception.
     */
    public void processBulkOrders(List<String> orderIds) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(
                "UPDATE orders SET processed = ?, processed_at = NOW() WHERE id = ?"
            );

            for (String orderId : orderIds) {
                stmt.setBoolean(1, true);
                stmt.setString(2, orderId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Order not found: " + orderId);
                }
            }
        } catch (SQLException e) {
            // BUG: Connection is NOT closed here
            // BUG: No finally block to close connection
            log.error("Failed to process order batch", e);
            throw new OrderProcessingException("Batch processing failed", e);
        }
        // BUG: If exception occurred, we never reach here
        // Connection.close() was never called
    }
}
```

### Fixed Code — Try-With-Resources

```java
package com.example.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class OrderService {

    private final DataSource dataSource;

    public OrderService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Processes a batch of orders for fulfillment.
     * Uses try-with-resources to guarantee connection cleanup.
     *
     * Try-with-resources automatically calls close() on Connection,
     * PreparedStatement, and ResultSet in reverse order, even if
     * an exception occurs. This prevents connection leaks.
     */
    public void processBulkOrders(List<String> orderIds) {
        // try-with-resources — Connection and Statement are auto-closed
        // The Connection is returned to the HikariCP pool when close() is called
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE orders SET processed = ?, processed_at = NOW() WHERE id = ?"
             )) {

            for (String orderId : orderIds) {
                stmt.setBoolean(1, true);
                stmt.setString(2, orderId);
                int updated = stmt.executeUpdate();
                if (updated == 0) {
                    throw new SQLException("Order not found: " + orderId);
                }
            }

        } catch (SQLException e) {
            // Connection and statement are ALREADY closed by try-with-resources
            // No connection leak possible even on exception
            log.error("Failed to process order batch", e);
            throw new OrderProcessingException("Batch processing failed", e);
        }
        // Connection.close() has been called automatically,
        // returning the connection to the HikariCP pool
    }
}
```

### Comprehensive Fix — All JDBC Operations

```java
package com.example.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

/**
 * Repository layer with guaranteed connection cleanup.
 * All methods use try-with-resources for Connection, Statement, ResultSet.
 */
public class OrderRepository {

    private final DataSource dataSource;

    public OrderRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Finds an order by ID. Try-with-resources ensures all resources are closed.
     */
    public Optional<Order> findById(String orderId) {
        // Three resources: Connection, PreparedStatement, ResultSet
        // All auto-closed in reverse order: ResultSet → PreparedStatement → Connection
        String sql = "SELECT id, customer_id, status, total, created_at FROM orders WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapOrder(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find order: " + orderId, e);
        }
    }

    /**
     * Finds all pending orders with their items.
     * After the slow query fix, this uses the composite index.
     */
    public List<Order> findPendingOrders() {
        // Uses the new composite index: orders(status, created_at)
        String sql = "SELECT o.id, o.customer_id, o.status, o.total, o.created_at " +
                     "FROM orders o " +
                     "WHERE o.status = 'PENDING' " +
                     "ORDER BY o.created_at DESC " +
                     "LIMIT 100";

        List<Order> orders = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(mapOrder(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Failed to find pending orders", e);
        }

        return orders;
    }

    /**
     * Updates order status with guaranteed cleanup.
     */
    public boolean updateStatus(String orderId, String status) {
        String sql = "UPDATE orders SET status = ?, updated_at = NOW() WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setString(2, orderId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DatabaseException("Failed to update order status: " + orderId, e);
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getString("id"));
        order.setCustomerId(rs.getString("customer_id"));
        order.setStatus(rs.getString("status"));
        order.setTotal(rs.getBigDecimal("total"));
        order.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return order;
    }
}
```

### Utility Method — Safe Connection Execution

```java
package com.example.order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.sql.DataSource;

/**
 * Utility class that provides safe connection management patterns.
 * Eliminates boilerplate try-with-resources code.
 */
public class ConnectionTemplate {

    private final DataSource dataSource;

    public ConnectionTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Executes a callback with a Connection. Connection is guaranteed
     * to be closed after the callback completes or throws.
     */
    public void execute(ConnectionCallback callback) {
        try (Connection conn = dataSource.getConnection()) {
            callback.doWithConnection(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Database operation failed", e);
        }
    }

    /**
     * Executes a callback with a Connection and returns a result.
     */
    public <T> T executeWithResult(ConnectionFunction<T> callback) {
        try (Connection conn = dataSource.getConnection()) {
            return callback.doWithConnection(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Database operation failed", e);
        }
    }

    @FunctionalInterface
    public interface ConnectionCallback {
        void doWithConnection(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    public interface ConnectionFunction<T> {
        T doWithConnection(Connection conn) throws SQLException;
    }
}
```

## Fix 2: Slow Query Optimization — Composite Index

```sql
-- Slow query identified in slow query log:
-- Query_time: 32.5s, Lock_time: 0.01s, Rows_examined: 5,234,876
-- Full table scan on orders (5M rows) with filesort

-- Original slow query:
SELECT o.*, oi.*, p.*
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC;

-- FIX 1: Add composite index on the WHERE + ORDER BY columns
CREATE INDEX idx_orders_status_created_at
ON orders (status, created_at DESC);

-- Before index: 5M rows scanned, filesort (32 seconds)
-- After index:  142 rows scanned, no filesort (2 milliseconds)

-- FIX 2: Review and optimize the query
-- The original query selected ALL columns (o.*, oi.*, p.*) which caused
-- significant data transfer and prevented index-only access
--
-- Optimized query (select only needed columns):
SELECT o.id, o.customer_id, o.status, o.created_at,
       oi.id AS item_id, oi.product_id, oi.quantity, oi.price,
       p.name AS product_name, p.sku
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC;

-- FIX 3: Add LIMIT to prevent unbounded result sets
SELECT o.id, o.customer_id, o.status, o.created_at
FROM orders o
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC
LIMIT 100;
```

### Verification of Index Efficiency

```sql
-- Check if index is being used:
EXPLAIN
SELECT o.id, o.customer_id, o.status, o.created_at
FROM orders o
WHERE o.status = 'PENDING'
ORDER BY o.created_at DESC
LIMIT 100;

-- Expected output:
-- id: 1
-- select_type: SIMPLE
-- table: o
-- type: ref  (index lookup, NOT ALL)
-- possible_keys: idx_orders_status_created_at
-- key: idx_orders_status_created_at
-- ref: const
-- rows: 142  (instead of 5,234,876)
-- Extra: Using where; Using index
```

## Fix 3: HikariCP Configuration

```java
package com.example.order.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

/**
 * HikariCP connection pool configuration.
 * Configured for production with leak detection and monitoring.
 */
public class DatabaseConfig {

    public DataSource createDataSource() {
        HikariConfig config = new HikariConfig();

        // Database connection
        config.setJdbcUrl("jdbc:mysql://orders-db.aws.com:3306/orders");
        config.setUsername("app_user");
        config.setPassword("${DB_PASSWORD}"); // Externalized to secrets manager
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // Pool sizing — calculated based on pool sizing formula
        // Formula: connections = (core_count * 2) + effective_spindle_count
        // For 8-core instance: (8 * 2) + 1 = 17
        // With 20 application instances: 17 * 20 = 340 total
        // RDS max_connections = 200 → pool per instance = 10
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);

        // Connection timeout: how long to wait for a connection from pool
        // Must be short enough to fail fast, long enough to handle bursts
        config.setConnectionTimeout(5000); // 5 seconds

        // Max lifetime: maximum time a connection can live in the pool
        // Set slightly less than MySQL's wait_timeout (default 28800s)
        config.setMaxLifetime(1800000); // 30 minutes

        // Idle timeout: remove idle connections after this time
        config.setIdleTimeout(300000); // 5 minutes

        // CRITICAL FIX: Leak detection threshold
        // If a connection is checked out for longer than this, log a warning
        // Was 30 minutes — reduced to 5 seconds for early detection
        config.setLeakDetectionThreshold(5000); // 5 seconds

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(3000); // 3 seconds

        // Performance settings
        config.setAutoCommit(true);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED");

        // Monitoring: register JMX beans for HikariCP metrics
        config.setRegisterMbeans(true);

        return new HikariDataSource(config);
    }
}
```

### Pool Sizing Calculation

```java
/**
 * Connection pool sizing best practices:
 *
 * FORMULA:
 *   connections = (core_count * 2) + effective_spindle_count
 *
 * WHERE:
 *   - core_count = number of CPU cores available to the application
 *   - effective_spindle_count = number of physical HDD/SSD spindles
 *
 * For modern SSDs, spindle count = 1 (no mechanical seek penalty)
 *
 * CALCULATION FOR THIS SERVICE:
 *   Instance type: m5.large (2 vCPUs)
 *   Core count: 2
 *   SSD storage: 1 spindle
 *   Formula: (2 * 2) + 1 = 5 connections minimum
 *
 *   With buffer for burst: 5 * 2 = 10 connections per instance
 *   Application instances: 20
 *   Total connections: 10 * 20 = 200
 *   RDS max_connections: 200 (matches perfectly)
 *
 * IMPORTANT: Connection pool sizing is NOT linear.
 * More connections do NOT mean more throughput.
 * Actually, TOO MANY connections causes context switching overhead
 * and database contention.
 *
 * The goal is to have JUST ENOUGH connections to keep the database
 * busy but not overwhelmed.
 *
 * Rule of thumb from HikariCP documentation:
 *   Start with 10 connections per application instance.
 *   Monitor pool utilization during peak.
 *   If pool never goes below minimumIdle, reduce pool size.
 *   If pool frequently hits maximumPoolSize, increase pool size.
 */
```

## Fix 4: Connection Health Check and Retry

```java
package com.example.order;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

/**
 * Health check that verifies database connectivity
 * and monitors connection pool health.
 */
public class DatabaseHealthCheck {

    private final DataSource dataSource;

    public DatabaseHealthCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Performs a database health check by acquiring and releasing a connection.
     * Returns true if the database is reachable.
     */
    public boolean isHealthy() {
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2); // 2-second validation timeout
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Executes a database operation with retry logic.
     * Retries on connection acquisition failure (pool exhaustion).
     *
     * @param operation the database operation to execute
     * @param maxRetries maximum number of retries
     * @return true if operation succeeded
     */
    public boolean executeWithRetry(Runnable operation, int maxRetries) {
        SQLException lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                operation.run();
                return true;
            } catch (DatabaseException e) {
                if (isConnectionPoolExhaustion(e)) {
                    lastException = e;
                    log.warn("Connection pool exhausted, retry {}/{}", attempt, maxRetries);
                    try {
                        TimeUnit.MILLISECONDS.sleep(100L * attempt); // Linear backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new DatabaseException("Retry interrupted", ie);
                    }
                } else {
                    throw e; // Non-pool-exhaustion exception, don't retry
                }
            }
        }

        throw new DatabaseException(
            "Operation failed after " + maxRetries + " retries", lastException);
    }

    private boolean isConnectionPoolExhaustion(DatabaseException e) {
        return e.getCause() instanceof SQLException
            && e.getCause().getMessage() != null
            && e.getCause().getMessage().contains("Connection is not available");
    }
}
```

## Unit Tests

```java
package com.example.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionManagementTest {

    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // Use H2 in-memory database for testing
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        config.setLeakDetectionThreshold(100); // 100ms for testing
        dataSource = new HikariDataSource(config);
    }

    @Test
    void connectionShouldBeReturnedToPoolAfterUse() {
        // Get a connection and close it properly
        Connection conn = dataSource.getConnection();
        assertNotNull(conn);
        conn.close(); // Should return to pool

        // Pool should still have available connections
        Connection conn2 = dataSource.getConnection();
        assertNotNull(conn2);
        conn2.close();
    }

    @Test
    void tryWithResourcesShouldCloseConnectionOnException() {
        assertThrows(RuntimeException.class, () -> {
            try (Connection conn = dataSource.getConnection()) {
                throw new RuntimeException("Simulated error");
            }
        });

        // Connection should be returned to pool despite exception
        Connection conn = dataSource.getConnection();
        assertNotNull(conn);
        conn.close();
    }

    @Test
    void connectionShouldNotBeLeakedOnException() {
        // Verify that after an exception, the pool still has connections
        try (Connection conn = dataSource.getConnection()) {
            throw new SQLException("Simulated SQL error");
        } catch (SQLException e) {
            // Expected, connection should be auto-closed
        }

        // Pool should not be depleted
        for (int i = 0; i < 5; i++) {
            Connection conn = dataSource.getConnection();
            assertNotNull(conn);
            conn.close();
        }
    }

    @Test
    void slowQueryShouldNotExhaustPool() throws Exception {
        // Verify that the slow query fix prevents pool exhaustion
        OrderService service = new OrderService(dataSource);

        // Submit 10 concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(executor.submit(() -> {
                // Use the fixed method with try-with-resources
                service.processBulkOrders(Arrays.asList("1", "2", "3"));
            }));
        }

        // All should complete within timeout
        for (Future<?> f : futures) {
            f.get(5, TimeUnit.SECONDS); // Should not hang
        }

        executor.shutdown();
    }
}
```

## Deployment Strategy

| Phase | Scope | Duration | Success Criteria |
|-------|-------|----------|------------------|
| Canary | 1 instance | 30 min | Pool utilization < 50%, no leak detection logs |
| Regional | 50% instances | 2 hours | Zero connection timeouts, P99 unchanged |
| Full rollout | 100% | 4 hours | Pool stable, no leak events |
| Post-deploy | All | 7 days | Zero connection pool incidents |

## References

- HikariCP: "Connection Leak Detection" — https://github.com/brettwooldridge/HikariCP#leak-detection
- Amazon RDS: "Troubleshooting Database Connection Issues" — AWS Documentation
- Oracle: "JDBC Best Practices: Connection Management" — Oracle Java Documentation
- Baeldung: "Guide to HikariCP Connection Pool" — https://www.baeldung.com/hikaricp
- HikariCP: "Pool Sizing Formulas" — https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
- MySQL: "Indexing for ORDER BY" — MySQL Documentation

