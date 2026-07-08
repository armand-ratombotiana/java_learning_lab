package com.dataeng.fifteen;

import java.util.*;
import java.util.stream.*;

public class QualityCheckEngine {
    private final List<QualityRule> rules = new ArrayList<>();
    private final AlertService alertService;

    public QualityCheckEngine(AlertService alertService) { this.alertService = alertService; }

    public void addRule(QualityRule rule) { rules.add(rule); }

    public QualityReport execute(String datasetName, List<Map<String, Object>> data) {
        QualityReport report = new QualityReport(datasetName, System.currentTimeMillis());
        for (QualityRule rule : rules) {
            RuleResult result = rule.evaluate(data);
            report.addResult(result);
            if (!result.passed() && rule.severity() >= 8) {
                alertService.sendAlert(datasetName, result);
            }
        }
        return report;
    }

    public record QualityReport(String datasetName, long timestamp, List<RuleResult> results) {
        public QualityReport(String datasetName, long timestamp) {
            this(datasetName, timestamp, new ArrayList<>());
        }
        public void addResult(RuleResult r) { results.add(r); }
        public double overallScore() {
            return results.stream().mapToDouble(RuleResult::score).average().orElse(0);
        }
        public boolean allPassed() { return results.stream().allMatch(RuleResult::passed); }
    }

    public record RuleResult(String ruleName, boolean passed, double score, String details) {}
    public record QualityRule(String name, int severity, java.util.function.Function<List<Map<String, Object>>, RuleResult> check) {
        public RuleResult evaluate(List<Map<String, Object>> data) { return check.apply(data); }
    }

    public interface AlertService {
        void sendAlert(String dataset, RuleResult result);
    }

    public static QualityRule notNullRule(String columnName, int severity) {
        return new QualityRule("not_null_" + columnName, severity, data -> {
            long total = data.size();
            long nulls = data.stream().filter(row -> row.get(columnName) == null).count();
            double nullRate = total > 0 ? (double) nulls / total : 0;
            return new RuleResult("not_null_" + columnName, nullRate < 0.05, (1 - nullRate) * 100,
                nulls + "/" + total + " nulls in " + columnName);
        });
    }

    public static QualityRule uniqueRule(String columnName, int severity) {
        return new QualityRule("unique_" + columnName, severity, data -> {
            long total = data.size();
            long distinct = data.stream().map(row -> row.get(columnName)).distinct().count();
            double uniqueRate = total > 0 ? (double) distinct / total : 0;
            return new RuleResult("unique_" + columnName, uniqueRate > 0.99, uniqueRate * 100,
                distinct + "/" + total + " distinct in " + columnName);
        });
    }

    public static QualityRule rangeRule(String columnName, double min, double max, int severity) {
        return new QualityRule("range_" + columnName, severity, data -> {
            long violations = data.stream()
                .map(row -> row.get(columnName))
                .filter(v -> v instanceof Number)
                .map(v -> ((Number) v).doubleValue())
                .filter(v -> v < min || v > max)
                .count();
            long total = data.size();
            double passRate = total > 0 ? 1 - (double) violations / total : 0;
            return new RuleResult("range_" + columnName, violations == 0, passRate * 100,
                violations + " values outside [" + min + ", " + max + "]");
        });
    }
}
