# Exercises: Linear Algebra

## Theoretical Exercises

### Problem 1: Vector Spaces
Prove that the set of all n×n symmetric matrices forms a vector space. What is its dimension?

### Problem 2: Linear Independence
Determine whether the vectors v₁ = (1,2,3), v₂ = (4,5,6), v₃ = (7,8,10) are linearly independent.

### Problem 3: Matrix Multiplication
Prove that matrix multiplication is associative but not commutative. Provide a counterexample.

### Problem 4: Determinant Properties
Prove that det(AB) = det(A)det(B) for 2×2 matrices.

### Problem 5: Eigenvalues
Show that a 2×2 matrix A has eigenvalues λ satisfying λ² - tr(A)λ + det(A) = 0.

### Problem 6: Gram-Schmidt
Apply the Gram-Schmidt process to orthogonalize {(1,1,0), (1,0,1), (0,1,1)}.

### Problem 7: SVD
Explain how SVD relates to PCA. What do the singular values represent?

### Problem 8: Condition Number
Explain why a large condition number makes a linear system difficult to solve numerically.

## Programming Exercises

### Exercise 1: Vector Operations
Implement vector addition, subtraction, scalar multiplication, dot product, and cross product.

### Exercise 2: Matrix Operations
Implement matrix addition, multiplication, transpose, and trace.

### Exercise 3: Gaussian Elimination
Implement Gaussian elimination with partial pivoting to solve linear systems.

### Exercise 4: Determinant
Implement determinant calculation for n×n matrices using Laplace expansion.

### Exercise 5: Matrix Inverse
Implement matrix inversion using the adjugate method. Handle singular matrices gracefully.

### Exercise 6: Eigenvalue Approximation
Implement the power iteration method for finding the dominant eigenvalue.

### Exercise 7: QR Decomposition
Implement Gram-Schmidt QR decomposition. Verify that Q is orthogonal (QᵀQ = I).

### Exercise 8: SVD for 2×2
Implement SVD for 2×2 matrices. Test on various matrices.

### Exercise 9: Linear Regression
Use the normal equation (XᵀX)⁻¹Xᵀy to solve a linear regression problem.

### Exercise 10: PageRank
Implement a simplified PageRank algorithm using power iteration on a stochastic matrix.

## Mini-Project: 3D Graphics Engine
Build a simple 3D graphics engine that uses transformation matrices for rotation, translation, and scaling. Render a wireframe cube with perspective projection.

## Real-World Project: Recommender System
Build a collaborative filtering recommender system using SVD. Implement matrix completion and rank-k approximation for predicting user ratings on a movie dataset.
