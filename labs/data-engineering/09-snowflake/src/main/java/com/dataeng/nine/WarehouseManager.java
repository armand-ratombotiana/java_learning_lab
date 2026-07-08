package com.dataeng.nine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseManager {
    private final Connection conn;

    public WarehouseManager(Connection conn) { this.conn = conn; }

    public void createWarehouse(String name, String size, int autoSuspendSec) throws SQLException {
        String sql = String.format(
            "CREATE WAREHOUSE IF NOT EXISTS %s WITH WAREHOUSE_SIZE='%s' AUTO_SUSPEND=%d AUTO_RESUME=TRUE",
            name, size, autoSuspendSec);
        try (Statement stmt = conn.createStatement()) { stmt.execute(sql); }
    }

    public void alterSize(String name, String newSize) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER WAREHOUSE " + name + " SET WAREHOUSE_SIZE='" + newSize + "'");
        }
    }

    public void suspend(String name) throws SQLException {
        try (Statement stmt = conn.createStatement()) { stmt.execute("ALTER WAREHOUSE " + name + " SUSPEND"); }
    }

    public void resume(String name) throws SQLException {
        try (Statement stmt = conn.createStatement()) { stmt.execute("ALTER WAREHOUSE " + name + " RESUME"); }
    }

    public List<String> listWarehouses() throws SQLException {
        List<String> result = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW WAREHOUSES")) {
            while (rs.next()) {
                result.add(rs.getString("name") + "|" + rs.getString("size") + "|" + rs.getString("state"));
            }
        }
        return result;
    }
}
