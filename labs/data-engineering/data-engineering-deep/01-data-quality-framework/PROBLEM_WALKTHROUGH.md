# Lab 01: Problem Walkthrough — Build a Composite Quality Score

## Problem Statement

Implement a `CompositeQualityScorer` that evaluates a dataset across 4 dimensions: completeness, uniqueness, consistency, and timeliness. Return a normalized score (0-100) and a list of failed rules.

## Walkthrough

### Step 1: Define the Scoring Model

```java
public record QualityDimension(String name, double weight, double score) {
    public double weighted() { return weight * score; }
}

public record CompositeScore(double overall, List<QualityMetric> failures, List<QualityDimension> dimensions) {}
```

### Step 2: Implement Completeness

```java
public double completenessScore(List<String> values) {
    long nonNull = values.stream().filter(Objects::nonNull).count();
    return (double) nonNull / values.size() * 100;
}
```

### Step 3: Implement Uniqueness

```java
public double uniquenessScore(List<String> values) {
    var nonNull = values.stream().filter(Objects::nonNull).toList();
    long distinct = nonNull.stream().distinct().count();
    return (double) distinct / Math.max(nonNull.size(), 1) * 100;
}
```

### Step 4: Implement Consistency

```java
public double consistencyScore(List<String> values, Pattern pattern) {
    long matching = values.stream().filter(Objects::nonNull).filter(v -> pattern.matcher(v).matches()).count();
    return (double) matching / Math.max(values.size(), 1) * 100;
}
```

### Step 5: Implement Timeliness

```java
public double timelinessScore(List<Instant> timestamps, Duration maxAge) {
    Instant cutoff = Instant.now().minus(maxAge);
    long fresh = timestamps.stream().filter(t -> t.isAfter(cutoff)).count();
    return (double) fresh / timestamps.size() * 100;
}
```

### Step 6: Composite Calculator

```java
public class CompositeQualityScorer {
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
            if (score < 80) failures.add(new QualityMetric("Composite", dim.name(), FAIL, "Score: " + score, Instant.now()));
        }

        double overall = results.stream().mapToDouble(QualityDimension::weighted).sum()
            / results.stream().mapToDouble(QualityDimension::weight).sum();
        return new CompositeScore(overall, failures, results);
    }
}
```

## Complexity

- **Time**: O(N) per column per dimension
- **Space**: O(D) where D = number of dimensions
