# Lab 06: Mock Interview — Naive Bayes Classifier

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: Bayes theorem, Gaussian NB, log-space posteriors, naive assumption, Laplace smoothing, text classification

---

**Interviewer**: "Walk me through the math this lab implements, from Bayes to the
final label."

**Candidate**: "Bayes theorem says P(y|x) ∝ P(y)·P(x|y). The naive assumption makes
P(x|y) factor into a product over features, Πᵢ P(xᵢ|y), and Gaussian NB models
each P(xᵢ|y) as a normal density. The lab's `GaussianNB.fit` computes the class
priors — `samples.size() / n` — plus per-class mean and sample variance for every
feature, `var[j] /= Math.max(1, samples.size() - 1)`, and `predict` scores each
class as `log(prior) + Σ log(pdf(x_j, mu_j, v_j))`. The `Math.max(1, ...)` guard
is a real detail: a single-sample class would otherwise divide by zero. The class
with the highest log-posterior wins."

**Interviewer**: "Why does the code work in log space instead of multiplying
probabilities directly?"

**Candidate**: "Underflow. With 50 features at probabilities around 0.1, the
product is 10⁻⁵⁰ — below double precision's useful range — and every class would
score zero, making the argmax meaningless. The code sums log-probabilities
instead, which keeps the ranking identical because log is monotonic. There are
two defensive touches in the lab: `gaussianPdf` adds `eps = 1e-9` inside the
variance so a zero-variance feature can't blow up, and `predict` adds `1e-12`
inside `Math.log(...)` so a pathological PDF value can't produce log(0) = −∞.
These are exactly the numerical guards you'd keep in a production spam filter."

**Interviewer**: "The demo scores 1.00 training accuracy on the Iris-style data.
What does that tell you?"

**Candidate**: "That the three synthetic clusters are cleanly separated by the
per-class Gaussian means — the setosa-like cluster sits around (5.0, 3.6),
versicolor around (6.8, 3.2), virginica around (6.0, 2.7) — with variances small
enough that the density peaks don't overlap. Training accuracy on the training
set is a sanity check, not a verdict: with 5 samples per class, the variance
estimates are noisy, and the real test is the three holdout emails, which all
classify correctly. The lesson I'd defend in an interview: naive Bayes shines
precisely on small, high-dimensional datasets where variance-hungry models
starve."

**Interviewer**: "When would you pick Gaussian versus Multinomial versus Bernoulli
Naive Bayes?"

**Candidate**: "The likelihood family must match the feature type. Gaussian — what
this lab implements — fits continuous features like the spam-word ratio and link
ratio in the walkthrough, parameterized by mean and variance. Multinomial fits
counts — word frequencies in a document — with likelihood (count + 1)/(N_c + |V|)
under Laplace smoothing. Bernoulli fits binary presence/absence, useful when
'mentions the word at all' matters more than 'mentions it 17 times'. Spam
filtering famously uses Multinomial or Bernoulli over a token vocabulary. The
choice is a modeling assumption about the feature distribution, and mixing the
wrong family with the right data silently degrades the posterior."

**Interviewer**: "Why is Laplace smoothing needed, and where would this lab's
Gaussian code need it?"

**Candidate**: "Smoothing prevents zero probabilities. In Multinomial NB, an
unseen word in a test document gets count 0, so P(x|y) = 0 and the entire
posterior product collapses to zero — one unknown word kills the class. The
fix, P = (count + 1)/(N_c + |V|), reserves probability mass for unseen events.
The walkthrough computes it directly: a word seen 3 times in 10 spam words over a
4-word vocabulary gets (3+1)/(10+4) = 0.2857, while an unseen word still gets
1/14 = 0.0714 instead of 0. This lab's Gaussian variant doesn't need count
smoothing — the continuous density is never exactly zero — but it does add `eps`
for the degenerate zero-variance case, which is the Gaussian cousin of Laplace."

**Interviewer**: "What does the 'naive' assumption actually buy you, and when does
it hurt?"

**Candidate**: "It buys tractability: factoring P(x|y) into a product means each
feature's parameters are estimated independently, so with d features you fit d
univariate Gaussians per class — O(d·n) total — instead of one d-dimensional
Gaussian with a d×d covariance matrix, which needs O(d²) parameters and blows up
with correlated features. It hurts when features are strongly correlated: the
product double-counts shared evidence. In spam, 'free' and 'offer' co-occur, so
their densities each claim the same evidence, and the posterior becomes
overconfident. Empirically the rankings survive the error — correlated noise
hurts both classes roughly equally — which is why naive Bayes still works in
text, but if you need calibrated probabilities, model the correlation or use a
linear model like Lab 02."

**Interviewer**: "Your walkthrough classifies emails on two features. How does
this extend to a real word-vocabulary spam filter?"

**Candidate**: "The architecture is the same; the features change. Instead of
two engineered ratios, you'd have 10,000 word-frequency features — one per
vocabulary token — fed to Multinomial or Bernoulli NB. That's where the naive
assumption earns its keep: 10,000 independent univariate estimates is cheap,
while any model modeling the joint word distribution needs orders of magnitude
more data. The lab's `fit` and `predict` shapes are unchanged — `double[][] X`
just gets wider. The `eps` and log-space machinery become mandatory at that
scale, which is exactly why the lab builds them in. The tradeoff to mention:
sparse binary features let Bernoulli NB run in microseconds per email, which is
why Gmail-scale filtering has been naive Bayes for decades."

**Interviewer**: "Is the decision boundary of naive Bayes linear? What are the
implications?"

**Candidate**: "Yes — in log space the decision is a sum of per-feature log
ratios, which is a linear function of the features, so the boundary is a
hyperplane. That's a nice symmetry with logistic regression from Lab 02: naive
Bayes generates the same family of boundaries but from the generative side —
model P(x|y), then invert with Bayes — while logistic regression is
discriminative — model P(y|x) directly. The implication is practical: if your
data isn't linearly separable, naive Bayes won't magically fix it, and you'd
reach for trees (Lab 03) or kernels (Lab 04). The generative framing does give
you something extra, though: you can sample synthetic emails from the fitted
distributions for debugging."

**Interviewer**: "How do priors behave when the classes are imbalanced?"

**Candidate**: "Priors are the class frequencies: with 99% ham, log(prior) starts
every ham score 4.6 points ahead of spam — that's log(0.99/0.01) — and a spam
email must produce strong feature evidence to overcome it. That's statistically
correct behavior, but it explains two real-world effects: naive Bayes spam
filters have a 'safe' prior bias that under-flag novel spam, and the optimal
threshold is not 0.5 — you'd shift the decision boundary or reweight priors to
trade recall for precision. The lab's balanced 5/5 data hides this; the fix in
production is to report precision/recall (Lab 10) and pick the operating point
from the business cost, not from symmetry."

**Interviewer**: "How do you compare naive Bayes against the other classifiers in
this lab series?"

**Candidate**: "Three axes. Data: naive Bayes wins on small, high-dimensional
data — text — because it needs only univariate estimates; logistic regression
and SVMs need enough data to tune a joint boundary; trees need enough to keep
splits stable. Calibration: naive Bayes posteriors are often overconfident due to
double-counted correlations, while logistic regression's are calibrated by
construction — so for a scoring pipeline, Lab 02 wins unless I recalibrate.
Interpretability: NB gives you per-feature log-ratio contributions — 'this
email is spam because free and offer both fired' — which is as auditable as a
tree path. In practice I benchmark all three with Lab 10's cross-validation and
let the metrics decide, but NB is the cheapest baseline that's never
embarrassing."

**Interviewer**: "How do you monitor a deployed naive Bayes filter?"

**Candidate**: "Three signals. Score drift: the distribution of log-posterior
margins should stay stable; a widening margin for one class means the feature
means are drifting — re-fit the per-class means, which is cheap because fit is
just averaging. Precision/recall per period: spam filters are graded on recall
of true spam and precision against false positives — a mislabeled ham email is
a support ticket, and the metrics from Lab 10 catch both directions. Retraining
cadence: spam adapts within weeks, so the walkthrough's fit-once demo is the
prototype, not the system — I'd re-estimate means, variances, and priors daily
from a sliding window, and that's the real advantage of NB's cheap fit over the
other models in this lab series."

**Interviewer**: "What would you do if a feature had near-zero variance in the
training data?"

**Candidate**: "The lab's `gaussianPdf` handles it gracefully: `2*var + 1e-9`
keeps the exponent finite, and the density becomes a narrow spike around the
mean — the feature votes almost deterministically. That's defensible when the
feature genuinely is constant within a class, like 'the sender's domain is
internal'. But there's a trap: sample variance on 3 samples is a noisy estimate,
and a spike built on noise over-commits the posterior — the `var[j] /=
Math.max(1, samples.size() - 1)` denominator is already using Bessel's
correction for exactly this reason. In production I add a variance floor
rather than the bare `eps`: a minimum variance keeps the spike from becoming a
single-point gamble."
