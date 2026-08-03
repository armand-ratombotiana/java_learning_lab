# Lab 01: Mock Interview — Descriptive Statistics

**Role**: Data Analyst / Data Scientist
**Duration**: 60 minutes
**Focus**: Mean, median, mode, variance, standard deviation, quartiles, IQR, outliers, coefficient of variation

---

**Interviewer**: "Walk me through the metrics you'd pull for a new dataset before any modeling."

**Candidate**: "A small set of summaries that behave well under bad data. Mean — `mean(double[])`,
sensitive to outliers, right for symmetric data. Median — `median(double[])`, the 50th
percentile, the robust choice when data is skewed or has garbage values. Mode — `mode(double[])`,
returns all tied values rather than guessing one, so a dataset with two equally common
categories doesn't get a lie. Variance — `variance` and `populationVariance`, the two
flavors matter. Quartiles and IQR — `q1`, `q3`, `iqr`, the non-parametric spread. And the
coefficient of variation — `coefficientOfVariation`, the relative spread. The lab's
`DescriptiveStatistics` computes all of these in one pass over the data, and the demo
covers three canonical cases: {1..10}, a skewed set with a dominant mode, and a constant
set where the variance is exactly zero — which is a great smoke test for the formulas."

**Interviewer**: "Derive why the sample variance divides by n-1 and not n."

**Candidate**: "The sample variance's job is to estimate the population variance from n
observations. The deviations are measured from the sample mean, not the population mean,
and the sample mean is the value that minimizes the sum of squared deviations — it
borrows one degree of freedom to fit the data. So the residuals around x-bar are on
average smaller than the residuals around the true mean mu: E[sum(x_i - x-bar)^2] =
(n-1)·sigma^2. Dividing by n would make the estimator systematically too small — biased
low. Dividing by n-1 repairs exactly that bias. The lab demonstrates it numerically:
`variance({1..10})` = 9.1667 while `populationVariance({1..10})` = 8.25. At n = 10 the
difference is 11% — at n = 3 it's 50%. The practical rule: when the data is a sample,
use n-1; when it's the full population, use n."

**Interviewer**: "Why report standard deviation instead of variance?"

**Candidate**: "Units. Variance is in squared units — dollars squared, seconds squared —
which is meaningless to a stakeholder. The standard deviation takes the square root and
returns to the original scale: 'watch time varies by plus or minus 14 minutes' is a
statement a PM can sanity-check. The math is the same information — same ranking, same
formulas — but standard deviation inherits the data's units, so it's what goes into
reports and product conversations. The lab returns both: `variance({1..10})` = 9.1667,
`stdDev({1..10})` = 3.0277, and the relationship sqrt(9.1667) = 3.0277 is exact — a
built-in consistency check that the two formulas agree."

**Interviewer**: "The lab's mode returns all tied values. Why would you ever want that?"

**Candidate**: "Because picking one arbitrarily is a silent lie. If 40% of values are 4 and
40% are 7, a single-mode implementation that returns one of them hides the bimodality —
which is usually the interesting fact about the data. Returning `{4, 7}` tells the analyst
'there are two competing centers here'. The lab's `mode` iterates the frequency map and
returns the set of values at the maximum count: for {1..10} every value appears once, so
every value is a mode — which is also correct: uniform data has no meaningful mode. For
{1,1,2,3,4,4,4,5} it returns {4.0} alone. That behavior — always reporting the complete
set — forces the analyst to think about what the answer means instead of trusting a
single number."

**Interviewer**: "How are the quartiles computed, and why does the lab return 3 for Q1 of {1..10}?"

**Candidate**: "The lab uses the textbook inclusive method: Q1 is the median of the lower
half, Q3 the median of the upper half, and IQR = Q3 - Q1. For {1..10}, the overall median
is 5.5; the lower half is {1..5} whose median is 3, the upper half is {6..10} whose
median is 8, so Q1 = 3, Q3 = 8, IQR = 5. Every implementation has its own tie-breaking —
Tukey's hinges, exclusive, nearest-rank — and they disagree at the margins, so the
defensible answer is 'be explicit about the convention', which the lab is. The IQR is the
robust spread: it's the middle 50% of data, immune to the extreme 25% on each side, which
is why it backs the outlier rules."

**Interviewer**: "How do you detect outliers with these tools?"

**Candidate**: "The IQR-based rule: anything below Q1 - 1.5·IQR or above Q3 + 1.5·IQR is a
candidate outlier — the Tukey fence that box plots draw. It's robust because both inputs,
the quartiles, are rank statistics: an outlier can move them by at most one rank position.
The alternative is the z-score rule — |x - mean| / stdDev > 3 — but that's circular: the
outlier inflates both mean and stdDev, and with a big enough outlier the z-score of every
other point collapses, a failure mode called masking. In the lab's terms: on the skewed
set {1,1,2,3,4,4,4,5,100}, Q1 = 1.5, Q3 = 4, IQR = 2.5, fence = 4 + 3.75 = 7.75, and 100
gets flagged cleanly — while the mean has already been dragged to 13.78. Then the honest
next step is a conversation, not a formula: is 100 a typo, a real customer, or a segment?"

**Interviewer**: "When do the mean and median disagree, and what does the disagreement mean?"

**Candidate**: "They disagree exactly when the distribution is skewed, and the direction
tells you the skew. The mean is pulled toward the tail because it sums every value; the
median only counts ranks. Right-skewed data — income, house prices, latency — has mean >
median: a few billionaires pull the average up while the median household sees nothing.
Left-skewed is the mirror. The gap is a magnitude you can report: 'the median price is
200K but the mean is 310K, driven by the top 2% of listings' is an actionable sentence.
The demo makes it concrete: {1..10} is symmetric, so mean = median = 5.5 — the same
number from two very different formulas is itself evidence of symmetry."

**Interviewer**: "When would you report IQR instead of standard deviation?"

**Candidate**: "When the spread question is about typical behavior: IQR = middle 50%,
unaffected by tails. It's the right summary for skewed distributions — a right-skewed
metric's standard deviation is dominated by the tail, so 'the average deviation' is
describing outliers, not people. Standard deviation remains right when the distribution
is roughly symmetric and you need the full spread — and it's what variance decompositions
use downstream: ANOVA, regression, power analysis all consume variances, so the SD is
where parametric statistics start. In the lab's demo, the skewed set tells the story: the
IQR covers values 1.5 to 4 — the actual typical range — while the standard deviation,
inflated by the tail, describes a range most observations never visit."

**Interviewer**: "What is the coefficient of variation, and when is it meaningful?"

**Candidate**: "CV = stdDev / mean, the relative spread — a dimensionless ratio, so you can
compare variability across different units or scales: a latency of 12ms with SD 3ms (CV =
25%) is far more stable than a latency of 100ms with SD 25ms (CV = 25% — wait, that's the
same). Let me redo that: it's meaningful precisely because it normalizes: CV 25% on a
12ms baseline is 3ms of noise; CV 25% on a 100ms baseline is 25ms of noise — the ratio
lets you compare them directly. The lab's demo: `coefficientOfVariation({1..10})` =
54.43%. Two caveats: it's undefined or exploding when the mean is near zero — the sign
flips as the mean crosses zero, so CV is only sane for positive-scale metrics like prices,
latencies, and durations."

**Interviewer**: "How do descriptive statistics fit into a monitoring pipeline?"

**Candidate**: "As a rolling summary over a time window, computed on every batch: mean,
median, p95, IQR per metric, stored as time series. Two checks matter. First, mean vs
median divergence in the window — it's the canary for skew events, like a spike of
outliers. Second, a constant or near-zero variance — a dead sensor, an instrumented metric
nobody is writing to. The lab's constant-set demo, `variance({5,5,5,5,5})` = 0, is exactly
that signal. And the alerting rule must be IQR-fence-based, not z-score-based, for the
masking reasons we covered — a z-score alert on an already-outlier-polluted window will
quietly go silent exactly when it should fire."

**Interviewer**: "What is the difference between descriptive and inferential statistics?"

**Candidate**: "Descriptive statistics summarize what the data is: the lab's mean, median,
variance, IQR — summaries of a dataset that's right in front of you, no uncertainty
involved. Inferential statistics use a sample to make claims about a population — a
confidence interval for a conversion rate, a t-test comparing two groups — and they
carry uncertainty explicitly. The bridge is the lab itself: `DescriptiveStatistics`
computes the summaries, and the mean and variance it produces are exactly the inputs the
later labs' t-tests and power calculations consume. The boundary question — 'are these 100
users a sample of a bigger population or the whole story?' — determines which toolset is
even allowed."

**Interviewer**: "Your team is monitoring page load time. How do you avoid paging fatigue from descriptive stats?"

**Candidate**: "By computing the descriptive summary first and alerting on the right
comparison. If you alert on the mean every minute, skew and outliers produce noise pages;
if you alert on median and IQR shift, you get signal. Concretely: track per-window median
and IQR, and fire only when the median leaves Q1-1.5·IQR..Q3+1.5·IQR of a recent baseline
window. That single rule uses the robust statistics exactly as designed. The classic
failure mode this avoids: alerting on mean + 3 sigma, which under a skew event both moves
the mean and inflates sigma — masking again — so the alert stops firing at the moment it
should fire hardest."

**Interviewer**: "What are the failure modes of these five statistics, and how do you catch them?"

**Candidate**: "Mean: silently pulled by outliers — catch it by always reporting median
alongside. Variance: inflated by the same outliers — catch it by reporting IQR, which is
a rank statistic. Mode: misleading when ties exist — which is why the lab returns the
full tied set instead of an arbitrary pick. Quartiles: convention-sensitive — catch it by
stating the convention (the lab uses the inclusive method: median of lower/upper half).
CV: breaks near zero mean — only use on positive-scale data. The catch for all of them:
never report a single summary without its spread and without knowing the distribution's
shape — the demo's three cases exist to force that reflex: symmetric, skewed-with-mode,
and constant."

**Interviewer**: "A PM says 'the average is 13.78, so most customers see 13.78.' How do you respond?"

**Candidate**: "I show them the median and the shape. On the skewed demo set, the median is
4 — the center of the data is 4, and 13.78 is what you get when one outlier worth 100
joins a cluster around 3-4. That's the entire point of pairing mean with median: the gap
is the skew, and the skew is the story. The right product answer would be 'most customers
see around 4; the average is dragged up by a small set of heavy users — here's the IQR,
here's where the long tail starts, and here's a decision that depends on which segment we
care about.' Descriptive statistics don't answer 'what should we do' — but they tell you
which question you're actually asking."
