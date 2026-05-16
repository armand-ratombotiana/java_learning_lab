# Clustering - Quiz

## Assessment Questions

### Question 1
What is the main objective of K-means clustering?

A) Maximize within-cluster variance
B) Minimize within-cluster variance
C) Maximize between-cluster variance
D) Minimize between-cluster variance

**Answer: B** - K-means aims to minimize the within-cluster sum of squares (WCSS), which measures how close points are to their cluster centroid.

---

### Question 2
How do you determine the optimal number of clusters in K-means?

A) Always use 3 clusters
B) Use the Elbow method or Silhouette score
C) Use all data points as clusters
D) Randomly select

**Answer: B** - The Elbow method plots WCSS vs number of clusters and looks for an "elbow" point. Silhouette score measures how similar points are to their own cluster vs other clusters.

---

### Question 3
What is a key advantage of DBSCAN over K-means?

A) Requires specifying number of clusters upfront
B) Can find arbitrarily shaped clusters
C) Always produces spherical clusters
D) Cannot handle noise

**Answer: B** - DBSCAN can find clusters of arbitrary shape because it defines clusters as dense regions, not based on distance from centroids.

---

### Question 4
In DBSCAN, what is a "core point"?

A) Any point in the dataset
B) A point with at least minPts neighbors within epsilon
C) The centroid of a cluster
D) A point on the cluster boundary

**Answer: B** - A core point has at least minPts other points within its epsilon-neighborhood, indicating it's in a dense region.

---

### Question 5
What does hierarchical clustering produce?

A) A single partition of data
B) A dendrogram showing hierarchical relationships
C) A fixed number of clusters
D) Only flat clustering

**Answer: B** - Hierarchical clustering builds a hierarchy of clusters, typically visualized as a dendrogram, showing how clusters merge or split.

---

### Question 6
What is the difference between agglomerative and divisive hierarchical clustering?

A) Agglomerative starts with all points as separate clusters, divisive starts with one cluster
B) Agglomerative is faster
C) Divisive always produces better results
D) No difference

**Answer: A** - Agglomerative (bottom-up) starts with each point as a cluster and merges them. Divisive (top-down) starts with one cluster and recursively splits it.

---

### Question 7
What is the linkage criterion in hierarchical clustering?

A) How to calculate distance between points
B) How to calculate distance between clusters
C) How to select the number of clusters
D) How to initialize centroids

**Answer: B** - Linkage defines how to compute the distance between two clusters - common methods are single, complete, and average linkage.

---

### Question 8
What is a limitation of K-means?

A) Cannot handle large datasets
B) Assumes spherical clusters of similar size
C) Cannot use Euclidean distance
D) Cannot be used for clustering

**Answer: B** - K-means assumes clusters are convex and isotropic (spherical), and works best when clusters are of similar size and density.

---

### Question 9
What happens in K-means when a cluster becomes empty?

A) Algorithm terminates
B) Random centroid is reselected
C) Cluster is removed from consideration
D) Algorithm restarts

**Answer: B** - When a cluster becomes empty, a common approach is to randomly reselect a centroid or split the largest cluster.

---

### Question 10
What is the Silhouette score range?

A) -1 to 1
B) 0 to 1
C) -1 to 0
D) 0 to infinity

**Answer: A** - Silhouette score ranges from -1 to 1, where 1 means clusters are well separated, 0 means overlapping, and -1 means points may be assigned to wrong clusters.

---

### Question 11 (Bonus)
What is the time complexity of standard K-means?

A) O(n)
B) O(n²)
C) O(n * k * i * d) where i = iterations, k = clusters, d = dimensions
D) O(n log n)

**Answer: C** - K-means complexity is O(n * k * i * d), making it efficient for large datasets.

---

### Question 12 (Bonus)
In DBSCAN, what happens to points that are not core points and not density-reachable?

A) They become noise/outliers
B) They are assigned to the nearest cluster
C) They are always classified as core points
D) They form their own cluster

**Answer: A** - Points that cannot be reached from any core point are labeled as noise/outliers.