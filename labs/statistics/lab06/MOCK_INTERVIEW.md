# Lab 06: Mock Interview — Bayesian Statistics

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Bayes' theorem, Beta-Binomial conjugate, posterior sampling, credible intervals, Monte Carlo, Bayesian vs frequentist

---

**Interviewer**: "Walk me through the Bayesian machinery in this lab from prior to posterior."

**Candidate**: "The pipeline: a prior distribution encodes what we believe before seeing
data; the likelihood describes how the data is generated given a parameter; and Bayes'
theorem combines them — posterior is proportional to likelihood times prior. The lab's
`BayesianStatistics` implements the Beta-Binomial conjugate pair: a Beta(alpha, beta)
prior on a conversion rate, and data with s successes and f failures updates to
Beta(alpha + s, beta + f). The demo runs a concrete case: Beta(9, 3) prior updated with
a small sample gives a posterior whose credible interval is [0.4822, 0.9398] — wide
because the data is thin. The magic of conjugacy: the posterior stays in the same family
as the prior, so each update is just adding counts — alpha and beta grow by the
observed successes and failures, and every subsequent update is the same simple addition."

**Interviewer**: "Derive the Beta-Binomial posterior."

**Candidate**: "The model: rate p ~ Beta(alpha, beta), and data X ~ Binomial(n, p) with s
successes. The Beta density is proportional to p^(alpha-1)·(1-p)^(beta-1); the binomial
likelihood is proportional to p^s·(1-p)^(n-s). Bayes says the posterior density is
proportional to the product: p^(alpha+s-1)·(1-p)^(beta+n-s-1) — which is, by inspection,
a Beta(alpha + s, beta + n - s) density. The update rule is the entire theorem for this
pair: add successes to alpha, failures to beta, done. The lab's `updateBeta` does
exactly that — `new BetaPosterior(alpha + successes, beta + failures)` — and the
conjugate structure is why the lab can compute posterior intervals in closed form via
`betaInv` rather than by numerical integration: the posterior is a Beta, so its
quantiles are a function call."

**Interviewer**: "What is a credible interval, and how does it differ from a confidence interval?"

**Candidate**: "A credible interval is a probability statement about the parameter: from
the posterior, there is a 95% probability that the rate lies in [0.4822, 0.9398]. The
parameter is treated as random, the data as fixed, and the interval is a direct read of
the posterior's quantiles — the lab's `credibleIntervalBeta` uses `betaInv` at
(1-alpha)/2 and (1+alpha)/2. A confidence interval is the frequentist dual: the *data*
is random, the parameter fixed, and the 95% is a property of the procedure — across
repeated experiments, 95% of such intervals will contain the true value. The practical
difference: the credible interval answers the question people actually ask — 'given this
data, where is the rate?' — while the confidence interval answers 'if I repeat this
experiment forever, how often does the recipe work?' Both are valuable; they're just
different propositions."

**Interviewer**: "Walk through the lab's Monte Carlo posterior sampling."

**Candidate**: "When the posterior isn't a conjugate family, you sample from it instead of
solving for it. The lab's `monteCarloPosterior` draws parameter values from the
posterior distribution and records them: sample p from the Beta posterior, generate
data under p, keep the summary. The demo's A/B example is the payoff: `bayesAB` samples
rates for variant A and variant B independently, then counts how often B's sampled rate
exceeds A's — P(B > A). With control at 100/1000 and variant B at 120/1000, the demo
computes P(B > A) = 0.9238 — an 92.4% probability that B is better, no significance
threshold required, and the individual credible intervals [0.0829, 0.1202] for A and
[0.1013, 0.1416] for B tell the same story. The strength of the approach: any posterior,
any loss function, any question you can express as a computation on samples."

**Interviewer**: "How does the lab sample from the Beta distribution?"

**Candidate**: "The lab implements `sampleBeta` via the standard transformation: two
independent gamma variates, one with shape alpha and one with shape beta, and the ratio
gamma_alpha / (gamma_alpha + gamma_beta) is Beta(alpha, beta). The gammas come from
Marsaglia and Tsang's method: accept-reject on the cubic transform, cached shape
constants, and `Math.log`-based rejection — the standard, numerically stable choice that
handles large alpha and beta gracefully. Each draw is one random sample from the
posterior, and the seed is fixed in the demos so runs are reproducible. The conceptual
point: sampling is the universal posterior tool — if you can sample from the posterior,
you can answer any question about it: means, quantiles, probabilities of ordering, loss
minimization. The lab's closed-form Beta plus its sampler are the two routes the rest of
the lab branches from."

**Interviewer**: "How does the prior influence the result, and how do you choose one?"

**Candidate**: "The prior is the starting belief, and the data drags it toward the
likelihood with strength proportional to the data's size. A strong prior — large alpha +
beta — resists the data; a weak prior — small alpha + beta — is dominated quickly. The
lab's demo makes the mechanism visible: the strong-prior example barely moves from the
prior because the sample is tiny, while the 1000-observation sample overwhelms a
moderate prior. Choice principles: use domain knowledge when it exists — historical
rates, expert bounds; use a weakly informative prior when it doesn't — the lab's Beta
with small parameters, which regularizes without dominating; and always state the prior
explicitly, because a posterior without its prior is an unverifiable claim. The honest
benchmark: compare the posterior under your prior against the same posterior under a
flat prior; if they differ materially, your conclusions are prior-driven."

**Interviewer**: "When would you choose Bayesian over frequentist analysis?"

**Candidate**: "When the question is naturally a probability statement about a parameter —
'what's the chance B beats A?' — Bayesian answers directly, while the frequentist
p-value answers a different question ('if the true rate were equal, how unlikely is this
data?') that stakeholders routinely misinterpret. Bayesian handles sequential monitoring
cleanly: you can look at the posterior at any sample size without the alpha-inflation
machinery that fixed-horizon tests need. Small samples: the prior regularizes, where
frequentist small-sample tests are fragile. When to stay frequentist: established
regulatory or reporting conventions, where the procedure's long-run guarantees are the
requirement; and when there's no defensible prior, the Bayesian posterior is just the
likelihood with extra work. The lab's `bayesAB` demo is the pitch: P(B > A) = 0.9238 is
a decision-ready sentence."

**Interviewer**: "The demo shows A's interval and B's interval overlapping, yet P(B > A) is high. Reconcile that."

**Candidate**: "Interval overlap is a famous red herring. Credible intervals overlap when
each posterior has nontrivial spread — the intervals [0.0829, 0.1202] and [0.1013,
0.1416] do overlap — but the *joint* question, 'is B's rate actually above A's?', is a
statement about the paired samples, not about the two marginal intervals. Because the
two posteriors' uncertainties are positively correlated across the shared prior scale,
the overlap region doesn't translate into 'they're likely equal'. The demo computes the
right quantity directly: sample both posteriors together, count pairs where B > A, and
get 0.9238. The lesson: never compare two intervals by eyeball — compute the posterior
probability of the comparison you actually care about. The same trap kills frequentist
readings: non-overlapping 95% CIs are stricter than a proper difference test, and
overlap says almost nothing."

**Interviewer**: "How do you scale this machinery to more than two variants?"

**Candidate**: "The same sampling pipeline, generalized: give every variant its own
Beta posterior from its own observed counts, sample all of them jointly, and answer any
comparison question on the samples — P(B > A), P(C is best), the probability that each
variant is the top performer, expected regret from choosing wrong. The lab's
`bayesAB` is the two-arm case; the multi-arm extension is conceptually identical because
the machinery is already Monte Carlo. The practical extensions: multi-armed bandit
allocation — draw from each posterior and assign traffic proportional to P(arm is best) —
and posterior expected loss, which converts the sampling output into a decision. The
complications are the same as frequentist multi-comparison: with many arms, 'P(arm is
best)' shrinks mechanically, and the honest reporting shows the full ranking, not just
the winner."

**Interviewer**: "What happens as the sample size grows? How does the prior's influence fade?"

**Candidate**: "The posterior mean is a weighted average of the prior mean and the sample
proportion, with weights proportional to their precisions — the prior's total count
alpha + beta against the sample's n. Add a few observations to a strong prior and the
posterior barely moves; add thousands and the prior is negligible. The lab's two demos
are the two ends: the strong-prior example — Beta with a large alpha + beta — stays
near the prior because the sample is tiny, while the 1000-observation A/B case is
dominated by the data. The formal statement: the posterior concentrates around the true
parameter at rate 1/sqrt(n) — the Bayesian version of the CLT, the Bernstein-von Mises
theorem — so prior and likelihood agree in the limit. The practical lesson: the prior
is a small-sample mechanism, not a fixed belief — and the honest check is to report the
posterior under your prior and under a flat prior together."

**Interviewer**: "How do you verify the lab's Monte Carlo results are correct?"

**Candidate**: "Three ways. Closed-form cross-checks: the credible interval computed by
sampling should match the closed-form `credibleIntervalBeta` — the lab's own demo shows
both routes agreeing on the same Beta posterior, which is a built-in test of the sampler.
Deterministic seeds: the demos fix `Random(42)`, so the outputs are reproducible — any
change in the sampler changes the numbers, making regressions visible. And moment
checks: a sampled posterior's mean should approach alpha/(alpha + beta) and its
quantiles the closed-form inverse-Beta values as the draw count grows. The deeper
point: Monte Carlo has a convergence rate of 1/sqrt(N), so the lab's draw counts are
modest by design — the interview answer is to check that the *answer* is stable across
seeds and draw counts before trusting the *decision*."

**Interviewer**: "How do you present Bayesian results to a non-technical stakeholder?"

**Candidate**: "Translate the posterior into the decision, not the distribution. 'Variant B
has a 92% probability of beating control' is the whole message — no p-values, no
intervals. Second: show the uncertainty honestly — 'the expected lift is 2%, with a 90%
chance the true lift is between 0.5% and 3.5%' — so the decision-maker sees both the
estimate and its width, which is what determines whether to ship, run longer, or run
bigger. Third: make the prior visible — 'we started with a modest prior from last
quarter's data; the posterior is dominated by this test's numbers' — because an
executive who doesn't know there was a prior will assume there wasn't one. The lab's
P(B > A) and credible intervals are exactly the vocabulary for that conversation: one
probability for the decision, one interval for the uncertainty."
