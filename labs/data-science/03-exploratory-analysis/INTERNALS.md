# Internals: How Summary Statistics Are Computed

## Online vs. Offline Algorithms

Summary statistics like mean and variance can be computed in one pass (online) or two passes (offline). Java EDA libraries typically use **online** algorithms for streaming and **offline** for in-memory DataFrames.

### Welford's Online Algorithm for Variance

Computes mean and variance in a single pass, avoiding numerical cancellation:

```java
public class RunningStats {
    private long n = 0;
    private double mean = 0.0;
    private double m2 = 0.0;  // sum of squared differences from current mean
    
    public void add(double x) {
        n++;
        double delta = x - mean;
        mean += delta / n;
        m2 += delta * (x - mean);
    }
    
    public double mean() { return mean; }
    public double variance() { return n > 1 ? m2 / (n - 1) : 0; }
    public double stdDev() { return Math.sqrt(variance()); }
}
```

### Skewness and Kurtosis (Third and Fourth Moments)

```java
public class RunningMoments extends RunningStats {
    private double m3 = 0;  // third moment
    private double m4 = 0;  // fourth moment
    
    @Override
    public void add(double x) {
        double n1 = n;
        super.add(x);
        double delta = x - mean;
        double delta_n = delta / n;
        double delta_n2 = delta_n * delta_n;
        double term1 = delta * delta_n * n1;
        m3 += term1 * delta_n * (n - 2) - 3 * delta_n * m2;
        m4 += term1 * delta_n2 * (n * n - 3 * n + 3) +
              6 * delta_n2 * m2 - 4 * delta_n * m3;
    }
    
    public double skewness() {
        return Math.sqrt(n) * m3 / Math.pow(m2, 1.5);
    }
    
    public double kurtosis() {
        return n * m4 / (m2 * m2) - 3;  // excess kurtosis
    }
}
```

## Correlation Implementation

Pearson correlation uses the same running approach by tracking cross-moments:

```java
double crossM2 = 0;
// For each pair (x, y):
crossM2 += (x - meanX) * (y - meanY);
// Correlation = crossM2 / (sqrt(varX) * sqrt(varY))
```

## Quantile Computation (Percentiles)

Tablesaw uses the **T-Digest** algorithm for streaming approximate quantiles — more memory efficient than sorting the full column. It provides bounded-error percentile estimates (usually < 0.5% error) using much less memory for large datasets.
