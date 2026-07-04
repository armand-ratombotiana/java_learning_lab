# Why Feature Engineering Matters

Feature engineering is often the difference between a good model and a great one. Andrew Ng's observation — *"Coming up with features is difficult, time-consuming, requires expert knowledge. Applied machine learning is basically feature engineering"* — captures its centrality.

## Impact on Model Performance

| Approach | Accuracy | Interpretability | Effort |
|---|---|---|---|
| Raw features only | 72% | High | Low |
| + Feature engineering | 86% | Medium | Medium |
| + Deep learning (raw features) | 84% | Low | High |
| + Feature engineering + deep learning | 91% | Low | Very high |

## Feature Engineering vs. Deep Learning

Deep learning automates feature extraction for unstructured data (images, audio, text). For structured/tabular data (which dominates enterprise ML), hand-crafted features still outperform raw inputs, even with gradient-boosted trees.

```java
// Feature engineering for tabular data often beats raw input to tree models
public class FeaturePipeline {
    public static Table engineerFeatures(Table raw) {
        return raw
            .addColumn("age_squared", raw.doubleColumn("age").multiply(raw.doubleColumn("age")))
            .addColumn("income_per_capita", raw.doubleColumn("household_income")
                .divide(raw.doubleColumn("household_size")))
            .addColumn("log_revenue", applyLog(raw.doubleColumn("revenue")))
            .addColumn("is_weekend", raw.dateColumn("date").dayOfWeek()
                .isGreaterThan(5));
    }
}
```
