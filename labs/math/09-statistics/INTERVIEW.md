# Interview Questions on Statistics

## Easy

1. Compute mean, median, mode of an array.
2. Calculate standard deviation.
3. Explain the difference between variance and standard deviation.

## Medium

4. Implement linear regression (single variable).
5. Calculate Pearson correlation coefficient.
6. Implement Welford's online variance algorithm.
7. Explain p-value and significance level.

## Hard

8. Implement the Kolmogorov-Smirnov test.
9. Implement bootstrap resampling for confidence intervals.
10. Design an A/B testing framework with statistical power analysis.
11. Implement PCA (Principal Component Analysis) from scratch.

## Java: Bootstrap Confidence Interval

```java
public static double[] bootstrapCI(double[] data, int B, double alpha) {
    Random rng = new Random();
    double[] means = new double[B];
    int n = data.length;
    for (int b = 0; b < B; b++) {
        double sum = 0;
        for (int i = 0; i < n; i++)
            sum += data[rng.nextInt(n)];
        means[b] = sum / n;
    }
    Arrays.sort(means);
    int lo = (int) (B * alpha / 2);
    int hi = (int) (B * (1 - alpha / 2));
    return new double[]{means[lo], means[hi]};
}
```

## Java: Simple Linear Regression

```java
public record LinearRegression(double slope, double intercept, double rSquared) {
    public static LinearRegression fit(double[] x, double[] y) {
        int n = x.length;
        double mx = mean(x), my = mean(y);
        double num = 0, den = 0;
        for (int i = 0; i < n; i++) {
            num += (x[i] - mx) * (y[i] - my);
            den += (x[i] - mx) * (x[i] - mx);
        }
        double slope = num / den;
        double intercept = my - slope * mx;
        // R-squared
        double ssRes = 0, ssTot = 0;
        for (int i = 0; i < n; i++) {
            ssRes += Math.pow(y[i] - (slope * x[i] + intercept), 2);
            ssTot += Math.pow(y[i] - my, 2);
        }
        double rSquared = 1 - ssRes / ssTot;
        return new LinearRegression(slope, intercept, rSquared);
    }
}
```
