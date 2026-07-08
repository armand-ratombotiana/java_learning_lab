package com.dataeng.fifteen;

import java.util.*;

public class SchemaValidator {
    public record SchemaDiff(List<String> newColumns, List<String> missingColumns,
                              List<String> typeChanges, boolean hasBreakingChanges) {}

    public record ColumnDef(String name, String type, boolean nullable) {}

    public SchemaDiff compare(List<ColumnDef> expected, List<ColumnDef> actual) {
        Map<String, ColumnDef> expectedMap = new HashMap<>();
        Map<String, ColumnDef> actualMap = new HashMap<>();
        for (ColumnDef c : expected) expectedMap.put(c.name(), c);
        for (ColumnDef c : actual) actualMap.put(c.name(), c);

        List<String> newCols = new ArrayList<>();
        List<String> missingCols = new ArrayList<>();
        List<String> typeChanges = new ArrayList<>();

        for (String name : actualMap.keySet()) {
            if (!expectedMap.containsKey(name)) {
                newCols.add(name);
            }
        }

        for (String name : expectedMap.keySet()) {
            if (!actualMap.containsKey(name)) {
                missingCols.add(name);
            } else if (!expectedMap.get(name).type().equals(actualMap.get(name).type())) {
                typeChanges.add(name + ": " + expectedMap.get(name).type() + " -> " + actualMap.get(name).type());
            }
        }

        boolean breaking = !missingCols.isEmpty() || !typeChanges.isEmpty();
        return new SchemaDiff(newCols, missingCols, typeChanges, breaking);
    }

    public String classifyChange(SchemaDiff diff) {
        if (diff.hasBreakingChanges()) return "BREAKING";
        if (!diff.newColumns().isEmpty()) return "EVOLUTION";
        return "NO_CHANGE";
    }
}
