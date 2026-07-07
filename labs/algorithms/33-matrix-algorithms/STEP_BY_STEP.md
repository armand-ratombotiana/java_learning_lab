# Matrix Algorithms — Step by Step Guide

## Step 1: Implement Naive Multiplication

Write simple O(n^3) matrix multiplication for verification. Used as base case for Strassen.

## Step 2: Implement Strassen's Algorithm

Write recursive function strassen(A, B). If n <= threshold, use naive. Extract submatrices A11..A22, B11..B22. Compute M1..M7 recursively. Combine results to form C. Test on random 4x4, 8x8, 16x16 matrices against naive.

## Step 3: Implement Gaussian Elimination

Write solve(A, b). For each column k: find pivot (max absolute value in column). Swap rows. Eliminate below. After forward elimination, back substitute. Test on 3x3 system with known solution.

## Step 4: Implement Partial Pivoting

Ensure pivoting selects the maximum absolute value in the column from current row downward. This improves numerical stability significantly.

## Step 5: Implement LU Decomposition

Modify Gaussian elimination to store multipliers in L. Return L, U, and permutation P. Test: P*A == L*U (within tolerance).

## Step 6: Implement Determinant and Inverse

Determinant: product of U diagonals * sign of permutation. Inverse: solve A * X = I using LU for each column. Test: A * inv(A) == I.

## Step 7: Implement Power Iteration

Generate random initial vector. Iterate: multiply by A, normalize. Compute Rayleigh quotient. Stop when eigenvalue converges. Test on a known symmetric matrix with known eigenvalues.