# Linear Algebra for ML - Quiz (30 Questions)

## Multiple Choice

### Q1
What is the result of dot product [1,2,3] dot [4,5,6]?
- A) 27
- B) 32
- C) 40
- D) 21

Answer: B

### Q2
Matrix multiplication is:
- A) Commutative: AB = BA
- B) Not commutative in general
- C) Only defined for square matrices
- D) Same as element-wise multiplication

Answer: B

### Q3
The determinant of identity matrix I_n is:
- A) n
- B) 0
- C) 1
- D) -1

Answer: C

### Q4
For eigenvalue lambda and eigenvector v: Av = lambda v. What is Av + 2v?
- A) (lambda + 2)v
- B) lambda Av
- C) 2lambda v
- D) (lambda - 2)v

Answer: A

### Q5
SVD decomposes matrix A as:
- A) LU
- B) QR
- C) U Sigma VT
- D) PDP-1

Answer: C

### Q6
The trace of matrix A equals:
- A) Product of eigenvalues
- B) Sum of eigenvalues
- C) Determinant
- D) Inverse

Answer: B

### Q7
A matrix is orthogonal if:
- A) det = 0
- B) AT = A
- C) AT A = I
- D) All eigenvalues are 1

Answer: C

### Q8
L2 norm of vector [3,4] is:
- A) 5
- B) 7
- C) 12
- D) 25

Answer: A

### Q9
What is the pseudoinverse of diagonal matrix diag(2,3)?
- A) diag(0.5, 0.33)
- B) diag(2, 3)
- C) diag(-2, -3)
- D) 1/diag(2,3)

Answer: A

### Q10
Cholesky decomposition requires:
- A) Any square matrix
- B) Positive definite matrix
- C) Orthogonal matrix
- D) Singular matrix

Answer: B

---

## True/False

### Q11
For any matrices A, B: det(AB) = det(A)det(B)

Answer: True

### Q12
Eigenvectors of symmetric matrix are always orthogonal.

Answer: True

### Q13
The inverse of a product equals the product of inverses in reverse order: (AB)-1 = B-1 A-1

Answer: True

### Q14
All square matrices are diagonalizable.

Answer: False (defective matrices exist)

### Q15
tr(ABC) = tr(CAB) = tr(BCA) (cyclic property)

Answer: True

### Q16
SVD always produces orthonormal U and V matrices.

Answer: True

### Q17
Power iteration always converges to the dominant eigenvalue.

Answer: True (if eigenvalue is real and unique)

### Q18
The nullity of a matrix equals n - rank(A).

Answer: True (Rank-Nullity Theorem)

### Q19
AT A is always positive definite.

Answer: False (semidefinite at minimum)

### Q20
Gram-Schmidt orthogonalization produces orthonormal vectors.

Answer: True (after normalization)

---

## Short Answer

### Q21
What is the condition number of matrix A? When is it useful?

Answer: cond(A) = norm(A) * norm(A-1). Measures numerical stability. High cond means ill-conditioned.

### Q22
Explain why SVD is preferred over eigendecomposition for PCA.

Answer: SVD works on any matrix, more numerically stable, avoids computing XXT directly.

### Q23
What is the computational complexity of matrix multiplication?

Answer: O(n^3) for n x n matrices using naive algorithm.

### Q24
Define rank of a matrix. How do you compute it?

Answer: Maximum number of linearly independent rows/columns. Compute via row reduction or SVD.

### Q25
What is the purpose of partial pivoting in Gaussian elimination?

Answer: Improve numerical stability by swapping rows to put largest pivot at diagonal position.

---

## Calculation Problems

### Q26
Compute eigenvalues of [[2,1],[1,2]].

Answer: det([[2-lambda,1],[1,2-lambda]]) = (2-lambda)^2 - 1 = 0
lambda^2 - 4lambda + 3 = 0
lambda = 1 or lambda = 3

### Q27
Find the dot product and cosine similarity of a=[1,0], b=[1,1].

Answer: dot = 1, norm(a)=1, norm(b)=sqrt(2), cos = 1/sqrt(2) = 0.707

### Q28
For A = [[1,2],[3,4]], compute det and trace.

Answer: det = 1*4 - 2*3 = -2, trace = 1+4 = 5

### Q29
Verify that [1,1] is eigenvector of [[3,1],[1,3]], find eigenvalue.

Answer: [[3,1],[1,3]]*[1,1] = [4,4] = 4*[1,1], eigenvalue = 4

### Q30
If norm(x)1 = 10 and norm(x)inf = 4, what can you say about x?

Answer: L1 norm is sum of absolute values = 10. L-inf is max absolute value = 4. Vector has at least 3 non-zero elements on average.