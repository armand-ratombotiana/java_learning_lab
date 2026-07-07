# Matrix Algorithms — Internal Implementation Details

The StrassenMultiply class implements recursive Strassen multiplication. Base case: when n <= 64, use naive O(n^3) multiplication. For larger matrices, split into four n/2 x n/2 blocks. Compute the 7 Strassen products recursively. Combine the results to form the output matrix. The overhead of copying blocks is minimized by using submatrix views (offsets into the original array) when possible.

The GaussianElimination class uses partial pivoting: for each column k, scan rows k..n-1 for the maximum absolute value. Swap the pivot row to position k. If the pivot is near zero (within EPS), the matrix is singular. Eliminate below the pivot by subtracting multiples of the pivot row.

The MatrixDecomposition class performs LU decomposition: factor A into L and U in-place. The diagonal entries of L are implicitly 1. The multipliers are stored in the lower part of the matrix (below diagonal). Returns a permutation array piv of length n recording row swaps.

Determinant computation: when LU decomposition is complete, multiply the diagonal entries of U, multiplied by -1 for each row swap. If pivot was zero at any point, determinant is 0.

Matrix inverse: solves A * X = I using LU decomposition. For each column j of I, forward substitute L * y = P * e_j, then back substitute U * x = y. The result vector x becomes column j of the inverse.

Power iteration: initializes a random vector b0. For each iteration: b_{k+1} = A * b_k / ||A * b_k||. Convergence is monitored by tracking the Rayleigh quotient lambda = b^T A b / (b^T b). Stop when ||A * b - lambda * b|| < EPS. The method converges linearly with ratio |lambda_2 / lambda_1|.