# Linear Algebra for ML - Interview Questions

## Fundamentals

### Q1: Explain the difference between a vector and a matrix
Answer: A vector is a 1D array of numbers representing a point in n-dimensional space. A matrix is a 2D array with rows and columns representing linear transformations or collections of vectors.

### Q2: What is linear independence?
Answer: Vectors are linearly independent if no vector can be expressed as a linear combination of the others. Mathematically: c1*v1 + ... + cn*vn = 0 only if all ci = 0.

### Q3: What is the rank of a matrix?
Answer: The maximum number of linearly independent rows (or columns). Equals the dimension of the column/row space. Can be computed via row reduction or SVD.

### Q4: Explain the four fundamental subspaces
Answer: For A in R^m x n:
- Column space: span of A's columns (subset of R^m)
- Nullspace: solutions to Ax = 0 (subset of R^n)
- Row space: span of A's rows (subset of R^n)
- Left nullspace: solutions to y^T A = 0 (subset of R^m)
Dimensions: rank, n-rank, rank, m-rank

---

## Matrix Operations

### Q5: When is AB = BA?
Answer: Generally never (not commutative). Exceptions:
- Diagonal matrices commute
- Identity commutes with all
- A and f(A) where f is polynomial
- AB = BA if both are powers of same matrix

### Q6: How do you compute matrix inverse?
Answer: A^-1 = adj(A)/det(A) or via Gaussian elimination. For large matrices, use LU decomposition. Conditions: matrix must be non-singular (det != 0).

### Q7: What is the complexity of matrix multiplication?
Answer: O(n^3) for n x n matrices using naive algorithm. Strassen: O(n^2.807). Optimized libraries use blocked algorithms and cache optimization.

### Q8: Explain the Woodbury identity
Answer: (A + UCV)^-1 = A^-1 - A^-1 U (C^-1 + V A^-1 U)^-1 V A^-1
Used for efficient inverse of rank-k updated matrices.

---

## Eigenvalues and Eigenvectors

### Q9: What is the relationship between eigenvalues and determinant?
Answer: det(A) = product of eigenvalues (including complex). If det = 0, at least one eigenvalue is zero (matrix is singular).

### Q10: How do you find eigenvalues numerically?
Answer: 
- Power iteration: dominant eigenvalue
- QR algorithm: all eigenvalues
- For symmetric matrices: bisection on characteristic polynomial

### Q11: What makes symmetric matrices special?
Answer:
- All eigenvalues are real
- Eigenvectors can be chosen orthonormal
- Spectral theorem: A = Q Lambda Q^T
- Always diagonalizable

### Q12: What is a defective matrix?
Answer: A matrix that cannot be diagonalized. Has fewer than n linearly independent eigenvectors. Example: [[1,1],[0,1]] has only one eigenvector.

---

## SVD and Decompositions

### Q13: Explain SVD and its applications
Answer: A = U Sigma V^T where U, V are orthonormal, Sigma is diagonal with singular values.
Applications:
- Dimensionality reduction (truncated SVD)
- Pseudoinverse: A+ = V Sigma+ U^T
- PCA via covariance matrix
- Image compression

### Q14: When would you use SVD over eigendecomposition?
Answer:
- When matrix is not square
- When matrix is not symmetric (SVD always exists)
- For numerical stability (avoids computing XX^T)
- In recommendation systems (collaborative filtering)

### Q15: What is the condition number and why does it matter?
Answer: cond(A) = sigma_max / sigma_min
- Measures numerical stability
- High condition number means ill-conditioned
- Affects accuracy of linear system solutions
- Related to singular values

### Q16: Explain LU, QR, and Cholesky decompositions
Answer:
- LU: A = LU (L lower, U upper) - for general square matrices
- QR: A = QR (Q orthogonal, R upper) - for least squares
- Cholesky: A = LL^T (L lower triangular) - for SPD matrices, faster than LU

---

## Linear Systems

### Q17: How do you solve Ax = b efficiently for multiple b?
Answer: Use LU decomposition once: solve L*y = b, then U*x = y. Pre-compute L, U to solve any b quickly.

### Q18: What is the normal equation and when is it used?
Answer: A^T A x = A^T b
Used in linear regression when n > m (more equations than unknowns). Solution: x = (A^T A)^-1 A^T b
Requires A^T A to be invertible (full column rank).

### Q19: When should you use iterative solvers?
Answer:
- For very large sparse systems
- When direct methods are too slow/memory intensive
- Conjugate gradient for SPD matrices
- GMRES for non-symmetric systems

### Q20: What is regularization and how does it affect the solution?
Answer: Adds lambda*I to A^T A, making it invertible even when rank-deficient.
Ridge: x = (A^T A + lambda*I)^-1 A^T b
Lasso: Uses L1 penalty, requires iterative solvers

---

## Machine Learning Applications

### Q21: How is linear algebra used in neural networks?
Answer:
- Forward pass: matrix multiplications (Y = XW + b)
- Backpropagation: gradient computations via chain rule
- Weight matrices: W in R^n_l x n_{l-1}
- Activations stored as matrices/vectors

### Q22: Explain PCA from linear algebra perspective
Answer:
1. Center data: X' = X - mean
2. Compute covariance: C = X'^T X' / (n-1)
3. Eigendecomposition: C = Q Lambda Q^T
4. Project: Y = X' Q_k
Principal components = eigenvectors of covariance matrix

### Q23: How does SVD help in recommendation systems?
Answer: User-item matrix R is decomposed: R = U Sigma V^T
Predicted ratings: R_ij = U_i * Sigma * V_j^T
Truncated SVD keeps top k components for efficiency and noise reduction.

### Q24: What is the role of linear algebra in optimization?
Answer:
- Gradient descent: vector operations
- Newton method: Hessian matrix
- Conjugate gradient: matrix-vector products
-KKT conditions: linear systems in constrained optimization

### Q25: How do you handle very large matrices that dont fit in memory?
Answer:
- Sparse representations (COO, CSR, CSC)
- Distributed computing (MapReduce, Spark)
- Randomized algorithms (randomized SVD)
- Out-of-core computation
- Use specialized libraries (Eigen, SuiteSparse)

---

## Coding Questions

### Q26: Write code to multiply two matrices
```java
Matrix multiply(Matrix A, Matrix B) {
    Matrix C = new Matrix(A.rows, B.cols);
    for i in rows:
        for j in cols:
            for k in A.cols:
                C[i][j] += A[i][k] * B[k][j];
    return C;
}
```

### Q27: Implement power iteration
```java
Vector powerIteration(Matrix A, int maxIter) {
    Vector v = randomUnitVector(A.cols);
    for i in range(maxIter):
        v = A.multiply(v);
        v = v.normalize();
    return v;
}
```

### Q28: How do you compute pseudoinverse?
Answer: Using SVD:
1. Compute A = U Sigma V^T
2. Take reciprocals of non-zero singular values
3. A+ = V Sigma+ U^T

### Q29: Implement Gaussian elimination
```java
Vector gaussianSolve(Matrix A, Vector b) {
    // Forward elimination with partial pivoting
    // Back substitution
}
```

### Q30: Explain how you would parallelize matrix multiplication
Answer:
- Divide into blocks
- Each thread computes one block: C[i][j] = sum_k A[i][k] * B[k][j]
- Use thread pools or GPU
- Consider memory locality for cache efficiency