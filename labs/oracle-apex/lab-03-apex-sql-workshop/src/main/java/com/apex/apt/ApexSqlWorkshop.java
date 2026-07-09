package com.apex.apt;

import java.util.*;

public class ApexSqlWorkshop {
    public record TableInfo(String name, String owner, List<String> columns, long rows) {}
    public record QueryResult(String sql, List<String> columns, List<List<String>> rows, long elapsedMs) {}
    public record ImportResult(String table, int rowsImported, int errors, List<String> messages) {}

    private final Map<String, TableInfo> tables = new LinkedHashMap<>();
    private final List<String> queryHistory = new ArrayList<>();

    public void registerTable(TableInfo t) { tables.put(t.name(), t); }

    public TableInfo getTable(String name) { return tables.get(name); }
    public List<TableInfo> getAllTables() { return List.copyOf(tables.values()); }

    public QueryResult executeQuery(String sql) {
        long start = System.currentTimeMillis();
        queryHistory.add(sql);
        var parsed = parseSql(sql);
        long elapsed = System.currentTimeMillis() - start;
        var cols = new ArrayList<String>();
        if (!parsed.isEmpty()) {
            var table = tables.get(parsed.get(0));
            if (table != null) cols.addAll(table.columns());
        }
        if (cols.isEmpty()) cols.add("result");
        return new QueryResult(sql, cols, List.of(List.of("Simulated result")), elapsed);
    }

    private List<String> parseSql(String sql) {
        var result = new ArrayList<String>();
        var upper = sql.toUpperCase();
        for (var t : tables.keySet()) {
            if (upper.contains(t.toUpperCase())) result.add(t);
        }
        return result;
    }

    public ImportResult importData(String table, List<Map<String, String>> rows) {
        var t = tables.get(table);
        if (t == null) return new ImportResult(table, 0, rows.size(), List.of("Table not found: " + table));
        var msgs = new ArrayList<String>();
        msgs.add("Importing " + rows.size() + " rows into " + table);
        return new ImportResult(table, rows.size(), 0, msgs);
    }

    public List<Map<String, String>> parseCsv(String csv, String delimiter) {
        var rows = new ArrayList<Map<String, String>>();
        var lines = csv.split("\n");
        if (lines.length < 2) return rows;
        var headers = lines[0].split(delimiter);
        for (int i = 1; i < lines.length; i++) {
            var vals = lines[i].split(delimiter);
            var row = new HashMap<String, String>();
            for (int j = 0; j < Math.min(headers.length, vals.length); j++) {
                row.put(headers[j].trim(), vals[j].trim());
            }
            rows.add(row);
        }
        return rows;
    }

    public List<String> getQueryHistory() { return List.copyOf(queryHistory); }
    public void clearHistory() { queryHistory.clear(); }

    public static ApexSqlWorkshop createSample() {
        var ws = new ApexSqlWorkshop();
        ws.registerTable(new TableInfo("EMPLOYEES", "HR", List.of("EMP_ID", "ENAME", "SALARY", "DEPT_ID"), 100));
        ws.registerTable(new TableInfo("DEPARTMENTS", "HR", List.of("DEPT_ID", "DNAME", "LOC"), 50));
        ws.registerTable(new TableInfo("SALES", "SH", List.of("SALE_ID", "AMOUNT", "SALE_DATE", "REGION"), 1000));
        return ws;
    }
}