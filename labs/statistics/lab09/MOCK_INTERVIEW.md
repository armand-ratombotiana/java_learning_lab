# Lab 09: Mock Interview — Non-parametric Statistics

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Mann-Whitney U, Wilcoxon signed-rank, Kruskal-Wallis H, Friedman Q, ranking and ties, robustness

---

**Interviewer**: "When do you reach for non-parametric tests instead of t-tests or ANOVA?"

**Candidate**: "Four situations: normality is clearly violated — heavy tails, multi-modal,
ordinal data like star ratings; outliers are present and I don't want a single extreme
value deciding the test; sample sizes are tiny, where the CLT hasn't kicked in and the
t-approximation is untrustworthy; and when the data is ordinal — ranks and ratings
where the intervals between values are not meaningful. The trade-off is honest: rank-
based tests lose a little power when the parametric assumptions genuinely hold — about
5% relative efficiency versus the t-test — but they protect the error rate when
assumptions fail. The lab's `NonParametricTests` class implements the four workhorses:
Mann-Whitney U, Wilcoxon signed-rank, Kruskal-Wallis H, and Friedman Q."

**Interviewer**: "Derive the Mann-Whitney U statistic."

**Candidate**: "Combine both samples, rank everything 1..n1+n2 with average ranks for
ties, then sum the ranks of group 1: R1. If group 1 systematically outranks group 2, R1
is large. The U statistic compares R1 to its expectation under H0: U1 = R1 - n1(n1+1)/2
— the number of (group-2, group-1) pairs where the group-2 value precedes the group-1
value. U2 = n1·n2 - U1. The lab's `mannWhitneyU` computes `u1 = r1 - n1 * (n1 + 1) /
2.0` and `u2 = n1 * n2 - u1`; the record exposes `smallerU()` for the standard table
lookup. Demo: {2,4,6,8} vs {1,3,5,7} gives U1 = 10, U2 = 6 — under H0 the smaller U has
expectation n1·n2/2 = 8, and 6 is well within the null's range."

**Interviewer**: "What exactly does Mann-Whitney test — equality of medians or something else?"

**Candidate**: "Strictly, it tests stochastic dominance under the shift model: H0 is that
a random draw from group 1 is equally likely to exceed a draw from group 2 as vice versa
— P(X1 > X2) = 0.5 — not median equality per se. The lab's demo shows the pure shift
case: {2,4,6,8} versus {10,12,14,16} — every value of group 1 is below every value of
group 3, so U1 = 0, U2 = 16, smallerU = 0. That's the most extreme possible statistic,
and the interpretation is unambiguous: group 3 stochastically dominates group 1. If the
distributions differ in shape rather than location, U is still valid for the dominance
question but not for 'the medians are equal'."

**Interviewer**: "Walk through the Wilcoxon signed-rank test and what makes it more powerful than the sign test."

**Candidate**: "For paired data, compute differences d_i = after_i - before_i, drop the
zeros, rank the absolute differences, then attach the sign of the difference back to
each rank. W+ sums the ranks of positive differences, W- the negative ones. The lab's
`wilcoxonSignedRank` builds the absolute differences with signs, ranks them with the
shared `rank` helper, and accumulates W+ and W-. Under H0, W+ and W- are exchangeable
with expectation n(n+1)/4. It beats the sign test because it uses magnitude — the sign
test counts only direction, discarding the information that one pair changed by 1 and
another by 9. Demo: all five differences are negative, so W+ = 0.0, W- = 15.0, n = 5 —
the maximum possible imbalance for n = 5."

**Interviewer**: "Why is the ranking step so important, and how does the lab handle ties?"

**Candidate**: "Ranking removes the distributional assumption: whatever the underlying
density, the ranks of n values are a fixed object, so tests built on ranks are valid
without normality and robust to outliers — an outlier at 10^6 becomes just 'the highest
rank'. Ties must be handled or the tests become anti-conservative. The lab's `rank`
sorts a clone and assigns `1.0 + (first + last) / 2.0` — the average of the tied
positions. Take {1,1,2}: the first 1 gets positions 0 and 1, so rank 1.5; the 2 gets
position 2, so rank 3. Average ranks preserve the rank-sum identity and keep the tests'
null distributions valid. The same helper backs Mann-Whitney and Wilcoxon, and the
correlation lab's Spearman uses the identical convention."

**Interviewer**: "Derive the Kruskal-Wallis H statistic."

**Candidate**: "Rank all N observations across k groups together. For each group g with
n_g members, compute the rank sum R_g. If groups are equivalent, each group's rank sum
should be near n_g·(N+1)/2. H = 12/(N(N+1)) · sum(R_g^2/n_g) - 3(N+1) — the
12/(N(N+1)) factor standardizes the scale so H is approximately chi-square with k-1
degrees of freedom under H0. The lab's `kruskalWallis` implements exactly that: it
flattens the groups into one array with a group index, ranks once, accumulates
`h += sumR * sumR / nG`, then applies `12.0/(totalN*(totalN+1))*h - 3.0*(totalN+1)`.
Demo: {2,4,6}, {8,10,12}, {14,16,18} — perfectly separated groups — gives H = 7.200000,
while nearly identical groups give H = 0.800000."

**Interviewer**: "When is Kruskal-Wallis the right call instead of ANOVA?"

**Candidate**: "Kruskal-Wallis is the non-parametric replacement for one-way ANOVA: same
hypothesis — k groups, location differences — but valid when the normality or
homogeneity assumptions of ANOVA fail. The efficiency story mirrors the two-group case:
nearly as powerful as ANOVA when data is normal, much more robust when it isn't. The
demo numbers tell the robustness story directly: the separated groups give H = 7.2 —
highly significant against a chi-square with df = 2 (critical value 5.99) — even though
the raw values are small integers; and the interleaved groups give H = 0.800000,
correctly not significant. If I added a 10^6 outlier to one group, ANOVA's F would be
distorted by the variance, while H would barely move."

**Interviewer**: "Explain the Friedman test's design and its statistic."

**Candidate**: "Friedman handles the randomized block design: b blocks (subjects, days),
k treatments, one observation per cell. Ranking happens within each block — each row
gets ranks 1..k — so block-level differences cancel entirely. R_j is the sum of
treatment j's ranks across blocks, and Q = 12/(b·k·(k+1)) · sum(R_j^2) - 3b(k+1),
approximately chi-square with k-1 df. The lab's `friedman` ranks each row with the
shared `rank` helper, sums the per-treatment rank columns, and applies the formula.
Demo: 4 blocks x 3 treatments where treatment 3 always wins within-block gives
Q = 8.000000 — exactly the critical value for df = 2 at alpha = 0.05. It's the perfect
teaching case: blocks absorb the subject effects, so only within-row ordering matters."

**Interviewer**: "A colleague ran a paired t-test on data with a 100x outlier and got significance; the Wilcoxon says no. Who's right?"

**Candidate**: "Depends on the question, and that's the real answer. The t-test's p-value
is driven by the outlier's huge deviation — it's testing the mean difference, which the
outlier dominates. Wilcoxon tests whether the typical within-pair direction is
consistent — robust to the outlier by construction. If the question is 'does the
intervention shift the typical user?', Wilcoxon's answer is the honest one; if the
question is 'does the intervention change average behavior including the extremes?',
the t-test is the right question but the sample is too small to answer it. The decision
rule I'd apply: check residuals first — one 100x observation in n = 10 is not a
distribution, it's a data-quality issue — then decide whether that observation
represents real behavior or an artifact. Tests don't resolve data-quality questions;
they only answer the question the statistic encodes."

**Interviewer**: "How do these rank tests behave when sample sizes get large?"

**Candidate**: "They become asymptotically normal, which is what makes them usable at
scale. Mann-Whitney's U, standardized, converges to a standard normal under H0; the
Kruskal-Wallis H converges to chi-square with k-1 df; and the Friedman Q to chi-square
with k-1 df. So the table-lookup route (exact values for small n) and the normal/
chi-square route (large n) agree in the middle, and implementations typically switch
between them automatically — the lab's small demos sit in the exact-table regime. The
asymptotics also come with a tie correction: heavy ties reduce the variance of the
statistic, and the standard implementations apply the tie-adjusted variance. The
practical consequence: these tests never 'break' at scale — they become the fastest,
most robust option exactly when data is plentiful and messy."

**Interviewer**: "How would you explain the trade-off of non-parametric methods to a PM who wants 'the same power as before'?"

**Candidate**: "The ARE (asymptotic relative efficiency) of Mann-Whitney vs t-test is
3/pi ~ 0.955 under normality — so to match the t-test's power you need about 1.05x the
sample size, a rounding error in most experiment plans. Under heavy tails, the direction
flips: rank-based methods can be arbitrarily more efficient because the variance the
t-test needs is unstable. So the PM's concern is backwards: if the metric is skewed —
revenue, latency, session length — the non-parametric test is the one that needs fewer
samples to reliably detect the effect, and its error rate is honest. The lab's numbers
make the mechanism visible: the tests operate purely on ranks, so the value scale
doesn't enter at all."

**Interviewer**: "Four tests, four stats — which do you use when, in one chart?"

**Candidate**: "One sample or paired data: Wilcoxon signed-rank — the lab's
`wilcoxonSignedRank`, reporting W+ and W-, replacing the paired t-test. Two independent
samples: Mann-Whitney U — `mannWhitneyU`, reporting U1/U2 and `smallerU`, replacing the
two-sample t-test. k independent samples: Kruskal-Wallis H — `kruskalWallis`, replacing
one-way ANOVA. Blocked design with k treatments per block: Friedman Q — `friedman`,
replacing repeated-measures ANOVA. The pipeline rule that ties them to the rest of the
curriculum: check normality and variance assumptions first — the t/ANOVA path — and
switch to the rank path when they break; both paths share the ranking machinery, and
both report statistics that a chi-square table converts into decisions."
