# Lab 05: Mock Interview — Correlation & Regression

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Pearson correlation, Spearman rank correlation, OLS regression, normal equations, Gaussian elimination, R-squared

---

**Interviewer**: "Walk me through the correlation measures in this lab and when each is appropriate."

**Candidate**: "Three related but distinct tools. Pearson's r — `pearson`, the covariance
of z-scores: sum((x - x-bar)(y - y-bar)) / sqrt(sum(...)·sum(...)) — measures *linear*
association on the original scale, so it's the right choice when both variables are
continuous and the relationship is roughly straight. Spearman's rho — `spearman`, the
Pearson correlation computed on *ranks* — measures monotonic association and is robust
to outliers and nonlinear but monotonic relationships. And the lab's regression machinery,
which extends correlation to prediction. The demo runs the canonical cases: perfect
positive (r = 1.0), perfect negative (r = -1.0), nearly perfect (0.999587), a moderate
clean case (0.714286), and the perfectly tied case (1.0). The teaching point: r = 0
doesn't mean 'no relationship' — it means 'no linear relationship', and Spearman is the
check for what's hiding in the curve."

**Interviewer**: "Derive the OLS regression estimates for the slope and intercept."

**Candidate**: "OLS minimizes the sum of squared residuals, sum over i of (y_i - a - b·x_i)^2.
Setting the two partial derivatives to zero gives the normal equations. From the slope
derivative: sum(x_i·(y_i - a - b·x_i)) = 0; from the intercept derivative: sum(y_i - a -
b·x_i) = 0. Solve: the intercept equation says the residual mean is zero — a = y-bar -
b·x-bar — the regression line passes through the means. Substituting and solving the
slope equation gives b = sum((x_i - x-bar)(y_i - y-bar)) / sum((x_i - x-bar)^2) — the
covariance divided by the variance of x. The lab's `SimpleRegression` implements exactly
this closed form. The demo: the height-weight case gives Weight = -143.0 + 5.0·Height —
for every extra inch, 5 more pounds — and R-squared 0.999997, so the linear model
explains essentially all the variation."

**Interviewer**: "What is R-squared, and what is it not?"

**Candidate**: "R-squared is the proportion of variance in y explained by the model:
1 - SSE/SST, where SSE is the residual sum of squares and SST is the total variation of
y around its mean. The lab's `simpleRegression` returns `rsquared = 1 - sse/sst`, and the
demo's heights/weights give R^2 ≈ 1.0 while the noisy marketing case gives R^2 =
0.510204. What it is not: not a measure of causal strength — a high R^2 can come from
spurious correlation; not a measure of model correctness — you can get high R^2 with a
misspecified model (curvature, outliers) or overfitting, which is why the multiple
regression demo's recovery of exact coefficients — 1.0000, 2.0000, -0.5000 with residuals
at -0.0000 — is the honest check: R^2 near 1 plus residuals near zero plus recovered
coefficients is what a *correct* model looks like."

**Interviewer**: "What are the normal equations, and how does the lab solve them?"

**Candidate**: "In matrix form the OLS problem is: minimize ||y - X·b||^2, which gives
X^T·X·b = X^T·y — the normal equations. X^T·X is the Gram matrix: it's symmetric, and
its entries are the pairwise sums of products of the predictors — the correlation
structure of the design, scaled. The lab's `LinearRegression` builds X^T·X and X^T·y
directly and solves the system with Gaussian elimination with partial pivoting —
`eliminate` scales each pivot row to a leading 1 and subtracts multiples of it from the
rows below, swapping rows when the pivot is (near) zero to keep the solution stable. The
demo's three-predictor case recovers the true coefficients exactly, residuals -0.0000 —
the full pipeline working end to end, from data to normal equations to solved
coefficients to predictions."

**Interviewer**: "Why does the lab solve normal equations with Gaussian elimination instead of matrix inversion?"

**Candidate**: "Because inversion is a worse tool for the job. Computing X^T·X inverse
directly costs more and, more importantly, magnifies numerical error: the inverse's
entries can be large and unstable when the matrix is nearly singular, and any error in
the inverse pollutes the solution in every coordinate. Gaussian elimination with partial
pivoting solves the system without forming the inverse — the swap rows when a pivot is
near zero, which keeps the factorization stable even for ill-conditioned designs. For
pedagogical and small-data purposes it's ideal: the pivoting behavior is visible and
testable (the lab's tests cover the pivoted path), and the failure mode is explicit —
a zero pivot reports the design as singular, which is exactly the signal multicollinear
data should produce."

**Interviewer**: "The lab's `nrmse` (normalized RMSE) — how do you interpret it?"

**Candidate**: "RMSE = sqrt(SSE/n), the typical error of the model in y's units; `nrmse`
divides by the range of observed y, giving a scale-free error measure: 'the model is off
by about 7% of the data's range'. The demo shows the contrast: on the clean constructed
data the model recovers coefficients exactly and the residuals are -0.0000 — the fit is
exact, and the diagnostics all agree. On the noisier real-style data, `nrmse` lands
around 0.51, and the R^2 of 0.51 tells the same story from the other direction: half the
variation explained, half left as noise. The interview-level habit: always report error
in both forms — absolute (RMSE) for the business ('off by 2.3 minutes') and normalized
(nRMSE) for comparing across datasets with different scales."

**Interviewer**: "How does Spearman's rank correlation work, and what does it catch that Pearson misses?"

**Candidate**: "Spearman replaces each variable's values with their ranks and computes
Pearson on the ranks. Because ranks strip the value scale, the measure is invariant to
monotonic transformations — log, square, rank, anything monotonic — and it detects
*any* monotonic relationship, not just linear ones. An exponential relationship gives
Pearson r well below 1 but Spearman rho = 1. Outliers are bounded to one rank step, so
they can't dominate. The lab's `spearman` uses the same `rank` helper with average ranks
for ties, then `pearson` on the ranks — two pieces of the lab reused. When to prefer it:
Likert scales, ordinal data, heavy-tailed metrics, or any time the scatter plot looks
monotonic but curved. The honest trade-off: Pearson is more efficient when the
relationship is genuinely linear; Spearman wins on robustness."

**Interviewer**: "What are the failure modes of correlation, and how do you catch them?"

**Candidate**: "Five classics. Nonlinear relationships invisible to Pearson — catch by
plotting and by Spearman. Outliers that manufacture or destroy correlation — catch with
robust methods and scatter plots; the lab's demo data is clean, so the lesson is in the
construction: r = 1.0 for the tied case and r = 0.7143 for the moderate case, both
visually checkable. Aggregation effects — Simpson's paradox style: correlation computed
on pooled groups can flip sign within groups; check within-group correlations. Range
restriction: correlating on a filtered subset shrinks r; state the population. And
spurious correlation from unrelated trending series — two growing time series correlate
beautifully with zero mechanism; the lab's time-series lab (07) shows how to deflate
this with detrending and autocorrelation analysis."

**Interviewer**: "The multiple regression demo recovers coefficients exactly. Why does that matter for confidence in the code?"

**Candidate**: "Because exact recovery is the gold standard test of the linear algebra.
The data was constructed from a known model — y = 1.0·x1 + 2.0·x2 - 0.5·x3 — so the
pipeline's answer is known in advance: coefficients (1.0000, 2.0000, -0.5000), residuals
at -0.0000, SSE ≈ 0. If the normal-equation solver had a sign error, a scaling bug, or
an unstable pivot path, the recovery would be off by visible amounts, and the residuals
would not be numerically zero. The same construction technique is what the lab's unit
tests use: build data from a known model, fit, assert the coefficients come back. It's
the pattern I'd use for any regression library — never trust a solver you haven't
verified against data whose answer you already know."

**Interviewer**: "What is multicollinearity, and what does the normal-equation solver have to do with it?"

**Candidate**: "Multicollinearity is near-linear dependence among the predictors — x2 ≈
x1·3 + noise. The consequences live in X^T·X: the matrix becomes nearly singular, its
condition number explodes, and the normal equations have no unique, stable solution —
tiny data perturbations produce wildly different coefficients, even though predictions
stay stable. This is exactly where the lab's Gaussian elimination with partial pivoting
earns its keep: a zero or tiny pivot flags the singularity, and the solver either
reports it or produces the huge unstable coefficients that are the signature of the
problem. Detection: variance inflation factors — VIF = 1/(1 - R_j^2) per predictor,
with VIF > 10 the standard warning — and the practical fixes: drop or combine collinear
columns, or regularize (ridge) the problem. The demo's three predictors are independent,
so the recovery is clean; the interview answer names what changes when they're not."

**Interviewer**: "When would you prefer simple regression over multiple regression?"

**Candidate**: "The question is bias-variance, not preference. Simple regression trades the
ability to adjust for confounders — and the risk of omitted-variable bias — for the
simplest explainable model. If a single variable is the business lever and the others
are noise, the simple model's slope is the number to report. Multiple regression is the
default when confounding is plausible: two correlated predictors can each appear
significant or not, and the multiple model separates their contributions — the demo's
three-predictor fit shows how distinct coefficients emerge from joint data. The
compromise: fit the multiple model for inference, and if one predictor dominates, report
both — the unadjusted slope for the product story and the adjusted slope for the causal
claim. The lab's `LinearRegression` handles both cases with the same machinery."

**Interviewer**: "How do you evaluate a regression model beyond R-squared?"

**Candidate**: "A stack of checks, cheapest first. Residuals: plot vs fitted — patterns
(cones, curves) reveal heteroscedasticity and misspecification; the clean demo's
residuals are -0.0000, the shape every model aspires to. Error metrics: RMSE in y's
units, nRMSE normalized — the lab's `nrmse` — and MAE for the median-style error. Out-of-
sample behavior: cross-validate, because in-sample R^2 is a floor, not a ceiling.
Coefficient sanity: signs and magnitudes must match domain knowledge — the height-weight
demo's slope of 5.0 lbs/inch is the kind of number a doctor would nod at. And
influence analysis: identify points whose removal moves the fit materially — one
influential point can manufacture an entire regression, which is where the correlation
lab's robustness lessons and the regression's leverage diagnostics meet."
