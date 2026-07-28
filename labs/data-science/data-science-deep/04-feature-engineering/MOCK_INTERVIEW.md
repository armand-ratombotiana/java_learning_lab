# Mock Interview: Feature Engineering

**Interviewer**: You're given a dataset with 500 features and 10,000 rows. How do you approach feature engineering?

**Candidate**: First, I'd split into train/test immediately to avoid data leakage. Then I'd profile: check missing rates, zero variance features, and correlation clusters. For numeric features, I'd examine distributions (skew, outliers). For categoricals, check cardinality. I'd use recursive feature elimination with cross-validation (RFECV) to identify the most predictive features, then focus engineering efforts there.

**Interviewer**: 50 of the 500 features are high-cardinality categoricals (>100 categories each). How do you encode them?

**Candidate**: One-hot would create 5000+ columns — too sparse. I'd use target encoding with cross-validation to prevent leakage. For each categorical, I'd compute category-specific target means smoothed toward the global mean, using only training folds. I'd also consider frequency encoding as a simpler alternative. If cardinality is extremely high (>1000), I'd first aggregate rare categories into "other" (top-100 categories), then apply target encoding.

**Interviewer**: After encoding, you have 600 features. Many are correlated. How do you handle this?

**Candidate**: Correlation-based filtering: compute pairwise Pearson correlations, and for any pair with |r| > 0.95, drop one feature (keep the one with higher variance or higher mutual information with the target). Then I'd apply L1 regularization (Lasso) to drive irrelevant coefficients to zero. Finally, I'd use permutation importance on a validation set to confirm selected features are actually predictive.

**Interviewer**: Let's code. Implement variance threshold feature selection.

**Candidate**:
```java
public boolean[] varianceThreshold(double[][] X, double threshold) {
    int p = X[0].length;
    boolean[] keep = new boolean[p];
    for (int j = 0; j < p; j++) {
        double[] col = new double[X.length];
        for (int i = 0; i < X.length; i++) col[i] = X[i][j];
        double mean = Arrays.stream(col).average().orElseThrow();
        double var = Arrays.stream(col).map(v -> Math.pow(v - mean, 2)).sum() / (col.length - 1);
        keep[j] = var >= threshold;
    }
    return keep;
}
```

**Interviewer**: When would variance thresholding be a bad idea?

**Candidate**: When features are on different scales. A feature ranging [0, 1] will have low variance even if it's highly predictive. I always standardize features before applying variance thresholding. Also, a feature could have high variance but be pure noise — variance isn't the same as predictive power. That's why I'd follow with a supervised selector (mutual information or model-based).
