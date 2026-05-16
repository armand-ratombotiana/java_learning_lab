# Dimensionality Reduction - Flashcards

### Card 1: PCA Objective
**Q:** What does PCA maximize?
**A:** PCA finds directions (principal components) that maximize variance of projected data.

### Card 2: PCA Process
**Q:** What are the main steps of PCA?
**A:** 1) Center data (subtract mean), 2) Compute covariance matrix, 3) Find eigenvalues/eigenvectors, 4) Select top k components, 5) Project data.

### Card 3: Supervised vs Unsupervised
**Q:** Is PCA supervised or unsupervised? How about LDA?
**A:** PCA is unsupervised (no labels). LDA is supervised (uses class labels to maximize separability).

### Card 4: Number of Components
**Q:** How do you determine number of components?
**A:** Choose k such that cumulative explained variance > 90-95%, or use scree plot elbow.

### Card 5: LDA Purpose
**Q:** What is the goal of LDA?
**A:** LDA finds projections that maximize between-class variance relative to within-class variance.

### Card 6: t-SNE
**Q:** What is t-SNE good for?
**A:** High-dimensional data visualization - preserves local neighborhoods but not global structure.

### Card 7: Standardization
**Q:** When should you standardize before PCA?
**A:** Always, when features have different scales, to prevent dominant features.

### Card 8: Curse of Dimensionality
**Q:** What is the curse of dimensionality?
**A:** In high dimensions, distances become less meaningful, data becomes sparse, requiring more samples.

### Card 9: Feature vs Dimensionality Reduction
**Q:** What's the difference?
**A:** Feature selection picks subset of original features. Dimensionality reduction creates new features.

### Card 10: Kernel PCA
**Q:** What does kernel PCA do?
**A:** Uses kernel trick to perform non-linear dimensionality reduction.

### Card 11: LDA Limitation
**Q:** What is a key limitation of LDA?
**A:** Only creates at most (c-1) components where c is number of classes.

### Card 12: SVD and PCA
**Q:** How are SVD and PCA related?
**A:** SVD can compute PCA efficiently; V (right singular vectors) are PCA principal components.