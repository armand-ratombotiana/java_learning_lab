# Lab 05: Mock Interview — K-Nearest Neighbors

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Distance metrics, K selection, weighted voting, curse of dimensionality, normalization, complexity

---

**Interviewer**: "Why is KNN called a lazy learner, and where is that visible in
your lab code?"

**Candidate**: "Because there is no training phase — the 'model' is the training
data itself. The lab's `predict(trainX, trainY, testX, k, weighted)` does
everything at query time: it computes `euclidean(trainX[i], testX)` for every
training point, sorts the neighbor list, and takes the top K. That's the whole
algorithm. The flip side of laziness is deferred cost: every prediction is O(n·d)
for the distance sweep plus O(n log n) for the sort, so serving latency grows
linearly with the training set. For the Netflix walkthrough with six taste
vectors that's instant, but it's the exact reason production KNN systems build
KD-trees, ball trees, or LSH indexes instead of scanning everything."

**Interviewer**: "Walk me through the voting logic, including the weighted
variant."

**Candidate**: "The code builds `List<Neighbor>` holding each label and its
distance, sorts by distance, and keeps the first K. Unweighted voting adds 1.0 per
neighbor — `votes.merge(nb.label, w, Double::sum)` — and picks the class with the
largest total. Weighted voting replaces the 1.0 with `1.0 / nb.dist`, so a
neighbor 1 unit away votes ten times as hard as one 10 units away; the special
case `nb.dist == 0 ? 1e6` handles an exact duplicate test point, which would
otherwise be a division by zero. Ties are broken by `Collections.max` on the vote
map. The demo's point (3,3) at K=3 returns 0 under both schemes — the three
nearest points are all cluster 0, so weighting changes nothing there."

**Interviewer**: "Your demo reports accuracy 1.00 for K=1, 3, and 5. What do you
actually learn from that?"

**Candidate**: "That the demo's test set is trivially easy: two test points, each
nestled inside its own tight cluster of three training points. Every K in 1..5
finds only neighbors of the correct class, so all three rows read 1.00. What I
learn is that accuracy on a two-point test set is a smoke test, not an
evaluation — which is why the walkthrough and Lab 10 exist: cross-validation
with a proper test split, and confusion-matrix metrics. In a real taste-matching
problem I'd expect K=1 to show high variance — every new viewer is assigned by a
single nearest user — and larger K to smooth that out at the cost of boundary
precision."

**Interviewer**: "How do you actually choose K?"

**Candidate**: "Cross-validation, sweeping K over a sensible range. The GUIDE
suggests trying K from 1 up to roughly √n and picking the value with the lowest
validation error. The bias-variance story explains the curve: small K is
low-bias, high-variance — the decision boundary hugs every training point — while
large K is high-bias, low-variance — it smooths over real structure. In the
walkthrough I test K=1, 3, 5 explicitly, and in production I'd wrap that in Lab
10's `crossVal` so the choice isn't anecdotal. A practical detail: K should
reflect class balance; with imbalanced data, majority classes dominate the vote
long before K gets large."

**Interviewer**: "Why do all three distance functions — Euclidean, Manhattan,
Minkowski — matter for KNN?"

**Candidate**: "Because 'nearest' is defined by the metric, and different metrics
define different neighborhoods. Euclidean √Σ(x−y)² is the default for continuous
features — it's the lab's only implementation — but Manhattan Σ|x−y| is far more
robust when features have outliers or redundant dimensions, since it doesn't
square deviations. Minkowski generalizes both: p=1 is Manhattan, p=2 is
Euclidean, and higher p emphasizes the largest single dimension difference. In
the walkthrough's similarity check, vectors 3 units apart in each of three
dimensions score 5.1962 Euclidean, 9.0 Manhattan, and 4.3267 Minkowski-p=3 —
three different notions of closeness, and the metric choice changes the
neighbor set, which changes the vote."

**Interviewer**: "What is the curse of dimensionality, concretely for KNN?"

**Candidate**: "As dimension d grows, distances concentrate: the ratio of
farthest to nearest neighbor distance approaches 1, so every point is almost
equidistant and 'nearest' becomes meaningless — a vote over near-random
neighbors. The math: the volume of a unit hypercube grows as 2^d, so fixed-size
training data thins out exponentially, and the ball containing the K neighbors
must keep expanding. For taste vectors in 10 genres it's already visible; for
photo feature vectors in 128 dimensions it's fatal without mitigation. The
mitigations are exactly the tools from the other labs: dimensionality reduction
(Lab 08's PCA), or distance concentration resistant approaches like ensembles."

**Interviewer**: "How does feature scaling interact with the distance metric?"

**Candidate**: "Brutally — a feature on a 0–7 scale, like the taste vectors in
the demo, swamps a feature on a 0–1 scale, like an engagement ratio. The
distance is dominated by the biggest-magnitude axis, so the model silently
ignores the small-scale features. The GUIDE is explicit: KNN is distance-
sensitive and features must be normalized — z-score or min-max — before the
distance is computed. Notice the demo's features are already on one scale, which
is why it behaves. In production I store the normalization parameters and apply
them at query time to the new point, otherwise serving silently drifts from the
trained geometry."

**Interviewer**: "What is the difference between KNN classification and
regression?"

**Candidate**: "Same neighbor selection, different aggregation. Classification —
what the lab implements — takes a vote over the K labels. Regression averages
the K target values, optionally weighted by 1/distance so nearby neighbors
contribute more to the mean. The mechanics would be identical up to the final
step: replace `Collections.max(votes...)` with a weighted average of the
neighbors' y values. Netflix uses exactly this hybrid thinking: the predicted
rating for an unseen title is the weighted mean of similar users' ratings, which
is item-based collaborative filtering wearing a KNN costume — a great interview
answer because it shows KNN isn't just a classifier."

**Interviewer**: "What happens when the training data is imbalanced?"

**Candidate**: "The majority class wins the votes. With 95% 'no' labels, even
K=3 near a rare positive point may find two majority neighbors and vote no —
minority regions shrink because they're outvoted by density, not proximity.
Fixes: stratified neighbors — require a minimum number of minority-class
neighbors — class-weighted voting, or oversampling. The deeper point: KNN's
decision depends on the local density of classes, so the model's confidence
estimate is a local density ratio, which is honestly a feature — it tells you
where the model is near its data — but it means the class distribution is part
of the inductive bias, and the lab's balanced two-cluster demo hides that."

**Interviewer**: "When would you pick KNN over the parametric models from earlier
labs?"

**Candidate**: "Three situations. First, no training-time budget and frequent
data updates: KNN is insert-and-serve, while logistic regression (Lab 02) and
SVMs (Lab 04) need refits. Second, highly nonlinear boundaries on small data:
the demo's clusters would need polynomial features for a linear model to match
what KNN gets for free, because KNN partitions space locally. Third,
interpretability per prediction: 'these three users with taste vectors near
yours liked it' is a story a product team can defend. The costs are the flip
side: prediction latency, memory for the training set, and the curse of
dimensionality — which is why the big players use it for small, low-dimensional,
frequently-refreshed similarity problems."

**Interviewer**: "How do you serve KNN at production scale, say 100 million
viewers?"

**Candidate**: "Four layers. Index: a KD-tree or ball tree reduces the average
query from O(n) to O(log n) in low dimensions; past ~20 dimensions those degrade,
and LSH (locality-sensitive hashing) becomes the right tool — buckets of
nearby points so queries touch a candidate set instead of everything. Storage:
keep the taste vectors in memory or a key-value store, since the training set is
the model. Approximation: accept approximate nearest neighbors — 99% of the
ranking value at 1/1000th the cost. And staleness: KNN is only as good as the
stored points, so the index needs a refresh pipeline. The lab's scan is the
specification; production is engineering on top of that spec."

**Interviewer**: "The demo's point (3,3) is ambiguous — equidistant between
clusters. How does the code behave there?"

**Candidate**: "It's the interesting case. (3,3) is roughly equidistant: the two
nearest training points at distance ~1.80 are both class 0, and the next
candidate class-1 point at ~2.83 ties with the third class-0 point at 2.83. With
K=3 the sort keeps three neighbors — all class 0 by the stable insertion order —
so both plain and weighted voting return 0. The tie-break is order-dependent,
which is fragile: change the training data order and the verdict could flip.
That's a real interview insight: the lab's `Collections.max` tie-break is
deterministic but arbitrary, and in production I'd add a distance-based tie
breaker — the nearest neighbor wins ties — or report the vote margin so the
downstream system knows when the call was close."

**Interviewer**: "How would you evaluate a KNN model properly?"

**Candidate**: "Same rigor as any classifier, from Lab 10: hold out a test set,
run k-fold cross-validation to pick K and the metric, and report confusion-matrix
metrics — precision and recall matter more than accuracy when classes are
imbalanced, like fraud or spam. KNN adds one specific check: verify that
accuracy is stable as K sweeps the sensible range — the demo's flat 1.00 is a
suspicious flatness that says the problem is easy, not that K is irrelevant. And
monitor the distance distribution at serving time: if new points' nearest-
neighbor distances drift upward, the deployed data is drifting from the stored
points, and it's time to refresh the index."
