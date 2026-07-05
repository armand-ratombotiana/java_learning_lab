package com.db.connectionpooling;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates HikariCP pool monitoring and metrics.
 *
 * Shows how to inspect active/idle/waiting connections and
 * simulate pool exhaustion.
 */
public class PoolMonitor {

    static DataSource createPool(int maxSize) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:h2:mem:monitordb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(maxSize);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(2000);   // 2 seconds wait
        config.setPoolName("MonitorPool");
        config.setRegisterMbeans(true);
        return new HikariDataSource(config);
    }

    static void printPoolMetrics(HikariDataSource hds) {
        var mx = hds.getHikariPoolMXBean();
        System.out.printf("  Active: %d | Idle: %d | Total: %d | Awaiting: %d%n",
            mx.getActiveConnections(), mx.getIdleConnections(),
            mx.getTotalConnections(), mx.getThreadsAwaitingConnection());
    }

    public static void main(String[] args) throws Exception {
        HikariDataSource ds = (HikariDataSource) createPool(3);
        System.out.println("=== HikariCP Pool Monitoring ===\n");

        System.out.println("Initial state:");
        printPoolMetrics(ds);

        System.out.println("\nBorrowing 3 connections...");
        Connection c1 = ds.getConnection();
        Connection c2 = ds.getConnection();
        Connection c3 = ds.getConnection();
        printPoolMetrics(ds);

        System.out.println("\nTrying to borrow a 4th (should wait or timeout)...");
        long start = System.nanoTime();
        AtomicInteger timeoutCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);

        Thread t = new Thread(() -> {
            try {
                ds.getConnection();  // will timeout after 2s
            } catch (SQLException e) {
                timeoutCount.incrementAndGet();
                System.out.println("  Connection timeout: " + e.getMessage());
            } finally {
                latch.countDown();
            }
        });
        t.start();
        latch.await(5, TimeUnit.SECONDS);

        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        System.out.printf("  Timeout after ~%dms%n", elapsed);

        System.out.println("\nReturning connections...");
        c3.close();
        printPoolMetrics(ds);
        c2.close();
        c1.close();

        TimeUnit.MILLISECONDS.sleep(100);
        System.out.println("\nAll returned:");
        printPoolMetrics(ds);

        System.out.println("\nJMX Monitoring:");
        System.out.println("  HikariCP exposes MBeans via registerMbeans=true");
        System.out.println("  View with: jconsole or any JMX client");
        System.out.println("  MBean: com.zaxxer.hikari:type=Pool (MonitorPool)");

        ds.close();
    }
}
