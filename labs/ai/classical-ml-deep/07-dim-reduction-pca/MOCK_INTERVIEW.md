# Mock Interview: PCA via SVD

**Topic:** Derive PCA using SVD — explain variance, reconstruction, whitening

## Core Questions

### Q1: Derive PCA.

**Answer:**
**Goal:** Find directions of maximum variance in data.

Let $X \in \mathbb{R}^{n \times d}$ be centered (zero-mean).

Find $w$ maximizing variance of projected data: $\max_{w} w^T \Sigma w$ s.t. $\|w\|=1$

Using Lagrangian: $\mathcal{L} = w^T \Sigma w - \lambda(w^T w - 1)$
$\frac{\partial \mathcal{L}}{\partial w} = 2\Sigma w - 2\lambda w = 0 \Rightarrow \Sigma w = \lambda w$

$w$ is an eigenvector of $\Sigma = \frac{1}{n} X^T X$, $\lambda$ is the corresponding eigenvalue.

First principal component = eigenvector with largest eigenvalue.

### Q2: Relate PCA to SVD.

**Answer:**
SVD of centered $X$: $X = U \Sigma V^T$

Where $U \in \mathbb{R}^{n \times r}$, $\Sigma \in \mathbb{R}^{r \times r}$, $V \in \mathbb{R}^{d \times r}$, $r = \text{rank}(X)$.

- Columns of $V$ are right singular vectors = principal components (eigenvectors of $X^T X$)
- Singular values $\sigma_i$: $\sigma_i^2 = \lambda_i \cdot n$ (eigenvalues of $X^T X$)
- Projected data (scores): $T = XV = U \Sigma$

**Why SVD is preferred:** Numerically stable, avoids computing $X^T X$, handles $n < d$ naturally.

### Q3: How do you choose the number of components?

**Answer:**
Proportion of variance explained by $k$ components:
$\frac{\sum_{i=1}^k \lambda_i}{\sum_{i=1}^d \lambda_i} = \frac{\sum_{i=1}^k \sigma_i^2}{\sum_{i=1}^d \sigma_i^2}$

**Methods:**
- **Elbow plot:** Look for knee in cumulative variance curve
- **Threshold:** Choose $k$ such that $>90\%$ (or $95\%$) variance retained
- **Kaiser rule:** Keep components with eigenvalue $>1$ (for correlation-based PCA)
- **Cross-validation:** Measure reconstruction error on held-out data

### Q4: Explain reconstruction and whitening.

**Answer:**
**Reconstruction:** $\hat{X} = T_k V_k^T = X V_k V_k^T$

Reconstruction error: $\|X - \hat{X}\|_F^2 = \sum_{i=k+1}^d \sigma_i^2$ (sum of discarded singular values)

**Whitening:** Transforms data to have identity covariance.

ZCA whitening: $X_{\text{white}} = X V \Sigma^{-1} V^T$ (rotates back to original axes)
PCA whitening: $X_{\text{white}} = X V \Sigma^{-1} = U$ (decorrelated, unit variance)

Applications: Preprocessing for ICA, improving convergence of neural nets.

## Advanced

- **Kernel PCA:** Apply kernel trick, eigendecompose kernel matrix $K$
- **Incremental PCA:** For streaming/large data, update SVD block by block
- **Sparse PCA:** Add L1 penalty for interpretable components
- **Probabilistic PCA:** Generative model with isotropic noise — PPCA as special case of factor analysis
