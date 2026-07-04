# Internals of Statistics

## Algorithms for Numeric Stability

### Welford's Online Algorithm for Variance

```java
public class RunningStats {
    private int n = 0;
    private double mean = 0, m2 = 0; // m2 = sum of (x - mean)^2

    public void add(double x) {
        n++;
        double delta = x - mean;
        mean += delta / n;
        m2 += delta * (x - mean);
    }

    public double mean() { return mean; }
    public double variance() { return m2 / (n - 1); }
    public double stddev() { return Math.sqrt(variance()); }
}
```

### Median of Medians ($O(n)$ selection)

```java
public static double median(double[] data) {
    // QuickSelect: expected O(n)
    return quickSelect(data.clone(), data.length / 2);
}
```

## Correlation

Pearson's $r$:

$$
r = \frac{\sum(x_i - \bar{x})(y_i - \bar{y})}{\sqrt{\sum(x_i - \bar{x})^2 \sum(y_i - \bar{y})^2}}
$$

```java
public static double pearsonCorrelation(double[] x, double[] y) {
    double mx = mean(x), my = mean(y);
    double num = 0, denX = 0, denY = 0;
    int n = x.length;
    for (int i = 0; i < n; i++) {
        double dx = x[i] - mx, dy = y[i] - my;
        num += dx * dy;
        denX += dx * dx;
        denY += dy * dy;
    }
    return num / Math.sqrt(denX * denY);
}
```

## Quantile Estimation

Use the **P² algorithm** (Jain & Chlamtac) for streaming quantile estimation without storing data.
