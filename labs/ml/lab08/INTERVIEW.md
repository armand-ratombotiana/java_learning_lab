# Lab 08 Interview: Principal Component Analysis

## Q1: What does PCA do?
Finds orthogonal directions of maximum variance in the data and projects it onto a lower-dimensional subspace.

## Q2: Should you standardize before PCA?
Yes — PCA is sensitive to feature scales; use standardization (z-score) first.

## Q3: How do you choose the number of components?
Select k such that cumulative explained variance ≥ threshold (e.g., 95%).

## Q4: Are principal components interpretable?
Not always — they are linear combinations of original features, making interpretation difficult.

## Q5: What is the relationship between PCA and SVD?
PCA can be computed via SVD of the centered data matrix (more numerically stable).
