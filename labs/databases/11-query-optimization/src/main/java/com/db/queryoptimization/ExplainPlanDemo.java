package com.db.queryoptimization;

import java.sql.*;

/**
 * Demonstrates EXPLAIN PLAN usage in PostgreSQL and H2 databases.
 *
 * EXPLAIN shows the query execution plan, helping identify
 * full table scans, index usage, join strategies, and cost estimates.
 */
public class ExplainPlanDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:explain;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== EXPLAIN PLAN Examples ===\n");

            System.out.println("1. Simple SELECT (no index):");
            explainQuery(conn, "SELECT * FROM users WHERE email = 'user_500@example.com'");

            System.out.println("\n2. After adding index on email:");
            conn.createStatement().execute("CREATE INDEX idx_email ON users(email)");
            explainQuery(conn, "SELECT * FROM users WHERE email = 'user_500@example.com'");

            System.out.println("\n3. JOIN query:");
            explainQuery(conn,
                "SELECT u.name, o.total FROM users u " +
                "JOIN orders o ON u.id = o.user_id " +
                "WHERE o.total > 100");

            System.out.println("\n4. Aggregation with GROUP BY:");
            explainQuery(conn,
                "SELECT u.name, COUNT(o.id) AS order_count, SUM(o.total) AS total_spent " +
                "FROM users u LEFT JOIN orders o ON u.id = o.user_id " +
                "GROUP BY u.id, u.name " +
                "ORDER BY total_spent DESC NULLS LAST LIMIT 10");
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100), email VARCHAR(255))");
            st.execute("CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id BIGINT, total DECIMAL(10,2), created_at TIMESTAMP)");

            // Insert sample data
            for (int i = 0; i < 1000; i++) {
                st.execute("INSERT INTO users VALUES (" + i + ", 'User " + i +
                    "', 'user_" + i + "@example.com')");
                if (i % 3 == 0) {
                    st.execute("INSERT INTO orders (user_id, total, created_at) VALUES (" +
                        i + ", " + (Math.random() * 500) + ", NOW())");
                }
            }
            System.out.println("  Inserted 1000 users and ~333 orders");
        }
    }

    static void explainQuery(Connection conn, String sql) throws SQLException {
        // H2 uses EXPLAIN ANALYZE for execution plans
        String explainSql = "EXPLAIN ANALYZE " + sql;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(explainSql)) {
            while (rs.next()) {
                System.out.println("  " + rs.getString(1));
            }
        }
    }
}
