package com.databases.queryopt;

import java.util.*;
import java.util.regex.Pattern;

public class QueryPlanAnalyzer {
    private static final Set<String> FULL_SCAN = Set.of("seq scan", "full scan", "table scan");
    private static final Set<String> INDEX_SCAN = Set.of("index scan", "index only scan", "index lookup");

    public record QueryPlan(String query, String rawPlan, double estimatedCost, double estimatedRows,
        List<String> scanTypes, List<String> warnings) {

        public static QueryPlan parse(String query, String rawPlan) {
            double cost = parseCost(rawPlan);
            double rows = parseRows(rawPlan);
            var scans = analyzeScans(rawPlan);
            var warns = generateWarnings(query, rawPlan);
            return new QueryPlan(query, rawPlan, cost, rows, scans, warns);
        }

        public boolean hasTableScan() {
            return scanTypes.stream().anyMatch(s -> FULL_SCAN.stream().anyMatch(s::contains));
        }

        public boolean hasIndexScan() {
            return scanTypes.stream().anyMatch(s -> INDEX_SCAN.stream().anyMatch(s::contains));
        }

        public String generateSummary() {
            var sb = new StringBuilder();
            sb.append("Query: ").append(query).append("\n");
            sb.append("Cost: ").append(estimatedCost).append("\n");
            sb.append("Rows: ").append(estimatedRows).append("\n");
            sb.append("Scans: ").append(String.join(", ", scanTypes)).append("\n");
            if (!warnings.isEmpty()) {
                sb.append("Warnings:\n");
                warnings.forEach(w -> sb.append("  - ").append(w).append("\n"));
            }
            return sb.toString();
        }

        private static double parseCost(String plan) {
            var m = Pattern.compile("cost=(\\d+\\.?\\d*)").matcher(plan);
            return m.find() ? Double.parseDouble(m.group(1)) : -1;
        }

        private static double parseRows(String plan) {
            var m = Pattern.compile("rows=(\\d+)").matcher(plan);
            return m.find() ? Double.parseDouble(m.group(1)) : -1;
        }

        private static List<String> analyzeScans(String plan) {
            List<String> s = new ArrayList<>();
            String lower = plan.toLowerCase();
            for (String i : INDEX_SCAN) { if (lower.contains(i)) s.add(i); }
            for (String i : FULL_SCAN) { if (lower.contains(i)) s.add(i); }
            return s;
        }

        private static List<String> generateWarnings(String query, String plan) {
            List<String> w = new ArrayList<>();
            if (FULL_SCAN.stream().anyMatch(s -> plan.toLowerCase().contains(s)))
                w.add("Table scan - consider adding an index");
            if (Pattern.compile("\\bOR\\b", Pattern.CASE_INSENSITIVE).matcher(query).find())
                w.add("OR clause may prevent index usage");
            if (Pattern.compile("LIKE\\s+'%", Pattern.CASE_INSENSITIVE).matcher(query).find())
                w.add("Leading wildcard LIKE prevents index usage");
            return w;
        }
    }
}
