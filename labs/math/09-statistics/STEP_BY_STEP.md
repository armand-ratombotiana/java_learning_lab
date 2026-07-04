# Step-by-Step: Statistics in Java

## Compute Descriptive Statistics

```java
public class DescriptiveStats {
    private final double[] data;

    public DescriptiveStats(double[] data) {
        this.data = data.clone();
        Arrays.sort(this.data);
    }

    public double mean() {
        return Arrays.stream(data).average().orElse(0);
    }

    public double median() {
        int n = data.length;
        if (n % 2 == 1) return data[n / 2];
        return (data[n / 2 - 1] + data[n / 2]) / 2.0;
    }

    public double[] quartiles() {
        return new double[]{data[data.length / 4],
                            data[data.length / 2],
                            data[3 * data.length / 4]};
    }

    public double stddev() {
        double mu = mean();
        return Math.sqrt(Arrays.stream(data)
            .map(x -> (x - mu) * (x - mu))
            .average().orElse(0));
    }
}
```

## Two-Sample t-Test

```java
public static double tTest(double[] sample1, double[] sample2) {
    double m1 = mean(sample1), m2 = mean(sample2);
    double v1 = variance(sample1), v2 = variance(sample2);
    int n1 = sample1.length, n2 = sample2.length;
    double t = (m1 - m2) / Math.sqrt(v1/n1 + v2/n2);
    return t; // compare with t-distribution critical values
}
```

## Z-Score Normalization

```java
public static double[] zScoreNormalize(double[] data) {
    double mu = mean(data);
    double sigma = stddev(data);
    return Arrays.stream(data)
        .map(x -> (x - mu) / sigma)
        .toArray();
}
```
