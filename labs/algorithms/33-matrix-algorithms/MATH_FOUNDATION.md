# Matrix Algorithms — Mathematical Foundation

## Matrix Multiplication Complexity

Naive multiplication of n x n matrices requires n^3 multiplications and n^3 additions. Strassen reduces multiplications to n^{log_2 7} ≈ n^{2.807}. The best known algorithms (Williams 2012, Alman 2021) achieve O(n^{2.371552}) using tensor techniques not practical for realistic n.

## Gaussian Elimination as LU

Solving Ax = b via Gaussian elimination: O(2n^3/3) operations. Forward elimination: for k=1..n-1, for i=k+1..n, for j=k+1..n+1: A[i][j] -= A[i][k]/A[k][k] * A[k][j]. Back substitution: for i=n..1: x[i] = (A[i][n+1] - sum_{j=i+1}^n A[i][j]*x[j]) / A[i][i].

## Numerical Stability

Partial pivoting ensures |A[k][k]| is the maximum in column k, bounding multipliers |m_ik| <= 1. This controls error growth. Without pivoting, errors can grow as O(2^n). With partial pivoting, error growth is typically O(n) in practice (though O(2^n) worst-case exists).

## SVD Existence

Every m x n real matrix A can be factored as A = U * Sigma * V^T where U is m x m orthogonal, V is n x n orthogonal, and Sigma is m x n diagonal with sigma_1 >= sigma_2 >= ... >= sigma_r > 0 (r = rank). The columns of U are left singular vectors; columns of V are right singular vectors.

## Eckart-Young Theorem

The best rank-k approximation to A (in Frobenius norm) is A_k = U_k * Sigma_k * V_k^T, where we keep only the k largest singular values and corresponding vectors. The approximation error is sqrt(sigma_{k+1}^2 + ... + sigma_r^2). This is the foundation of PCA and dimensionality reduction.

## Power Iteration Convergence

Power iteration computes the dominant eigenvector. Starting from random v_0: v_{k+1} = A v_k / ||A v_k||. The angle between v_k and the dominant eigenvector decreases by factor |lambda_2 / lambda_1| each iteration. Convergence is linear; for closely-spaced eigenvalues, convergence is slow.