# Math Foundation: Key Statistics for Data Wrangling

## 1. Z-Score Normalization

Maps data to a distribution with mean = 0 and standard deviation = 1.

$$ z = \frac{x - \mu}{\sigma} $$

```java
public DoubleColumn zScore(DoubleColumn col) {
    double mean = col.mean();
    double std = col.stdDev();
    double[] result = new double[col.size()];
    for (int i = 0; i < col.size(); i++) {
        result[i] = (col.getDouble(i) - mean) / std;
    }
    return DoubleColumn.create("z_" + col.name(), result);
}
```

## 2. Min-Max Normalization

Scales values to range [0, 1].

$$ x' = \frac{x - x_{min}}{x_{max} - x_{min}} $$

```java
public DoubleColumn minMaxScale(DoubleColumn col) {
    double min = col.min();
    double max = col.max();
    if (Math.abs(max - min) < 1e-10) {
        return DoubleColumn.create("scaled_" + col.name(), 
            Collections.nCopies(col.size(), 0.5));
    }
    return col.subtract(min).divide(max - min).setName("scaled_" + col.name());
}
```

## 3. IQR for Outlier Detection

$$ IQR = Q3 - Q1 $$
$$ Outlier = x < Q1 - 1.5 \times IQR \text{ or } x > Q3 + 1.5 \times IQR $$

```java
public Selection outlierMask(DoubleColumn col) {
    double q1 = col.quartile(1);
    double q3 = col.quartile(3);
    double iqr = q3 - q1;
    double lower = q1 - 1.5 * iqr;
    double upper = q3 + 1.5 * iqr;
    return col.isGreaterThan(upper).or(col.isLessThan(lower));
}
```

## 4. Imputation: Mean vs. Median

| Method | Use Case | Robustness |
|---|---|---|
| Mean | Symmetric, no outliers | Not robust (one outlier shifts mean) |
| Median | Skewed distributions | Robust |
| Mode | Categorical data | Appropriate for labels |

```java
public double imputeValue(DoubleColumn col) {
    double skew = col.skewness();
    return Math.abs(skew) > 1.0 ? col.median() : col.mean();
}
```
