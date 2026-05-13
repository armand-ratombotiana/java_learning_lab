# Feature Engineering Exercises

## Exercise 1-10: Scaling

### Exercise 1: Standardize Data
Standardize the array [10, 20, 30, 40, 50] using z-score normalization.

### Exercise 2: Min-Max Scaling
Scale data to [0, 1] range using min-max scaling.

### Exercise 3: Robust Scaling
Apply robust scaling using median and IQR.

### Exercise 4: Log Transform
Apply log transformation to [1, 10, 100, 1000].

### Exercise 5: Square Root Transform
Apply sqrt transformation to skewed data.

### Exercise 6: Power Transform
Apply Box-Cox transformation.

### Exercise 7: Clip Outliers
Clip values to 2 standard deviations.

### Exercise 8: Scale to Range
Scale to arbitrary range [a, b].

### Exercise 9: Inverse Scaling
Reverse standardization to get original values.

### Exercise 10: Batch Scaling
Scale multiple columns independently.

---

## Exercise 11-20: Encoding

### Exercise 11: Label Encoding
Label encode ["cat", "dog", "mouse", "cat"].

### Exercise 12: One-Hot Encoding
One-hot encode ["A", "B", "C"].

### Exercise 13: Target Encoding
Target encode categories with continuous target.

### Exercise 14: Frequency Encoding
Encode by frequency of each category.

### Exercise 15: Ordinal Encoding
Encode with meaningful order.

### Exercise 16: Binary Encoding
Encode categories as binary numbers.

### Exercise 17: Hash Encoding
Use hash function for high cardinality.

### Exercise 18: Embedding Lookup
Create embedding for categorical features.

### Exercise 19: Leave-One-Out Encoding
Implement leave-one-out target encoding.

### Exercise 20: WOE Encoding
Calculate Weight of Evidence for categories.

---

## Exercise 21-30: Imputation

### Exercise 21: Mean Imputation
Fill missing values with column mean.

### Exercise 22: Median Imputation
Use median for skewed data.

### Exercise 23: Mode Imputation
Impute categorical with mode.

### Exercise 24: Forward Fill
Propagate last valid value forward.

### Exercise 25: Backward Fill
Propagate next valid value backward.

### Exercise 26: KNN Imputation
Impute using K nearest neighbors.

### Exercise 27: Multiple Imputation
Create multiple imputed datasets.

### Exercise 28: Indicator Variables
Add missingness indicators.

### Exercise 29: Constant Imputation
Fill with domain-specific constant.

### Exercise 30: Interpolation
Use linear interpolation for time series.

---

## Exercise 31-40: Binning

### Exercise 31: Equal Width Bins
Create 5 equal-width bins for [0, 100].

### Exercise 32: Equal Frequency Bins
Create 4 quantile bins.

### Exercise 33: Custom Bins
Create domain-specific age groups.

### Exercise 34: Binned Statistics
Calculate mean target per bin.

### Exercise 35: WOE Binning
Apply WOE-based binning for target.

### Exercise 36: Decision Tree Bins
Use decision tree to find optimal splits.

### Exercise 37: Chi Merge
Merge similar bins using chi-square.

### Exercise 38: Monotonic Bins
Create monotonic binned feature.

### Exercise 39: Sparse Bins
Handle bins with few samples.

### Exercise 40: Interaction Bins
Combine multiple features into bins.

---

## Exercise 41-50: Feature Creation

### Exercise 41: Polynomial Features
Create degree-2 polynomial features.

### Exercise 42: Interaction Features
Create all pairwise interactions.

### Exercise 43: Ratio Features
Create ratio of two features.

### Exercise 44: Difference Features
Create differences between features.

### Exercise 45: Aggregation Features
Create aggregated statistics.

### Exercise 46: Date Features
Extract year, month, day, dayofweek.

### Exercise 47: Cyclical Features
Encode cyclical features with sin/cos.

### Exercise 48: Text Features
Create bag of words features.

### Exercise 49: TF-IDF Features
Create TF-IDF representation.

### Exercise 50: N-gram Features
Create bigram and trigram features.

---

## Exercise 51-60: Feature Selection

### Exercise 51: Variance Threshold
Remove low-variance features.

### Exercise 52: Correlation Filtering
Remove highly correlated features.

### Exercise 53: Forward Selection
Select features using forward selection.

### Exercise 54: Backward Elimination
Eliminate features using backward elimination.

### Exercise 55: Recursive Feature Elimination
Use RFE with a model.

### Exercise 56: L1 Regularization
Use Lasso for feature selection.

### Exercise 57: Tree Importance
Select based on tree feature importance.

### Exercise 58: Permutation Importance
Calculate permutation importance.

### Exercise 59: SHAP Values
Use SHAP for feature importance.

### Exercise 60: Domain Knowledge
Select based on domain expertise.

---

## Exercise 61-70: Dimensionality Reduction

### Exercise 61: PCA from Scratch
Implement PCA manually.

### Exercise 62: PCA Transform
Transform data to principal components.

### Exercise 63: Variance Explained
Calculate cumulative variance explained.

### Exercise 64: Choose Components
Determine optimal number of components.

### Exercise 65: Kernel PCA
Apply kernel PCA for non-linear.

### Exercise 66: t-SNE Visualization
Use t-SNE for 2D visualization.

### Exercise 67: UMAP
Apply UMAP for dimensionality reduction.

### Exercise 68: SVD
Apply singular value decomposition.

### Exercise 69: ICA
Apply independent component analysis.

### Exercise 70: Random Projection
Use random projection for fast reduction.

---

## Exercise 71-80: Advanced Transformations

### Exercise 71: PCA Reconstruction
Reconstruct data from reduced dimensions.

### Exercise 72: Feature Discretization
Discretize continuous features.

### Exercise 73: Sparse Features
Handle sparse feature representation.

### Exercise 74: Time Series Features
Create lag and lead features.

### Exercise 75: Rolling Statistics
Create rolling mean and std features.

### Exercise 76: Expanding Features
Create expanding window features.

### Exercise 77: Difference Features
Create first and second differences.

### Exercise 78: Trend Features
Fit and extract trend coefficients.

### Exercise 79: Seasonality Features
Extract seasonal components.

### Exercise 80: Fourier Features
Create Fourier basis features.

---

## Exercise 81-90: Domain-Specific

### Exercise 81: Geospatial Features
Create distance and location features.

### Exercise 82: Network Features
Create graph-based features.

### Exercise 83: Temporal Features
Create time-based features.

### Exercise 84: Categorical Interactions
Create interactions between categoricals.

### Exercise 85: Quantile Transformation
Transform to uniform/normal distribution.

### Exercise 86: Power Transformer
Apply Yeo-Johnson transformation.

### Exercise 87: Column Transformer
Apply different transformers to columns.

### Exercise 88: Feature Union
Combine multiple feature sets.

### Exercise 89: Custom Transformer
Create custom transformer class.

### Exercise 90: Feature Pipeline
Build complete feature engineering pipeline.

---

## Exercise 91-100: Projects

### Exercise 91: Credit Risk Features
Create features for credit scoring.

### Exercise 92: Customer Features
Build customer segmentation features.

### Exercise 93: Text Classification Features
Create NLP features for text classification.

### Exercise 94: Time Series Features
Build time series forecasting features.

### Exercise 95: Recommendation Features
Create collaborative filtering features.

### Exercise 96: Image Features
Create features from images.

### Exercise 97: Signal Processing Features
Extract features from signals.

### Exercise 98: Survival Analysis Features
Build features for survival analysis.

### Exercise 99: A/B Test Features
Create features for experimentation.

### Exercise 100: Production Pipeline
Build production-grade feature pipeline.