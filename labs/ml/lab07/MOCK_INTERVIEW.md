# Lab 07: Mock Interview — K-Means & Hierarchical Clustering

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Lloyd's algorithm, inertia/WCSS, elbow method, silhouette, k-means++, hierarchical clustering, linkage

---

**Interviewer**: "Walk me through the K-Means loop in this lab."

**Candidate**: "Lloyd's algorithm alternates two steps. Assignment: for each point,
`kMeans` computes `dist(data[i], centroids[k])` to every centroid and labels the
point with the nearest one. Update: it accumulates `sums[c][j] += data[i][j]` and
`counts[c]++` per cluster, then re-centers each centroid at the mean of its
points, `centroids[k][j] = sums[k][j] / counts[k]`. That repeats for `maxIter`
iterations. Two implementation details matter: initialization picks K rows
randomly from the data — `data[rng.nextInt(n)].clone()` with a fixed seed 42, so
the demo is deterministic — and the update skips empty clusters with
`if (counts[k] == 0) continue`, which can happen and must not divide by zero."

**Interviewer**: "Your demo prints inertia 65.66 for K=2 and 0.45 for K=3. What is
inertia telling you?"

**Candidate**: "Inertia is the within-cluster sum of squared distances — the code
recomputes centroids from the labels and sums `dist(point, centroid)²` over every
point. It's the objective K-Means minimizes, so it's monotonically
non-increasing in K. The jump in the demo is dramatic: with K=2, one centroid
lands between the (1,1) and (5,5) blobs and absorbs both, so distances are
large — 65.66; at K=3 the three blobs each get a centroid, and inertia collapses
to 0.45. That's a perfect elbow: the marginal gain at K=4 (0.3875) and K=5
(0.3375) is tiny, so the structure really is three clusters."

**Interviewer**: "How does the elbow method work, and what are its failure modes?"

**Candidate**: "Run K-Means for K=1..10, plot inertia vs K, and pick the point
where the curve's slope stops dropping sharply — the elbow. The intuition: each
new centroid absorbs a real cluster until K exceeds the true structure, after
which it just carves up existing clusters for marginal gains. Failure modes:
real data often has no sharp elbow — the curve decays smoothly and the choice is
arbitrary; and inertia is scale-dependent and favors spherical clusters, so the
elbow can lie. That's why I pair it with the silhouette score: s = (b − a)/max(a,
b), where a is mean intra-cluster distance and b is mean nearest-other-cluster
distance; s near +1 means cohesive and separated. And I never trust either
without domain judgment — for playlists, K=3 must also mean 'these three genres
make sense'."

**Interviewer**: "Why is the demo's initialization a liability in production?"

**Candidate**: "Because random picks can be bad picks. Two randomly chosen points
from the same blob initialize two centroids inside one cluster, and Lloyd's
algorithm converges to a local optimum from there — the demo's seed 42 happens to
land well, but another seed might produce a folded solution with far higher
inertia. k-means++ fixes initialization: pick the first centroid uniformly, then
pick each subsequent centroid with probability proportional to its squared
distance from the nearest chosen centroid — deliberately spreading the seeds.
Standard practice: run K-Means 10–20 times from different seeds and keep the
lowest-inertia result. The lab's seeded loop is the textbook algorithm; the
determinism is a feature for demos and a trap for production."

**Interviewer**: "What are K-Means' structural assumptions, and how do they show up
in the demo?"

**Candidate**: "K-Means assumes clusters are convex and roughly isotropic —
spherical — and it partitions all points, with no notion of noise or
outliers. The demo's blobs are perfectly spherical and separated, which is why
the assignment is instant and clean. Real data breaks all three: elongated
clusters get split across centroids, overlapping clusters get arbitrary
boundaries at the midpoint, and outliers yank centroids toward themselves — one
runaway track can displace a whole genre centroid. That's where hierarchical
clustering earns its keep: it doesn't assume K upfront and produces a dendrogram
you can cut at any height, at the cost of O(n²) or worse on large data."

**Interviewer**: "Single versus complete linkage — what's the difference and when
does it matter?"

**Candidate**: "Both are agglomerative: start with each point its own cluster and
merge the closest pair repeatedly. Single linkage defines cluster distance as the
minimum distance between any two points — the nearest neighbors — which produces
long, chained clusters and fails on data with thin bridges. Complete linkage uses
the maximum — the farthest pair — which produces compact clusters but is
sensitive to outliers. The walkthrough data would cluster identically under
either, but real music libraries with gradual genre transitions show the
difference dramatically: single linkage chains everything into one blob; average
linkage is the usual compromise. The dendrogram — the merge tree — is the
deliverable: cut it at a height and you get K clusters without re-running the
algorithm."

**Interviewer**: "When would you choose hierarchical clustering over K-Means?"

**Candidate**: "Three situations. Unknown K: the dendrogram shows the full merge
history, so you pick the cut level after seeing the structure — K-Means forces
you to commit before running. Small data: n up to a few thousand, since
agglomerative methods compute an n×n distance matrix and merge in O(n³) naively;
K-Means is O(n·K·iter) and scales to millions. Nested structure: if you want
'genres, sub-genres, and sub-sub-genres', the dendrogram gives the whole
hierarchy in one run, while K-Means needs a separate run per level. For
Spotify's tens of millions of tracks, though, hierarchical is infeasible and
K-Means is the workhorse — hierarchy is the small-data, exploratory tool."

**Interviewer**: "How do you evaluate a clustering result, given there are no
labels?"

**Candidate**: "Two families. Internal metrics measure the clustering itself:
inertia (lower is tighter), silhouette (cohesion vs separation), and Davies-
Bouldin. The demo's elbow is an internal evaluation — the sharp drop at K=3 is
the algorithm's own vote. External metrics compare against ground truth when it
exists — adjusted Rand index or mutual information — and are the honest check on
synthetic data with known blobs. The gap I always flag: internal metrics prefer
compact clusters, which is what K-Means already optimizes, so they validate
convergence more than truth. Real acceptance criteria come from the business:
do the three discovered playlist genres map to real listening behavior?"

**Interviewer**: "How does feature scaling affect clustering, compared to KNN?"

**Candidate**: "Identically — both are distance-based. Euclidean distance sums
squared deltas per dimension, so a danceability feature on a 0–10 scale
dominates a lyric density feature on a 0–1 scale, and the clusters silently
form along the big axis only. The lab's features are pre-scaled to the same
range, so the three blobs separate cleanly. In production I standardize before
clustering — the same normalization discipline Lab 05 teaches — and note the
corollary: the inertia numbers, and the elbow, are scale-dependent, so you
standardize once, cluster, and never mix scaled and raw values."

**Interviewer**: "How do you assign a new point to a cluster after training?"

**Candidate**: "Store the centroids and assign by nearest-centroid — the lab's
`centroids()` extension materializes them, and assignment is the assignment step
of Lloyd's algorithm reused at query time: compute `dist(newPoint, centroid[k])`
for each k and take the argmin. In the walkthrough, a new customer at (3.0k
spend, 5.0 visits) lands in the mid-tier cluster at distance 1.44. This is where
K-Means resembles KNN (Lab 05): the model is the centroids, prediction is
distance, and the cheap serving story is the same — O(K·d) per query, no
retraining. The difference: KNN keeps every point; K-Means compresses the data
to K centroids, which is the point of clustering."

**Interviewer**: "How does K-Means relate to the PCA lab that comes next?"

**Candidate**: "They're complementary preprocessing tools. PCA (Lab 08) finds
orthogonal directions of maximum variance and projects onto them — it's a linear
feature transform, unsupervised but not a partition. K-Means partitions the
space into Voronoi cells. The standard pipeline: reduce 50 noisy audio features
to 2 principal components, then cluster in the 2-D projection — the walkthrough's
two-feature energy/danceability space is exactly the shape of a PCA output.
The synergy has a caveat: PCA can discard exactly the low-variance axis that
separates two small but real clusters, so I cluster on the raw features or a
tuned component count, not blindly on the top-2."

**Interviewer**: "How do you choose K for the walkthrough, and how would you defend
it?"

**Candidate**: "The elbow is unambiguous here: inertia drops from 65.66 at K=2 to
0.45 at K=3, then plateaus at 0.39 and 0.34 — 14x reduction at the elbow and
barely 15% after. The centroids back it up: cluster 0 at (energy 1.03,
danceability 0.95) is the chill group, cluster 2 at (5.03, 5.05) the danceable
mid group, cluster 1 at (9.03, 0.98) the high-energy acoustic group — three
coherent, nameable genres. I'd present both the metric and the names in review,
because K-Means answers 'what K minimizes the objective' and the domain answers
'what K means something' — and a defensible choice uses both."
