# Linear Algebra - Quiz

## Part A: Multiple Choice

**Q1.** What is the dot product of vectors [1, 2, 3] and [4, 5, 6]?
- A) 32
- B) 42
- C) 22
- D) 36

**Q2.** Which matrix operation preserves dimensionality?
- A) Inverse
- B) Transpose
- C) Determinant
- D) Trace

**Q3.** For a symmetric matrix, eigenvalues are always:
- A) Complex
- B) Real
- C) Negative
- D) Zero

**Q4.** In SVD A = UΣV^T, the singular values are:
- A) In U
- B) In V
- C) In Σ
- D) In A

**Q5.** A matrix is orthogonal if:
- A) A = A^T
- B) A^T * A = I
- C) det(A) = 1
- D) A * A = I

## Part B: True/False

1. Matrix multiplication is commutative. (T/F)
2. The identity matrix has all eigenvalues equal to 1. (T/F)
3. The rank of a matrix equals the number of non-zero singular values. (T/F)
4. The inverse of a 2x2 matrix [[a,b],[c,d]] is (1/det)[[d,-b],[-c,a]]. (T/F)
5. Eigenvectors of different eigenvalues are always linearly independent. (T/F)

## Part C: Short Answer

1. What is the condition number of a matrix? What does it measure?
2. Explain the difference between eigenvalues and singular values.
3. Why is SVD preferred over eigendecomposition for rectangular matrices?
4. What is the computational complexity of matrix multiplication?
5. Describe when you would use L1 vs L2 norm.

## Part D: Problems

**Q1.** Compute the eigenvalues of A = [[2, 1], [1, 2]]

**Q2.** Find the SVD of a 2x2 identity matrix.

**Q3.** If A has eigenvalues [1, 2, 3], what is det(A)? What is tr(A)?

**Q4.** Given vectors v1 = [1,0] and v2 = [1,1], find the projection of v1 onto v2.

**Q5.** What is the rank of [[1,2,3],[4,5,6],[7,8,9]]?

## Answers
- A: A (32)
- B: T,F,T,T,T
- Q1: λ = 1,3
- Q2: I = I * I * I^T
- Q3: det = 6, tr = 6
- Q4: projection = [1, 1]
- Q5: rank = 2