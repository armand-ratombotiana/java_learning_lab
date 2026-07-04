# Interview Questions: EDA

## Conceptual

1. **You run `summary()` on a dataset and everything looks normal. What's the next thing you check?**
   - Group by a meaningful categorical column and compare statistics. Simpson's paradox hides in aggregate.

2. **What is Simpson's paradox and why does it matter for EDA?**
   - A trend that appears in grouped data reverses when groups are combined. E.g., UC Berkeley gender bias case: each department admitted women at similar rates, but aggregate showed bias because women applied to more competitive departments.

3. **How do you detect a non-linear relationship in EDA without ML techniques?**
   - Scatter plot + LOESS smoothed trend line
   - Compare Pearson (linear) vs Spearman (monotonic) correlation — big gap suggests non-linearity
   - Mutual information — captures any dependency

## Coding

4. **Write Java code to detect which numeric columns have a high proportion of outliers.**

```java
public static List<String> findOutlierColumns(Table data, double threshold) {
    List<String> result = new ArrayList<>();
    for (String col : data.numericColumnNames()) {
        DoubleColumn c = data.doubleColumn(col);
        double q1 = c.percentile(25);
        double q3 = c.percentile(75);
        double iqr = q3 - q1;
        long outlierCount = c.where(
            c.isLessThan(q1 - 1.5 * iqr).or(c.isGreaterThan(q3 + 1.5 * iqr))
        ).size();
        double ratio = (double) outlierCount / c.size();
        if (ratio > threshold) {
            result.add(col + " (" + String.format("%.1f%%", ratio * 100) + ")");
        }
    }
    return result;
}
```

5. **How would you automate the detection of data quality issues in a new dataset?**
   - Schema validation (column presence, types, nullability)
   - Range checks (age between 0 and 120, salary > 0)
   - Unique constraint checks (primary keys)
   - Distribution shift detection (compare to historical profile)
   - Row count anomaly detection
