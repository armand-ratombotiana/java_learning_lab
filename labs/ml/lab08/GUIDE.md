# Lab 08 Guide: Principal Component Analysis

## Step 1 — Center the Data
Subtract mean from each feature: X_centered = X − μ

## Step 2 — Covariance Matrix
Σ = (1/n) · X_centeredᵀ · X_centered

## Step 3 — Eigendecomposition
Compute eigenvalues λ and eigenvectors v of Σ.

## Step 4 — Sort Components
Sort eigenvectors by descending eigenvalue.

## Step 5 — Project Data
Select top k eigenvectors; project X_centered onto them.

## Step 6 — Explained Variance Ratio
λᵢ / Σ λⱼ — how much variance each component captures.

## Step 7 — Run Tests
Apply PCA to reduce 4D data to 2D.
