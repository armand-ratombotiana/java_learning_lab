# Lab 03: Mock Interview — Decision Trees & Random Forests

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: ID3, entropy, information gain, Gini, CART, bagging, feature importance, pruning

---

**Interviewer**: "Walk me through the impurity calculation in this lab."

**Candidate**: "The lab computes entropy as E = −Σ pᵢ log₂(pᵢ) over the label
distribution. The code in `entropy(String[] labels)` counts labels into a map,
turns each count into a probability `p = c/n`, and accumulates `-p * (Math.log(p) /
Math.log(2))` — the `Math.log(2)` division converts natural logs to base 2, which
is the convention that makes entropy 1.0 for a balanced binary split. That's the
pure-node anchor: a node with a single class has entropy 0. For the play-tennis
dataset, the root has 9 yes and 5 no, so entropy is about 0.94 — high impurity,
which is exactly why the tree wants to split."

**Interviewer**: "How does `infoGain` decide which feature to split on?"

**Candidate**: "Information gain is the drop in impurity: gain(feat) = H(parent) −
Σ (nᵥ/n)·H(childᵥ). The code's `infoGain(data, labels, feat)` counts how many rows
take each value of the feature, partitions the labels by value into `subLabels`,
computes the weighted entropy `(subArr.length / n) * entropy(subArr)`, and subtracts
from the parent entropy. Then `buildTree` loops every unused feature and keeps the
one with maximum gain — the ID3 rule. In the walkthrough that picks
`price_tier` at the root of an Airbnb booking tree, because splitting listings by
price tier reduces label chaos more than any other feature does."

**Interviewer**: "The demo says the root splits on 'outlook' and reports 4/14
accuracy. Is that a bad model?"

**Candidate**: "No — that 4/14 is a quirk of the demo's simplified `predict`, not
the tree. The tree itself is built correctly: the root splits on outlook with
children rain, overcast, sunny, and each subtree recurses with `used.clone()` so
feature availability is tracked per branch. But the demo's predictor reads
`sample[0]` for every split — it doesn't map the node's feature name to a column —
so a humidity node reads 'sunny' instead of the humidity value and returns
'unknown'. The fix is a column map: `predict(node, sample, col)` looks up
`col.get(node.label)`. With that fix, the same build scores 14/14 on training
data. This is a great interview lesson: separate the learning algorithm from the
serving bug."

**Interviewer**: "What is the difference between ID3 and CART?"

**Candidate**: "Three differences. ID3 uses entropy and information gain and
produces multi-way splits — one branch per value — which is what this lab does:
sunny/overcast/rain, or low/mid/high price tiers. CART uses Gini impurity
1 − Σpᵢ² and produces binary splits, thresholding or bucketing so each split has
exactly two children. Gini has the same ordering behavior as entropy but skips the
logarithm, so it's faster on large datasets. For high-cardinality categoricals,
ID3-style multiway splits fragment the data badly, which is one reason CART's
binary splits are the production default; the lab gives you ID3's cleaner math and
CART as the industrial variant."

**Interviewer**: "When would you pick Gini over entropy?"

**Candidate**: "Performance, mostly. Gini is `1 - Σ p²` — a few multiplications and
a subtraction — while entropy needs a `Math.log`, which the lab's code calls per
class per node. On millions of candidate splits, that log is measurable CPU. The
ordering of candidate features is almost always identical between the two, so
tree structure barely changes. I use entropy when I want slightly sharper
sensitivity to distribution changes, Gini as the default, and I'm honest that the
practical difference is small — if your model's quality flips on the impurity
choice, your validation setup is too noisy."

**Interviewer**: "How does `buildTree` know when to stop?"

**Candidate**: "Three terminal cases. First, purity: if every remaining label is
the same, `uniqueLabels.size() == 1` and it returns a leaf with that class.
Second, exhaustion: if every feature is used but labels still differ, `bestFeat`
stays -1 and it returns a leaf with the `majority(labels)` class. Third — implicit
in this implementation but crucial in production — depth and minimum-samples
limits, which the lab deliberately omits so you see the raw algorithm. The
`used.clone()` on recursion is a subtle correctness point: each branch gets its own
copy of the used-feature mask, so a feature used in one subtree remains available
in sibling subtrees."

**Interviewer**: "The demo's play-tennis tree — how would the ID3 tree actually
look?"

**Candidate**: "Root: outlook. The overcast branch is pure (4/4 yes), so it's an
immediate leaf. The sunny branch has mixed labels, so it recurses and picks
humidity — sunny/high is all 'no', sunny/normal all 'yes'. The rain branch picks
wind — rain/weak all 'yes', rain/strong all 'no'. That's the classic textbook
tree, and the walkthrough rebuilds it on Airbnb features with the fixed predictor,
so the final structure — price_tier at root, then distance/wifi/pool deeper — is
fully visible in the children keys. A fully grown tree like this has zero training
error, which is the clue that it's overfit and needs depth control or
pruning."

**Interviewer**: "How does a random forest fix the overfitting of deep trees?"

**Candidate**: "Variance reduction by averaging. A single deep tree is
low-bias, high-variance — it adapts to every training sample, so it flops around
between resamples. Bagging trains T trees on bootstrap samples — draw n rows with
replacement — and averages predictions, and the average of many near-independent
high-variance models has far lower variance while bias stays put. Random forests
add the second randomization: at each split, only a random subset of features is
considered, which decorrelates the trees further. The lab's `buildTree` is exactly
the tree you'd wrap in that loop: train T copies on bootstrap draws, majority-vote
for classification, mean for regression."

**Interviewer**: "How would you implement feature importance from this lab's code?"

**Candidate**: "Mean decrease in impurity: every time `buildTree` chooses feature f
at a node, record the reduction in impurity that split produced — `bestGain`
weighted by the fraction of samples that pass through the node — and accumulate it
per feature across the whole forest. Features chosen at the root with big gains
accumulate the most. The alternative, permutation importance, is more robust:
shuffle one feature's values on the validation set and measure the accuracy drop.
The impurity version is nearly free — it's computed during training — but it
favors high-cardinality features, which is why I verify with permutation when the
stakes are high."

**Interviewer**: "What are the failure modes of axis-aligned trees you'd hit in
practice?"

**Candidate**: "Three classics. Extrapolation: trees predict by averaging leaf
training values, so a 10,000-square-foot listing falls into the largest leaf's
mean — no slope, no interpolation, unlike the linear models from Lab 01. Diagonal
structure: a boundary along amount + velocity = c takes many axis-aligned splits
to approximate, where a linear model or SVM gets it in one. And unstable
structure: deep trees flip their topology on tiny perturbations — the demo's tree
itself is data-hungry. Those are the reasons forests and boosting (Lab 09) exist:
the instability averages out, and the leaf-mean prediction becomes an ensemble of
many local models."

**Interviewer**: "How do trees handle missing values or new categories at serving
time?"

**Candidate**: "The lab's predictor shows the naive case: `child == null` returns
'unknown' when the value wasn't seen in training. Production systems do better.
For missing values, CART uses surrogate splits — pick the backup feature that best
mimics the primary split — or route by the majority branch. For new categories, a
back-off to the parent's majority class is common. There's a design decision
hiding here: categorical cardinality. One-hot encoding a zip code feature with
40,000 values explodes multiway trees; either CART binary-splits the one-hot
columns or you bucket rare values into an 'other' category during preprocessing."

**Interviewer**: "How deep should you let a tree grow?"

**Candidate**: "Deep enough to capture signal, shallow enough to generalize — and
the honest answer is cross-validation decides, not intuition. The lab grows trees
to full purity, which on real data guarantees overfitting: every training point
can end up in its own leaf. The standard recipe: tune max_depth, min_samples_split,
and min_samples_leaf via k-fold CV (Lab 10's `crossVal`), then optionally prune —
collapse subtrees whose error improvement doesn't beat a penalty. For the Airbnb
booking tree, a depth cap of 3–5 with a minimum leaf of ~20 bookings gives an
interpretable, stable model the product team can actually read."

**Interviewer**: "How does the tree's choice of root feature compare with the
logistic regression weight story from Lab 02?"

**Candidate**: "They answer the same question differently. Logistic regression says
'price tier shifts log-odds by a fitted coefficient' — a smooth, global, linear
effect. ID3 says 'price tier cuts entropy the most, so split there first' — a
local, discrete, hierarchical effect. The tree can model interactions for free:
the effect of wifi may only matter for mid-tier listings, and the tree finds that
in one subtree while logistic regression needs an explicit wifi×tier term. The
tradeoff is the flip side: the tree's splits are step functions that need depth to
approximate smooth slopes, and it doesn't give you a coefficient to quote. I
typically ship trees for ranking where interactions dominate, and linear models
where coefficients must be audited."
