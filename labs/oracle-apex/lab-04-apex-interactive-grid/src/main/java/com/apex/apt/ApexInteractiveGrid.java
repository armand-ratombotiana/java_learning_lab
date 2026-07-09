package com.apex.apt;

import java.util.*;

public class ApexInteractiveGrid {
    public record ColumnDef(String name, String type, boolean sortable, boolean aggregatable, boolean editable) {}
    public record Aggregation(String column, String function) {}
    public record ControlBreak(String column, boolean showAvg) {}
    public record GridConfig(String sql, List<ColumnDef> columns, List<Aggregation> aggregations, List<ControlBreak> breaks) {}

    private final Map<String, GridConfig> configs = new LinkedHashMap<>();
    private final List<Map<String, Object>> data = new ArrayList<>();

    public void registerGrid(String name, GridConfig config) { configs.put(name, config); }

    public GridConfig getConfig(String name) { return configs.get(name); }

    public void loadData(List<Map<String, Object>> rows) { data.clear(); data.addAll(rows); }

    public List<Map<String, Object>> getData() { return List.copyOf(data); }

    public Map<String, Object> getAggregations() {
        var result = new LinkedHashMap<String, Object>();
        for (var config : configs.values()) {
            for (var agg : config.aggregations()) {
                var vals = data.stream().map(r -> r.get(agg.column()))
                    .filter(v -> v instanceof Number).map(v -> ((Number)v).doubleValue()).toList();
                double colAgg = switch (agg.function().toUpperCase()) {
                    case "SUM" -> vals.stream().mapToDouble(Double::doubleValue).sum();
                    case "AVG" -> vals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    case "MIN" -> vals.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                    case "MAX" -> vals.stream().mapToDouble(Double::doubleValue).max().orElse(0);
                    case "COUNT" -> vals.size();
                    default -> 0;
                };
                result.put(agg.column() + "_" + agg.function(), colAgg);
            }
        }
        return result;
    }

    public List<List<Map<String, Object>>> groupByColumn(String column) {
        var groups = new LinkedHashMap<Object, List<Map<String, Object>>>();
        for (var row : data) {
            var key = row.get(column);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        return List.copyOf(groups.values());
    }

    public boolean updateCell(int rowIdx, String column, Object value) {
        if (rowIdx < 0 || rowIdx >= data.size()) return false;
        if (!data.get(rowIdx).containsKey(column)) return false;
        data.get(rowIdx).put(column, value);
        return true;
    }

    public int addRow(Map<String, Object> row) {
        data.add(row);
        return data.size();
    }

    public boolean deleteRow(int rowIdx) {
        if (rowIdx < 0 || rowIdx >= data.size()) return false;
        data.remove(rowIdx);
        return true;
    }

    public static ApexInteractiveGrid createSample() {
        var grid = new ApexInteractiveGrid();
        var cols = List.of(
            new ColumnDef("EMP_ID", "NUMBER", true, false, false),
            new ColumnDef("ENAME", "VARCHAR2", true, false, true),
            new ColumnDef("SALARY", "NUMBER", true, true, true),
            new ColumnDef("DEPT_ID", "NUMBER", true, false, true)
        );
        grid.registerGrid("EMPLOYEES_IG", new GridConfig("SELECT * FROM EMPLOYEES", cols,
            List.of(new Aggregation("SALARY", "SUM"), new Aggregation("SALARY", "AVG")),
            List.of(new ControlBreak("DEPT_ID", true))));
        grid.loadData(List.of(
            Map.of("EMP_ID", 100, "ENAME", "King", "SALARY", 24000, "DEPT_ID", 10),
            Map.of("EMP_ID", 101, "ENAME", "Kochhar", "SALARY", 17000, "DEPT_ID", 20),
            Map.of("EMP_ID", 102, "ENAME", "De Haan", "SALARY", 17000, "DEPT_ID", 20)
        ));
        return grid;
    }
}