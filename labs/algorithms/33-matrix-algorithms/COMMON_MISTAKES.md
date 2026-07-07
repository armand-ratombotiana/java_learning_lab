# Matrix Algorithms — Common Mistakes

1. Strassen base case too large: if the threshold for naive multiplication is too large (e.g., 256), the asymptotic benefits of Strassen are lost. A threshold of 32-64 is typical.

2. Strassen submatrix copying: copying matrices for each recursive call adds O(n^2) overhead per level. Using submatrix views (offset-based) reduces overhead but complicates code.

3. Gaussian elimination: not applying the same row operations to the RHS vector b. When swapping rows or eliminating, b must be modified consistently.

4. Partial pivoting scanning only below the diagonal: the pivot search must include the current row and all rows below it. Scanning only below (excluding current row) may miss a better pivot.

5. LU decomposition: forgetting that L has unit diagonal (all 1s), which is not stored explicitly. The actual multipliers are stored in the lower triangular part.

6. Determinant sign from permutation: each row swap multiplies the determinant by -1. Counting the parity of swaps can be done by tracking the permutation array.

7. Matrix inverse: assuming all matrices are invertible. A matrix with zero determinant (singular) has no inverse. Check |det| > EPS before attempting inversion.

8. Power iteration initial vector: if the initial vector is orthogonal to the dominant eigenvector, power iteration may converge to the wrong eigenvalue. Use a random initial vector to avoid this.

9. Not handling non-square matrices in multiplication: matrix multiplication requires A.columns == B.rows. Always check dimensions.