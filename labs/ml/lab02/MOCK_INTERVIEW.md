# Lab 02: Mock Interview — Logistic Regression

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Sigmoid, cross-entropy loss, decision boundary, gradient descent, class imbalance, regularization

---

**Interviewer**: "Why does the lab use a sigmoid instead of a raw linear score?"

**Candidate**: "Because a raw linear score is unbounded and uninterpretable as a
probability. The sigmoid σ(z) = 1 / (1 + e⁻ᶻ) maps any real `z` into (0, 1), so the
output can be read as P(class = 1). The lab's `sigmoid(double z)` is exactly that
one-liner, and `predictProb` computes the linear score `z = Σ wᵢxᵢ` — note the
features carry a leading bias column of 1s, so `w[0]` is the intercept — before
passing it through. In the fraud walkthrough, the learned weights
[-8.95, 0.86, 1.99] mean a $100 bump in transaction amount adds 0.86 to the log-odds,
which is how I explain the model to risk analysts."

**Interviewer**: "Walk me through the decision boundary in your code."

**Candidate**: "The boundary is where the model is indifferent: σ(βᵀx) = 0.5, which
collapses to βᵀx = 0 because the sigmoid is monotonic and crosses 0.5 at z = 0. In
the code, `predictClass` implements the rule directly: `predictProb >= 0.5 ? 1 : 0`.
For the fraud demo that boundary is -8.95 + 0.86·amount + 1.99·velocity = 0 — a line
in feature space, since the model is linear in the features. The walkthrough prints
that line explicitly so the team can see exactly where the risk split sits and can
shift it later by moving the 0.5 threshold instead of retraining."

**Interviewer**: "Why cross-entropy instead of MSE for training?"

**Candidate**: "Convexity. With a sigmoid hypothesis, the squared-error loss is
non-convex in the weights — there are local minima, and gradient descent can get
stuck. Cross-entropy, J(β) = −(1/m) Σ [y log(ŷ) + (1−y) log(1−ŷ)], is convex in β for
logistic regression, so gradient descent provably converges to the global optimum.
There's also a statistical argument: cross-entropy is the negative log-likelihood of
the Bernoulli model, so minimizing it is maximum-likelihood estimation, and the
gradient takes the elegant form (1/m) Σ (h(x) − y)·x — exactly what the lab's `fit`
computes as `err = pred - y` times `X[i][j]`. The same clean gradient is why the
update loop looks so similar to linear regression's."

**Interviewer**: "Your demo's weights are -9.99, 3.70, -0.91 and accuracy is 1.00.
What do you make of that?"

**Candidate**: "The negative `w[0]` is the bias: with zero features, the log-odds
start near -10, so small transactions are strongly classified as safe. The demo data
is two clean clusters, so a separating line exists and 5000 epochs at lr 0.1 drive
training accuracy to 100%. I would not trust that 1.00 at face value — it's training
accuracy on 8 points, which says nothing about generalization. The more interesting
artifact is `w[2] = -0.91`: in that particular data arrangement the second feature is
slightly redundant after the first, so its coefficient stays small. With real fraud
data I'd expect correlated features to produce exactly this kind of unstable-looking
coefficient — which is where regularization comes in."

**Interviewer**: "Your test point (4.0, 4.5) comes back prob=0.6698, class=1. Why
isn't the probability closer to 1?"

**Candidate**: "Because it sits near the decision boundary, and probability decays
smoothly as you approach it. The log-odds there are ln(0.6698/0.3302) ≈ 0.71 — close
to zero — so the point is only mildly inside the class-1 region. This is a feature,
not a bug: the sigmoid gives us calibrated confidence, which lets us defer
ambiguous transactions to human review instead of hard-cutting at 0.5. In the fraud
walkthrough I exploit exactly that: a $250 transaction at velocity 1.2 scores
prob=0.0121 and is auto-approved, while a $600 one at velocity 4.8 scores 0.9970 and
is blocked."

**Interviewer**: "How does the gradient update in `fit` work, step by step?"

**Candidate**: "Three loops. The epoch loop runs the whole batch 5000 times. The
sample loop computes `pred = predictProb(w, X[i])` and `err = pred - y[i]` — the
residual between predicted probability and true label — then accumulates
`grad[j] += err * X[i][j]` for every feature. After the batch, the update loop steps
every weight `w[j] -= lr * grad[j] / m`, dividing by the sample count m so the step
is the mean gradient. Because cross-entropy's gradient is `(h − y)x`, the residual
form is identical to linear regression's — the hypothesis changed, the machinery
didn't. That's the core insight of the whole lab sequence."

**Interviewer**: "Fraud datasets are ~0.1% positive. How does that break this lab's
code, and what do you do?"

**Candidate**: "Three breakages. First, `accuracy` becomes nearly useless: predicting
all-zeros scores 99.9%. Second, the decision boundary collapses — the optimizer
drifts toward the majority class because every sample's error is dominated by
negatives. Third, the 0.5 threshold is wrong for the business, since one false
negative costs more than many false positives. Fixes: class weights in the loss
(up-weight the minority), oversample fraud cases, and — most importantly — pick
the operating threshold from a precision-recall curve rather than 0.5, and measure
with precision/recall/F1 (Lab 10) instead of accuracy. The lab code trains on
balanced synthetic data, which is the clean case that makes the mechanics visible."

**Interviewer**: "What would L1 or L2 regularization change in this implementation?"

**Candidate**: "The loss gains a penalty term — L2 adds λ‖w‖², L1 adds λ‖w‖₁ — so
the gradient gets an extra 2λw or λ·sign(w) term. In code that's one more line in
the update loop of `fit`: `w[j] -= lr * (grad[j]/m + λ * w[j])` for L2. What it buys
for fraud: L1 pushes irrelevant velocity-like features to exactly zero, giving a
sparse, cheap-to-evaluate model; L2 keeps features but shrinks them, which stabilizes
coefficients when features are correlated. The demo's near-zero `w[2]` is precisely
the situation regularization exists for. For high-volume payment scoring, a sparse
L1 model saves real latency per transaction."

**Interviewer**: "How do you evaluate this classifier properly — which metrics?"

**Candidate**: "Start with the confusion matrix — TP, FP, FN, TN (Lab 10's
`confusionMatrix`). From it, precision = TP/(TP+FP) — of flagged fraud, how much was
real — and recall = TP/(TP+FN) — of real fraud, how much we caught. F1 balances
them. For Stripe, recall dominates at first: missed fraud is direct loss, while
false positives are just friction; later we tune for precision to reduce the
friction on legitimate users. AUC summarizes threshold-independent ranking quality,
but with 0.1% positives I look at the precision-recall curve, not ROC — a
misleadingly high AUC hides a uselessly low precision at high recall."

**Interviewer**: "What does the lab's claim that logistic regression is 'linear' in
log space mean, and when does that limitation hurt?"

**Candidate**: "ln[p/(1−p)] = βᵀx is linear in the features, so the decision
boundary is a hyperplane. That hurts whenever the boundary is nonlinear — fraud
patterns like 'high amount AND high velocity' with interactions are still fine,
but something like a ring-shaped risk region is not. The fixes are the standard
bag of tricks: add polynomial features (amount², amount·velocity), use a kernel
trick à la Lab 04, or move to trees (Lab 03/09) that partition adaptively. The
beauty of the lab's implementation is that adding features costs nothing in code —
just append columns to X — so you can always test whether the linearity
assumption is the bottleneck."

**Interviewer**: "How important is feature scaling for this gradient descent?"

**Candidate**: "Very. The update `w[j] -= lr * grad[j]/m` uses one global learning
rate; if features live on different scales — say amount in dollars, thousands,
against a velocity in units — the gradient components differ by orders of
magnitude and one lr either diverges the big feature or starves the small one. The
lab data is already scaled to small values, which is why lr 0.1 works. In the
walkthrough I deliberately keep the engineered features in $100s and transactions
per hour. Standard practice: z-score or min-max scale before `fit`, and remember
the decision boundary formula then lives in scaled space — interpret carefully."

**Interviewer**: "How do you get probabilities versus decisions in production?"

**Candidate**: "The lab separates them cleanly: `predictProb` returns the calibrated
probability, `predictClass` applies the 0.5 threshold. In production I'd serve the
probability and let downstream policy decide: auto-approve under 0.3, human review
between 0.3 and 0.7, block over 0.7. That threshold band is business policy, not
model output — it changes with fraud economics without retraining. I also log the
raw probability per transaction, because calibration drift is the earliest signal
that the feature distribution moved. And when the team asks 'why did this get
blocked', the linear log-odds let me show the contribution per feature, which is
the audit trail regulators want."

**Interviewer**: "What is your approach to validate this model before shipping?"

**Candidate**: "Hold out a temporal test set — fraud evolves, so random splits
leak future patterns into training. Then report the full confusion-matrix metrics
(Lab 10), not just accuracy. I'd run k-fold CV on the training portion to pick the
learning rate and any regularization strength, then fit once on the full training
window and freeze. Finally, sanity-check calibration: predicted probability should
match observed fraud rate in buckets — if 0.9-bucket fraud is 60%, recalibrate.
The lab's `fit(X, y, 0.1, 5000)` gives me a fast iteration loop; validation is
where I spend the real time."

**Interviewer**: "Your walkthrough predicts a new transaction as 0.9970 fraud.
Walk me through that number's path through the code."

**Candidate**: "`predictProb(w, t)` computes z = -8.95 + 0.86·6.0 + 1.99·4.8 = 5.75,
then σ(5.75) = 1/(1 + e⁻⁵·⁷⁵) ≈ 0.9970. `predictClass` compares 0.9970 ≥ 0.5, so
the label is 1. Two things I like about showing this: the weights translate
directly into log-odds contributions — the velocity feature, weight 1.99, is
doing the heavy lifting at 4.8 transactions per hour — and the probability output
gives the risk team a graded score, not just a flag. If the business later says
'we only block at 0.99', I move the threshold without touching the model."
