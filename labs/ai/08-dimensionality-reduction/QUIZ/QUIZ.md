# Dimensionality Reduction - Quiz

### Question 1
What is the main purpose of dimensionality reduction?

A) Increase the number of features
B) Reduce computational cost and combat the curse of dimensionality
C) Make data more complex
D) Add more noise to the data

**Answer: B** - Dimensionality reduction reduces the number of features while preserving important information, helping with computational cost and the curse of dimensionality.

---

### Question 2
What does PCA maximize?

A) Between-class variance
B) Sum of squared distances to nearest neighbors
C) Variance of projected data
D) Cluster separation

**Answer: C** - PCA finds orthogonal directions (principal components) that maximize the variance of the projected data.

---

### Question 3
In PCA, what is a principal component?

A) Original feature
B) Linear combination of original features that captures maximum variance
C) The mean of the data
D) A cluster center

**Answer: B** - Principal components are orthogonal linear combinations of original features, ordered by the amount of variance they explain.

---

### Question 4
What is the difference between PCA and LDA?

A) PCA is supervised, LDA is unsupervised
B) PCA maximizes variance, LDA maximizes class separability
C) Both maximize variance
D) Both are unsupervised

**Answer: B** - PCA is unsupervised (doesn't use labels), LDA is supervised and maximizes class discrimination.

---

### Question 5
What is t-SNE primarily used for?

A) Linear dimensionality reduction
B) Data visualization (especially high-dimensional data)
C) Feature selection
D) Data compression

**Answer: B** - t-SNE is a non-linear technique primarily used for visualization, preserving local structure.

---

### Question 6
How do you choose the number of components in PCA?

A) Always use all components
B) Use explained variance threshold (e.g., 95%) or scree plot elbow
C) Use test data
D) Random selection

**Answer: B** - Choose based on explained variance (cumulative > 90-95%) or look for elbow in scree plot.

---

### Question 7
What is the curse of dimensionality?

A) Too few features causing underfitting
B) With many features, data becomes sparse and distances become less meaningful
C) Too many clusters needed
D) Overfitting due to too many samples

**Answer: B** - In high dimensions, data points become equidistant, making distance-based methods unreliable.

---

### Question 8
What is a limitation of t-SNE?

A) Cannot preserve global structure
B) Always preserves exact distances
C) Is linear
D) Cannot be used for visualization

**Answer: A** - t-SNE focuses on preserving local neighborhoods, not global structure.

---

### Question 9
What does LDA stand for?

A) Linear Discriminant Analysis
B) Latent Dirichlet Allocation
C) Linear Dimensionality Analysis
D) Local Data Aggregation

**Answer: A** - Linear Discriminant Analysis is a supervised dimensionality reduction technique.

---

### Question 10
When should you standardize data before PCA?

A) Only for classification tasks
B) When features have different scales
C) Never
D) Only for visualization

**Answer: B** - Standardization is crucial when features have different scales, otherwise features with larger ranges dominate.

---

### Question 11 (Bonus)
What is kernel PCA?

A) PCA using kernel trick for non-linear dimensionality reduction
B) PCA with fast computation
C) PCA for classification
D) PCA with multiple kernels

**Answer: A** - Kernel PCA applies the kernel trick to PCA, enabling non-linear feature extraction.

---

### Question 12 (Bonus)
What is the relationship between SVD and PCA?

A) They are completely different
B) PCA can be computed using SVD, SVD provides the principal components
C) SVD is a type of PCA
D) PCA is faster than SVD always

**Answer: B** - Singular Value Decomposition (SVD) can compute PCA efficiently and provides the principal components.