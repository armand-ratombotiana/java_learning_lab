# Interview Questions: Feature Engineering

## Conceptual

1. **What is the difference between feature selection and feature extraction?**
   - Selection: choose existing features (subset). Extraction: create new features from combinations (PCA, autoencoders).

2. **Explain the bias-variance trade-off in feature engineering.**
   - More features → lower bias (model can fit more patterns) but higher variance (overfitting risk). Feature selection/pruning manages this trade-off.

3. **When would you use target encoding vs. one-hot encoding?**
   - Target encoding: high cardinality, when the target is available and you use cross-validation to prevent leakage.
   - One-hot: low cardinality (< 20 categories), interpretability matters.

## Coding

4. **Implement a binarizer that converts a continuous column into quartile bins.**

```java
public static IntColumn binByQuantile(DoubleColumn col) {
    double q1 = col.percentile(25);
    double q2 = col.percentile(50);
    double q3 = col.percentile(75);
    IntColumn bins = IntColumn.create(col.name() + "_bin", col.size());
    for (int i = 0; i < col.size(); i++) {
        double v = col.getDouble(i);
        if (v <= q1) bins.set(i, 0);
        else if (v <= q2) bins.set(i, 1);
        else if (v <= q3) bins.set(i, 2);
        else bins.set(i, 3);
    }
    return bins;
}
```

5. **How would you detect and handle a feature that is 99% constant?**
   - `col.countUnique() < 10` for 100K rows → candidate for removal
   - Check variance: `col.stdDev() < 0.001 * col.mean()`
   - For tree models, the feature will get near-zero importance
   - Action: drop the feature or combine it with another via interaction

6. **Design a system for automated feature engineering on relational data with customer → transactions → products.**
   - Entity-level aggregations (count, sum, mean, std, min, max, most recent)
   - Time-based features (recency, frequency, monetary value — RFM)
   - Cross-entity features (ratio of customer spend to category average)
   - Use Deep Feature Synthesis or custom DSL for multi-table traversal
