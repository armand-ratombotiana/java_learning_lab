package com.db.sqlfundamentals;

import java.sql.*;

/**
 * Demonstrates SQL JOIN operations via JDBC.
 * Covers INNER JOIN, LEFT JOIN, RIGHT JOIN, and CROSS JOIN.
 */
public class JoinQueriesDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:joindemo;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== INNER JOIN ===");
            innerJoin(conn);

            System.out.println("\n=== LEFT JOIN ===");
            leftJoin(conn);

            System.out.println("\n=== RIGHT JOIN ===");
            rightJoin(conn);

            System.out.println("\n=== CROSS JOIN ===");
            crossJoin(conn);
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE customers (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50))");
            st.execute("CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, customer_id BIGINT, total DECIMAL(10,2))");
            st.execute("INSERT INTO customers VALUES (1,'Alice'),(2,'Bob'),(3,'Carol')");
            st.execute("INSERT INTO orders VALUES (1,1,50.00),(2,1,30.00),(3,2,20.00)");
            // customer_id=3 (Carol) has no orders
        }
    }

    static void printResultSet(ResultSet rs, String... cols) throws SQLException {
        while (rs.next()) {
            StringBuilder sb = new StringBuilder("  ");
            for (String c : cols) {
                sb.append(c).append("=").append(rs.getString(c)).append(" ");
            }
            System.out.println(sb);
        }
    }

    static void innerJoin(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name, o.id AS order_id, o.total
            FROM customers c
            JOIN orders o ON c.id = o.customer_id
            ORDER BY c.name
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            printResultSet(rs, "name", "order_id", "total");
        }
    }

    static void leftJoin(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name, o.id AS order_id, o.total
            FROM customers c
            LEFT JOIN orders o ON c.id = o.customer_id
            ORDER BY c.name
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            printResultSet(rs, "name", "order_id", "total");
        }
    }

    static void rightJoin(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name, o.id AS order_id, o.total
            FROM customers c
            RIGHT JOIN orders o ON c.id = o.customer_id
            ORDER BY o.id
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            printResultSet(rs, "name", "order_id", "total");
        }
    }

    static void crossJoin(Connection conn) throws SQLException {
        String sql = """
            SELECT c.name, o.id AS order_id
            FROM customers c
            CROSS JOIN orders o
            ORDER BY c.name, o.id
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            printResultSet(rs, "name", "order_id");
        }
    }
}
