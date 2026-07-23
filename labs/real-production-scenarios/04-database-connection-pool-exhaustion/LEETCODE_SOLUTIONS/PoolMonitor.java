package com.prod.solutions.connectionpool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simulates HikariCP connection pool metrics monitoring.
 * In production, HikariCP exposes these metrics via JMX,
 * which can be scraped by Prometheus and alerted on.
 */
public class PoolMonitor {

    private final String poolName;
    private final int maxPoolSize;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private final AtomicInteger idleConnections = new AtomicInteger(0);
    private final AtomicInteger pendingThreads = new AtomicInteger(0);
    private final AtomicLong totalConnectionsCreated = new AtomicLong(0);
    private final AtomicLong connectionTimeoutCount = new AtomicLong(0);

    public PoolMonitor(String poolName, int maxPoolSize) {
        this.poolName = poolName;
        this.maxPoolSize = maxPoolSize;
    }

    public static void main(String[] args) throws InterruptedException {
        PoolMonitor monitor = new PoolMonitor("app-db-pool", 10);

        System.out.println("=== HikariCP Connection Pool Monitor ===");
        System.out.println("Simulating pool metrics...\n");

        // Simulate pool activity
        monitor.simulateNormalLoad();
        monitor.printMetrics();

        monitor.simulateLeak();
        monitor.printMetrics();

        monitor.simulateRecovery();
        monitor.printMetrics();

        System.out.println("\nKey metrics to alert on:");
        System.out.println("  - pool.%s.activeConnections".formatted("app-db-pool"));
        System.out.println("  - pool.%s.pendingThreads".formatted("app-db-pool"));
        System.out.println("  - pool.%s.connectionTimeoutCount".formatted("app-db-pool"));
        System.out.println("  - pool.%s.connectionCreationRate".formatted("app-db-pool"));
    }

    void simulateNormalLoad() {
        for (int i = 0; i < 5; i++) {
            acquireConnection();
        }
        for (int i = 0; i < 5; i++) {
            releaseConnection();
        }
    }

    void simulateLeak() {
        System.out.println(">>> SIMULATING CONNECTION LEAK <<<");
        for (int i = 0; i < 8; i++) {
            acquireConnection();
            // BUG: Connections not released!
        }
        pendingThreads.set(15);
        connectionTimeoutCount.set(3);
    }

    void simulateRecovery() {
        System.out.println(">>> RECOVERY: Returning all connections <<<");
        while (activeConnections.get() > 0) {
            releaseConnection();
        }
        pendingThreads.set(0);
    }

    synchronized void acquireConnection() {
        if (activeConnections.get() + idleConnections.get() < maxPoolSize) {
            activeConnections.incrementAndGet();
            totalConnectionsCreated.incrementAndGet();
        } else if (idleConnections.get() > 0) {
            idleConnections.decrementAndGet();
            activeConnections.incrementAndGet();
        } else {
            pendingThreads.incrementAndGet();
        }
    }

    synchronized void releaseConnection() {
        if (activeConnections.get() > 0) {
            activeConnections.decrementAndGet();
            idleConnections.incrementAndGet();
        }
    }

    void printMetrics() {
        System.out.printf("""
                %n--- %s Metrics ---
                Active:        %d
                Idle:          %d
                Pending:       %d
                Total Created: %d
                Timeouts:      %d
                Utilization:   %.1f%%
                """,
                poolName,
                activeConnections.get(),
                idleConnections.get(),
                pendingThreads.get(),
                totalConnectionsCreated.get(),
                connectionTimeoutCount.get(),
                (activeConnections.get() * 100.0 / maxPoolSize));
    }
}
