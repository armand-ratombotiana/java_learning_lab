package solution;

import java.sql.*;
import java.util.*;

public class PostgreSQLSolution {

    private final Connection connection;

    public PostgreSQLSolution(Connection connection) {
        this.connection = connection;
    }

    // JSONB Operations
    public void insertJsonb(String table, String jsonColumn, String jsonData) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s) VALUES (?)", table, jsonColumn);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, jsonData);
            stmt.executeUpdate();
        }
    }

    public String queryJsonb(String table, String jsonColumn, String path) throws SQLException {
        String sql = String.format("SELECT %s->>'%s' FROM %s", jsonColumn, path, table);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getString(1) : null;
        }
    }

    public List<Map<String, Object>> queryJsonbArray(String table, String jsonColumn) throws SQLException {
        String sql = String.format("SELECT %s FROM %s", jsonColumn, table);
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String json = rs.getString(1);
                results.add(parseJson(json));
            }
        }
        return results;
    }

    // JSON Operators
    public void updateJsonb(String table, String jsonColumn, String key, String value) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = %s || '{\"%s\":\"%s\"}'", table, jsonColumn, jsonColumn, key, value);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public List<String> searchJsonb(String table, String jsonColumn, String searchKey, String searchValue) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE %s @> '{\"%s\":\"%s\"}'", table, jsonColumn, searchKey, searchValue);
        List<String> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(rs.getString("id"));
            }
        }
        return results;
    }

    // Array Operations
    public void insertArray(String table, String column, String[] values) throws SQLException {
        String sql = String.format("INSERT INTO %s (%s) VALUES (?)", table, column);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setArray(1, connection.createArrayOf("text", values));
            stmt.executeUpdate();
        }
    }

    public String[] queryArray(String table, String column) throws SQLException {
        String sql = String.format("SELECT %s FROM %s", column, table);
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                Array array = rs.getArray(1);
                return (String[]) array.getArray();
            }
        }
        return new String[0];
    }

    public List<String> unnestArray(String table, String column) throws SQLException {
        String sql = String.format("SELECT unnest(%s) as element FROM %s", column, table);
        List<String> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(rs.getString("element"));
            }
        }
        return results;
    }

    // Full Text Search
    public void createTsVectorIndex(String table, String column) throws SQLException {
        String sql = String.format("CREATE INDEX %s_fts_idx ON %s USING GIN(to_tsvector('english', %s))",
            table, table, column);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }

    public List<String> searchFullText(String table, String column, String searchQuery) throws SQLException {
        String sql = String.format("SELECT * FROM %s WHERE to_tsvector('english', %s) @@ plainto_tsquery('english', ?)",
            table, column);
        List<String> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, searchQuery);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("id"));
            }
        }
        return results;
    }

    // Window Functions
    public List<Map<String, Object>> runningTotal(String table, String orderColumn) throws SQLException {
        String sql = String.format("SELECT *, SUM(value) OVER (ORDER BY %s) as running_total FROM %s",
            orderColumn, table);
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(Map.of("id", rs.getString("id"), "running_total", rs.getDouble("running_total")));
            }
        }
        return results;
    }

    public List<Map<String, Object>> rankOverPartition(String table, String partitionBy, String orderBy) throws SQLException {
        String sql = String.format("SELECT *, RANK() OVER (PARTITION BY %s ORDER BY %s) as rank FROM %s",
            partitionBy, orderBy, table);
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(Map.of("id", rs.getString("id"), "rank", rs.getInt("rank")));
            }
        }
        return results;
    }

    // CTEs (Common Table Expressions)
    public List<Map<String, Object>> recursiveCTE(String startNode) throws SQLException {
        String sql = """
            WITH RECURSIVE graph AS (
                SELECT id, name, 1 as depth FROM nodes WHERE id = ?
                UNION ALL
                SELECT n.id, n.name, g.depth + 1 FROM nodes n
                JOIN graph g ON n.parent_id = g.id WHERE depth < 10
            )
            SELECT * FROM graph
            """;
        List<Map<String, Object>> results = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, startNode);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(Map.of("id", rs.getString("id"), "depth", rs.getInt("depth")));
            }
        }
        return results;
    }

    // Upsert (INSERT ON CONFLICT)
    public void upsert(String table, String id, Map<String, Object> data) throws SQLException {
        String columns = String.join(", ", data.keySet());
        String placeholders = String.join(", ", Collections.nCopies(data.size(), "?"));
        String sql = String.format("""
            INSERT INTO %s (id, %s) VALUES (?, %s)
            ON CONFLICT (id) DO UPDATE SET %s
            """, table, columns, placeholders,
            data.keySet().stream().map(k -> k + " = EXCLUDED." + k).collect(java.util.stream.Collectors.joining(", ")));

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            int idx = 2;
            for (Object value : data.values()) {
                stmt.setObject(idx++, value);
            }
            stmt.executeUpdate();
        }
    }

    // Helper method
    private Map<String, Object> parseJson(String json) {
        return Map.of("data", json);
    }

    // Batch Operations
    public void batchInsert(String table, List<Map<String, Object>> records) throws SQLException {
        String sql = String.format("INSERT INTO %s (id, data) VALUES (?, ?)", table);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Map<String, Object> record : records) {
                stmt.setString(1, (String) record.get("id"));
                stmt.setString(2, (String) record.get("data"));
                stmt.addBatch();
            }
            stmt.executeBatch();
            connection.commit();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // COPY command for bulk load
    public void bulkLoadFromCSV(String table, String csvPath) throws SQLException {
        String sql = String.format("COPY %s FROM '%s' WITH (FORMAT csv, HEADER true)", table, csvPath);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
}