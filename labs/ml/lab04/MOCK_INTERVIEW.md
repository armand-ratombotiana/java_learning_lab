# Lab 04: Mock Interview — Support Vector Machines

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Maximum margin, primal hinge loss, subgradient descent, kernels, soft margin, C tuning

---

**Interviewer**: "What is the SVM objective your lab optimizes?"

**Candidate**: "The primal soft-margin objective: minimize ½‖w‖² + C·Σ ξᵢ subject to
yⁱ(w·xⁱ + b) ≥ 1 − ξᵢ. The lab implements it as hinge loss — for each sample with
`margin = y[i] * dot`, the code checks `if (margin < 1)`: inside the margin, the
sample contributes a penalty and the gradient is `w - C·y[i]·X[i][j]`; outside, only
the regularization term `w` remains. The ½‖w‖² term is the margin maximizer —
maximizing margin 2/‖w‖ is equivalent to minimizing ‖w‖² — and C trades margin
width against misclassification tolerance. That single `if` is the entire SVM
philosophy in one branch: points safely beyond the margin only shrink the weight;
points inside the margin push the boundary toward them."

**Interviewer**: "Your demo reports 0.50 training accuracy on separable data. What
went wrong?"

**Candidate**: "Convergence, not capacity — this is the classic subgradient-descent
failure mode, and it's a gift in disguise for interviews. The lab calls
`fit(X, y, 1.0, 0.01, 2000)`: C=1.0 with 2000 epochs. The regularization term `lr*w`
dominates the early updates, the weights stay small — around 0.33 — and the
decision plane `w·x ≥ 0` with that bias never fully separates the clusters, so half
the points misclassify. The walkthrough demonstrates the fix: raising C to 5.0 and
running 20000 epochs pushes the weights to [-1.42, 0.24, 0.26] and accuracy to 1.00.
Lesson: with subgradient methods you must verify convergence — 0.50 here is a
training-loop symptom, not a model-capacity verdict."

**Interviewer**: "What exactly are support vectors, and where are they in your code?"

**Candidate**: "Support vectors are the training points that lie on or inside the
margin — the ones with yⁱ(w·xⁱ) ≤ 1 — and they alone determine the decision
boundary; delete the others and the solution is unchanged. In the code, they're
precisely the samples that take the `margin < 1` branch in `fit`, contributing
`-C·y[i]·X[i][j]` to the gradient. In the demo's separable data, the four
boundary-adjacent points on each cluster define the margin, and the learned
weights are the normal vector of the plane halfway between them. This is also the
sparsity story: at prediction time, only those points matter, which is why kernel
SVMs can be fast to evaluate even in high dimensions."

**Interviewer**: "How does the kernel trick replace the dot product, and why is it
'free'?"

**Candidate**: "The dual formulation only ever touches training points through
inner products xᵢ·xⱼ, and the kernel K(xᵢ, xⱼ) computes that inner product in a
feature space we never materialize: linear xᵢ·xⱼ, polynomial (γ·xᵢ·xⱼ + r)ᵈ, and
RBF exp(−γ‖xᵢ−xⱼ‖²). The 'free' part: computing K is O(d), but it's equivalent to
a dot product in an infinite-dimensional space for RBF — the mapping is defined
implicitly. In the walkthrough's kernel check, RBF with γ=0.5 gives K(p0,p1) =
e^(−0.5·0.5) = 0.7788 for nearby points but K(p0,p2) = 0.0001 for far ones — the
kernel is a similarity measure. The catch: the kernelized decision function is a
weighted sum over support vectors, so training data must be kept at serving time."

**Interviewer**: "What does C control, and how do you pick it?"

**Candidate**: "C is the misclassification budget: small C allows more margin
violations in exchange for a wide margin, large C forces points to be correctly
classified, and as C → ∞ you recover the hard-margin SVM. The demo shows the
practical side: C=1.0 was too weak relative to the regularization pressure, and
C=5.0 separated the clusters. In production I tune C with cross-validation (Lab
10's `crossVal`) on a grid of log-spaced values, because the right C depends on
noise: clean data wants large C, noisy data wants small C to avoid chasing
outliers. There's a nice symmetry with logistic regression's λ — C ≈ 1/λ — so my
tuning instincts carry over between labs."

**Interviewer**: "When is the RBF kernel a trap?"

**Candidate**: "Two traps. First, overfitting: with the right γ, RBF can memorize
the training set — each point becomes its own island, so γ must be tuned just like
C. Second, scalability: the kernel matrix is n×n, and RBF means every training
point is a support vector in the worst case, so prediction cost grows with the
dataset — no good for Twitter-scale serving with millions of tweets. Practical
rules: try linear first (TF-IDF text features are often linearly separable in
high dimension), use RBF when the boundary is genuinely curved and the dataset is
moderate, and scale features before computing γ·‖xᵢ−xⱼ‖² — the demo's 2D data
looks separable precisely because the features are on one scale."

**Interviewer**: "How do you get probabilities out of an SVM?"

**Candidate**: "You don't, natively — an SVM outputs a signed distance to the
plane. The standard calibration is Platt scaling: fit a logistic function over the
decision values on a holdout set to map them to P(class=1). For content
moderation, that matters: a moderation queue needs a graded risk score to rank
tweets, not just ±1. The alternative is to train logistic regression directly —
Lab 02's `predictProb` gives calibrated probabilities for free, at the cost of the
margin framing. In practice I often run both: SVM for the decision quality, then
calibrate, or just note that the two models agree on boundaries when the data is
separable."

**Interviewer**: "Your features have a bias column of 1s. What does the learned
bias actually do?"

**Candidate**: "The bias shifts the plane off the origin. The geometric margin
formulation with `y(w·x) ≥ 1` needs a free parameter b because the plane doesn't
have to pass through the origin — the lab's leading 1 column absorbs b as w[0],
so the decision rule stays `dot(w, x) ≥ 0`. In the demo, w[0] = -1.42 is
exactly what balances the two clusters: the safe cluster's scores come out
negative and the toxic cluster's positive. Without the bias column, the fitted
plane would be forced through the origin and the demo's data — centered around
(1, 2) and (5, 6) — would never separate. It's the same trick logistic regression
used, and the same reason every lab's feature rows start with a 1."

**Interviewer**: "SVM versus logistic regression — when do you choose which?"

**Candidate**: "They're cousins: both linear classifiers with different losses.
Logistic regression minimizes log loss and gives calibrated probabilities, so it's
the default when downstream decisions need scores or when interpretable
coefficients matter. SVM minimizes hinge loss with the margin framing — it's
sparse in support vectors and, with kernels, handles nonlinear boundaries, so it
wins on hard classification problems with moderate data. The lab sequence makes
the comparison concrete: Lab 02's demo and Lab 04's demo are nearly the same
synthetic clusters, and both separate them — but logistic regression converges
cleanly with default settings while the SVM's subgradient loop needs
convergence care, which is a realistic operational difference worth remembering."

**Interviewer**: "How do you handle multiclass with an SVM, which is binary?"

**Candidate**: "One-vs-rest: train K binary SVMs, each separating one class from
the rest, and assign the class whose SVM has the largest decision value. That's
what the demo's binary structure generalizes to — the lab's `fit` and `predict`
stay untouched, just run K times. The alternative, one-vs-one with voting,
explodes combinatorially. For the moderation use case, classes would be
'safe', 'borderline', 'toxic' — two one-vs-rest SVMs, or a threshold pair on the
calibrated score. And for real production multiclass, I'd usually skip to a
forest or boosting model, because K SVMs at Twitter scale get expensive fast."

**Interviewer**: "How does the margin concept fail on imbalanced data?"

**Candidate**: "The hinge loss sums per-sample penalties equally, so with 0.1%
positives the boundary gets pulled toward the minority cluster — the plane can
slide past it entirely and still pay a tiny total penalty. Two fixes: class
weights (scale C per class, e.g., C_pos = C·(n_neg/n_pos)) or oversampling the
minority. There's a subtler point: the margin itself is measured in feature
units, so unscaled features dominate it — a link-density feature on [0, 1] is
never heard against a raw count feature in the thousands. The walkthrough data is
pre-scaled, but I always check feature ranges before trusting a margin."

**Interviewer**: "How would you test this SVM before production?"

**Candidate**: "Split temporally — moderation labels drift — then report
precision/recall/F1 per class (Lab 10's confusion-matrix metrics), not raw
accuracy, because 'safe' dominates. Validate that training converged: the demo's
0.50 is the warning example — I'd re-run with 10x epochs and confirm accuracy and
weights stabilize. Then check the support-vector fraction: if nearly every
training point is a support vector, the model is likely overfit and C or γ are
mispicked. Finally, calibrate the decision values and monitor the score
distribution in production — a drift in the moderation queue's decision-value
histogram is the earliest signal that content characteristics changed."
