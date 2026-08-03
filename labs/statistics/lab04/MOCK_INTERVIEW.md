# Lab 04: Mock Interview — Analysis of Variance (ANOVA)

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: One-way ANOVA, sum-of-squares decomposition, F-test, Tukey HSD, assumptions, two-way ANOVA

---

**Interviewer**: "Explain what ANOVA tests and when you'd choose it over many t-tests."

**Candidate**: "ANOVA tests whether k group means are all equal against 'at least one
differs'. It's the generalization of the two-sample t-test, and it exists because doing
pairwise t-tests across k groups inflates the false-positive rate — three groups means
three comparisons, and each comparison at alpha = 0.05 pushes the overall error rate
toward 14%. The F-test is the omnibus: one statistic, one p-value, the whole family at
once. In product terms: compare three checkout designs, four pricing pages, five
recommendation algorithms. The lab's `OneWayAnova` computes the full decomposition —
`anovaResult` holds SSB, SSW, SST, MSB, MSW, F, and p — and the demo's constructed
case gives F = 34.6667 with p = 0.000010: group separation that a single glance at the
means makes obvious."

**Interviewer**: "Derive the sum-of-squares decomposition SSB + SSW = SST."

**Candidate**: "Start with the total variation around the grand mean, SST = sum over all
observations of (x_ij - x-bar)^2. Split each deviation into a group part and a within-
group part: x_ij - x-bar = (x_i-bar - x-bar) + (x_ij - x_i-bar) — the group's offset
from the grand mean plus the observation's offset from its own group mean. Squaring and
summing, the cross term vanishes because deviations within a group sum to zero. So SST =
SSB + SSW exactly: between-group sum of squares, sum over groups of n_i·(x_i-bar -
x-bar)^2, plus within-group sum of squares, sum over groups of their internal squared
deviations. The lab's demo computes all three: SSB = 173.3333, SSW = 30, SST = 203.3333,
and 173.3333 + 30 = 203.3333 — the identity is a built-in correctness check on the
arithmetic."

**Interviewer**: "How do SSB and SSW become the F statistic?"

**Candidate**: "Divide each sum of squares by its degrees of freedom to get mean squares.
SSB has k-1 degrees of freedom — k group means, one constraint from the grand mean — so
MSB = SSB/(k-1). SSW has N-k degrees of freedom — N observations, k group means fitted —
so MSW = SSW/(N-k), which is the pooled within-group variance, the ANOVA version of the
t-test's pooled variance. Under H0, MSB and MSW both estimate the same common variance,
so F = MSB/MSW is near 1; under the alternative, MSB picks up the group separation and F
grows. The lab's `OneWayAnova` computes F = 173.3333/2 / (30/12) = 34.6667 with
p = 0.000010. The demo's second case is the null analog: nearly identical group means
give F = 0.3810, p = 0.691185 — correctly silent, showing the F-test isn't just a
number-churning ritual."

**Interviewer**: "What does the p-value mean here, and what does 'F = 34.67' say about the null?"

**Candidate**: "Under the null — all group means equal — F follows the F-distribution
with (k-1, N-k) degrees of freedom, a family of right-skewed curves whose shape depends
on both df values. The p-value is the probability of observing an F at least this large
under that null: 0.000010 for the demo — under H0, an F this extreme occurs once in a
hundred thousand experiments. So the data is dramatically incompatible with 'all means
equal'. The companion number is the critical value: for (2, 12) df at alpha = 0.05 it's
about 3.89, and F = 34.67 dwarfs it. Reporting discipline: the F statistic alone means
nothing without its df pair, and the p-value alone doesn't say *which* groups differ —
that's what the post-hoc test, Tukey HSD, is for."

**Interviewer**: "Why do we need Tukey HSD after a significant F?"

**Candidate**: "Because the omnibus answer is yes/no — 'at least one group differs' — and
the business question is 'which ones, and by how much?'. Running pairwise t-tests reopens
the multiple-comparisons problem. Tukey's Honestly Significant Difference solves it by
using the studentized range distribution: instead of comparing each pair with a normal or
t critical value, HSD = q(alpha, k, N-k)·sqrt(MSW/n) — one threshold for all pairs, and
any pair whose mean difference exceeds HSD is significant while the family-wise error
rate stays at alpha. The lab's demo: F = 34.6667 significant, then HSD = 2.4584 — group C
differs from A and B, while A and B are not different from each other. The workflow is
the whole story: F-test gates, HSD localizes, and the three-group means are read as a
ranking, not a significance dump."

**Interviewer**: "Walk through the lab's HSD computation."

**Candidate**: "Three ingredients. The mean squared error MSW — the pooled within-group
variance, 2.5 in the demo. The studentized range critical value q(alpha, k, N-k) — for
k = 3 groups, N = 15, alpha = 0.05, the demo's `studentizedRangeCriticalValue` uses the
approximation q = 0.55 + 0.113·sqrt(k) + ... with a Tukey-table fit, giving the threshold
HSD = 2.4584. The comparison rule: compute the absolute mean differences — the demo's
group means are 3.0, 5.0, and 10.0, so differences are 2.0, 7.0, and 5.0 — and mark each
pair as significant if its difference exceeds HSD. Result: |A-B| = 2.0 < 2.4584 (not
significant), while |A-C| = 7.0 and |B-C| = 5.0 both exceed it. The approximation's
accuracy is exactly what the lab's inline table values check — the demo cross-validates
the critical value against published Tukey table entries."

**Interviewer**: "What assumptions does one-way ANOVA make, and how do you check them?"

**Candidate**: "Three, the same family as the t-test. Independence — observations within
and across groups are independent; violated by clustered data or time trends, checked by
design, not by test. Normality of the residuals — the within-group errors, not the raw
values; checked with a Q-Q plot or Shapiro-Wilk, and the F-test is fairly robust to
moderate departures at balanced sample sizes. Homogeneity of variance — equal within-
group variances; checked with Levene's test or the max-to-min variance ratio, and the
rule of thumb is the largest variance should be no more than ~4x the smallest for the
test to stay honest. Fallbacks: Welch's ANOVA for variance violations, Kruskal-Wallis
from the non-parametric lab for normality failures. The lab's constructed data is
balanced and clean by design; the interview-level answer names the checks and the
fallbacks for when they fail."

**Interviewer**: "What is a balanced design, and why does balance matter?"

**Candidate**: "Balanced means equal group sizes — every group has the same n. It matters
three ways. Power: with equal n, the F-test has maximal power at a given total sample.
Robustness: the F-test's insensitivity to normality violations degrades when groups are
unequal, especially with unequal variances — the classic bad combination. Interpretability:
the grand mean is then a simple average of group means, and the decomposition stays
clean. The lab's demo uses n = 5 per group for exactly this reason, and the model's
sum-of-squares identity SSB + SSW = SST holds regardless — but the *distribution* of F
under the null is most honest when balanced. The practical rule: design for balance;
if the data is unbalanced, prefer Type III sums of squares and Welch-style corrections."

**Interviewer**: "How does two-way ANOVA extend the model?"

**Candidate**: "Two factors at once — A with levels, B with levels — and the design now
splits variation into main effect A, main effect B, the interaction A x B, and error.
The interaction is the new and interesting object: it tests whether A's effect depends
on B's level. A significant interaction overrides the main effects' simple reading — you
can't say 'factor A works' if it works only at one level of B. Degrees of freedom are
(a-1) for A, (b-1) for B, (a-1)(b-1) for the interaction, and each gets its own F-test.
The factorial lab (08) picks this up with the 2^2 and 2^3 contrast machinery, computing
effects A, B, and AB directly — same idea, sharper notation. For product work: A =
checkout design, B = user segment; the interaction answers 'does the winning design
depend on the segment?' — which is usually the real question."

**Interviewer**: "How do you use ANOVA with real product metrics that are far from normal?"

**Candidate**: "The F-test runs on means, and means of many observations are approximately
normal by the CLT — so for balanced groups with moderate sizes, the raw metric's
skewness matters less than people fear. Two honest caveats. First, the *within-group*
spread must still be homogeneous — right-skewed metrics often have variance growing with
the mean, violating that; log transforms or Welch's ANOVA are the standard fixes.
Second, count metrics with heavy zero-inflation (revenue per session, time-in-app) are
often better modeled directly — zero-inflated models or Poisson/negative-binomial GLMs —
than run through an F-test. The lab's framework still applies: the decomposition of
variation into between and within is the analysis-of-variance idea that underlies every
one of those models, so ANOVA is the right conceptual scaffold even when the exact
distribution needs a generalization."

**Interviewer**: "How do you determine sample size for an ANOVA with k groups?"

**Candidate**: "Two steps. First, the effect you care about: the smallest mean difference
(or the ratio of between-group to within-group variation) worth detecting. Second, the
power formula generalized from the t-test case: each group needs roughly n = 2·(z_alpha/2
+ z_beta)^2 / d^2 per comparison at the level of precision you want — the power lab's
formula applied pairwise — but the honest omnibus answer uses the non-central F
distribution: power = P(F > F_crit | non-centrality parameter n·sum(alpha_i^2)/sigma^2),
where alpha_i are the group offsets. The pattern that survives into the lab: required n
scales with 1/d^2 — halve the detectable difference, quadruple the sample — and grows
with k only mildly. Practically: size the test for the smallest pairwise difference that
matters, run the omnibus, and let Tukey HSD handle the follow-ups."

**Interviewer**: "The demo's second case shows F = 0.3810. What does an F below 1 mean?"

**Candidate**: "F below 1 means the between-group variation is smaller than the within-
group variation — the group means are closer together than the noise around them. In the
demo, the constructed groups have nearly identical means, so MSB is small relative to
MSW and F = 0.3810 with p = 0.691185: no evidence of group differences. A p-value that
large on an F-test is exactly the 'underpowered or truly no effect' ambiguity — the
p-value alone can't distinguish 'no effect' from 'couldn't detect one', and the
follow-up question is always the MDE the sample could have detected. Also worth saying:
a very small F with a small p would be a red flag — it would suggest the decomposition
itself is broken (negative variance estimates, wrong df), not a clever finding."
