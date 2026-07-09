package com.databases.advsql;

import java.util.*;
import java.util.stream.*;

public class SqlOptimizer {

    public record TableStats(String name, long rows, long blocks, int avgRowLength) {}
    public record ColumnStats(String colName, long ndv, long nulls, boolean hasHistogram) {}
    public record IndexStats(String indexName, int blevel, long leafBlocks, double clusteringFactor) {}
    public record QueryPlan(String operation, String object, long estCardinality, double estCost, String accessPredicate) {}

    private final Map<String, TableStats> tables = new HashMap<>();
    private final Map<String, IndexStats> indexes = new HashMap<>();
    private final Map<String, ColumnStats> columns = new HashMap<>();

    public void addTableStats(TableStats ts) { tables.put(ts.name(), ts); }
    public void addIndexStats(IndexStats is) { indexes.put(is.indexName(), is); }
    public void addColumnStats(ColumnStats cs) { columns.put(cs.col(), cs); }

    public double estimateFullScanCost(String table) {
        var ts = tables.get(table);
        if (ts == null) return Double.MAX_VALUE;
        return ts.blocks() * 1.0; // 1 block read per I/O
    }

    public double estimateIndexScanCost(String indexName) {
        var idx = indexes.get(indexName);
        if (idx == null) return Double.MAX_VALUE;
        // cost = B-tree height + leaf blocks scanned + table access cost
        return idx.blevelDepth() + (idx.leafBlocks() * 0.1) + (idx.clusteringFactor() * 0.01);
    }

    public double estimateJoinCost(String table1, String table2, String joinColumn, double selectivity) {
        var t1 = tables.get(table1);
        var t2 = tables.get(table2);
        if (t1 == null || t2 == null) return Double.MAX_VALUE;
        double nestedLoopsCost = t1.blocks() * t2.blocks() * selectivity;
        double hashJoinCost = t1.blocks() + t2.blocks() * 1.2;
        return Math.min(nestedLoopsCost, hashJoinCost);
    }

    public QueryPlan generatePlan(String sql) {
        var plan = new ArrayList<String>();
        // simplified plan generation
        String table = parseTable(sql);
        if (table == null) return new QueryPlan("TABLE ACCESS", "UNKNOWN", 0, 0, "N/A");
        var ts = tables.get(table);
        long rows = ts != null ? ts.rows() : 1000;
        double cost = estimateFullScanCost(table);
        boolean hasIndex = indexes.keySet().stream().anyMatch(k -> k.contains(table));
        if (hasIndex) {
            cost = indexes.values().stream()
                .filter(i -> i.indexName().contains(table))
                .mapToDouble(this::estimateIndexScanCost)
                .min().orElse(cost);
        }
        return new QueryPlan(
            hasIndex ? "INDEX RANGE SCAN" : "TABLE ACCESS FULL",
            table, rows, cost,
            hasIndex ? table + "_idx" : null
        );
    }

    private String parseTable(String sql) {
        String upper = sql.toUpperCase();
        for (var t : tables.keySet()) {
            if (upper.contains(t.toUpperCase())) return t;
        }
        return null;
    }

    public ColumnStats getColumnStats(String col) { return columns.get(col); }
    public IndexStats getIndexStats(String idx) { return indexes.get(idx); }
    public TableStats getTableStats(String t) { return tables.get(t); }

    public static SqlOptimizer sampleOptimizer() {
        var opt = new SqlOptimizer();
        opt.addTableStats(new TableStats("EMPLOYEES", 100000, 2000, 80));
        opt.addTableStats(new TableStats("DEPARTMENTS", 50, 5, 60));
        opt.addColumnStats(new ColumnStats("dept_id", 50, 0, true));
        opt.addColumnStats(new ColumnStats("salary", 50000, 1000, true));
        opt.addColumnStats(new ColumnStats("hire_date", 5000, 0, false));
        opt.addIndexStats(new IndexStats("emp_dept_idx", 2, 500, 0.8));
        opt.addIndexStats(new IndexStats("emp_pk", 1, 2000, 1.0));
        opt.addIndexStats(new IndexStats("emp_salary_idx", 3, 250, 0.6));
        return opt;
    }
}