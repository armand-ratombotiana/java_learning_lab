package com.learning.lab.module18;

import java.sql.*;
import java.util.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 18: Database Access ===");
        jdbcConnectionDemo();
        jdbcCRUDDemo();
        jdbcTransactionsDemo();
        jdbcStatementsDemo();
        jdbcPreparedStatementDemo();
        jdbcResultSetDemo();
        jdbcBatchProcessing();
        connectionPoolingDemo();
    }

    static void jdbcConnectionDemo() {
        System.out.println("\n--- JDBC Connection ---");
        System.out.println("Steps to connect:");
        System.out.println("1. Load driver: Class.forName(\"com.mysql.cj.jdbc.Driver\")");
        System.out.println("2. Get connection: DriverManager.getConnection(url, user, pass)");
        System.out.println("3. Execute queries");
        System.out.println("4. Close resources");
        
        String url = "jdbc:mysql://localhost:3306/mydb";
        String user = "root";
        String password = "password";
        System.out.println("\nConnection URL format:");
        System.out.println("jdbc:mysql://host:port/database?options");
        System.out.println("jdbc:postgresql://host:port/database");
        System.out.println("jdbc:oracle:thin:@host:port:service");
    }

    static void jdbcCRUDDemo() {
        System.out.println("\n--- JDBC CRUD Operations ---");
        System.out.println("CREATE:");
        System.out.println("  PreparedStatement ps = conn.prepareStatement(");
        System.out.println("    \"INSERT INTO users (name, email) VALUES (?, ?)\");");
        System.out.println("  ps.setString(1, \"John\");");
        System.out.println("  ps.setString(2, \"john@email.com\");");
        System.out.println("  int rows = ps.executeUpdate();");
        
        System.out.println("\nREAD:");
        System.out.println("  Statement stmt = conn.createStatement();");
        System.out.println("  ResultSet rs = stmt.executeQuery(\"SELECT * FROM users\");");
        System.out.println("  while (rs.next()) {");
        System.out.println("    System.out.println(rs.getString(\"name\"));");
        System.out.println("  }");
        
        System.out.println("\nUPDATE:");
        System.out.println("  ps = conn.prepareStatement(\"UPDATE users SET name=? WHERE id=?\");");
        System.out.println("  ps.setString(1, \"Jane\");");
        System.out.println("  ps.setInt(2, 1);");
        System.out.println("  ps.executeUpdate();");
        
        System.out.println("\nDELETE:");
        System.out.println("  ps = conn.prepareStatement(\"DELETE FROM users WHERE id=?\");");
        System.out.println("  ps.setInt(1, 1);");
        System.out.println("  ps.executeUpdate();");
    }

    static void jdbcTransactionsDemo() {
        System.out.println("\n--- JDBC Transactions ---");
        System.out.println("Transaction control:");
        System.out.println("  conn.setAutoCommit(false);");
        System.out.println("  // execute operations");
        System.out.println("  conn.commit(); // or conn.rollback();");
        
        System.out.println("\nSavepoints:");
        System.out.println("  Savepoint sp = conn.setSavepoint(\"before_update\");");
        System.out.println("  // operations");
        System.out.println("  conn.rollback(sp); // rollback to savepoint");
        
        System.out.println("\nIsolation levels:");
        System.out.println("  Connection.TRANSACTION_READ_UNCOMMITTED");
        System.out.println("  Connection.TRANSACTION_READ_COMMITTED");
        System.out.println("  Connection.TRANSACTION_REPEATABLE_READ");
        System.out.println("  Connection.TRANSACTION_SERIALIZABLE");
    }

    static void jdbcStatementsDemo() {
        System.out.println("\n--- JDBC Statements ---");
        System.out.println("Statement - Static SQL");
        System.out.println("  Statement stmt = conn.createStatement();");
        System.out.println("  stmt.executeQuery(\"SELECT * FROM users\");");
        
        System.out.println("\nPreparedStatement - Precompiled SQL");
        System.out.println("  Better performance, prevents SQL injection");
        System.out.println("  Supports parameter binding");
        
        System.out.println("\nCallableStatement - Stored Procedures");
        System.out.println("  CallableStatement cs = conn.prepareCall(\"{call proc_name(?)}\");");
    }

    static void jdbcPreparedStatementDemo() {
        System.out.println("\n--- PreparedStatement Examples ---");
        System.out.println("Parameter types:");
        System.out.println("  setString(int pos, String val)");
        System.out.println("  setInt(int pos, int val)");
        System.out.println("  setDouble(int pos, double val)");
        System.out.println("  setDate(int pos, Date val)");
        System.out.println("  setNull(int pos, int sqlType)");
        System.out.println("  setObject(int pos, Object val)");
        System.out.println("\nBatch operations:");
        System.out.println("  ps.setString(1, \"Alice\");");
        System.out.println("  ps.addBatch();");
        System.out.println("  ps.setString(1, \"Bob\");");
        System.out.println("  ps.addBatch();");
        System.out.println("  int[] results = ps.executeBatch();");
    }

    static void jdbcResultSetDemo() {
        System.out.println("\n--- ResultSet Operations ---");
        System.out.println("Navigation:");
        System.out.println("  rs.next() - Move to next row");
        System.out.println("  rs.previous() - Move backward");
        System.out.println("  rs.first() / rs.last()");
        System.out.println("  rs.absolute(row) - Go to specific row");
        
        System.out.println("\nData retrieval:");
        System.out.println("  rs.getString(\"column\") / rs.getString(colIndex)");
        System.out.println("  rs.getInt(\"column\")");
        System.out.println("  rs.getDouble(\"column\")");
        System.out.println("  rs.getDate(\"column\")");
        System.out.println("  rs.getObject(\"column\", Class.class)");
        
        System.out.println("\nResultSet types:");
        System.out.println("  TYPE_FORWARD_ONLY (default)");
        System.out.println("  TYPE_SCROLL_INSENSITIVE");
        System.out.println("  TYPE_SCROLL_SENSITIVE");
    }

    static void jdbcBatchProcessing() {
        System.out.println("\n--- Batch Processing ---");
        System.out.println("Batch processing benefits:");
        System.out.println("  - Reduces database round trips");
        System.out.println("  - Improves performance for bulk operations");
        
        System.out.println("\nExample:");
        System.out.println("  conn.setAutoCommit(false);");
        System.out.println("  Statement stmt = conn.createStatement();");
        System.out.println("  for (User u : users) {");
        System.out.println("    stmt.addBatch(\"INSERT INTO users VALUES(\" + u + \")\");");
        System.out.println("  }");
        System.out.println("  int[] results = stmt.executeBatch();");
        System.out.println("  conn.commit();");
    }

    static void connectionPoolingDemo() {
        System.out.println("\n--- Connection Pooling ---");
        System.out.println("Popular connection pools:");
        System.out.println("  - HikariCP (Spring Boot default)");
        System.out.println("  - Apache DBCP2");
        System.out.println("  - C3P0");
        System.out.println("  - Druid");
        
        System.out.println("\nHikariCP configuration:");
        System.out.println("  spring.datasource.hikari.maximum-pool-size=10");
        System.out.println("  spring.datasource.hikari.minimum-idle=5");
        System.out.println("  spring.datasource.hikari.connection-timeout=30000");
    }
}

class User {
    Long id;
    String name;
    String email;
}
