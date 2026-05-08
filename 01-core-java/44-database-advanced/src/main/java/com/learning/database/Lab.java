package com.learning.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class Lab {

    private static final String URL = "jdbc:h2:mem:advdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    record User(int id, String name, String email, double balance) {}

    public static void main(String[] args) throws Exception {
        System.out.println("=== Database Advanced Lab ===\n");

        init();
        indexing();
        joinsAndAggregation();
        transactionIsolation();
        pessimisticLocking();
        optimisticLocking();
        partitionDemo();
        connectionPooling();
        cleanUp();
    }

    static void init() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    balance DECIMAL(12,2) NOT NULL DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    total DECIMAL(12,2) NOT NULL,
                    status VARCHAR(20) DEFAULT 'PENDING',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status)");

            try (var ps = conn.prepareStatement(
                    "MERGE INTO users(id, name, email, balance) KEY(id) VALUES(?, ?, ?, ?)")) {
                Object[][] data = {
                    {1, "Alice", "alice@test.com", 5000.00},
                    {2, "Bob", "bob@test.com", 3000.00},
                    {3, "Carol", "carol@test.com", 8000.00},
                    {4, "Dave", "dave@test.com", 2000.00}
                };
                for (var row : data) {
                    ps.setInt(1, (int) row[0]);
                    ps.setString(2, (String) row[1]);
                    ps.setString(3, (String) row[2]);
                    ps.setDouble(4, (double) row[3]);
                    ps.executeUpdate();
                }
            }

            try (var ps = conn.prepareStatement(
                    "MERGE INTO orders(id, user_id, total, status) KEY(id) VALUES(?, ?, ?, ?)")) {
                Object[][] data = {
                    {1, 1, 250.00, "SHIPPED"},
                    {2, 1, 150.00, "PENDING"},
                    {3, 2, 500.00, "SHIPPED"},
                    {4, 3, 1200.00, "PENDING"},
                    {5, 1, 75.00, "CANCELLED"}
                };
                for (var row : data) {
                    ps.setInt(1, (int) row[0]);
                    ps.setInt(2, (int) row[1]);
                    ps.setDouble(3, (double) row[2]);
                    ps.setString(4, (String) row[3]);
                    ps.executeUpdate();
                }
            }
            System.out.println("Schema & seed data initialized.");
        }
    }

    static void indexing() throws SQLException {
        System.out.println("\n--- Indexing & Query Planning ---");
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("EXPLAIN SELECT * FROM orders WHERE user_id = 1");
            if (rs.next()) System.out.println("  Plan: " + rs.getString(1));
        }
        System.out.println("""
  B-Tree index: default, good for range/equality
  Hash index:   exact lookups only (MEMORY engine)
  Composite:    (user_id, status) -> covers WHERE user_id=? AND status=?
  Covering:     all needed columns in index (no table access)
    """);
    }

    static void joinsAndAggregation() throws SQLException {
        System.out.println("--- Joins & Aggregation ---");
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement();
             var rs = stmt.executeQuery("""
                SELECT u.name, COUNT(o.id) AS order_count,
                       COALESCE(SUM(o.total), 0) AS total_spent,
                       MAX(o.created_at) AS last_order
                FROM users u
                LEFT JOIN orders o ON u.id = o.user_id
                GROUP BY u.id, u.name
                ORDER BY total_spent DESC
            """)) {
            while (rs.next()) {
                System.out.printf("  %-10s orders=%-2d spent=$%-8.2f last=%s%n",
                    rs.getString("name"),
                    rs.getInt("order_count"),
                    rs.getDouble("total_spent"),
                    rs.getTimestamp("last_order") != null ? rs.getTimestamp("last_order") : "N/A");
            }
        }
    }

    static void transactionIsolation() throws SQLException {
        System.out.println("\n--- Transaction Isolation Levels ---");
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            int level = conn.getTransactionIsolation();
            System.out.println("  Default level: " + switch (level) {
                case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
                case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
                case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
                case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
                default -> "UNKNOWN";
            });
            System.out.println("""
  READ_UNCOMMITTED: dirty reads, non-repeatable reads, phantom reads
  READ_COMMITTED:  no dirty reads, but non-repeatable + phantom
  REPEATABLE_READ: no dirty/non-repeatable, but phantom reads
  SERIALIZABLE:    all anomalies prevented, lowest concurrency
    """);
        }
    }

    static void pessimisticLocking() throws Exception {
        System.out.println("--- Pessimistic Locking (SELECT ... FOR UPDATE) ---");
        var result = new String[1];
        var t1 = new Thread(() -> {
            try (var conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                conn.setAutoCommit(false);
                var ps = conn.prepareStatement("SELECT balance FROM users WHERE id = ? FOR UPDATE");
                ps.setInt(1, 1);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    double bal = rs.getDouble("balance");
                    System.out.println("  [T1] Read balance: " + bal);
                    Thread.sleep(100);
                    var upd = conn.prepareStatement("UPDATE users SET balance = ? WHERE id = 1");
                    upd.setDouble(1, bal - 100);
                    upd.executeUpdate();
                    conn.commit();
                    System.out.println("  [T1] Deducted $100");
                }
            } catch (Exception e) { System.out.println("  [T1] " + e.getMessage()); }
        });

        var t2 = new Thread(() -> {
            try {
                Thread.sleep(20);
                try (var conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    conn.setAutoCommit(false);
                    var ps = conn.prepareStatement("SELECT balance FROM users WHERE id = ? FOR UPDATE NOWAIT");
                    ps.setInt(1, 1);
                    var rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.println("  [T2] Acquired lock after T1 released");
                    }
                }
            } catch (Exception e) { System.out.println("  [T2] Lock conflict: " + e.getMessage()); }
        });

        t1.start(); t2.start();
        t1.join(); t2.join();
    }

    static void optimisticLocking() throws SQLException {
        System.out.println("\n--- Optimistic Locking (Version Column) ---");
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS version INT NOT NULL DEFAULT 0");
            stmt.execute("UPDATE users SET version = 0 WHERE version IS NULL");
        }

        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var ps = conn.prepareStatement(
                "UPDATE users SET balance = balance + ?, version = version + 1 WHERE id = ? AND version = ?")) {
            ps.setDouble(1, 500);
            ps.setInt(2, 2);
            ps.setInt(3, 0);
            int rows = ps.executeUpdate();
            System.out.println("  Version matched: " + (rows > 0) + " (updated " + rows + " row(s))");
        }

        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var ps = conn.prepareStatement(
                "UPDATE users SET balance = balance + ?, version = version + 1 WHERE id = ? AND version = ?")) {
            ps.setDouble(1, 500);
            ps.setInt(2, 2);
            ps.setInt(3, 0);
            int rows = ps.executeUpdate();
            System.out.println("  Stale version: " + (rows == 0) + " (updated " + rows + " row(s))");
        }
    }

    static void partitionDemo() throws SQLException {
        System.out.println("\n--- Partitioning & Sharding ---");
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS orders_partitioned (
                    id INT, user_id INT, total DECIMAL(12,2),
                    order_year INT, status VARCHAR(20)
                )
            """);
            System.out.println("""
  Horizontal partitioning (sharding): split by user_id % N
  Vertical partitioning: split wide columns into separate tables
  Range partitioning: split by date / ID range
  List partitioning: split by region / status
  JDBC sharding: ShardingSphere, Vitess, CitusDB
    """);
        }
    }

    static void connectionPooling() throws SQLException {
        System.out.println("--- Connection Pool ---");
        var pool = new ArrayDeque<Connection>();
        for (int i = 0; i < 3; i++) pool.push(DriverManager.getConnection(URL, USER, PASSWORD));
        System.out.println("  Pool size: " + pool.size());
        var c = pool.poll();
        System.out.println("  Borrowed: " + c.getMetaData().getURL());
        pool.offer(c);
        System.out.println("  Returned to pool");
        pool.forEach(con -> { try { con.close(); } catch (SQLException e) {} });
        System.out.println("  HikariCP: 32 threads * 2 connections = -1 (automatic)");
    }

    static void cleanUp() throws SQLException {
        try (var conn = DriverManager.getConnection(URL, USER, PASSWORD);
             var stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE orders_partitioned IF EXISTS");
            stmt.execute("DROP TABLE orders IF EXISTS");
            stmt.execute("DROP TABLE users IF EXISTS");
            System.out.println("\nCleanup complete.");
        }
    }
}
