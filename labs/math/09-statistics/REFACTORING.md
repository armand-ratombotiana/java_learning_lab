# Refactoring Statistics Code

## Create a Dedicated Statistics Class

```java
// BEFORE: scattered calculations
double m = 0;
for (double x : data) m += x;
m /= data.length;

double v = 0;
for (double x : data) v += (x - m) * (x - m);
v /= (data.length - 1);

// AFTER
public class Sample {
    private final double[] data;
    public Sample(double[] data) { this.data = data.clone(); }
    public double mean() { return Arrays.stream(data).average().orElse(0); }
    public double variance() {
        double m = mean();
        return Arrays.stream(data).map(x -> (x-m)*(x-m)).sum() / (data.length-1);
    }
    public double stddev() { return Math.sqrt(variance()); }
}
```

## Use Commons Math

```java
// INSTEAD of implementing from scratch
DescriptiveStatistics stats = new DescriptiveStatistics(data);
double mean = stats.getMean();
double std = stats.getStandardDeviation();
double skew = stats.getSkewness();
double kurt = stats.getKurtosis();
```

## Stream-Based Pipelines

```java
// Use streams for efficient processing
double mean = Arrays.stream(data).summaryStatistics().getAverage();
```
