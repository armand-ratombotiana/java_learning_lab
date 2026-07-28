package com.dataengineering.deep.lab01;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DataQualityFramework {

    public record QualityMetric(String ruleName, String column, Status status, String message, Instant timestamp) {
        public enum Status { PASS, WARN, FAIL }
    }

    @FunctionalInterface
    public interface QualityRule {
        QualityMetric evaluate(String column, List<String> values);
    }

    public record NullCheckRule(double threshold) implements QualityRule {
        public QualityMetric evaluate(String column, List<String> values) {
            long nullCount = values.stream().filter(Objects::isNull).count();
            double nullRate = (double) nullCount / values.size();
            QualityMetric.Status s = nullRate <= threshold ? QualityMetric.Status.PASS
                : nullRate <= threshold * 1.5 ? QualityMetric.Status.WARN : QualityMetric.Status.FAIL;
            return new QualityMetric("NullCheck", column, s,
                "Null rate: %.2f%% (threshold: %.2f%%)".formatted(nullRate * 100, threshold * 100), Instant.now());
        }
    }

    public record MinMaxCheckRule(double min, double max) implements QualityRule {
        public QualityMetric evaluate(String column, List<String> values) {
            var doubles = values.stream().filter(Objects::nonNull).mapToDouble(Double::parseDouble);
            double actualMin = doubles.min().orElse(Double.NaN);
            double actualMax = doubles.max().orElse(Double.NaN);
            boolean ok = actualMin >= min && actualMax <= max;
            return new QualityMetric("MinMaxCheck", column, ok ? QualityMetric.Status.PASS : QualityMetric.Status.FAIL,
                "Range [%.2f, %.2f] expected [%.2f, %.2f]".formatted(actualMin, actualMax, min, max), Instant.now());
        }
    }

    public record UniquenessCheckRule(double threshold) implements QualityRule {
        public QualityMetric evaluate(String column, List<String> values) {
            var nonNull = values.stream().filter(Objects::nonNull).toList();
            long distinct = nonNull.stream().distinct().count();
            double rate = (double) distinct / Math.max(nonNull.size(), 1);
            QualityMetric.Status s = rate >= threshold ? QualityMetric.Status.PASS : QualityMetric.Status.FAIL;
            return new QualityMetric("UniquenessCheck", column, s,
                "Distinct rate: %.2f%% (threshold: %.2f%%)".formatted(rate * 100, threshold * 100), Instant.now());
        }
    }

    public record PatternMatchRule(Pattern pattern) implements QualityRule {
        public QualityMetric evaluate(String column, List<String> values) {
            long matching = values.stream().filter(Objects::nonNull).filter(v -> pattern.matcher(v).matches()).count();
            double rate = (double) matching / Math.max(values.size(), 1);
            QualityMetric.Status s = rate == 1.0 ? QualityMetric.Status.PASS : QualityMetric.Status.WARN;
            return new QualityMetric("PatternMatch", column, s,
                "Pattern match rate: %.2f%%".formatted(rate * 100), Instant.now());
        }
    }

    public static class QualityEngine {
        private final List<QualityRule> rules = new ArrayList<>();
        public QualityEngine addRule(QualityRule rule) { rules.add(rule); return this; }
        public List<QualityMetric> evaluate(String column, List<String> values) {
            return rules.stream().map(r -> r.evaluate(column, values)).toList();
        }
    }

    public record ColumnProfile(String column, long count, long nullCount, long distinctCount,
                                double nullRate, double distinctRate) {
        public static ColumnProfile profile(String column, List<String> values) {
            long count = values.size();
            long nullCount = values.stream().filter(Objects::isNull).count();
            long distinctCount = values.stream().filter(Objects::nonNull).distinct().count();
            return new ColumnProfile(column, count, nullCount, distinctCount,
                (double) nullCount / count, (double) distinctCount / count);
        }
    }

    public record QualityDimension(String name, double weight, double score) {
        public double weighted() { return weight * score; }
    }

    public record CompositeScore(double overall, List<QualityMetric> failures, List<QualityDimension> dimensions) {}

    public static class CompositeQualityScorer {
        private final List<QualityDimension> dimensions = new ArrayList<>();
        public CompositeQualityScorer addDimension(String name, double weight) {
            dimensions.add(new QualityDimension(name, weight, 0));
            return this;
        }
        public CompositeScore evaluate(Map<String, List<String>> columns, Map<String, Pattern> patterns) {
            List<QualityDimension> results = new ArrayList<>();
            List<QualityMetric> failures = new ArrayList<>();
            for (var dim : dimensions) {
                double score = switch (dim.name()) {
                    case "completeness" -> completenessScore(columns.getOrDefault(dim.name(), List.of()));
                    case "uniqueness" -> uniquenessScore(columns.getOrDefault(dim.name(), List.of()));
                    case "consistency" -> {
                        var col = columns.getOrDefault(dim.name(), List.of());
                        var pat = patterns.getOrDefault(dim.name(), Pattern.compile(".*"));
                        yield consistencyScore(col, pat);
                    }
                    default -> 0;
                };
                results.add(new QualityDimension(dim.name(), dim.weight(), score));
                if (score < 80) {
                    failures.add(new QualityMetric("Composite", dim.name(), QualityMetric.Status.FAIL,
                        "Score: " + score, Instant.now()));
                }
            }
            double overall = results.stream().mapToDouble(QualityDimension::weighted).sum()
                / results.stream().mapToDouble(QualityDimension::weight).sum();
            return new CompositeScore(overall, failures, results);
        }
        private double completenessScore(List<String> values) {
            long nonNull = values.stream().filter(Objects::nonNull).count();
            return (double) nonNull / values.size() * 100;
        }
        private double uniquenessScore(List<String> values) {
            var nonNull = values.stream().filter(Objects::nonNull).toList();
            long distinct = nonNull.stream().distinct().count();
            return (double) distinct / Math.max(nonNull.size(), 1) * 100;
        }
        private double consistencyScore(List<String> values, Pattern pattern) {
            long matching = values.stream().filter(Objects::nonNull).filter(v -> pattern.matcher(v).matches()).count();
            return (double) matching / Math.max(values.size(), 1) * 100;
        }
    }

    public static void main(String[] args) {
        var data = List.of("alice", "bob", "charlie", null, "eve", "frank", null, "grace");
        var engine = new QualityEngine()
            .addRule(new NullCheckRule(0.2))
            .addRule(new UniquenessCheckRule(0.8));
        System.out.println("Quality metrics for 'name' column:");
        engine.evaluate("name", data).forEach(System.out::println);
        System.out.println("Profile: " + ColumnProfile.profile("name", data));
        System.out.println("Composite: " + new CompositeQualityScorer()
            .addDimension("completeness", 0.4)
            .addDimension("uniqueness", 0.6)
            .evaluate(Map.of("completeness", data, "uniqueness", data), Map.of()));
    }
}
