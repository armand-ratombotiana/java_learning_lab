package com.databases.deep.lab01;

import java.sql.*;
import java.util.*;

/**
 * QueryOptimizationLab — explores EXPLAIN plans, join algorithms, and cost estimation.
 *
 * Build an in-memory H2 database with realistic data, run queries with
 * EXPLAIN, and display parsed plan nodes.
 */
public class QueryOptimizationLab {

    record PlanNode(String nodeType, double startupCost, double totalCost, int planRows, int planWidth, List<PlanNode> children) {}

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:queryopt;DB_CLOSE_DELAY=-1")) {
            setup(conn);
            runExplain(conn, "SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id WHERE c.email LIKE '%@example.com'");
            runExplain(conn, "SELECT * FROM orders WHERE total_amount > 100");
            runExplain(conn, "SELECT c.name, COUNT(o.id) FROM customers c LEFT JOIN orders o ON c.id = o.customer_id GROUP BY c.name HAVING COUNT(o.id) > 5");
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (var st = conn.createStatement()) {
            st.execute("CREATE TABLE customers (id INT PRIMARY KEY, name VARCHAR(100), email VARCHAR(200))");
            st.execute("CREATE TABLE orders (id INT PRIMARY KEY, customer_id INT, total_amount DECIMAL(10,2), created_at TIMESTAMP)");
            st.execute("CREATE INDEX idx_orders_customer ON orders(customer_id)");
            st.execute("CREATE INDEX idx_customers_email ON customers(email)");

            Random rnd = new Random(42);
            for (int i = 1; i <= 10_000; i++) {
                st.execute(String.format("INSERT INTO customers VALUES (%d, 'Customer_%d', 'user%d@example.com')", i, i, i));
            }
            for (int i = 1; i <= 100_000; i++) {
                int cid = rnd.nextInt(10_000) + 1;
                double amount = Math.round(rnd.nextDouble() * 1000 * 100.0) / 100.0;
                st.execute(String.format("INSERT INTO orders VALUES (%d, %d, %.2f, CURRENT_TIMESTAMP)", i, cid, amount));
            }
            st.execute("ANALYZE customers");
            st.execute("ANALYZE orders");
        }
    }

    static void runExplain(Connection conn, String sql) throws SQLException {
        System.out.println("=== EXPLAIN ===");
        System.out.println("SQL: " + sql);
        try (var st = conn.createStatement(); var rs = st.executeQuery("EXPLAIN (FORMAT JSON) " + sql)) {
            while (rs.next()) System.out.println(rs.getString(1));
        }
        System.out.println();
    }
}