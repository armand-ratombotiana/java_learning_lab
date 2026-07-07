# How Matrix Algorithms Work

## Strassen Multiplication

Given 2x2 block matrices A = [[A11, A12], [A21, A22]], B = [[B11, B12], [B21, B22]]:
Compute 7 products:
M1 = (A11 + A22) * (B11 + B22)
M2 = (A21 + A22) * B11
M3 = A11 * (B12 - B22)
M4 = A22 * (B21 - B11)
M5 = (A11 + A12) * B22
M6 = (A21 - A11) * (B11 + B12)
M7 = (A12 - A22) * (B21 + B22)

Then combine:
C11 = M1 + M4 - M5 + M7
C12 = M3 + M5
C21 = M2 + M4
C22 = M1 - M2 + M3 + M6

This uses 7 multiplications instead of 8. Recursively, the total is O(n^{log_2 7}).

## Gaussian Elimination with Partial Pivot

For column k (0 to n-1):
- Find pivot: row p where |A[p][k]| is maximal for p >= k.
- Swap rows k and p.
- If A[k][k] == 0, matrix is singular.
- For rows i > k:
  - factor = A[i][k] / A[k][k].
  - For columns j >= k: A[i][j] -= factor * A[k][j].
- Record multipliers in L below diagonal.

## LU Decomposition

During Gaussian elimination, store the multipliers (factors) in the lower triangular matrix L (with 1s on diagonal). The upper triangular matrix U is the result of elimination. Solve Ax = b:
- Forward substitution: Ly = b (solve for y using forward substitution since L is lower triangular).
- Back substitution: Ux = y (solve for x since U is upper triangular).

## Determinant

det(A) = product(U[i][i]) * sign(p) where sign(p) is the parity of the number of row swaps during LU decomposition.

## Matrix Inverse

Solve A * X = I column by column. For each column j, solve A * x_j = e_j (the j-th unit vector) using LU decomposition and forward/back substitution.