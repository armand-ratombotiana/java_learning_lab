# Lab 05 Guide: K-Nearest Neighbors

## Step 1 — Distance Functions
Implement Euclidean, Manhattan, and Minkowski distance.

## Step 2 — Find K Nearest Neighbors
Sort all training points by distance; take top K.

## Step 3 — Majority Vote
Count class labels among K neighbors; return most frequent.

## Step 4 — Weighted Voting
Multiply votes by 1/distance to give closer neighbors higher influence.

## Step 5 — Choosing K
Use cross-validation; small K → low bias, high variance; large K → high bias, low variance.

## Step 6 — Run Tests
Test on synthetic 2D data with K=3 and K=5.
