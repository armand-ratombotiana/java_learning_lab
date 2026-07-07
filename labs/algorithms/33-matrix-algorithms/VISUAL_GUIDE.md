# Matrix Algorithms — Visual Guide

## Strassen 2x2 Block Structure

A = [[A11,A12],[A21,A22]], B = [[B11,B12],[B21,B22]]. 7 products computed from linear combinations of these blocks. Example: M1 = (A11+A22)*(B11+B22). Recombine with additions/subtractions to get C. Visually: blocks move, recombine, and overlap in the 7 products.

## Gaussian Elimination Steps

Matrix: [[2,1,-1],[1,3,2],[3,-1,1]]. Step 1: pivot=2, eliminate col1 below: [1,3,2] -> [0,2.5,2.5]; [3,-1,1] -> [0,-2.5,2.5]. Step 2: pivot=2.5, eliminate col2: [0,-2.5,2.5] -> [0,0,5]. Back substitution: x3=1, x2=0.4, x1=0.8.

## LU Visual

Original matrix is transformed: U (upper triangular) stored in upper part, L multipliers stored in lower part (diagonal of L = 1 implicitly). A = P * L * U where P is permutation from pivoting.

## SVD Input and Output

A = U * Sigma * V^T. Visually: A (m x n) = U (m x m) * Sigma (m x n diagonal) * V^T (n x n). The singular values on Sigma's diagonal are sorted descending. Columns of U are left singular vectors (stretch directions in output space). Rows of V^T are right singular vectors (stretch directions in input space).

## Power Iteration Trajectory

Start with random vector v0. v1 = A*v0 / ||A*v0||. v2 = A*v1 / ||A*v1||. Each iteration rotates v toward the dominant eigenvector. Convergence measured by Rayleigh quotient lambda_k = v_k^T A v_k / v_k^T v_k. The sequence lambda_k converges to the dominant eigenvalue.