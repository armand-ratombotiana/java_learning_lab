# Lab 10: Mock Interview — Statistical Power & Effect Size

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Power, effect size (Cohen's d), sample size, minimum detectable effect, hypothesis testing trade-offs

---

**Interviewer**: "Define statistical power and why it matters in practice."

**Candidate**: "Power is the probability of rejecting the null hypothesis when the
alternative is true — 1 - beta, where beta is the Type II error rate. Practically it's
your detection rate: run the same experiment 100 times with a real effect, and power
tells you how many will correctly reach significance. The lab's `StatisticalPower`
class computes it exactly: power = 1 - Phi(z_alpha/2 - d·sqrt(n/2)) +
Phi(-z_alpha/2 - d·sqrt(n/2)) via `powerFromCohensD`, which threads the normal CDF
through the complementary error function: normCdf = 0.5·erfc(-z/sqrt(2)). The four
dials are sample size n, effect size d, alpha, and beta — fix any three and the fourth
is determined. In product teams, running underpowered experiments is the most common
silent failure: 'no significant effect' gets reported as 'no effect' when the test
simply couldn't have detected it."

**Interviewer**: "Derive the power formula for a two-sample means test."

**Candidate**: "Under H0 the standardized test statistic Z = (X-bar1 - X-bar2) /
(sigma·sqrt(2/n)) is standard normal. Under the alternative with mean difference delta,
the same statistic is normal with mean delta / (sigma·sqrt(2/n)) = d·sqrt(n/2) — where
d is Cohen's d, delta/sigma — and unit variance. The two-sided test rejects when
|Z| > z_alpha/2, so power = P(Z > z_alpha/2) + P(Z < -z_alpha/2) under the shifted
distribution = 1 - Phi(z_alpha/2 - d·sqrt(n/2)) + Phi(-z_alpha/2 - d·sqrt(n/2)). The
lab's `powerFromCohensD` implements exactly this expression, and
`powerTwoSampleMeans` first maps (delta, sigma, n) into d via `cohensD`. The demo shows
it end-to-end: delta = 2, sigma = 10, n = 100 gives d = 0.3551 and power = 0.6967."

**Interviewer**: "What is Cohen's d, and how does `pooledStdDev` work?"

**Candidate**: "Cohen's d is a scale-free effect size: d = (mean1 - mean2) / pooledStdDev.
Scale-free means it survives comparisons across experiments with different units — a d
of 0.5 for latency in milliseconds is the same strength as a d of 0.5 for revenue in
dollars. The pooled standard deviation is a variance-weighted average: sqrt(((n1-1)·
s1^2 + (n2-1)·s2^2) / (n1 + n2 - 2)), which is exactly the residual variance under the
equal-variance two-sample model — the same denominator the t-test uses. The lab's
`interpretCohensD` applies Cohen's benchmarks: 0.2 small, 0.5 medium, 0.8 large. The
demo: pooled SD = 9.8562, d = 0.3551 — between small and medium — with the
interpretation string 'small'."

**Interviewer**: "How do you compute sample size per group from a target power?"

**Candidate**: "Invert the power formula. From power = 1 - Phi(z_alpha/2 - d·sqrt(n/2)),
solve for n: n = 2·(z_alpha/2 + z_beta)^2 / d^2. The lab's `sampleSizeFromCohensD`
implements that inversion — `2 * (zAlpha2 + zBeta) * (zAlpha2 + zBeta) / (d * d)` — and
`sampleSizePerGroup` chains it through `cohensD` from raw means and sigma. The demo:
d = 0.2 -> n = 392.44 (round up to 393 per group), d = 0.5 -> 62.79 (63), d = 0.8 ->
24.53 (25). The inverse-square scaling is the key practical lesson: to halve the minimum
detectable effect you quadruple the sample size. That's why real platforms show power
curves — the lab's `RecommendationPower` prints one for n = 10..100."

**Interviewer**: "What's a minimum detectable effect, and how is it computed?"

**Candidate**: "MDE is the smallest true effect a test can reliably detect at fixed n,
alpha, and power — invert the sample-size equation for delta: MDE = sigma·(z_alpha/2 +
z_beta)·sqrt(2/n). The lab's `minimumDetectableEffect` computes exactly this —
`sigma * (zAlpha2 + zBeta) * Math.sqrt(2.0 / n)` — and the demo shows the inverse-square
behavior directly: with sigma = 10, MDE drops from 3.9620 at n = 100 to 2.8016 at
n = 200. MDE is the number PMs actually understand: instead of 'power 80%', say 'at
10,000 users per group, this test can detect a 4-minute lift in watch time with 80%
probability'. It converts statistical machinery into a product decision."

**Interviewer**: "Walk through the normal quantile machinery: erfc, normCdf, normInv."

**Candidate**: "The lab implements three layers. `erfc(x)` is the complementary error
function 2/sqrt(pi)·integral from x of e^(-t^2) dt, evaluated with a rational
approximation (Abramowitz-Stegun-style, 1.26551223 coefficient series). `normCdf(z)` is
0.5·erfc(-z/sqrt(2)) — the standard normal CDF. `normInv(p)` is the inverse CDF — the
Acklam algorithm with three regions (p < 0.02425, central, upper) followed by one
Halley-style refinement step. Every power and sample-size computation in the lab
funnels through these three: erfc powers the CDF, the CDF powers power, and normInv
powers z_alpha/2 and z_beta. The round-trip sanity check — z = 1.96 in, p = 0.975, then
back out z' = 1.960000 — verifies the whole chain at the sixth decimal."

**Interviewer**: "Why does power = 0.80 with alpha = 0.05 keep coming up? What's the trade-off?"

**Candidate**: "They're conventions, not laws: Fisher's 0.05 alpha and the 'adequately
powered' 0.80 standard from Cohen's power analysis tradition. But the trade-off is real
and worth stating explicitly. At fixed n, lowering alpha (stricter Type I control)
raises z_alpha/2 and lowers power — you detect fewer real effects; raising power to 0.90
at fixed alpha costs roughly 30% more sample (z_beta goes from 0.842 to 1.282:
(0.842+1.96)^2 vs (1.282+1.96)^2). In practice I treat them as context-dependent: a
harmless UI experiment can tolerate alpha = 0.10 for more power; a safety-critical
change demands alpha = 0.01. The lab's GUIDE table shows the sample sizes: 393
(d = 0.2), 63 (d = 0.5), 25 (d = 0.8) per group at 80% power."

**Interviewer**: "A PM says 'we tested it and there was no effect.' What do you ask before believing that claim?"

**Candidate**: "One question, always: 'What was the minimum detectable effect at your
sample size?' If the MDE at their n is larger than the effect that would have mattered
to the product, the test is uninformative — absence of evidence is not evidence of
absence. The lab's toolchain makes the conversation precise: plug their n, alpha, sigma
into `minimumDetectableEffect`, get the MDE, and check whether it fits inside the
business's decision window. If the MDE is 4 minutes but the team cared about a 2-minute
lift, they need 4x the sample (inverse-square law), not a bigger slogan. The
`RecommendationPower` demo shows the full chain: pooled SD 9.86, d = 0.36, power at
n = 100 is only 0.697 — they should have sized to ~62-63 per group for d = 0.5, or
restated the goal."

**Interviewer**: "How does power relate to the significance tests from the earlier labs?"

**Candidate**: "They're the two sides of the same coin. The t-test labs established the
decision rule — reject when the statistic exceeds the critical value — power quantifies
how often that rule works when there's a real effect. The chi-square labs' p-value
under H0 vs the power function's 1 - beta under H1 are the same machinery viewed from
both hypotheses. The factorial lab's effect estimates feed straight in: once you know
effect A = 6.00 from a 2^2 design with sigma = 10, you can compute the sample size the
confirmation run needs — d = 6/10 = 0.6, so n = 2·(1.96+0.842)^2/0.36 ~ 44 per group.
One formula, one story across the whole curriculum."

**Interviewer**: "What happens to power when the two groups have unequal variances?"

**Candidate**: "The formulas so far assume the pooled (equal-variance) model, and when
variances differ materially, the naive numbers are wrong in a predictable direction: the
effective standard deviation sits between the two sigmas, and the test that uses the
pooled estimate either overstates power (if it averages over a small-variance group)
or understates it. The fix is the Welch-style treatment: use the separate-variances
standard error, sqrt(s1^2/n1 + s2^2/n2), in place of sigma·sqrt(2/n), and the power and
sample-size formulas carry through with that standard error — with unequal n, the
variance term is dominated by the small-n, large-variance group, which is why balanced
designs with balanced variances are the efficient choice. The practical discipline:
check the variance ratio before sizing, and if it's beyond ~4, size with the Welch
form rather than the pooled one."

**Interviewer**: "How do you distinguish statistical significance from practical significance?"

**Candidate**: "They're different questions, and the lab's toolkit answers both
separately. Statistical significance is a property of the test given the sample: at
large n, even a tiny effect produces p < 0.05. Practical significance is a property of
the effect size relative to the business: is d large enough to matter? That's where
Cohen's d and the MDE come in — d = 0.3551 and 'small' is the *effect's* verdict
regardless of n, and the MDE is the smallest effect the experiment could have certified.
The pattern that should always accompany a significant result: 'the effect is real, and
it is small' — the first clause from the p-value, the second from the effect size. The
failure mode this guards against: celebrating a significant 0.01-point conversion lift
with millions of users, when the practical decision is 'not worth shipping' —
significance tells you the data is informative, not that the product move is right."

**Interviewer**: "How does peeking at results during an experiment change power and error rates?"

**Candidate**: "Stopping or even looking early and deciding at each glance based on the
p-value inflates the Type I error rate — the family-wise problem in time instead of in
space: at k looks, the chance of a false positive by the end climbs well above alpha,
roughly like the multiple-comparisons bound from the hypothesis-testing lab. The power
consequences are subtle too: stopping early when results look good overstates the
effect (winner's curse), and stopping early when results look bad understates power —
the apparent 'savings' in sample size are an accounting illusion. The standard fixes:
fixed-horizon designs with a pre-registered n (what this lab's formulas compute),
alpha-spending sequential designs (O'Brien-Fleming-style boundaries), or Bayesian
posterior monitoring with pre-specified stopping rules, which the Bayesian lab's
machinery supports. The rule that survives everything: the stopping rule must be
written before the data is collected."
