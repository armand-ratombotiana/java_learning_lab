# Clustering - Flashcards

## Quick Review Cards

### Card 1: K-means Objective
**Q:** What does K-minimize optimize?
**A:** K-means minimizes within-cluster sum of squares (WCSS) - sum of squared distances from points to their cluster centroids.

---

### Card 2: K-means Algorithm Steps
**Q:** What are the two main steps in K-means?
**A:** 1) Assignment: Assign each point to nearest centroid, 2) Update: Recalculate centroids as mean of assigned points. Repeat until convergence.

---

### Card 3: Choosing K
**Q:** How does the Elbow method work?
**A:** Plot WCSS vs number of clusters (K). Look for an "elbow" point where decreasing K further yields diminishing returns - this is the optimal K.

---

### Card 4: DBSCAN Parameters
**Q:** What are the two main parameters in DBSCAN?
**A:** eps (epsilon) - maximum distance between points to be considered neighbors, and minPts - minimum points needed to form a dense region.

---

### Card 5: DBSCAN Point Types
**Q:** What are the three types of points in DBSCAN?
**A:** Core points (density-reachable), border points (reachable from core but not core), noise points (not reachable from any core).

---

### Card 6: Hierarchical Clustering
**Q:** What does a dendrogram show?
**A:** A tree-like diagram showing the hierarchical relationships between clusters, with the y-axis representing distance/ dissimilarity.

---

### Card 7: Linkage Methods
**Q:** What is single linkage vs complete linkage?
**A:** Single linkage uses minimum distance between any two points in clusters. Complete linkage uses maximum distance.

---

### Card 8: K-means Limitations
**Q:** What are the main limitations of K-means?
**A:** Assumes spherical clusters, requires specifying K, sensitive to initialization, affected by outliers.

---

### Card 9: Silhouette Score
**Q:** How do you interpret Silhouette score?
**A:** High score (close to 1) = well-defined clusters, near 0 = overlapping clusters, negative = points possibly misclassified.

---

### Card 10: Agglomerative Clustering
**Q:** How does agglomerative clustering work?
**A:** Start with each point as its own cluster, iteratively merge closest clusters until one cluster remains.

---

### Card 11: Cluster Validity Indices
**Q:** What are external vs internal validity indices?
**A:** External compare to ground truth labels. Internal measure cohesion and separation without ground truth.

---

### Card 12: Handling Outliers
**Q:** Which algorithm handles outliers better?
**A:** DBSCAN automatically identifies noise points. K-means is sensitive to outliers which can distort centroids.