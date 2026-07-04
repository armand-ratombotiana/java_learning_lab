# Common Mistakes in Exploratory Data Analysis

## 1. Only Looking at Means

Reporting only averages hides distributions. Two very different datasets can have the same mean.

**Fix**: Always report median + IQR alongside mean, and visualize the distribution.

## 2. Ignoring Missing Data Patterns

Checking `countMissing()` is not enough. Missingness may be systematic.

**Fix**: Cross-tabulate missingness against other columns.

```java
// Check: do missing salary values cluster in a particular department?
Table missingByDept = data.where(data.column("salary").isMissing())
    .groupBy("department").count();
```

## 3. Chasing Every Correlation

With 100 columns, you get ~5000 pairwise correlations. At p < 0.05, ~250 will be "significant" by chance.

**Fix**: Use Bonferroni correction, or better, use mutual information and rank by magnitude. Focus on correlations r > |0.5|.

## 4. Not Segmenting

Global averages hide group differences. "The average response rate is 5%" hides that Group A responds at 2% and Group B at 20%.

**Fix**: Always segment by at least one meaningful categorical variable.

## 5. Over-Interpreting Small Samples

1000 data points with one outlier can show r = 0.4 that vanishes when the outlier is removed.

**Fix**: Always check influential points. Use Spearman (rank-based) as a robustness check.

## 6. Confirmation Bias in EDA

Looking for patterns that confirm what you already believe, ignoring contradictory evidence.

**Fix**: Formally note surprising findings. Share raw EDA outputs with colleagues before stating conclusions.
