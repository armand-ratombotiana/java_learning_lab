package com.dataeng.nine;

import java.sql.*;
import java.util.Properties;

public class SnowflakeConnector {
    private static final String JDBC_URL = "jdbc:snowflake://account.snowflakecomputing.com";

    public Connection connect(String user, String password, String warehouse,
                               String database, String schema) throws SQLException {
        Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        props.put("warehouse", warehouse);
        props.put("db", database);
        props.put("schema", schema);
        props.put("tracing", "INFO");
        props.put("loginTimeout", "60");
        return DriverManager.getConnection(JDBC_URL, props);
    }

    public ResultSet executeQuery(Connection conn, String sql) throws SQLException {
        return conn.createStatement().executeQuery(sql);
    }

    public int executeUpdate(Connection conn, String sql) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        }
    }

    public void createWarehouse(Connection conn, String name, String size,
                                 int autoSuspendSec) throws SQLException {
        String sql = String.format(
            "CREATE WAREHOUSE IF NOT EXISTS %s WITH WAREHOUSE_SIZE='%s' AUTO_SUSPEND=%d AUTO_RESUME=TRUE",
            name, size, autoSuspendSec);
        executeUpdate(conn, sql);
    }

    public void cloneTable(Connection conn, String source, String target) throws SQLException {
        executeUpdate(conn, "CREATE TABLE " + target + " CLONE " + source);
    }

    public void close(Connection conn) {
        try { if (conn != null && !conn.isClosed()) conn.close(); }
        catch (SQLException e) { System.err.println("Close error: " + e.getMessage()); }
    }
}
