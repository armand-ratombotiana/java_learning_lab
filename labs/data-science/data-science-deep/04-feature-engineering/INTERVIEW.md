# Feature Engineering — Interview Questions

### Q1: Categorical Encoding Choice
**Q**: Compare one-hot vs target encoding for a high-cardinality feature (1000+ categories).

**A**: One-hot creates 1000+ columns — memory-heavy and sparse. Target encoding collapses to a single column but risks target leakage if not done with cross-validation. For high cardinality: use frequency encoding (count/mean of category frequency), target encoding with smoothing and cross-validation, or embedding-based methods (entity embeddings for neural nets). One-hot with sparse storage works for models that handle sparsity well (linear models, trees with categorical support).

### Q2: Feature Importance Interpretation
**Q**: A feature has high variance but the model ignores it. Why?

**A**: (1) Variance is not predictive — noise, not signal. (2) The feature is correlated with another more predictive feature (redundancy). (3) The relationship is nonlinear and the model (e.g., linear model) can't capture it. (4) The feature requires transformation (log, interaction, or scaling).

### Q3: Automated Feature Engineering
**Q**: Design a system that automatically engineers features for tabular data.

**A**: The system should: (1) Profile each column (type, cardinality, missing %, distribution). (2) For numeric: generate transformations (log, sqrt, square, reciprocal, Box-Cox), binning (equal-width, equal-frequency), polynomial features (degree 2). (3) For categorical: frequency encoding, target encoding (with CV), one-hot (for low cardinality). (4) For datetime: extract hour, day, month, quarter, day-of-week, is-weekend, days-since-event, rolling statistics. (5) Generate interactions between top-k important features. (6) Apply feature selection (variance threshold, correlation filter, mutual information). Validate each feature's contribution via forward selection or permutation importance.

### Q4: Interaction Detection
**Q**: How do you automatically detect important feature interactions without domain knowledge?

**A**: (1) Tree-based: examine split paths in gradient boosted trees (feature pairs that co-occur in splits). (2) Friedman's H-statistic: measures how much of the prediction variance is explained by the interaction. (3) Exhaustive pairwise interactions for p < 50; for larger p, use Lasso on all pairwise products. (4) Factorization machines or FTRL models with feature crosses.

## Coding

### Q5: Mutual Information
```java
public double mutualInformation(double[] x, double[] y, int bins) {
    double[] xDisc = discretize(x, bins);
    double[] yDisc = discretize(y, bins);
    double[][] joint = new double[bins][bins];
    double[] margX = new double[bins], margY = new double[bins];
    int n = xDisc.length;
    for (int i = 0; i < n; i++) {
        int xi = (int) xDisc[i], yi = (int) yDisc[i];
        joint[xi][yi]++; margX[xi]++; margY[yi]++;
    }
    double mi = 0;
    for (int i = 0; i < bins; i++) {
        for (int j = 0; j < bins; j++) {
            if (joint[i][j] > 0) {
                mi += joint[i][j] / n * Math.log((joint[i][j] * n) / (margX[i] * margY[j]));
            }
        }
    }
    return mi;
}
```
