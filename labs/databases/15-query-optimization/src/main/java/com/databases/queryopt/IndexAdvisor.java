package com.databases.queryopt;

import java.util.*;

public class IndexAdvisor {
    private final Map<String, TableStats> tableStats = new HashMap<>();

    public record ColumnStats(String name, boolean indexed, long distinctValues, long tableRows) {
        public double getSelectivity() { return (double) distinctValues / tableRows; }
        public boolean isHighSelectivity() { return getSelectivity() > 0.1; }
        public String getRecommendation() {
            if (indexed) return "Already indexed";
            if (distinctValues < 100) return "Low cardinality";
            if (isHighSelectivity()) return "STRONGLY recommend adding index";
            return "Consider index for frequent queries";
        }
    }

    public static class TableStats {
        private final String name;
        private long rowCount;
        private final Map<String, ColumnStats> columns = new HashMap<>();
        public TableStats(String name) { this.name = name; }
        public void setRowCount(long n) { rowCount = n; }
        public void addColumn(String col, boolean indexed, long distinct, boolean nullable) {
            columns.put(col, new ColumnStats(col, indexed, distinct, rowCount));
        }
        public Map<String, ColumnStats> getColumns() { return Collections.unmodifiableMap(columns); }
    }

    public void analyzeTable(String name, long rows, Map<String, Object[]> cols) {
        var stats = new TableStats(name);
        stats.setRowCount(rows);
        for (var e : cols.entrySet()) {
            Object[] v = e.getValue();
            stats.addColumn(e.getKey(), (boolean)v[0], (long)v[1], (boolean)v[2]);
        }
        tableStats.put(name, stats);
    }

    public List<String> recommendIndexes() {
        List<String> recs = new ArrayList<>();
        for (var entry : tableStats.entrySet()) {
            for (var col : entry.getValue().getColumns().values()) {
                if (!col.indexed() && col.isHighSelectivity()) {
                    recs.add("CREATE INDEX idx_" + entry.getKey() + "_" + col.name()
                        + " ON " + entry.getKey() + "(" + col.name() + ");");
                }
            }
        }
        return recs;
    }

    public TableStats getStats(String name) { return tableStats.get(name); }
}
