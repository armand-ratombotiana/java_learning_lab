# Matrix Algorithms — Code Deep Dive

The StrassenMultiply class performs recursive multiplication. The base case: if n <= threshold (64), use naive O(n^3) multiplication. The recursive case: split A and B into four n/2 x n/2 submatrices. Compute the 7 Strassen products using recursive calls. Combine results to form the output matrix.

Submatrix extraction: create views (or copy) data into contiguous blocks. For performance, copying into contiguous blocks improves cache behavior. The 7 products: M1-M7, each computed from combinations of input blocks. The combine step uses elementwise addition and subtraction.

The GaussianElimination class solves Ax = b with partial pivoting. For each column k: find max |A[i][k]| for i >= k. If max < EPS, singular. Swap rows k and pivot. For i = k+1..n-1: factor = A[i][k] / A[k][k]; for j = k..n: A[i][j] -= factor * A[k][j]; b[i] -= factor * b[k].

Back substitution: for i = n-1 down to 0: x[i] = b[i]; for j = i+1..n-1: x[i] -= A[i][j] * x[j]; x[i] /= A[i][i].

The MatrixDecomposition class computes LU in-place. The result is stored in a single matrix: L in the lower triangle (with unit diagonal implicit), U in the upper triangle. The pivot array records row swaps.

Determinant: after LU, det = product(U[i][i]) * pivotSign (where pivotSign = 1 if even swaps, -1 if odd).

Inverse: solve A * inv = I using LU. For each column j of I: solve L * y = P * I_j, solve U * x = y, store x as column j of inverse.