# Clustering - Code Deep Dive

```python
import numpy as np
from sklearn.cluster import KMeans, DBSCAN, AgglomerativeClustering
from sklearn.mixture import GaussianMixture
from sklearn.metrics import silhouette_score, calinski_harabasz_score

# === K-MEANS ===

kmeans = KMeans(n_clusters=3, init='k-means++', n_init=10)
labels = kmeans.fit_predict(X)

# Elbow method
inertias = []
for k in range(1, 11):
    km = KMeans(n_clusters=k)
    km.fit(X)
    inertias.append(km.inertia_)

# Silhouette analysis
for k in range(2, 10):
    km = KMeans(n_clusters=k)
    labels = km.fit_predict(X)
    score = silhouette_score(X, labels)
    print(f"k={k}, silhouette={score:.3f}")

# === DBSCAN ===

dbscan = DBSCAN(eps=0.5, min_samples=5)
labels = dbscan.fit_predict(X)

# Find optimal eps using k-distance graph

# === HIERARCHICAL ===

# Agglomerative
agg = AgglomerativeClustering(n_clusters=3, linkage='ward')
labels = agg.fit_predict(X)

# Different linkages
for linkage in ['complete', 'average', 'single']:
    agg = AgglomerativeClustering(n_clusters=3, linkage=linkage)
    labels = agg.fit_predict(X)

# === GAUSSIAN MIXTURE MODELS ===

gmm = GaussianMixture(n_components=3, covariance_type='full')
gmm.fit(X)
labels = gmm.predict(X)

# Probabilities
probs = gmm.predict_proba(X)

# BIC for model selection
for n in range(1, 10):
    gmm = GaussianMixture(n_components=n)
    gmm.fit(X)
    print(f"n={n}, BIC={gmm.bic(X):.2f}")

# === EVALUATION ===

# Silhouette
sil_score = silhouette_score(X, labels)

# Calinski-Harabasz
ch_score = calinski_harabasz_score(X, labels)

# Davies-Bouldin
from sklearn.metrics import davies_bouldin_score
db_score = davies_bouldin_score(X, labels)
```