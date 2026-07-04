# Statistics

The science of collecting, analyzing, interpreting, and presenting data.

## Scope

- Descriptive statistics: mean, median, mode, variance, standard deviation
- Data visualization: histograms, box plots, scatter plots
- Inferential statistics: confidence intervals, hypothesis testing
- Regression: linear and multiple regression
- Correlation, covariance

## Java Implementation

```java
public class Statistics {
    public static double mean(double[] data) {
        double sum = 0;
        for (double x : data) sum += x;
        return sum / data.length;
    }

    public static double variance(double[] data) {
        double mu = mean(data);
        double sumSq = 0;
        for (double x : data) sumSq += (x - mu) * (x - mu);
        return sumSq / (data.length - 1); // sample variance
    }

    public static double stddev(double[] data) {
        return Math.sqrt(variance(data));
    }
}
```
