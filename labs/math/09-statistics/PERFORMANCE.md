# Statistics Performance

## Single-Pass vs Two-Pass Algorithms

| Statistic | Single-Pass | Two-Pass |
|-----------|-------------|----------|
| Mean | Yes | Yes |
| Variance | Yes (Welford) | Yes (standard) |
| Min/Max | Yes | Yes |
| Median | No (needs sort) | Yes |
| Quantiles | No (P² algorithm) | Yes |

## Welford's Algorithm (Single-Pass)

```java
// O(n) time, O(1) memory, numerically stable
public class WelfordVariance {
    private long count = 0;
    private double mean = 0, m2 = 0;

    public void update(double x) {
        count++;
        double delta = x - mean;
        mean += delta / count;
        m2 += delta * (x - mean);
    }

    public double variance() { return m2 / count; }
}
```

## Parallel Statistics with Streams

```java
DoubleSummaryStatistics stats = Arrays.stream(data).parallel()
    .collect(DoubleSummaryStatistics::new,
             DoubleSummaryStatistics::accept,
             DoubleSummaryStatistics::combine);
```

## Approximate Quantiles (T-Digest)

For large datasets, use T-Digest (1% error with 100x less memory than storing all data).
