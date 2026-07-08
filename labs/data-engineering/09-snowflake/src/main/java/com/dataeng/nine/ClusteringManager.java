package com.dataeng.nine;

import java.sql.*;

public class ClusteringManager {
    private final Connection conn;

    public ClusteringManager(Connection conn) { this.conn = conn; }

    public void setClusteringKey(String table, String... columns) throws SQLException {
        String keys = String.join(", ", columns);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " CLUSTER BY (" + keys + ")");
        }
    }

    public void dropClusteringKey(String table) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " DROP CLUSTERING KEY");
        }
    }

    public double getClusteringDepth(String table) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SYSTEM$CLUSTERING_DEPTH('" + table + "')")) {
            return rs.next() ? rs.getDouble(1) : -1;
        }
    }

    public void recluster(String table) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE " + table + " RECLUSTER");
        }
    }
}
