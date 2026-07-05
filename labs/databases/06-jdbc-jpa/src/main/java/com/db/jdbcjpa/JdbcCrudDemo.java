package com.db.jdbcjpa;

import java.sql.*;

/**
 * Demonstrates JDBC CRUD operations: CREATE, READ, UPDATE, DELETE.
 *
 * Uses H2 in-memory database.  Covers:
 * - PreparedStatement (parameterized queries, prevents SQL injection)
 * - Batch inserts
 * - Transaction management (commit/rollback)
 * - Generated keys retrieval
 */
public class JdbcCrudDemo {

    public static void main(String[] args) throws Exception {
        String url = "jdbc:h2:mem:jdbccrud;DB_CLOSE_DELAY=-1";

        try (Connection conn = DriverManager.getConnection(url)) {
            setup(conn);

            System.out.println("=== CREATE (INSERT) ===");
            long id1 = insertUser(conn, "Alice", "alice@example.com");
            long id2 = insertUser(conn, "Bob", "bob@example.com");
            System.out.printf("  Created users with ids: %d, %d%n", id1, id2);

            System.out.println("\n=== Batch INSERT ===");
            int[] batchResults = batchInsertUsers(conn,
                new String[]{"Carol", "Dave", "Eve"},
                new String[]{"carol@example.com", "dave@example.com", "eve@example.com"});
            System.out.printf("  Batch inserted %d rows%n", batchResults.length);

            System.out.println("\n=== READ (SELECT) ===");
            findAllUsers(conn);

            System.out.println("\n=== READ by ID ===");
            findUserById(conn, id1);

            System.out.println("\n=== UPDATE ===");
            updateUserEmail(conn, id1, "alice.new@example.com");
            findUserById(conn, id1);

            System.out.println("\n=== DELETE ===");
            deleteUser(conn, id2);
            findAllUsers(conn);

            System.out.println("\n=== Transaction: Rollback on error ===");
            transactionRollbackDemo(conn);
        }
    }

    static void setup(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("""
                CREATE TABLE users (
                    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name  VARCHAR(100) NOT NULL,
                    email VARCHAR(255) NOT NULL UNIQUE
                )
                """);
        }
    }

    static long insertUser(Connection conn, String name, String email) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    static int[] batchInsertUsers(Connection conn, String[] names, String[] emails) throws SQLException {
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < names.length; i++) {
                ps.setString(1, names[i]);
                ps.setString(2, emails[i]);
                ps.addBatch();
            }
            return ps.executeBatch();
        }
    }

    static void findAllUsers(Connection conn) throws SQLException {
        String sql = "SELECT id, name, email FROM users ORDER BY id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                System.out.printf("  %d | %-6s | %s%n",
                    rs.getLong("id"), rs.getString("name"), rs.getString("email"));
            }
        }
    }

    static void findUserById(Connection conn, long id) throws SQLException {
        String sql = "SELECT id, name, email FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("  Found: %d | %s | %s%n",
                        rs.getLong("id"), rs.getString("name"), rs.getString("email"));
                }
            }
        }
    }

    static void updateUserEmail(Connection conn, long id, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newEmail);
            ps.setLong(2, id);
            int affected = ps.executeUpdate();
            System.out.printf("  Updated %d row(s)%n", affected);
        }
    }

    static void deleteUser(Connection conn, long id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            System.out.printf("  Deleted %d row(s)%n", affected);
        }
    }

    static void transactionRollbackDemo(Connection conn) throws SQLException {
        conn.setAutoCommit(false);
        try {
            insertUser(conn, "Fake", "fake@example.com");
            // Intentional duplicate email — violates UNIQUE constraint
            insertUser(conn, "Duplicate", "fake@example.com");
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            System.out.println("  Rolled back transaction due to: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
        }
        findAllUsers(conn);
    }
}
