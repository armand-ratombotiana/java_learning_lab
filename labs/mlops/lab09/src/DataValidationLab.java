package com.mlops.lab09;

import java.util.*;
import java.util.stream.*;

/**
 * Data Validation & Quality — Lab 09.
 * <p>
 * Implements data validation concepts inspired by Great Expectations and Deequ.
 * Supports schema checks, null ratio thresholds, value range bounds,
 * distribution comparisons, and generates validation reports.
 */
public class DataValidationLab {

    /** Result of a single validation expectation. */
    static class ExpectationResult {
        final String expectation;
        final boolean passed;
        final double observed;
        final double threshold;
        final String details;

        ExpectationResult(String expectation, boolean passed, double observed,
                           double threshold, String details) {
            this.expectation = expectation;
            this.passed = passed;
            this.observed = observed;
            this.threshold = threshold;
            this.details = details;
        }

        String toMarkdown() {
            String icon = passed ? "✓" : "✗";
            return String.format("  %s %s (observed=%.2f, threshold=%.2f) %s",
                    icon, expectation, observed, threshold, details);
        }
    }

    /** Lightweight DataFrame-like structure for validation. */
    static class Dataset {
        final String name;
        final Map<String, List<Object>> columns = new LinkedHashMap<>();
        int rowCount;

        Dataset(String name) { this.name = name; }

        void addColumn(String name, Object... values) {
            columns.put(name, new ArrayList<>(Arrays.asList(values)));
            rowCount = Math.max(rowCount, values.length);
        }

        <T> List<T> getColumn(String name) {
            @SuppressWarnings("unchecked")
            List<T> col = (List<T>) columns.get(name);
            return col;
        }
    }

    /** Validates datasets against expectations. */
    static class DataValidator {
        private final List<ExpectationResult> results = new ArrayList<>();

        void expectColumnToExist(Dataset ds, String column) {
            boolean exists = ds.columns.containsKey(column);
            results.add(new ExpectationResult(
                    "expect_column_to_exist: " + column,
                    exists, exists ? 1.0 : 0.0, 1.0, ""));
        }

        void expectColumnNullRatioLessThan(Dataset ds, String column, double maxNullRatio) {
            List<Object> col = ds.getColumn(column);
            if (col == null) {
                results.add(new ExpectationResult(
                        "expect_column_null_ratio: " + column,
                        false, 1.0, maxNullRatio, "Column not found"));
                return;
            }
            long nullCount = col.stream().filter(Objects::isNull).count();
            double nullRatio = (double) nullCount / col.size();
            results.add(new ExpectationResult(
                    "expect_column_null_ratio_less_than: " + column,
                    nullRatio <= maxNullRatio, nullRatio, maxNullRatio,
                    String.format("(%d/%d null)", nullCount, col.size())));
        }

        void expectColumnValuesBetween(Dataset ds, String column, Number min, Number max) {
            List<Number> col = ds.getColumn(column);
            if (col == null) {
                results.add(new ExpectationResult(
                        "expect_column_values_between: " + column,
                        false, 0, 1, "Column not found"));
                return;
            }
            long outOfRange = col.stream()
                    .filter(Objects::nonNull)
                    .filter(v -> v.doubleValue() < min.doubleValue()
                            || v.doubleValue() > max.doubleValue())
                    .count();
            double violationRatio = (double) outOfRange / col.size();
            results.add(new ExpectationResult(
                    "expect_column_values_between: " + column,
                    violationRatio == 0, violationRatio, 0,
                    String.format("(%d/%d out of [%.1f, %.1f])",
                            outOfRange, col.size(), min, max)));
        }

        void expectColumnValuesUnique(Dataset ds, String column) {
            List<Object> col = ds.getColumn(column);
            if (col == null) return;
            long uniqueCount = col.stream().filter(Objects::nonNull).distinct().count();
            boolean allUnique = uniqueCount == col.stream().filter(Objects::nonNull).count();
            results.add(new ExpectationResult(
                    "expect_column_values_unique: " + column,
                    allUnique, uniqueCount, col.size(), ""));
        }

        void expectColumnValueDistribution(Dataset ds, String column,
                                            Map<Object, Double> expectedDistribution,
                                            double maxKLDivergence) {
            List<Object> col = ds.getColumn(column);
            if (col == null) return;
            long total = col.stream().filter(Objects::nonNull).count();
            if (total == 0) return;
            double klDiv = 0.0;
            for (Map.Entry<Object, Double> entry : expectedDistribution.entrySet()) {
                long actualCount = col.stream().filter(v -> entry.getKey().equals(v)).count();
                double actualP = (double) actualCount / total;
                double expectedP = entry.getValue();
                if (actualP > 0 && expectedP > 0) {
                    klDiv += actualP * Math.log(actualP / expectedP);
                }
            }
            results.add(new ExpectationResult(
                    "expect_column_distribution_kl_divergence: " + column,
                    klDiv <= maxKLDivergence, klDiv, maxKLDivergence, ""));
        }

        void printReport() {
            System.out.printf("Data Validation Report: %d expectations run%n", results.size());
            int passed = (int) results.stream().filter(r -> r.passed).count();
            System.out.printf("Passed: %d / %d%n%n", passed, results.size());
            for (ExpectationResult r : results) {
                System.out.println(r.toMarkdown());
            }
            System.out.printf("%n✓ Critical threshold: %s%n",
                    passed == results.size() ? "ALL PASSED" : "SOME FAILED");
        }

        boolean allPassed() {
            return results.stream().allMatch(r -> r.passed);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Data Validation & Quality ===\n");

        Dataset ds = new Dataset("customer_features");
        ds.addColumn("user_id", "u1", "u2", "u3", "u4", "u5");
        ds.addColumn("age", 25, 34, 45, null, 28);
        ds.addColumn("income", 55000, 72000, 95000, 48000, 120000);
        ds.addColumn("credit_score", 720, 680, 750, 710, 600);
        ds.addColumn("risk_tier", "low", "medium", "low", "high", "medium");
        ds.addColumn("transaction_count_7d", 12, 5, 25, 8, 150);

        DataValidator validator = new DataValidator();

        // Define expectations (validation suite)
        validator.expectColumnToExist(ds, "user_id");
        validator.expectColumnToExist(ds, "age");
        validator.expectColumnToExist(ds, "income");
        validator.expectColumnToExist(ds, "nonexistent_column");

        validator.expectColumnNullRatioLessThan(ds, "age", 0.05);
        validator.expectColumnNullRatioLessThan(ds, "user_id", 0.0);

        validator.expectColumnValuesBetween(ds, "age", 18, 100);
        validator.expectColumnValuesBetween(ds, "credit_score", 300, 850);
        validator.expectColumnValuesBetween(ds, "transaction_count_7d", 0, 100);

        validator.expectColumnValuesUnique(ds, "user_id");

        Map<Object, Double> riskDistribution = Map.of(
                "low", 0.4, "medium", 0.4, "high", 0.2);
        validator.expectColumnValueDistribution(ds, "risk_tier", riskDistribution, 0.5);

        System.out.println("Validating dataset: " + ds.name);
        System.out.printf("Columns: %s, Rows: %d%n%n", ds.columns.keySet(), ds.rowCount);
        validator.printReport();

        System.out.printf("%nValidation %s%n",
                validator.allPassed() ? "PASSED ✓" : "FAILED — check report above");
    }
}
