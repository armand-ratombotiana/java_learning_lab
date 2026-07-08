package com.databases.cockroachdb;

import java.sql.*;
import java.util.*;

public class DistributedSQLClient {
    private final String jdbcUrl;
    private final Properties props;
    private volatile Connection connection;

    public DistributedSQLClient(String jdbcUrl, String user, String password) {
        this.jdbcUrl = jdbcUrl;
        this.props = new Properties();
        props.setProperty("user", user);
        props.setProperty("password", password);
        props.setProperty("implicitSelectForUpdate", "false");
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(jdbcUrl, props);
            try (var stmt = connection.createStatement()) {
                stmt.execute("SET application_name = 'DistributedSQLClient'");
            }
        }
        return connection;
    }

    public List<Map<String, Object>> executeQuery(String sql, Object... params) throws SQLException {
        var conn = getConnection();
        try (var pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            try (var rs = pstmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        var conn = getConnection();
        try (var pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) pstmt.setObject(i + 1, params[i]);
            return pstmt.executeUpdate();
        }
    }

    public boolean executeDDL(String sql) throws SQLException {
        var conn = getConnection();
        try (var stmt = conn.createStatement()) {
            return stmt.execute(sql);
        }
    }

    public void executeInTransaction(RunnableWithSQL r) throws SQLException {
        var conn = getConnection();
        conn.setAutoCommit(false);
        try {
            r.run();
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new SQLException("Transaction failed", e);
        } finally {
            conn.setAutoCommit(true);
        }
    }

    @FunctionalInterface
    public interface RunnableWithSQL { void run() throws SQLException; }

    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        var meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        var results = new ArrayList<Map<String, Object>>();
        while (rs.next()) {
            var row = new LinkedHashMap<String, Object>();
            for (int i = 1; i <= cols; i++) row.put(meta.getColumnLabel(i), rs.getObject(i));
            results.add(row);
        }
        return results;
    }

    public void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException e) { /* ignore */ }
    }
}
