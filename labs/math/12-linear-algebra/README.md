# Linear Algebra

Vectors, matrices, eigenvalues, SVD, and linear transformations — the language of data science and machine learning.

## Scope

- Vector operations: addition, subtraction, scaling, dot product, cross product
- Matrix operations: addition, multiplication, transpose, determinant, inverse
- Gaussian elimination with partial pivoting for solving linear systems
- Eigenvalue decomposition via power iteration
- QR decomposition via Gram-Schmidt orthogonalization
- Singular Value Decomposition (SVD) for 2×2 matrices
- Linear transformations and their matrix representations

## Java Implementation

```java
package com.math12;

public class LinearAlgebra {
    public static double[] addVectors(double[] a, double[] b) { /* ... */ }
    public static double[][] multiplyMatrices(double[][] a, double[][] b) { /* ... */ }
    public static double determinant(double[][] matrix) { /* ... */ }
    public static double[][] inverse(double[][] matrix) { /* ... */ }
    public static double[] solveLinearSystem(double[][] a, double[] b) { /* ... */ }
    public static EigenDecomposition powerIteration(double[][] matrix, int iterations) { /* ... */ }
    public static double[][] gramSchmidtQR(double[][] matrix) { /* ... */ }
}
```

## Key Topics

- Vector spaces, subspaces, basis, and dimension
- Linear independence and spanning sets
- Matrix multiplication properties (associative, not commutative)
- Determinants and their geometric interpretation
- Eigenvalues and eigenvectors: definition and computation
- Matrix decompositions: QR, SVD
- Linear systems: consistency, uniqueness, condition number
- Applications: PCA, linear regression, PageRank

## Prerequisites

- Algebra fundamentals
- Basic calculus (for gradient-based methods)

## How to Use This Lab

1. Read THEORY.md for comprehensive mathematical treatment
2. Review MATH_FOUNDATION.md for prerequisite review
3. Study CODE_DEEP_DIVE.md for implementation patterns
4. Complete EXERCISES.md problem sets
5. Build the MINI_PROJECT for hands-on application
6. Challenge yourself with the REAL_WORLD_PROJECT

## Time Estimate

- Theory study: 2.5 hours
- Code implementation: 3 hours
- Exercises: 2 hours
- Projects: 3 hours
- **Total: ~10.5 hours**

## Difficulty: ★★★★☆ (Advanced)
