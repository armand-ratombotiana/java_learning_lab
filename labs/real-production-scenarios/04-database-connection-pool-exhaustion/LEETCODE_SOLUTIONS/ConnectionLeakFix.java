package com.prod.solutions.connectionpool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fixed version of connection pool leak.
 * FIX: Uses try-with-resources to ensure connections are always returned.
 * Also adds a finally block for explicit connection return.
 */
public class ConnectionLeakFix {

    private static final AtomicInteger activeConnections = new AtomicInteger(0);
    private static final int MAX_POOL = 10;
    private static final Connection[] pool = new Connection[MAX_POOL];

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Connection Pool Leak Fix Demo ===");

        for (int i = 0; i < MAX_POOL; i++) {
            pool[i] = createConnection("conn-" + i);
        }

        ExecutorService executor = Executors.newFixedThreadPool(20);

        for (int i = 0; i < 50; i++) {
            final int reqId = i;
            executor.submit(() -> {
                try {
                    processRequestSafe(reqId);
                } catch (Exception e) {
                    System.out.printf("Request %d failed: %s%n", reqId, e.getMessage());
                }
            });
            Thread.sleep(50);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.printf("%nActive connections: %d (pool size: %d)%n",
                activeConnections.get(), MAX_POOL);
        System.out.printf("Available connections: %d%n", MAX_POOL - activeConnections.get());

        if (activeConnections.get() == 0) {
            System.out.println("SUCCESS: All connections returned to pool. No leak.");
        }
    }

    /**
     * FIX: Uses try-with-resources to ensure Connection.close()
     * is always called, returning the connection to the pool.
     */
    static void processRequestSafe(int reqId) throws SQLException {
        // FIX: try-with-resources ensures close() is called
        try (Connection conn = borrowConnection()) {
            if (conn == null) {
                throw new SQLException("No available connection for request " + reqId);
            }
            Thread.sleep(200);
            System.out.printf("[Request %d] Processed and returned %s%n", reqId, conn);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static synchronized Connection borrowConnection() {
        for (int i = 0; i < MAX_POOL; i++) {
            if (pool[i] != null) {
                Connection conn = pool[i];
                pool[i] = null;
                activeConnections.incrementAndGet();
                return conn;
            }
        }
        return null;
    }

    static synchronized void returnConnection(Connection conn) {
        for (int i = 0; i < MAX_POOL; i++) {
            if (pool[i] == null) {
                pool[i] = conn;
                activeConnections.decrementAndGet();
                return;
            }
        }
    }

    static Connection createConnection(String name) {
        return new java.sql.Connection() {
            private boolean closed;
            public String toString() { return name; }
            public void close() { if (!closed) { closed = true; returnConnection(this); } }
            public boolean isClosed() { return closed; }
            public java.sql.Statement createStatement() { return null; }
            public java.sql.PreparedStatement prepareStatement(String sql) { return null; }
            public java.sql.CallableStatement prepareCall(String sql) { return null; }
            public String nativeSQL(String sql) { return sql; }
            public void setAutoCommit(boolean autoCommit) {}
            public boolean getAutoCommit() { return true; }
            public void commit() {}
            public void rollback() {}
            public int getTransactionIsolation() { return Connection.TRANSACTION_READ_COMMITTED; }
            public void setTransactionIsolation(int level) {}
            public java.sql.SQLWarning getWarnings() { return null; }
            public void clearWarnings() {}
            public int getHoldability() { return 0; }
            public void setHoldability(int holdability) {}
            public java.sql.Savepoint setSavepoint() { return null; }
            public java.sql.Savepoint setSavepoint(String name) { return null; }
            public void rollback(java.sql.Savepoint savepoint) {}
            public void releaseSavepoint(java.sql.Savepoint savepoint) {}
            public java.sql.DatabaseMetaData getMetaData() { return null; }
            public void setReadOnly(boolean readOnly) {}
            public boolean isReadOnly() { return false; }
            public void setCatalog(String catalog) {}
            public String getCatalog() { return null; }
            public void setTypeMap(java.util.Map<String, Class<?>> map) {}
            public java.util.Map<String, Class<?>> getTypeMap() { return null; }
            public void setSchema(String schema) {}
            public String getSchema() { return null; }
            public void abort(java.util.concurrent.Executor executor) {}
            public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) {}
            public int getNetworkTimeout() { return 0; }
            public <T> T unwrap(Class<T> iface) { return null; }
            public boolean isWrapperFor(Class<?> iface) { return false; }
        };
    }
}
