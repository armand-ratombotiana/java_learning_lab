package com.db.sqlfundamentals;

import java.sql.*;

/**
 * Demonstrates JDBC SELECT queries in Java.
 * Uses H2 in-memory database so this runs without external dependencies.
 *
 * Concepts: Connection, Statement, ResultSet, try-with-resources.
 */
public class JdbcSelectDemo {

    public static void main(String[] args) throws Exception {
        // H2 in-memory: no external DB needed
        String url = "jdbc:h2:mem:sqldemo;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);
            System.out.println("=== Basic SELECT ===");
            basicSelect(conn);

            System.out.println("\n=== SELECT with WHERE ===");
            selectWhere(conn, 25.0);

            System.out.println("\n=== SELECT with ORDER BY ===");
            selectOrderBy(conn);

            System.out.println("\n=== SELECT with LIMIT/OFFSET ===");
            selectPagination(conn, 2, 2);
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE products (
                    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name    VARCHAR(100),
                    price   DECIMAL(10,2),
                    cat     VARCHAR(50)
                )
                """);
            st.execute("INSERT INTO products VALUES " +
                "(1, 'Mouse',   29.99, 'Peripherals')," +
                "(2, 'Keyboard',89.99, 'Peripherals')," +
                "(3, 'Monitor',199.99, 'Displays')," +
                "(4, 'USB Cable',9.99, 'Cables')," +
                "(5, 'Webcam',   59.99, 'Peripherals')");
        }
    }

    static void basicSelect(Connection conn) throws SQLException {
        String sql = "SELECT id, name, price, cat FROM products";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                long id = rs.getLong("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String cat = rs.getString("cat");
                System.out.printf("  %d | %-12s | $%.2f | %s%n", id, name, price, cat);
            }
        }
    }

    static void selectWhere(Connection conn, double minPrice) throws SQLException {
        String sql = "SELECT name, price FROM products WHERE price >= ? ORDER BY price";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  %-12s $%.2f%n", rs.getString("name"), rs.getDouble("price"));
                }
            }
        }
    }

    static void selectOrderBy(Connection conn) throws SQLException {
        String sql = "SELECT name, price, cat FROM products ORDER BY cat ASC, price DESC";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %-12s | %-12s | $%.2f%n",
                    rs.getString("cat"), rs.getString("name"), rs.getDouble("price"));
            }
        }
    }

    static void selectPagination(Connection conn, int limit, int offset) throws SQLException {
        String sql = "SELECT * FROM products ORDER BY id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  id=%d %s $%.2f%n",
                        rs.getLong("id"), rs.getString("name"), rs.getDouble("price"));
                }
            }
        }
    }
}
