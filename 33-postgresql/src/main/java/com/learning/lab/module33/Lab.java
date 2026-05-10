package com.learning.lab.module33;

import java.sql.*;
import java.util.*;

public class Lab {
    private static final String URL = "jdbc:postgresql://localhost:5432/learningdb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";

    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 33: PostgreSQL Operations ===");

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            createTableDemo(conn);
            insertDemo(conn);
            queryDemo(conn);
            updateDemo(conn);
            deleteDemo(conn);
            transactionDemo(conn);
            batchOperationsDemo(conn);
            jsonbDemo(conn);
        }
    }

    static void createTableDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Create Table ---");
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created/verified");
        }
    }

    static void insertDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Insert ---");
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "John Doe");
            ps.setString(2, "john@example.com");
            int rows = ps.executeUpdate();
            System.out.println("Inserted " + rows + " row(s)");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    System.out.println("Generated ID: " + rs.getInt(1));
                }
            }
        }
    }

    static void queryDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Query ---");
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Email: " + rs.getString("email"));
                }
            }
        }
    }

    static void updateDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Update ---");
        String sql = "UPDATE users SET name = ? WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Jane Doe");
            ps.setString(2, "john@example.com");
            int rows = ps.executeUpdate();
            System.out.println("Updated " + rows + " row(s)");
        }
    }

    static void deleteDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Delete ---");
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, 1);
            int rows = ps.executeUpdate();
            System.out.println("Deleted " + rows + " row(s)");
        }
    }

    static void transactionDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Transaction ---");
        conn.setAutoCommit(false);
        try {
            String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "User1");
                ps.setString(2, "user1@example.com");
                ps.executeUpdate();

                ps.setString(1, "User2");
                ps.setString(2, "user2@example.com");
                ps.executeUpdate();
            }
            conn.commit();
            System.out.println("Transaction committed");
        } catch (Exception e) {
            conn.rollback();
            System.out.println("Transaction rolled back");
        } finally {
            conn.setAutoCommit(true);
        }
    }

    static void batchOperationsDemo(Connection conn) throws SQLException {
        System.out.println("\n--- Batch Operations ---");
        String sql = "INSERT INTO users (name, email) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 3; i++) {
                ps.setString(1, "BatchUser" + i);
                ps.setString(2, "batch" + i + "@example.com");
                ps.addBatch();
            }
            int[] results = ps.executeBatch();
            System.out.println("Batch inserted " + results.length + " rows");
        }
    }

    static void jsonbDemo(Connection conn) throws SQLException {
        System.out.println("\n--- JSONB Operations ---");
        String sql = "INSERT INTO users (name, email, metadata) VALUES (?, ?, ?::jsonb)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "Json User");
            ps.setString(2, "json@example.com");
            ps.setString(3, "{\"role\": \"admin\", \"preferences\": {\"theme\": \"dark\"}}");
            ps.executeUpdate();
            System.out.println("JSONB data inserted");
        }
    }
}