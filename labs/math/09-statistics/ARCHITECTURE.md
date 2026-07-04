# Architecture of Statistics

## Java Statistical Libraries

```
Apache Commons Math
├── DescriptiveStatistics
├── Regression (OLS, multiple)
├── Hypothesis tests (t-test, chi-squared)
├── ANOVA
├── Distribution families
└── Clustering (k-means)

Smile (Statistical Machine Intelligence & Learning Engine)
├── Classification, regression, clustering
├── Feature selection, dimensionality reduction
└── Visualization
```

## Data Analysis Pipeline

```
Raw Data → Clean → Transform → Analyze → Visualize → Report
           │                                │
           └─ Missing values, outliers      └─ Charts, tables
```

## Statistical Software Architecture

```java
interface StatModel {
    void fit(double[][] X, double[] y);
    double[] predict(double[][] X);
    double score(double[][] X, double[] y);
}

class LinearRegression implements StatModel { ... }
class LogisticRegression implements StatModel { ... }
```

## Streaming Statistics

```java
// For big data that doesn't fit in memory
interface StreamingStat {
    void observe(double value);
    double get();
}
class StreamingMean implements StreamingStat { ... }
class StreamingVariance implements StreamingStat { ... }
class StreamingQuantile implements StreamingStat { ... }
```
