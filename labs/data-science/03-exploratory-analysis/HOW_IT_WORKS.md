# How Exploratory Data Analysis Works

## The EDA Loop

```
   Load Data
      │
      ▼
  ┌──────────────┐
  │  Question    │───→ Generate hypotheses
  │  the Data    │
  └──────┬───────┘
         │
         ▼
  ┌──────────────┐
  │  Visualize   │───→ Plot distributions, relationships
  └──────┬───────┘
         │
         ▼
  ┌──────────────┐
  │  Compute     │───→ Summary stats, correlations
  │  Statistics  │
  └──────┬───────┘
         │
         ▼
  ┌──────────────┐
  │  Interpret   │───→ Did we answer the question?
  └──────┬───────┘
         │
    ┌────┴────┐
    │         │
    Yes       No ──→ back to question
    │
    ▼
   Next Question
```

## Key Techniques

### Univariate Analysis (one column)

```java
// Numeric: distribution shape
DoubleColumn age = data.doubleColumn("age");
System.out.println("Skewness: " + age.skewness());   // >1 = right-skewed
System.out.println("Kurtosis: " + age.kurtosis());   // >3 = heavy tails
System.out.println("Percentiles: " + age.percentile(25) + ", " +
    age.percentile(50) + ", " + age.percentile(75));

// Categorical: frequency
Table freq = data.groupBy("region").count();
freq.sortDescending("Count");
```

### Bivariate Analysis (two columns)

```java
// Numeric-Numeric: Pearson/Spearman correlation
double r = Smile.correlation(x, y);

// Categorical-Numeric: group stats
Table stats = data.groupBy("region").mean("salary");
stats.addColumn("std", data.groupBy("region").stdDev("salary"));

// Categorical-Categorical: contingency table (Chi-squared)
double chi2 = Smile.chiSquareTest(observed, expected);
```

### Multivariate Analysis (many columns)

```java
// Correlation matrix
double[][] corr = Smile.correlation(columns);
// Pass to heatmap renderer

// Parallel coordinates (for small datasets)
// Each row is a line; each column is an axis
// Useful for finding multi-column patterns
```
