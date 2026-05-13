# Linear Algebra for ML - Flashcards

## Vector Operations

Q: What is the dot product of [1,2,3] and [4,5,6]?
A: 1*4 + 2*5 + 3*6 = 32

Q: What is the L2 norm of [3,4]?
A: sqrt(3^2 + 4^2) = sqrt(9+16) = 5

Q: When are two vectors orthogonal?
A: When their dot product equals zero

Q: What is the cosine similarity between vectors a and b?
A: (a dot b) / (||a|| * ||b||)

Q: What is the projection of vector a onto vector b?
A: proj_b(a) = ((a dot b) / (b dot b)) * b

---

## Matrix Properties

Q: What is the trace of a matrix?
A: Sum of diagonal elements

Q: When is a matrix invertible?
A: When determinant is non-zero

Q: What is the transpose of matrix A?
A: AT where AT[i,j] = A[j,i]

Q: What is an orthogonal matrix?
A: Matrix Q where Q^T Q = I

Q: What is a symmetric matrix?
A: Matrix A where A = A^T

---

## Matrix Operations

Q: Is AB = BA for all matrices?
A: No, matrix multiplication is not commutative in general

Q: What is (AB)^T?
A: B^T A^T

Q: What is det(AB)?
A: det(A) * det(B)

Q: What is (AB)^-1?
A: B^-1 A^-1

Q: What is tr(ABC)?
A: tr(BCA) = tr(CAB) (cyclic property)

---

## Eigenvalues

Q: What is the characteristic equation?
A: det(A - lambda*I) = 0

Q: What is the sum of eigenvalues equal to?
A: trace(A)

Q: What is the product of eigenvalues equal to?
A: det(A)

Q: Are eigenvectors of symmetric matrices orthogonal?
A: Yes, for distinct eigenvalues

Q: What is the spectral theorem for symmetric matrices?
A: A = Q Lambda Q^T where Q is orthogonal

---

## SVD

Q: What does SVD decompose A as?
A: A = U Sigma V^T

Q: What are the properties of singular values?
A: sigma1 >= sigma2 >= ... >= 0

Q: What is the pseudoinverse of A?
A: A+ = V Sigma+ U^T where Sigma+ has reciprocals of non-zero sigmas

Q: What is the best rank-k approximation of A?
A: A_k = U_k Sigma_k V_k^T (Eckart-Young theorem)

Q: What is the condition number?
A: cond(A) = sigma_max / sigma_min

---

## Norms

Q: What is L1 norm?
A: ||x||1 = sum |xi|

Q: What is L2 norm?
A: ||x||2 = sqrt(sum xi^2)

Q: What is L-infinity norm?
A: ||x||inf = max |xi|

Q: What is Frobenius norm?
A: ||A||F = sqrt(sum Aij^2) = sqrt(sum sigma_i^2)

Q: What is spectral norm?
A: ||A||2 = sigma_max (largest singular value)

---

## Decompositions

Q: What does LU decomposition give?
A: A = LU where L is lower, U is upper triangular

Q: When can you use Cholesky decomposition?
A: When matrix is symmetric positive definite

Q: What does QR decomposition give?
A: A = QR where Q is orthogonal, R is upper triangular

Q: What is Gram-Schmidt used for?
A: Orthogonalizing a set of vectors

---

## Linear Systems

Q: What are normal equations for Ax = b?
A: A^T A x = A^T b

Q: What is the solution?
A: x = (A^T A)^-1 A^T b = A+ b

Q: When is least squares used?
A: When system is overdetermined (more equations than unknowns)

Q: What is partial pivoting in Gaussian elimination?
A: Swapping rows to put largest element at pivot position

---

## Key Formulas

Q: Gradient of x^T A x (A symmetric)?
A: 2Ax

Q: Gradient of ||Ax - b||^2?
A: 2A^T (Ax - b)

Q: Gradient of log det(X)?
A: (X^-1)^T

Q: Chain rule for matrix composition?
A: d/dX tr(f(g(X))) = (g'(X))^T * f'(g(X))