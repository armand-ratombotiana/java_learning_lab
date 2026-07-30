# Lab 03 Guide: Decision Trees & Random Forests

## Step 1 — Impurity Metrics
Compute entropy and Gini impurity for a set of labels.

## Step 2 — Information Gain
For each feature, compute weighted impurity reduction after split.

## Step 3 — ID3 / CART Split
Select feature with max information gain (ID3) or min Gini (CART).

## Step 4 — Recursive Partitioning
Split data recursively until max depth or pure node.

## Step 5 — Bagging
- Draw bootstrap sample (with replacement)
- Train tree on sample
- Repeat T times; average predictions for regression, majority vote for classification.

## Step 6 — Run Tests
Compile and run Main.java against the play-tennis dataset.
