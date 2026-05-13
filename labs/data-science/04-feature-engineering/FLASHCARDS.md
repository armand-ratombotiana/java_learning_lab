# Feature Engineering Flashcards

## Scaling

### Card 1: Standardization
**Q:** Formula and use case for z-score standardization?
**A:** z = (x - mean) / std; use when features have different scales and algorithm is sensitive to scale (regression, NN).

### Card 2: Min-Max Scaling
**Q:** Formula and use case for Min-Max scaling?
**A:** x' = (x - min) / (max - min); use when bounded output needed, e.g., neural networks.

### Card 3: Robust Scaling
**Q:** Formula and use case for robust scaling?
**A:** x' = (x - median) / IQR; use when data has outliers.

### Card 4: Log Transform
**Q:** When use log transformation?
**A:** For right-skewed data with positive values to reduce skewness and stabilize variance.

### Card 5: Power Transform
**Q:** When use Box-Cox or Yeo-Johnson?
**A:** To make data more Gaussian; Box-Cox for positive, Yeo-Johnson for any value.

### Card 6: Clipping
**Q:** What is clipping and when use it?
**A:** Limiting extreme values to percentile thresholds; prevents outlier influence.

### Card 7: When NOT to Scale
**Q:** When might you skip scaling?
**A:** Tree-based models (decision trees, random forest, gradient boosting) - they're scale-invariant.

### Card 8: Batch Effect
**Q:** What causes batch effects and how address?
**A:** Systematic differences between data sources; use standardization within batches or remove batch effect.

---

## Encoding

### Card 9: Label Encoding
**Q:** When use label encoding vs one-hot?
**A:** Label encoding for ordinal categories or tree-based models; one-hot for nominal low-cardinality.

### Card 10: Target Encoding
**Q:** Formula for target encoding?
**A:** category_mean * (count / (count + smoothing)) + global_mean * (smoothing / (count + smoothing))

### Card 11: Frequency Encoding
**Q:** When use frequency encoding?
**A:** For high cardinality features where frequency correlates with target.

### Card 12: WOE
**Q:** Formula for Weight of Evidence?
**A:** WOE = ln((distribution of goods) / (distribution of bads)); used in credit scoring.

### Card 13: Embedding
**Q:** When use categorical embeddings?
**A:** For very high cardinality categories in neural networks.

### Card 14: Missing as Category
**Q:** When treat missing as own category?
**A:** When missingness is informative (e.g., "prefer not to answer").

---

## Imputation

### Card 15: Mean Imputation
**Q:** When appropriate for mean imputation?
**A:** Missing at random, symmetric distribution, low missing percentage.

### Card 16: KNN Imputation
**Q:** How does KNN imputation work?
**A:** Find K nearest rows (by other features) and average their values for missing feature.

### Card 17: Iterative Imputer
**Q:** What is iterative imputation?
**A:** Model each feature with missing values as a function of other features; iterate until convergence.

### Card 18: When NOT to Impute
**Q:** When might you not impute?
**A:** When missingness is high (>50%), non-random, or represents important signal.

### Card 19: Multiple Imputation
**Q:** Purpose of multiple imputation?
**A:** Captures uncertainty from missing data by creating multiple complete datasets and combining results.

---

## Binning

### Card 20: Equal Width Bins
**Q:** Formula and when use equal width bins?
**A:** bin_width = (max - min) / n; use for uniform distributions, simple interpretation.

### Card 21: Equal Frequency Bins
**Q:** When use equal frequency (quantile) bins?
**A:** When you want equal number of observations per bin; handles skewed data better.

### Card 22: Decision Tree Bins
**Q:** Why use decision tree for binning?
**A:** Finds optimal splits that maximize information gain with respect to target.

### Card 23: WOE Binning
**Q:** Purpose of WOE-based binning?
**A:** Creates bins with discriminative power for binary classification.

### Card 24: Monotonic Bins
**Q:** Why enforce monotonic relationship?
**A:** Ensures feature has consistent relationship with target for interpretability.

---

## Feature Creation

### Card 25: Polynomial Features
**Q:** When create polynomial features?
**A:** When relationship between feature and target is non-linear; capture interactions.

### Card 26: Interaction Features
**Q:** What are interaction features?
**A:** Products, ratios, or other combinations of two or more features.

### Card 27: Ratio Features
**Q:** When are ratio features useful?
**A:** To create scale-invariant comparisons (e.g., debt-to-income ratio).

### Card 28: Cyclical Encoding
**Q:** Why encode cyclical features with sin/cos?
**A:** Preserves cyclical nature (e.g., 11pm close to 1am) for distance-based models.

### Card 29: DateTime Features
**Q:** Common date features to extract?
**A:** Year, month, day, day_of_week, hour, quarter, is_weekend, days_since_reference.

### Card 30: Aggregation Features
**Q:** When create aggregated features?
**A:** For group-level statistics (e.g., customer lifetime value, average purchase amount).

---

## Feature Selection

### Card 31: Variance Threshold
**Q:** Formula and when use variance threshold?
**A:** Remove features with variance below threshold; use for constant/near-constant features.

### Card 32: Correlation Filtering
**Q:** How does correlation-based selection work?
**A:** Remove one of highly correlated feature pair (|r| > threshold) to reduce multicollinearity.

### Card 33: Forward Selection
**Q:** How does forward selection work?
**A:** Start with no features, add one that improves model most, repeat until no improvement.

### Card 34: L1 Regularization
**Q:** How does Lasso do feature selection?
**A:** Shrinks some coefficients to exactly zero, effectively selecting important features.

### Card 35: Feature Importance
**Q:** What is permutation importance?
**A:** Shuffle feature values and measure increase in prediction error; higher = more important.

### Card 36: SHAP Values
**Q:** What do SHAP values show?
**A:** Contribution of each feature to each prediction; can be aggregated for global importance.

---

## Dimensionality Reduction

### Card 37: PCA
**Q:** What does PCA maximize?
**A:** Variance along principal components (linear combinations of original features).

### Card 38: Explained Variance
**Q:** How determine number of PCA components?
**A:** Choose number that explains desired variance (e.g., 95%) or use elbow in scree plot.

### Card 39: t-SNE
**Q:** When use t-SNE?
**A:** For 2D/3D visualization of high-dimensional data; preserves local structure.

### Card 40: UMAP
**Q:** Advantages of UMAP over t-SNE?
**A:** Faster, preserves global structure better, works in higher dimensions.

### Card 41: When NOT to Reduce
**Q:** When avoid dimensionality reduction?
**A:** When interpretability is critical, or when features already low-dimensional.

---

## Advanced

### Card 42: Feature Hashing
**Q:** When use feature hashing?
**A:** For streaming data or very high dimensional text data; trades accuracy for speed.

### Card 43: Target Leakage
**Q:** What is target leakage?
**A:** Using information that wouldn't be available at prediction time; causes overfitting.

### Card 44: Temporal Features
**Q:** Common temporal features?
**A:** Lag features, rolling statistics (mean, std), date components, time since event.

### Card 45: Cross Validation
**Q:** When use grouped CV for feature selection?
**A:** When data has groups (e.g., patients, users) to ensure same group doesn't appear in train/test.

### Card 46: Feature Stability
**Q:** How check feature stability?
**A:** Compare feature importance across different samples or time periods.

### Card 47: Domain Knowledge
**Q:** Why is domain knowledge important?
**A:** Helps create meaningful features, avoid leakage, select relevant features.

### Card 48: Automated Feature Engineering
**Q:** Tools for automated feature engineering?
**A:** Featuretools, automated ML platforms; still need domain expertise to evaluate.

---

## Best Practices

### Card 49: Pipeline
**Q:** Why use feature engineering pipeline?
**A:** Reproducibility, prevents data leakage, easier deployment.

### Card 50: Reproducibility
**Q:** Key to reproducible feature engineering?
**A:** Use deterministic transformations, save encoders, log all parameters.