# Lab 07 Guide: K-Means & Hierarchical Clustering

## Step 1 — Initialize Centroids
Randomly select K points from the dataset as initial centroids.

## Step 2 — Assign Clusters
For each point, assign to nearest centroid (Euclidean distance).

## Step 3 — Update Centroids
Recalculate centroid as mean of all points in cluster.

## Step 4 — Repeat
Steps 2–3 until centroids converge or max iterations reached.

## Step 5 — Elbow Method
Run K-Means for K=1..10, compute inertia, plot elbow curve.

## Step 6 — Hierarchical (Agglomerative)
Start with each point as its own cluster; repeatedly merge closest pairs (single/average/complete linkage).

## Step 7 — Run Tests
Test on synthetic 2D blobs.
