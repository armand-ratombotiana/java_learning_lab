# Lab 01: Data Quality Framework — Implementation Guide

## Step 1: Quality Metric Model

Define a `QualityMetric` record to represent a single quality check result:

```java
public record QualityMetric(String ruleName, String column, Status status, String message, Instant timestamp) {
    public enum Status { PASS, WARN, FAIL }
}
```

## Step 2: Rule Interface

```java
@FunctionalInterface
public interface QualityRule {
    QualityMetric evaluate(String column, List<String> values);
}
```

## Step 3: Implement Rules

### NullCheckRule

```java
public record NullCheckRule(double threshold) implements QualityRule {
    public QualityMetric evaluate(String column, List<String> values) {
        long nullCount = values.stream().filter(Objects::isNull).count();
        double nullRate = (double) nullCount / values.size();
        Status s = nullRate <= threshold ? Status.PASS : (nullRate <= threshold * 1.5 ? Status.WARN : Status.FAIL);
        return new QualityMetric("NullCheck", column, s,
            "Null rate: %.2f%% (threshold: %.2f%%)".formatted(nullRate * 100, threshold * 100), Instant.now());
    }
}
```

### MinMaxCheckRule

```java
public record MinMaxCheckRule(double min, double max, boolean passOnOutside) implements QualityRule {
    public QualityMetric evaluate(String column, List<String> values) {
        var doubles = values.stream().filter(Objects::nonNull).mapToDouble(Double::parseDouble);
        double actualMin = doubles.min().orElse(Double.NaN);
        double actualMax = doubles.max().orElse(Double.NaN);
        boolean ok = actualMin >= min && actualMax <= max;
        Status s = ok == passOnOutside ? Status.PASS : Status.FAIL;
        return new QualityMetric("MinMaxCheck", column, s,
            "Range [%.2f, %.2f] expected [%.2f, %.2f]".formatted(actualMin, actualMax, min, max), Instant.now());
    }
}
```

## Step 4: Engine

```java
public class QualityEngine {
    private final List<QualityRule> rules = new ArrayList<>();
    public QualityEngine addRule(QualityRule rule) { rules.add(rule); return this; }
    public List<QualityMetric> evaluate(String column, List<String> values) {
        return rules.stream().map(r -> r.evaluate(column, values)).toList();
    }
}
```

## Step 5: Profiler

```java
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
```
