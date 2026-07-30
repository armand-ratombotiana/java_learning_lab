# Lab 07 Interview: K-Means & Hierarchical Clustering

## Q1: How does K-Means initialize centroids?
Random initialization; k-means++ chooses distant points to improve convergence.

## Q2: What is the elbow method?
Plot inertia (WCSS) vs K; optimal K is where the rate of decrease sharply changes.

## Q3: What is the silhouette score?
Measures how similar a point is to its own cluster vs others. Range: [-1, 1].

## Q4: Difference between single and complete linkage in hierarchical clustering?
Single: distance between closest points; Complete: distance between farthest points.

## Q5: When to use hierarchical over K-Means?
When you want a dendrogram, don't know K beforehand, or have a small dataset.
