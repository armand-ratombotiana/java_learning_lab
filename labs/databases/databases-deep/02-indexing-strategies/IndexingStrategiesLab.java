package com.databases.deep.lab02;

import java.sql.*;

/**
 * IndexingStrategiesLab — explores B-tree, hash, partial, composite, and covering indexes.
 *
 * Uses H2 in-memory. Creates tables with various indexes and compares
 * EXPLAIN plans to illustrate when each index type is selected.
 */
public class IndexingStrategiesLab {

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:index;DB_CLOSE_DELAY=-1")) {
            setup(conn);
            System.out.println("=== B-tree index ===");
            runExplain(conn, "SELECT * FROM products WHERE price BETWEEN 50 AND 100");
            System.out.println("=== Partial index ===");
            runExplain(conn, "SELECT * FROM products WHERE status = 'DISCONTINUED' AND price > 10");
            System.out.println("=== Composite index col order ===");
            runExplain(conn, "SELECT * FROM products WHERE category = 'Electronics' ORDER BY price DESC");
            System.out.println("=== Covering index ===");
            runExplain(conn, "SELECT category, COUNT(*) FROM products GROUP BY category");
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (var st = conn.createStatement()) {
            st.execute("CREATE TABLE products (" +
                       "id INT PRIMARY KEY, name VARCHAR(200), price DECIMAL(10,2), " +
                       "category VARCHAR(50), status VARCHAR(20), created_at TIMESTAMP)");
            st.execute("CREATE INDEX idx_btree_price ON products(price)");
            st.execute("CREATE INDEX idx_partial ON products(price) WHERE status = 'DISCONTINUED'");
            st.execute("CREATE INDEX idx_composite ON products(category, price DESC)");
            st.execute("CREATE INDEX idx_covering ON products(category) INCLUDE (id)");

            for (int i = 1; i <= 50_000; i++) {
                String cat = i % 3 == 0 ? "Electronics" : i % 3 == 1 ? "Books" : "Clothing";
                String status = i % 10 == 0 ? "DISCONTINUED" : "ACTIVE";
                double price = Math.round((Math.random() * 1000) * 100.0) / 100.0;
                st.execute(String.format("INSERT INTO products VALUES (%d, 'Product_%d', %.2f, '%s', '%s', CURRENT_TIMESTAMP)",
                          i, i, price, cat, status));
            }
            st.execute("ANALYZE products");
        }
    }

    static void runExplain(Connection conn, String sql) throws SQLException {
        System.out.println("SQL: " + sql);
        try (var st = conn.createStatement(); var rs = st.executeQuery("EXPLAIN ANALYZE " + sql)) {
            while (rs.next()) System.out.println(rs.getString(1));
        }
        System.out.println();
    }
}