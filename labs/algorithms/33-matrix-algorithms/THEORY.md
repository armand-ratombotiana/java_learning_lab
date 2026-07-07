# Matrix Algorithms — Theoretical Foundation

## Matrix Multiplication and Strassen

Standard matrix multiplication of two n x n matrices computes C[i][j] = sum_k A[i][k] * B[k][j], requiring O(n^3) operations. Strassen's algorithm uses divide-and-conquer: each matrix is split into four n/2 x n/2 blocks. Instead of 8 multiplications (naive), Strassen uses 7 cleverly chosen multiplications with additions/subtractions, yielding recurrence T(n) = 7T(n/2) + O(n^2) with solution O(n^{log_2 7}) ≈ O(n^{2.807}).

## Gaussian Elimination

Gaussian elimination transforms a matrix A into row-echelon form through elementary row operations: swapping rows, multiplying rows by non-zero scalars, and adding multiples of one row to another. Partial pivoting (selecting the row with the largest absolute value in the current column) improves numerical stability. The algorithm is O(n^3) and is the basis for solving linear systems.

## LU Decomposition

LU decomposition factors a matrix A as A = P * L * U, where P is a permutation matrix (from pivoting), L is lower triangular with 1s on the diagonal, and U is upper triangular. Once the decomposition is computed (O(n^3)), solving a linear system reduces to forward substitution (Ly = Pb, O(n^2)) and back substitution (Ux = y, O(n^2)).

## Determinant and Inverse

The determinant can be computed from LU decomposition: det(A) = det(P) * product(U[i][i]), since det(L) = 1. The matrix inverse can be computed by solving A * x_j = e_j for each unit vector e_j, requiring O(n^3) for the LU decomposition plus O(n^3) for the n right-hand sides.

## Singular Value Decomposition

SVD factorizes A = U * Sigma * V^T, where U and V are orthogonal matrices and Sigma is diagonal with singular values sigma_1 >= sigma_2 >= ... >= sigma_r > 0. SVD provides the best low-rank approximation of a matrix (Eckart-Young theorem), which is the mathematical foundation of PCA, dimensionality reduction, and recommendation systems.

## Power Iteration

Power iteration finds the dominant eigenvalue and eigenvector of a matrix by repeatedly multiplying a random vector by the matrix and normalizing. The convergence rate depends on the ratio |lambda_2 / lambda_1|. The algorithm works well when eigenvalues are well-separated and is the basis for PageRank and other large-scale eigenvalue computations.