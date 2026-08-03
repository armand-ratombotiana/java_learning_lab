# Lab 09: Mock Interview — Gradient Boosting

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Boosting vs bagging, residual fitting, learning rate, stumps, AdaBoost, XGBoost, hyperparameters

---

**Interviewer**: "Walk me through what this lab's boosting loop does, iteration by
iteration."

**Candidate**: "The `GBC.fit` loop runs `nEstimators` times. Each round starts
from the current raw predictions, converts them to probabilities with the
sigmoid — `prob[i] = 1/(1 + exp(-rawPred[i]))` — and computes the negative
gradient of the logistic loss: `residuals[i] = y[i] - prob[i]`, with
`weights[i] = prob[i]*(1 - prob[i])` as the curvature. Then `Stump.fit(X,
residuals, weights)` scans every feature and midpoint threshold, choosing the
split that minimizes the weighted squared loss of the residuals, and the stump
stores its left/right leaf values. Finally the ensemble updates
`rawPred[i] += lr * stump.predict(X[i])`. Repeat. That's gradient boosting in
its purest form: fit the current mistakes, step toward them."

**Interviewer**: "Why does the stump fit residuals instead of the labels?"

**Candidate**: "Because 'correcting the previous ensemble' is what boosting
means. The residual y − F(x) is the error the current model makes; a weak
learner fitted to residuals learns a correction term. The name 'gradient
boosting' comes from the general frame: the residuals are the negative gradient
of the squared-error loss, and for classification with log loss the pseudo-
residual is y − p — exactly the code's `residuals[i] = y[i] - prob[i]`. Each
stump is a step down the loss surface, and the learning rate shrinks each step.
The comparison to Lab 03's bagging is the key interview point: random forests
average independent high-variance trees; boosting greedily reduces bias, one
correction at a time."

**Interviewer**: "What is the learning rate doing here, and what happens if you
set it wrong?"

**Candidate**: "The learning rate shrinks each tree's contribution:
`rawPred += lr * stump.predict(...)`, so the model is F(x) = Σ η·hₘ(x). Small η
means each stump nudges the score a little, so the ensemble needs many more
trees but overfits less — the walkthrough shows lr=0.1 needing 200 stumps where
lr=0.5 needs 50. Large η makes each stump a big step: on noisy data the
ensemble chases training-set noise and test performance degrades — the classic
overfit signature. In practice I tune the pair (η, n_estimators) together with
cross-validation, and I keep the numbers honest: on this lab's separable demo,
all settings reach 1.00 training accuracy, because separable data hides
generalization differences — the walkthrough calls that out explicitly."

**Interviewer**: "The demo uses depth-1 stumps. Why such weak learners?"

**Candidate**: "Because boosting's whole bet is that a large ensemble of weak,
slightly-better-than-chance learners can be arbitrarily strong as a sum — each
stump corrects a slice of the previous error, and the ensemble's capacity comes
from depth in the sequence, not depth in the tree. A depth-1 stump on one
feature is one threshold split: with two features that's only a handful of
candidate cuts per round, which is why `Stump.fit` can afford to scan every
feature and every midpoint. Shallow learners also keep variance per member low,
so the ensemble's variance stays controlled even with hundreds of members. In
production I'd use max_depth 3–6 — deeper than a stump, shallower than a full
tree — but the lab's depth-1 choice makes the mechanism visible."

**Interviewer**: "What do `leftVal` and `rightVal` mean, and how are they
computed?"

**Candidate**: "They're the leaf values of the stump: the constant the stump
predicts on each side of the threshold. The code computes weighted means of the
residuals: `lVal = lSum / lW` where `lSum` accumulates `residuals[i] *
weights[i]` for points on the left of the cut, and `lW` accumulates the
weights — the same for the right. Weighting by `prob*(1-prob)` is the Newton-
style second-order refinement: points where the model is uncertain get less
influence. The split chosen is the one minimizing weighted squared loss over the
residuals, `loss += weights[i] * err²`. So a stump isn't just 'a threshold' —
it's the optimal constant approximation of the current residuals on each side
of the best cut."

**Interviewer**: "How does AdaBoost compare with this gradient boosting?"

**Candidate**: "Same architecture, different engine. AdaBoost works directly on
sample weights: train a weak learner on the weighted data, compute its error,
give it weight α = ½ln((1−e)/e), and multiply misclassified samples' weights by
e^α so the next learner focuses on them. The lab's GUIDE describes exactly that
recipe. Gradient boosting generalizes it: instead of tweaking sample weights,
fit the learner to the negative gradient of a chosen loss — which recovers
AdaBoost when the loss is exponential. The practical consequence: gradient
boosting lets you pick the loss for the problem — squared for regression,
logistic for classification, custom for ranking — while AdaBoost is tied to its
error-based update. The lab implements the gradient variant, which is the one
modern libraries ship."

**Interviewer**: "What makes XGBoost faster than the plain loop here?"

**Candidate**: "Five engineering tricks, per the GUIDE. Approximate greedy split
finding: instead of scanning every midpoint like this lab's `Stump.fit`, it
buckets feature values into percentiles and evaluates only bucket boundaries.
Sparsity-aware learning: missing values get routed down the best side, with a
learned default direction. Cache-aware access: data is stored so the split
evaluation touches contiguous memory. Parallelized tree building: feature
histograms are computed in parallel across CPU cores. And it adds
regularization — L1/L2 on leaf weights plus min_child_weight — which makes
individual trees conservative, the boosting equivalent of the learning-rate
insurance. Same math as this lab; the engineering is what differs."

**Interviewer**: "Which hyperparameters matter most, and how do they interact?"

**Candidate**: "The GUIDE's list: n_estimators, learning_rate, max_depth,
subsample, colsample_bytree, min_child_weight. The core interaction is
(learning_rate, n_estimators): halve η and roughly double the tree count, tuned
jointly with early stopping on a validation set. max_depth controls per-tree
capacity — depth 3–6 typical; deeper trees need smaller η. subsample and
colsample_bytree inject the bagging-style randomness from Lab 03 into boosting —
row and column sampling — which reduces overfitting and is the single biggest
leverage point on wide, noisy data. min_child_weight prevents splits on tiny
fragments. The mental model: learning rate sets how conservatively you walk,
depth sets how far each step reaches, and the sampling knobs inject variance
reduction."

**Interviewer**: "What are the failure modes of gradient boosting on real data?"

**Candidate**: "Three. Overfitting through tree count: more stumps always lower
training loss, so without early stopping or a validation-based stopping rule
you learn noise — the walkthrough's 50 trees on 6 points is already at the
boundary of what's justified. Categorical and high-cardinality features: the
stump scan picks splits on raw values, so one-hot columns get underused per
tree — modern practice encodes categories with target encoding before boosting.
And interaction blindness per tree: a stump is one feature, so interactions
need multiple trees to build; with deep enough trees and enough rounds they
emerge, but it's why boosting's hyperparameter sensitivity is real. The honest
recipe: shallow trees, small η, many rounds, early stopping, and
cross-validation everywhere."

**Interviewer**: "How does boosting's bias-variance story differ from bagging's?"

**Candidate**: "Bagging reduces variance: independent high-variance trees
average out, leaving bias untouched. Boosting reduces bias: sequential
learners keep fixing the residuals, which is a bias attack, while each learner
stays weak to control variance. So they're complementary pills: if a random
forest underfits, boosting is often the better next try, and vice versa. The
interview nuance: modern boosted ensembles with subsampling, shrinkage, and
regularized trees also cut variance substantially — the lines blur — but the
mechanism distinction is the answer: parallel averaging (Lab 03) versus
sequential correction (Lab 09). The lab sequence makes it visceral: both wrap
the same `Stump`-like tree, but the training loops are philosophically
opposite."

**Interviewer**: "How do you explain this model's predictions to a business
audience?"

**Candidate**: "Same toolkit as forests: feature importance — for boosting,
usually measured as average gain across splits or permutation-based accuracy
drop — plus SHAP-style attribution when available. The lab's stump makes even
the ensemble legible at a coarse level: after 50 rounds, the trip is late if
its accumulated score crosses zero, and the dominant stumps can be listed:
'the first split on traffic > 2.75 alone accounted for most of the signal'.
The honest caveat: a 50-stump sum is not narratable the way a single Lab 03
tree is — so for regulated, must-explain decisions I'd often pair a boosting
model with a tree-based surrogate or limit depth. For ranking and forecasting,
where accuracy wins, the explainability cost is usually accepted."

**Interviewer**: "How would you validate this model beyond the demo's accuracy
print?"

**Candidate**: "The demo prints per-trip correctness and 6/6 — that's a smoke
test. Real validation: temporal split (trips yesterday train, today tests),
k-fold CV from Lab 10 for hyperparameter choice, and confusion-matrix metrics —
for late-arrival prediction, recall of actual lateness matters more than raw
accuracy, because the cost of an unpredicted late trip is high. Then
calibration: bucket predicted probabilities against observed late rates, since
the sigmoid of the raw sum is only as calibrated as the model. And monitoring
at serving: the residual distribution y − p per hour is the same signal the
training loop optimizes, so a drift in residuals means the trip distribution
changed — retrain from a sliding window, just like the lab refits stumps to
residuals."
