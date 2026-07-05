package com.db.postgresql;

import java.sql.*;

/**
 * Demonstrates PostgreSQL JSONB column operations via JDBC.
 *
 * PostgreSQL's JSONB data type allows indexing and querying
 * semi-structured data inside a relational database.
 */
public class JsonbOperations {

    static final String URL = "jdbc:postgresql://localhost:5432/academy";
    static final String USER = "academy_user";
    static final String PASSWORD = "academy_pass";

    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            setup(conn);

            System.out.println("=== Insert JSONB data ===");
            insertProduct(conn, "WR-M-001", "Wireless Mouse",
                """
                {"color":"black","dpi":1600,"wireless":true,"battery":"AA"}
                """);

            insertProduct(conn, "KB-M-001", "Mechanical Keyboard",
                """
                {"layout":"ANSI","switches":"Cherry MX Blue","backlit":true,"size":"full"}
                """);

            System.out.println("\n=== Query JSONB field (color) ===");
            queryByJsonField(conn, "color", "black");

            System.out.println("\n=== Query JSONB nested path ===");
            queryByJsonPath(conn, "switches");

            System.out.println("\n=== JSONB array containment ===");
            jsonbContainment(conn);

        } catch (SQLException e) {
            System.out.println("PostgreSQL required: " + e.getMessage());
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE SCHEMA IF NOT EXISTS academy");
            st.execute("SET search_path TO academy");
            st.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id          BIGSERIAL PRIMARY KEY,
                    sku         VARCHAR(50) UNIQUE NOT NULL,
                    name        VARCHAR(255) NOT NULL,
                    attributes  JSONB NOT NULL DEFAULT '{}'
                )
                """);
            st.execute("CREATE INDEX IF NOT EXISTS idx_attrs_gin ON products USING GIN (attributes)");
        }
    }

    static void insertProduct(Connection conn, String sku, String name, String attributes) throws SQLException {
        String sql = "INSERT INTO products (sku, name, attributes) VALUES (?, ?, ?::jsonb)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            ps.setString(2, name);
            ps.setString(3, attributes);
            ps.executeUpdate();
            System.out.printf("  Inserted: %s - %s%n", sku, name);
        }
    }

    static void queryByJsonField(Connection conn, String field, String value) throws SQLException {
        String sql = "SELECT sku, name, attributes->>? AS field_val FROM products WHERE attributes->>? = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, field);
            ps.setString(2, field);
            ps.setString(3, value);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  %-12s %-20s %s=%s%n",
                        rs.getString("sku"), rs.getString("name"), field, rs.getString("field_val"));
                }
            }
        }
    }

    static void queryByJsonPath(Connection conn, String key) throws SQLException {
        String sql = "SELECT sku, name, attributes FROM products WHERE attributes ?? ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("  %-12s %-20s has '%s'%n",
                        rs.getString("sku"), rs.getString("name"), key);
                }
            }
        }
    }

    static void jsonbContainment(Connection conn) throws SQLException {
        String sql = """
            SELECT sku, name FROM products
            WHERE attributes @> '{"wireless": true}'::jsonb
            """;
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %-12s %s (wireless)%n",
                    rs.getString("sku"), rs.getString("name"));
            }
        }
    }
}
