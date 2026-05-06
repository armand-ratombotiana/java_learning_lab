package com.learning.database;

import java.sql.*;
import java.util.*;

public class DatabaseTraining {

    public static void main(String[] args) {
        System.out.println("=== JDBC Database Training ===");
        
        demonstrateConnection();
        demonstratePreparedStatement();
        demonstrateTransaction();
        demonstrateResultSet();
    }

    private static void demonstrateConnection() {
        System.out.println("\n--- Connection Management ---");
        
        String url = "jdbc:h2:mem:testdb";
        String user = "sa";
        String password = "";
        
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected: " + !conn.isClosed());
            System.out.println("Auto-commit: " + conn.getAutoCommit());
            System.out.println("Catalog: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    private static void demonstratePreparedStatement() {
        System.out.println("\n--- PreparedStatement ---");
        
        String url = "jdbc:h2:mem:testdb";
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE users (id INT, name VARCHAR(255))");
            stmt.execute("INSERT INTO users VALUES (1, 'Alice'), (2, 'Bob')");
            
            String sql = "SELECT * FROM users WHERE id > ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, 0);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        System.out.println("User: " + rs.getInt("id") + " - " + rs.getString("name"));
                    }
                }
            }
            
            stmt.execute("DROP TABLE users");
            
        } catch (SQLException e) {
            System.err.println("PreparedStatement error: " + e.getMessage());
        }
    }

    private static void demonstrateTransaction() {
        System.out.println("\n--- Transaction Management ---");
        
        String url = "jdbc:h2:mem:testdb";
        
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.setAutoCommit(false);
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE accounts (id INT, balance DECIMAL(10,2))");
                stmt.execute("INSERT INTO accounts VALUES (1, 1000.00), (2, 500.00)");
                
                stmt.execute("UPDATE accounts SET balance = balance - 100 WHERE id = 1");
                stmt.execute("UPDATE accounts SET balance = balance + 100 WHERE id = 2");
                
                conn.commit();
                System.out.println("Transaction committed successfully");
                
                stmt.execute("DROP TABLE accounts");
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Transaction rolled back: " + e.getMessage());
            }
            
            conn.setAutoCommit(true);
            
        } catch (SQLException e) {
            System.err.println("Transaction error: " + e.getMessage());
        }
    }

    private static void demonstrateResultSet() {
        System.out.println("\n--- ResultSet Operations ---");
        
        String url = "jdbc:h2:mem:testdb";
        
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE products (id INT, name VARCHAR(100), price DECIMAL(10,2))");
            stmt.execute("INSERT INTO products VALUES (1, 'Laptop', 999.99), (2, 'Mouse', 29.99)");
            
            try (ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
                ResultSetMetaData metaData = rs.getMetaData();
                System.out.println("Columns: " + metaData.getColumnCount());
                
                while (rs.next()) {
                    System.out.printf("Product: %s - $%.2f%n", 
                        rs.getString("name"), 
                        rs.getDouble("price"));
                }
                
                rs.beforeFirst();
                System.out.println("Row count: " + rs.getRow());
            }
            
            stmt.execute("DROP TABLE products");
            
        } catch (SQLException e) {
            System.err.println("ResultSet error: " + e.getMessage());
        }
    }
}