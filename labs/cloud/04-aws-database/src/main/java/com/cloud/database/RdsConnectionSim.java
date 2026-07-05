package com.cloud.database;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class RdsConnectionSim {

    public static class RDSInstance {
        public final String instanceId;
        public final String engine;
        public final String endpoint;
        public final int port;
        public volatile boolean available;

        public RDSInstance(String instanceId, String engine, String endpoint, int port) {
            this.instanceId = instanceId;
            this.engine = engine;
            this.endpoint = endpoint;
            this.port = port;
            this.available = true;
        }
    }

    public static class RDSConnection {
        private final RDSInstance instance;
        private final String connectionId;
        private volatile boolean open;
        private final long createdAt;

        public RDSConnection(RDSInstance instance) {
            this.instance = instance;
            this.connectionId = "conn-" + UUID.randomUUID().toString().substring(0, 8);
            this.open = true;
            this.createdAt = System.currentTimeMillis();
        }

        public String query(String sql) {
            if (!open) throw new IllegalStateException("Connection closed");
            if (!instance.available) throw new RuntimeException("Instance unavailable: " + instance.instanceId);
            System.out.println("  [" + connectionId + "] Query: " + sql);
            return "Result for: " + sql;
        }

        public void close() { open = false; }
        public boolean isOpen() { return open; }
    }

    public static class RDSConnectionPool {
        private final RDSInstance instance;
        private final BlockingQueue<RDSConnection> pool;
        private final int maxSize;
        private final AtomicInteger created = new AtomicInteger(0);

        public RDSConnectionPool(RDSInstance instance, int maxSize) {
            this.instance = instance;
            this.maxSize = maxSize;
            this.pool = new LinkedBlockingQueue<>(maxSize);
        }

        public RDSConnection getConnection(long timeoutMs) throws Exception {
            RDSConnection conn = pool.poll();
            if (conn != null && conn.isOpen()) {
                System.out.println("Reused connection from pool");
                return conn;
            }
            if (created.get() < maxSize) {
                created.incrementAndGet();
                conn = new RDSConnection(instance);
                System.out.println("Created new connection (" + created.get() + "/" + maxSize + ")");
                return conn;
            }
            conn = pool.poll(timeoutMs, TimeUnit.MILLISECONDS);
            if (conn == null) throw new TimeoutException("No available connection");
            return conn;
        }

        public void returnConnection(RDSConnection conn) {
            if (conn.isOpen()) {
                pool.offer(conn);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        RDSInstance rds = new RDSInstance("db-prod-1", "postgres", "db-prod-1.abcdef.us-east-1.rds.amazonaws.com", 5432);
        RDSConnectionPool pool = new RDSConnectionPool(rds, 3);

        System.out.println("=== RDS Connection Pool ===");
        RDSConnection c1 = pool.getConnection(500);
        c1.query("SELECT * FROM users");
        pool.returnConnection(c1);

        RDSConnection c2 = pool.getConnection(500);
        c2.query("INSERT INTO orders VALUES (1, 'item')");
        pool.returnConnection(c2);
    }
}
