# Lab 01: Mock Interview — Linear Regression

**Role**: Machine Learning Engineer
**Duration**: 60 minutes
**Focus**: OLS closed-form, gradient descent, R², MSE, MAE, assumptions, feature scaling

---

**Interviewer**: "Walk me through how your lab's linear regression finds its parameters."

**Candidate**: "The lab implements two paths. First, closed-form OLS in `fitOLS(x, y)`
— it accumulates `sx`, `sy`, `sxx`, `sxy` in one pass and returns
`slope = (n*sxy - sx*sy) / (n*sxx - sx*sx)` with `intercept = (sy - slope*sx) / n`.
That is the univariate special case of β = (XᵀX)⁻¹Xᵀy, which minimizes the sum of
squared residuals. Second, `fitGD(x, y, lr, epochs)` runs batch gradient descent: for
each epoch it computes `dm += err * x[i]` and `db += err`, then updates
`m -= lr * dm / n`, `b -= lr * db / n`. On the demo data both converge to the same
line — OLS gives slope 2.5879 and intercept 1.1067, GD gives 2.6005 and 1.0187."

**Interviewer**: "Why would you ever use gradient descent if OLS gives the exact answer?"

**Candidate**: "Because the closed form inverts a p×p matrix, and the inversion is
O(p³) after building XᵀX at O(n·p²). For the univariate demo that is trivial, but when
n is in the millions or p is in the thousands, gradient descent is the only practical
route — each epoch is O(n·p) and you can stop early. The lab's `fitGD` shows the
fundamental machinery: compute the gradient, step against it with a learning rate,
repeat. The same update rule generalizes to logistic regression, SVMs, and neural
nets, so the OLS path is the 'why it's exact' and GD is the 'why it scales'."

**Interviewer**: "Your demo shows MSE(OLS)=0.0072 and MAE(OLS)=0.0696. What do these
two numbers tell you?"

**Candidate**: "Both are error aggregates, but they answer different questions. MSE
squares each error before averaging, so it is dominated by the worst prediction —
a single outlier dominates. MAE averages absolute errors, so it reports the typical
miss in the original units. Here MSE is about 0.0072 against a target range of 3.7 to
27.0, and MAE is 0.07, so the median prediction error is roughly seven cents on a
target that spans 23 units — the fit is very tight. In production I'd quote MAE to a
business audience because it's in price units, and use MSE to train, because the
quadratic loss concentrates optimization on large errors."

**Interviewer**: "What does R² = 0.9999 from your demo actually mean?"

**Candidate**: "The lab's `r2(y, yHat)` computes `1 - ssRes / ssTot`, where `ssRes` is
the squared error around the fitted line and `ssTot` is the squared error around the
mean of y. R² = 0.9999 means the model explains 99.99% of the variance in the target —
only 0.01% is left as residual. It is a relative measure, though: it compares your
model to the 'predict the mean always' baseline, so it says nothing about absolute
error. The GD path's 0.9998 is slightly lower because it stopped one hundredth of a
unit away from the OLS optimum."

**Interviewer**: "Walk me through the gradient descent update in `fitGD`. Why the
division by n?"

**Candidate**: "The code computes `dm += err * x[i]` and `db += err` over all samples,
then applies `m -= lr * dm / n`. The division by n turns the accumulated gradient into
the mean gradient, which is the (1/m) Σ term in the textbook update. Without it, the
step size effectively grows with the dataset and the learning rate would have to be
re-tuned every time the data grew. The sign is the key detail: we subtract the
gradient times the learning rate, because we're descending the MSE surface. And note
the gradient of MSE w.r.t. m is exactly (1/n) Σ (h(x) - y) x — the code's `err * x[i]`
is `(h - y) * x`, which matches the formula in the GUIDE."

**Interviewer**: "If I gave you a dataset with one huge outlier in y, which metric
would be most affected?"

**Candidate**: "MSE, by construction — the squared term makes a single outlier dominate
the sum, which is why the lab reports both. In a real pricing pipeline I would train
with a robust loss when outliers are expected, and report MAE alongside. There's a
second effect people miss: with OLS the influence of an outlier isn't just in the
metric, it moves the fitted line itself, because the closed form directly minimizes
that squared error. I'd diagnose with a residuals-vs-fitted plot — the GUIDE's Step 5
— where a wedge shape signals heteroscedasticity, and a curved cloud signals
non-linearity."

**Interviewer**: "When would gradient descent and OLS disagree in practice?"

**Candidate**: "Three cases. First, convergence: `fitGD` with a small learning rate and
few epochs stops early — the demo's GD slope of 2.6005 is already 0.0126 away from OLS
because it only ran 1000 epochs at lr 0.01. Second, conditioning: if features are on
wildly different scales, gradient descent crawls along the ill-conditioned direction,
while OLS is scale-invariant — in my production code I standardize features before GD.
Third, degeneracy: when XᵀX is near-singular (perfect collinearity), the closed form
explodes numerically while gradient descent still finds a usable solution."

**Interviewer**: "What are the assumptions of linear regression, and which ones does
your Zillow-style model risk violating?"

**Candidate**: "Linearity, independence of errors, homoscedasticity, normality of
residuals, and no multicollinearity. A home-price model with only square footage
risks non-linearity immediately — price per square foot is not constant across
markets or home sizes, so the residuals will curve. In the lab's synthetic data the
assumptions hold by construction, which is exactly why R² is 0.9999; the value of the
lab is seeing the clean case, and the job interview value is knowing the checklist to
verify on real data. I check them with the GUIDE's plots: residuals vs fitted for
linearity and homoscedasticity, Q-Q for normality."

**Interviewer**: "How do you detect multicollinearity, and why does it matter here?"

**Candidate**: "Compute the Variance Inflation Factor for each feature — VIF above 10
is the standard threshold. It matters because OLS can't uniquely attribute effect
when two features move together: the coefficient estimates get unstable even though
the fit is fine, and interpretation collapses. The lab's `fitOLS` is univariate so the
issue never arises, but in a multivariate extension the slope estimates for correlated
square-footage and room-count features would swing wildly between resamples. The
practical fix is feature selection, combining correlated features, or regularization —
L2 keeps them all but shrinks them."

**Interviewer**: "Your demo runs on data with x in [1, 10]. Suppose the real features
were home square footage, in the thousands, and price in the hundreds of thousands.
Anything break?"

**Candidate**: "Gradient descent, yes — this is a real failure mode. With x ~ 2000 and
y ~ 300000, the gradient components live on totally different magnitudes, and one
learning rate cannot serve both: too large and the slope update diverges, too small
and the intercept barely moves. I actually hit this in the walkthrough — with raw
square-footage features, `fitGD` with lr 1e-7 for 100000 epochs stopped at an
intercept of 0.08 while OLS said 51.73. Scaling the feature to thousands of square
feet fixed it: GD then converges to within 1.8 on the intercept. OLS is unaffected —
it's scale-invariant — which is a great sanity check."

**Interviewer**: "How would you extend this univariate model to a multivariate Zillow
price model?"

**Candidate**: "First, features: square footage, bedrooms, lot size, zip-code
dummies. That breaks `fitOLS`'s scalar formulas, so I'd implement the matrix form
β = (XᵀX)⁻¹Xᵀy using Gaussian elimination or Apache Commons Math, as the GUIDE
suggests. Second, scale everything before `fitGD` so the gradient is well-conditioned.
Third, handle the categorical zip codes — one-hot encoding adds many columns, which
is where I'd worry about the p³ inversion cost and switch to gradient descent. And
I'd add polynomial terms cautiously: the demo's error is tiny because the data is
linear; real housing data needs splines or log transforms."

**Interviewer**: "Why can't you just use this linear regression for classification,
like a spam detector?"

**Candidate**: "Because the output is unbounded and the loss is wrong for the job.
Linear regression minimizes squared error against a target that's really a
probability or a class; the line keeps extending past 0 and 1, and the squared loss
penalizes confident-but-wrong predictions less than a classifier should. That's
exactly why Lab 02 exists: the sigmoid squashes the linear score into [0, 1] and
cross-entropy gives a convex loss in that space. The takeaway from this lab is that
linear models are a family — the same gradient machinery reappears, but the
hypothesis and loss change with the problem type."

**Interviewer**: "How do you monitor a linear regression model in production after
deployment?"

**Candidate**: "Three signals. First, residual monitoring: track the distribution of
y - ŷ per hour; a mean shift is drift, a widening spread is heteroscedasticity
appearing. Second, feature drift: if new listings cluster where the training data
had no support, extrapolation errors will balloon even though R² on the training
window looks fine — the demo's line trained on 800 to 3600 square feet should never
quote a 7000-square-foot mansion with confidence. Third, retrain triggers: rolling
re-fit `fitOLS` on a sliding window and alert when the coefficient moves more than a
threshold — a changing slope per square foot is a market signal, not a model bug."

**Interviewer**: "What's a subtle failure you've seen with R² that this lab's demo
illustrates?"

**Candidate**: "R² can look spectacular while the model is useless. The demo's 0.9999
is real — the data is genuinely linear — but a high R² says nothing about
extrapolation, about whether the relationship is causal, or about absolute error
magnitude. A housing model with R² 0.95 but a $40k MAE is still bad for pricing. The
lab's habit of printing MSE, MAE, and R² together is the right instinct: R² for the
comparison story, MAE for the business story, MSE for the training signal. I also
watch for R² inflated by a few extreme points — the same outliers that dominate MSE."

**Interviewer**: "Why does the walkthrough prefer OLS for the final quoted price but
still keeps gradient descent around?"

**Candidate**: "For ten historical sales with one feature, OLS is exact, cheap, and
interpretable — the coefficients are the answer, and the walkthrough prints them as
'$117.48k per 1k square feet plus $51.73k base'. Gradient descent is kept as the
cross-check and the upgrade path: it converges to essentially the same line (slope
117.82, intercept 49.97), which validates the OLS math, and it's the machinery that
survives when the feature count grows past the point where the closed form is
affordable. So OLS is the production answer at this scale, and GD is the insurance
policy for scale."
