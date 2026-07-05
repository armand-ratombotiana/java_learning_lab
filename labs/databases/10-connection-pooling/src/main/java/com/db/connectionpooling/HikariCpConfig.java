package com.db.connectionpooling;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Demonstrates detailed HikariCP configuration and tuning.
 *
 * HikariCP is the default connection pool for Spring Boot 2.x+.
 * It is lightweight, fast, and provides JMX monitoring.
 */
public class HikariCpConfig {

    /**
     * Creates a fully-configured HikariCP DataSource.
     *
     * @param jdbcUrl    database URL
     * @param user       database user
     * @param password   database password
     * @param poolSize   max pool size
     * @param poolName   identifier for monitoring
     */
    public static DataSource createDataSource(String jdbcUrl, String user, String password,
                                               int poolSize, String poolName) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);

        // Core pool settings
        config.setMaximumPoolSize(poolSize);
        config.setMinimumIdle(poolSize / 2);
        config.setConnectionTimeout(5000);       // 5 second wait for a connection
        config.setIdleTimeout(300_000);           // 5 minutes idle before eviction
        config.setMaxLifetime(600_000);           // 10 minutes max lifetime
        config.setKeepaliveTime(60_000);          // 1 minute keepalive ping

        // Connection validation
        config.setConnectionTestQuery("SELECT 1");
        config.setConnectionInitSql("SET NAMES 'utf8'");
        config.setValidationTimeout(3000);

        // Performance
        config.setLeakDetectionThreshold(60_000); // log if connection held > 1 min
        config.setRegisterMbeans(true);            // enable JMX monitoring

        // Statement caching (database-specific)
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "500");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "4096");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        // Pool naming
        config.setPoolName(poolName);

        return new HikariDataSource(config);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== HikariCP Configuration ===");

        DataSource ds = createDataSource(
            "jdbc:h2:mem:hikaridemo;DB_CLOSE_DELAY=-1",
            "sa", "", 5, "DemoPool"
        );

        try (Connection conn = ds.getConnection();
             Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS ping (id INT PRIMARY KEY, ts TIMESTAMP)");
            st.execute("INSERT INTO ping VALUES (1, CURRENT_TIMESTAMP())");
            System.out.println("  Connection established and verified");
        }

        if (ds instanceof HikariDataSource hds) {
            System.out.println("\nPool configuration applied:");
            System.out.println("  Pool name:              " + hds.getPoolName());
            System.out.println("  Maximum pool size:       " + hds.getMaximumPoolSize());
            System.out.println("  Minimum idle:            " + hds.getMinimumIdle());
            System.out.println("  Connection timeout:      " + hds.getConnectionTimeout() + "ms");
            System.out.println("  Idle timeout:            " + hds.getIdleTimeout() + "ms");
            System.out.println("  Max lifetime:            " + hds.getMaxLifetime() + "ms");
            System.out.println("  Leak detection:          " + hds.getLeakDetectionThreshold() + "ms");
            System.out.println("  JMX monitoring:          " + hds.isRegisterMbeans());
        }

        hds.close();
    }
}
