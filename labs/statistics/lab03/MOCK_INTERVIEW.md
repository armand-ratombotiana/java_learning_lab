# Lab 03: Mock Interview — Hypothesis Testing (t-test, z-test, chi-square)

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: One/two-sample t-tests, paired t-test, z-test, chi-square goodness-of-fit and independence, p-values, Type I/II errors

---

**Interviewer**: "Walk me through the tests in this lab and the question each one answers."

**Candidate**: "Four families. One-sample t-test — `oneSampleTTest`, t = (x-bar - mu0) /
(s·sqrt(1/n)) — answers 'is this sample's mean different from a known value?', like
'does today's latency differ from our 200ms SLA?'. Two-sample t-test —
`twoSampleTTest` — compares two independent groups: variant A vs variant B. Paired
t-test — `pairedTTest` — compares two measurements on the same subjects: before vs
after. And chi-square — `chiSquareGoodnessOfFit` for 'does observed distribution match
an expected one?' and `chiSquareIndependence` for 'are two categorical variables
related?'. The demo runs all of them with real output: the one-sample t = 2.6171,
df = 9, p = 0.027945; the two-sample t = -3.2408, df = 8, p = 0.011864; the paired
t = 2.3905, df = 4, p = 0.075130. The statistics are the bridge to the later labs —
every one of these formulas reappears in the power and sample-size lab."

**Interviewer**: "Derive the two-sample t-test statistic and its degrees of freedom."

**Candidate**: "Under H0 the two samples come from populations with the same mean. The
difference of sample means, X-bar1 - X-bar2, has variance sigma^2·(1/n1 + 1/n2) under the
equal-variance assumption, so we standardize: t = (X-bar1 - X-bar2) / (s_p·sqrt(1/n1 +
1/n2)), where s_p is the pooled standard deviation — the degrees-of-freedom weighted
average of the two sample variances, sqrt(((n1-1)·s1^2 + (n2-1)·s2^2)/(n1 + n2 - 2)).
The degrees of freedom are n1 + n2 - 2: each sample gives up one degree of freedom for
its own mean. The lab's `twoSampleTTest` implements exactly this: `varPool = ((n1-1)*s1^2
+ (n2-1)*s2^2)/(n1+n2-2)`, then t = (mean1 - mean2)/sqrt(varPool*(1/n1 + 1/n2)). The demo:
two groups with means 5.1 and 7.4 give t = -3.2408, df = 8, p = 0.011864 — significant
at 0.05, so the groups differ."

**Interviewer**: "When would you use Welch's t-test instead, and how does it differ?"

**Candidate**: "Welch's test drops the equal-variance assumption — and it's the default
any time sample sizes or variances differ, which is most real A/B data. The statistic
uses the separate variances: t = (X-bar1 - X-bar2) / sqrt(s1^2/n1 + s2^2/n2), and the
degrees of freedom become the Welch-Satterthwaite approximation instead of n1 + n2 - 2:
a messy but computable formula that makes df depend on the variance imbalance. The
conceptual point: the pooled t-test bakes in 'the two populations have the same spread',
and if that's false, the pooled estimate is wrong in both groups. In practice the two
tests agree when variances and sizes are balanced — and disagree exactly when the data
is imbalanced, which is when the assumption matters most. The lab's demo is balanced,
so both routes agree; the lab's discussion notes this is the default choice for unequal
variances."

**Interviewer**: "What is a p-value, precisely?"

**Candidate**: "The probability of observing a statistic at least as extreme as the one
we got, computed under the null hypothesis — 'if H0 were true, how surprising is this?'.
It is not the probability that H0 is true, and it is not the probability that the
alternative is true. The lab's machinery makes the definition concrete: `pValue(t, df)`
computes the survival of the t distribution — 2·(1 - CDF(|t|)) for two-sided — so the
p-value is a property of the null sampling distribution plus the data, not of any belief
about hypotheses. The demo: t = 2.6171 with df = 9 gives p = 0.027945 — under H0, a
statistic this extreme happens only 2.8% of the time. Reporting discipline: always pair
p with the effect size and the sample size, because p conflates both — a huge n turns a
trivial effect into p = 0.0001."

**Interviewer**: "Explain Type I and Type II errors and how the lab's significance level relates to them."

**Candidate**: "Type I error — false positive — is rejecting a true null. The significance
level alpha is its ceiling: at alpha = 0.05, a correct null gets wrongly rejected up to
5% of the time, which is exactly the p < alpha rule. Type II error — false negative — is
failing to reject a false null; its complement, 1 - beta, is power. The relationship is
a trade-off: at fixed sample size, shrinking alpha shrinks power, and vice versa. The
lab's demos show both directions: the chi-square independence test on the constructed
table rejects with p = 0.002569 (no Type I error committed), while the paired t-test
demo, p = 0.075130 at n = 5, correctly stays silent — a tiny sample could not reasonably
separate signal from noise. The later power lab turns this trade-off into numbers: 80%
power, sample size formulas, MDE."

**Interviewer**: "When do you use one-tailed vs two-tailed tests?"

**Candidate**: "Two-tailed as the default, always. It tests 'the parameter differs' in
either direction, which matches most real questions — a new checkout could improve or
regress conversion. One-tailed ('we only care if it goes up') is justified in rare,
pre-committed cases — a pure guardrail where moving the wrong direction is equivalent to
no change — and even then it's a trap: the null can be rejected because the effect went
the *other* way and the one-sided test's math is then wrong. The lab's t-tests default to
two-sided, and the demo's p-values are all computed as 2·(1 - CDF(|t|)). The honest
version of the one-sided question is a two-sided test plus a directional hypothesis
about the sign of the estimate — same numbers, no cheating."

**Interviewer**: "Walk through the paired t-test demo. Why is it less significant than the two-sample case?"

**Candidate**: "The paired test computes differences per subject — d_i = after_i -
before_i — and tests whether the mean difference differs from zero: t = d-bar /
(s_d·sqrt(1/n)). Its power comes from canceling subject-level variation: if every subject
has a large baseline, the paired test removes it, leaving only the treatment effect. The
demo is the cautionary flip side: n = 5 with differences of small magnitude gives
t = 2.3905, df = 4, p = 0.075130 — not significant at 0.05. Two lessons. First, pairing
only helps if the two measurements are genuinely correlated — otherwise you pay for the
lost degrees of freedom. Second, small n is small n: p = 0.075 is not 'no effect', it's
'this sample cannot tell', exactly the conversation the power lab formalizes."

**Interviewer**: "Derive the chi-square goodness-of-fit statistic."

**Candidate**: "For k categories with observed counts O_i and expected counts E_i under
the null, the statistic is chi^2 = sum over i of (O_i - E_i)^2 / E_i. The intuition:
each term is a squared deviation standardized by its own expected count, so categories
with small expectations get relatively more weight — and that's also the warning, since
tiny E_i make the statistic explode, which is why the standard rule requires all E_i >=
5 (or pooling categories). Under the null, chi^2 is approximately chi-square distributed
with k-1 degrees of freedom — k counts, one constraint from matching the total. The lab's
`chiSquareGoodnessOfFit` computes the observed test statistic and the p-value via the
regularized gamma function, `1 - gammp(df/2, chi2/2)`. Demo: a four-category check gives
chi^2 = 2.0, df = 3, p = 0.572407 — consistent with H0."

**Interviewer**: "And the independence test? Walk through the lab's example."

**Candidate**: "For a contingency table with r rows and c columns, the null is 'row and
column variables are independent'. The expected count in each cell is (row total ·
column total) / grand total — the product rule for independence scaled to the table.
The statistic is the same cell-wise sum of (O - E)^2 / E, now with (r-1)(c-1) degrees of
freedom. The lab's `chiSquareIndependence` demo uses the classic example: a 2x2 table of
users by cohort and preference, expected counts computed from the margins, giving chi^2
= 9.0909, df = 1, p = 0.002569 — strongly dependent. The teaching point: for a 2x2 table
the whole test reduces to comparing the two conditional proportions, and p = 0.0026 is
what 'the proportions differ' looks like in chi-square language."

**Interviewer**: "What assumptions back these tests, and how do you check them?"

**Candidate**: "For the t-tests: independence of observations — the big one, violated by
shared-user clustering or time trends; approximate normality of the mean, which for
moderate n is the CLT's job, but for small n needs the data itself to be roughly
normal; and (for the pooled version) equal variances, checkable by an F-test on the
variances and fixable by Welch. For chi-square: expected counts at least 5 per cell and
independent observations — the same clustering warning, since doubled users double the
statistic. The lab's demos are small and controlled, so the assumptions hold by
construction; the interview-level answer is to name the check for each assumption and
the standard fallback: rank tests from the later labs for normality failures, Welch for
variance failures, and blocking for dependence."

**Interviewer**: "A PM runs an A/B test and reports 'z = 0.3727, p = 0.7094, no effect.' What do you make of this?"

**Candidate**: "Let's read the lab's numbers, because this is exactly the demo's
`zTestForProportions` output on two nearly identical conversion rates. The z statistic
for proportions is (p1 - p2) / sqrt(p-pooled·(1 - p-pooled)·(1/n1 + 1/n2)), and p =
0.7094 says: under the null of equal conversion, a difference this large or larger is
expected 71% of the time — totally unremarkable. So the statement 'no effect' is wrong;
the correct statement is 'this sample provides no evidence of an effect'. Whether that's
a finding depends entirely on power: if the test was sized to detect a 1% lift and the
true lift were 0.5%, p = 0.7 is the expected outcome of an underpowered test. The
decision rule I'd give: compute the MDE for the achieved sample size and check whether
the business would have cared about an effect that size."

**Interviewer**: "You're testing three variants against control. What's the multiple-comparisons problem?"

**Candidate**: "Three pairwise tests at alpha = 0.05 each: the chance that at least one
false positive fires is 1 - 0.95^3 ≈ 14% — the family-wise error rate grows with the
number of comparisons, and the naive procedure lies. Options: Bonferroni — divide alpha
by the number of comparisons, simple and conservative; Tukey's HSD — the ANOVA-paired
approach, less conservative when all pairwise comparisons are of interest; or a single
omnibus test first — one ANOVA or chi-square across all variants — and pairwise tests
only after the omnibus rejects. The lab's ANOVA lab (04) and this lab's chi-square
family are the machinery: run the global test, then structure the follow-ups. The
business answer to the PM: 'the more variants you test, the more of your budget you
spend on false positives — that's a real cost, not a technicality'."
