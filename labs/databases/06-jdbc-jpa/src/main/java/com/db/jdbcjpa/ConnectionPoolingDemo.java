package com.db.jdbcjpa;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Demonstrates HikariCP connection pooling configuration and usage.
 *
 * Key HikariCP settings:
 *   - maximumPoolSize:     max connections in pool
 *   - minimumIdle:         min idle connections
 *   - connectionTimeout:   max wait time for a connection
 *   - idleTimeout:         max time a connection stays idle
 *   - maxLifetime:         max lifetime of a connection in pool
 *   - poolName:            identifier for monitoring
 */
public class ConnectionPoolingDemo {

    static DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:pooling;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");

        // Pool configuration
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(3000);      // 3 seconds
        config.setIdleTimeout(300_000);          // 5 minutes
        config.setMaxLifetime(600_000);          // 10 minutes
        config.setPoolName("AcademyPool");

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    public static void main(String[] args) throws Exception {
        DataSource ds = createDataSource();

        // Setup table
        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS test (id INT PRIMARY KEY, val VARCHAR(50))");
            st.execute("INSERT INTO test VALUES (1, 'Hello from pool!')");
        }

        System.out.println("=== Connection Pooling with HikariCP ===\n");

        // Borrow and return connections
        for (int i = 0; i < 5; i++) {
            try (Connection conn = ds.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT val FROM test WHERE id = 1")) {

                rs.next();
                System.out.printf("  Connection %d: %s%n", i + 1, rs.getString("val"));
            }
        }

        // Access pool metrics (if we cast to HikariDataSource)
        if (ds instanceof HikariDataSource hds) {
            System.out.println("\n=== Pool Metrics ===");
            System.out.println("  Active connections:   " + hds.getHikariPoolMXBean().getActiveConnections());
            System.out.println("  Idle connections:     " + hds.getHikariPoolMXBean().getIdleConnections());
            System.out.println("  Total connections:    " + hds.getHikariPoolMXBean().getTotalConnections());
            System.out.println("  Threads awaiting:     " + hds.getHikariPoolMXBean().getThreadsAwaitingConnection());
            System.out.println("  Pool name:            " + hds.getPoolName());
        }

        System.out.println("\nConfiguration highlights:");
        System.out.println("  - maxPoolSize=10: at most 10 connections");
        System.out.println("  - minIdle=2: keep 2 connections warm");
        System.out.println("  - connectionTimeout=3s: fail fast if pool is exhausted");
        System.out.println("  - Statement caching: enabled (250 prepared statements)");
    }
}
