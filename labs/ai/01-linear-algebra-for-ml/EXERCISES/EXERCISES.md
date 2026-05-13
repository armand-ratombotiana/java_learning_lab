# Linear Algebra for ML - Exercises

## Exercise Set 1: Vector Operations

### Exercise 1.1
Given vectors a = [1, 2, 3] and b = [4, 5, 6], compute:
- a) a + b
- b) 2a - b
- c) a dot b
- d) L2 norm of a
- e) Cosine similarity

**Answer**: [5,7,9], [-2,-1,0], 32, sqrt(14), 0.974

### Exercise 1.2
Prove that L1 norm satisfies triangle inequality.

### Exercise 1.3
Given u = [1,1] and v = [1,-1], show orthogonal and normalize.

### Exercise 1.4
Implement projection in Java.

### Exercise 1.5
Standardize x = [100, 50, 25, 10]. Compute L1 and L2 norms.

---

## Exercise Set 2: Matrix Operations

### Exercise 2.1
For A = [[1,2],[3,4]] and B = [[5,6],[7,8]], compute:
A + B, 3A, AB, BA, Atranspose

### Exercise 2.2
Prove (AB)C = A(BC)

### Exercise 2.3
For A = [[2,1],[1,2]], compute det, inverse, verify.

### Exercise 2.4
Implement matrix multiplication (naive, block, Strassen).

### Exercise 2.5
95% sparse 100x100 matrix storage comparison.

---

## Exercise Set 3: Linear Systems

### Exercise 3.1
Solve with A = [[1,1,1],[2,1,3],[1,2,1]], b = [6,11,8]

### Exercise 3.2
A = [[4,3],[2,1]], b = [10,6]: compute inverse and solve.

### Exercise 3.3
Implement Gaussian elimination with partial pivoting.

### Exercise 3.4
Solve overdetermined system by least squares.

### Exercise 3.5
When to prefer LU over direct inversion?

---

## Exercise Set 4: Eigenvalues

### Exercise 4.1
Find eigenvalues of A = [[3,1],[1,3]].

### Exercise 4.2
Prove eigenvectors of symmetric matrix are orthogonal.

### Exercise 4.3
Show A = [[1,2],[0,1]] is defective.

### Exercise 4.4
Implement power iteration in Java.

### Exercise 4.5
Find stationary distribution of P = [[0.9,0.1],[0.5,0.5]].

---

## Exercise Set 5: SVD

### Exercise 5.1
For A = [[1,0],[0,2]], find SVD, rank, pseudoinverse.

### Exercise 5.2
A = [[1,2],[3,4],[5,6]]: rank and truncated SVD k=1.

### Exercise 5.3
Implement truncated SVD in Java.

### Exercise 5.4
Explain eigenvalues vs SVD for PCA.

### Exercise 5.5
1000x500 matrix storage with k=50 components.

---

## Exercise Set 6: Matrix Calculus

### Exercise 6.1
Gradient of: a^Tx, x^TAx, ||x-b||^2

### Exercise 6.2
Prove d/dX tr(AX) = A^T

### Exercise 6.3
Hessian of f(x) = x^TAx

### Exercise 6.4
Gradient of ||Ax||^2

### Exercise 6.5
Gradient/Hessian of f(X) = log det(X)

---

## Exercise Set 7: Decompositions

### Exercise 7.1
LU decomposition of A = [[2,1],[1,2]]. Solve Ax = [3,3].

### Exercise 7.2
Cholesky of A = [[4,2],[2,3]]

### Exercise 7.3
QR decomposition using Gram-Schmidt

### Exercise 7.4
Complexity comparison: LU vs Cholesky

### Exercise 7.5
Cholesky on singular matrix behavior

---

## Exercise Set 8: ML Applications

### Exercise 8.1
Normal equations for linear regression. Ridge regression?

### Exercise 8.2
Implement linear regression: normal eq, QR, gradient descent

### Exercise 8.3
PCA steps, complexity, choosing k

### Exercise 8.4
Generate samples from N(0,S) given S = LL^T

### Exercise 8.5
Matrix factorization: dimensions, SGD, regularization

---

## Exercise Set 9: Advanced

### Exercise 9.1
Prove Woodbury identity

### Exercise 9.2
Conjugate gradient convergence for SPD

### Exercise 9.3
Power method convergence rate

### Exercise 9.4
Random projections, Johnson-Lindenstrauss

### Exercise 9.5
Implement QR algorithm in Java

---

## Exercise Set 10: Programming

### Exercise 10.1
Vector class: basic ops, norms, distances

### Exercise 10.2
Matrix class: multiplication, transpose, trace, determinant

### Exercise 10.3
LU decomposition with pivoting, substitution, inverse

### Exercise 10.4
SVD: power iteration, singular vectors, truncated

### Exercise 10.5
Complete linear algebra ML library

---

## Answers

1.1: [5,7,9], [-2,-1,0], 32, sqrt(14), 0.974
2.1: AB=[[19,22],[43,50]], BA=[[23,34],[31,46]]
4.1: lambda1=4, v1=[1,1]; lambda2=2, v2=[1,-1]
6.1: a, 2Ax, 2(x-b)

