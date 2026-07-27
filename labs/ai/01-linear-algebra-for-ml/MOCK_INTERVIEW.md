# Mock Interview: Linear Algebra for ML

## Interview Context
This mock interview tests linear algebra fundamentals critical for ML interviews at top companies (Google, Meta, OpenAI).

## Question 1: Matrix Operations in ML
**Q**: How would you implement matrix multiplication efficiently, and where is it used in ML?

**Answer**: Matrix multiplication is the core operation in neural networks (forward pass: Wx + b). Efficient implementation uses:
- Cache blocking (tiling for L1/L2 cache)
- SIMD vectorization
- Strassen algorithm for large matrices (O(n^2.81))
- cuBLAS for GPU (uses tensor cores)

**Follow-up**: Derive the gradient of matrix multiplication for backpropagation.
```
dL/dW = dL/dy * x^T
dL/dx = W^T * dL/dy
```

## Question 2: Eigendecomposition & PCA
**Q**: Explain how eigendecomposition relates to PCA. Implement PCA computation.

**A**: PCA finds directions of maximum variance.
1. Center data: X - mean(X)
2. Compute covariance: C = (1/n) * X^T * X
3. Eigendecompose C: C = V * Lambda * V^T
4. Top k eigenvectors = principal components

```python
def pca(X, k):
    X_centered = X - np.mean(X, axis=0)
    cov = (X_centered.T @ X_centered) / (X.shape[0] - 1)
    eigenvalues, eigenvectors = np.linalg.eigh(cov)
    idx = np.argsort(eigenvalues)[::-1][:k]
    return eigenvectors[:, idx]
```

## Question 3: SVD in ML
**Q**: What is the SVD and where is it used in ML?

**A**: SVD decomposes X = U * Sigma * V^T.
Applications:
- Dimensionality reduction (PCA via SVD is more numerically stable)
- Matrix completion (collaborative filtering)
- Low-rank approximation
- Data whitening
- Latent semantic analysis (LSA)

## Question 4: Vector Spaces & Norms
**Q**: Explain L1, L2, and L-infinity norms and their roles in regularization.

**A**: 
- L2: ||w||_2 = sqrt(sum(w_i^2)). Ridge regression. Shrinks weights proportionally.
- L1: ||w||_1 = sum(|w_i|). Lasso regression. Produces sparsity (feature selection).
- L-infinity: ||w||_inf = max(|w_i|). Used in adversarial robustness.

## Question 5: Matrix Calculus
**Q**: Compute the gradient of f(w) = ||Xw - y||^2_2 with respect to w.

**A**:
f(w) = (Xw - y)^T * (Xw - y)
df/dw = 2X^T * (Xw - y)

**Follow-up**: What's the closed-form solution for linear regression?
w* = (X^T * X)^{-1} * X^T * y

## Preparation Tips
- Practice matrix operations by hand (3x3)
- Understand when SVD is preferred over eigendecomposition
- Know the relationship between covariance, correlation, and PCA
- Be comfortable with matrix calculus (gradients, chain rule)
