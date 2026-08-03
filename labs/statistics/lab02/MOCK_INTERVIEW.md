# Lab 02: Mock Interview — Probability Distributions

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Normal, Binomial, Poisson, exponential distributions, sampling, CLT, inverse transform, Box-Muller

---

**Interviewer**: "Walk me through the distributions this lab covers and when each one is the right model."

**Candidate**: "Four canonical families, each tied to a generative story. The normal
distribution — `NormalDistribution`, PDF `pdf(x)` and CDF `cdf(x)` — for sums and
averages of many small independent effects: measurement error, test scores, and everything
the CLT covers. The binomial — `BinomialDistribution`, `pmf` and `cdf` — for counts of
successes in n independent trials with fixed probability p: conversion counts, click
counts, defect counts. The Poisson — `PoissonDistribution` — for counts of rare events in
a fixed interval: arrivals per minute, error counts per hour. And the exponential —
`ExponentialDistribution`, `sample(rate)` — for waiting times between events, the
continuous partner of Poisson. The demo prints each family's PMF/CDF table for a small
case, which makes the shape visible: binomial n=10 peaks at 5 with p=0.5, Poisson with
lambda=3 peaks at 3, and the exponential density decays from its peak at zero."

**Interviewer**: "Derive the Box-Muller transform."

**Candidate**: "The problem: generate a standard normal from uniform randomness. The
geometric insight: if (X, Y) is a pair of independent standard normals, then in polar
coordinates the radius squared, R^2 = X^2 + Y^2, is exponential with rate 1/2, and the
angle Theta is uniform on [0, 2·pi) and independent of the radius. So invert both: R =
sqrt(-2·ln(U1)) and Theta = 2·pi·U2, then X = R·cos(Theta), Y = R·sin(Theta) are
independent standard normals. The lab's `sampleNormal` uses exactly the two-lines form —
`Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2)` — plus the standard
guards: u1 is generated as 1 - random.nextDouble() so that log is never taken at zero,
and u2 in (0,1] keeps the cosine argument valid. This is the workhorse behind every
sampling-based method in the later labs — Monte Carlo, Bayesian, bootstrap."

**Interviewer**: "When n is large, how do you actually compute the binomial CDF?"

**Candidate**: "You don't sum the PMF for a thousand terms — you approximate. The lab's
Binomial CDF implements the incomplete beta function route via continued fractions
(`betaContinuedFraction`), so `binomialCdf(15, 30, 0.5)` returns the exact CDF value
without iterating 16 factorials, which would overflow long before n reaches 200. The
practical ladder of approximations: when n·p and n·(1-p) are both at least 5, the normal
approximation with continuity correction is fine; when p is small and n large, the
Poisson approximation with lambda = n·p is the model. The demo's CDF at 5 for n=10, p=0.5
is 0.623047 — and the same machinery gives you the exact tail probabilities that power
the hypothesis tests in later labs."

**Interviewer**: "Explain the Poisson distribution and its relationship to the binomial."

**Candidate**: "Poisson(lambda) counts events in a fixed interval when events arrive
independently at a constant rate. It's the limit of Binomial(n, p) as n grows and p
shrinks with n·p = lambda fixed — the rare-event limit. The lab's `PoissonDistribution`
computes the PMF by the recurrence pmf(k) = pmf(k-1) · lambda/k, which starts at
pmf(0) = e^(-lambda) and is numerically stable, and the CDF by summing. The demo: lambda
= 3 gives CDF(4) = 0.815263. The practical use: error counts, request failures, user
signups per day — anything count-like with no upper bound where 'events are rare' is the
generative story. The mean equals the variance equals lambda — a quick check you can do
on real data before trusting the model."

**Interviewer**: "How does the lab sample from the Poisson distribution?"

**Candidate**: "By inversion of the CDF — the same `sample(CDF)` pattern used across the
lab: draw u uniformly, then walk the PMF/CDF cumulative sums until they cross u, and
return that k. `PoissonDistribution.sample(3.0)` with seed 42 returns 2.0 in the demo.
It's correct but O(lambda) per sample, so for large lambda the practical implementations
switch to rejection methods like Knuth's algorithm. The inversion pattern matters
conceptually: any distribution with an invertible or walkable CDF can be sampled from
uniform randomness — which is the one idea that unifies every sampler in this lab:
`sampleUniform`, `sampleNormal`, `sampleExponential` and the Poisson sampler are all
'push a uniform through the CDF'."

**Interviewer**: "Derive the exponential sampler and its memoryless property."

**Candidate**: "The exponential CDF is F(t) = 1 - e^(-rate·t), which inverts cleanly: t =
-ln(1 - u)/rate, the lab's `sampleExponential`: `Math.log(1 - random.nextDouble()) /
(-rate)`. The memoryless property is the deep result: P(T > a + b | T > a) = P(T > b) —
the process has no age. It's why the exponential is the waiting-time distribution for a
Poisson process: the time until the next event doesn't depend on how long you've already
waited. That's the property that makes Poisson-process simulation trivial — you sample
the next inter-arrival time, then the next, forever. In the demo, sampling with rate 2.0
produces short inter-arrival times clustered near zero, which is exactly the shape of a
memoryless process."

**Interviewer**: "Walk through the inverse-transform method and its failure modes."

**Candidate**: "To sample from a distribution: draw u ~ Uniform(0,1), then return
F^(-1)(u) — the quantile function at u. It works whenever you have the inverse CDF or can
walk the CDF. Failure modes matter. First, numerical accuracy of the CDF itself: if the
CDF is computed badly in the tails, the samples inherit the error — which is why the lab's
normal CDF goes through the complementary error function, `0.5 * erfc(-z/sqrt(2))`,
rather than naive integration. Second, boundary behavior: if your uniform draw can be
exactly 1, log and quantile functions blow up — the standard guards are generating 1 -
nextDouble() and rejecting exact zeros. Third, monotonicity: inversion needs a
non-decreasing CDF, so any numeric glitch that breaks monotonicity inverts the sampling
order. The exponential sampler shows the pattern: log(1-u) is the clean inverse of
1-e^(-t)."

**Interviewer**: "How do you test that a sampler is correct?"

**Candidate**: "Three levels, in order. Statistical: generate a large sample and compare
its empirical quantiles to the theoretical CDF — a Kolmogorov-Smirnov-style check; the
lab's demos print theoretical PMF/CDF tables you can compare against sample frequencies.
Moment checks: sample mean vs theoretical mean, sample variance vs theoretical variance
— for the lab's standard normal, mean 0.0 and variance 1.0. And exact boundary checks:
constant arguments produce degenerate output, like `sampleNormal(42)` on the same seed
always reproducing the same stream. The most powerful check is also the cheapest:
implement a reference quantile function independently and compare `F(F^(-1)(u)) == u`
over a dense grid — round-trip errors show up as systematic deviations in the samples."

**Interviewer**: "How does the CLT justify using the normal distribution for averages?"

**Candidate**: "The CLT says: sums (and averages) of many independent, identically
distributed random variables with finite variance converge to a normal distribution,
regardless of the original distribution's shape. So even if individual latencies are
wildly right-skewed, the average of 30 of them is approximately normal, and the
approximation improves as n grows. The two caveats that matter in practice: convergence
is slow for heavy-tailed distributions — you may need n in the hundreds, and the lab's
normal machinery's PDF/CDF tables assume the approximation has converged; and the theorem
needs finite variance, so Cauchy-like tails never converge. The practical pattern: use
the normal for averages and sums at scale, but for single observations of skewed data,
keep the skewed model — the binomial for counts, Poisson for rare events, exponential
for wait times."

**Interviewer**: "The lab distinguishes probability mass functions from density functions. Why does that distinction matter for code?"

**Candidate**: "A PMF returns actual probabilities — binomial pmf(6) = 0.205078 for n=10,
p=0.5 — values that sum to 1 over the support, so you can read them directly and use them
for decision rules. A PDF returns a density — a value in arbitrary units, possibly greater
than 1 near a sharp peak — and only integrates to 1. The lab keeps them as separate
methods, `pmf`/`pdf`, so callers never confuse the two. It matters for code because it
determines what you can do with the value: PMF values can be compared across k, multiplied
into likelihoods, and summed into CDFs; PDF values can only be used in ratios and
likelihood functions. Confusing them is a classic bug in hand-rolled likelihood code."

**Interviewer**: "How would you use this lab's machinery for a growth experiment with conversion data?"

**Candidate**: "Model the trial as binomial: n users per variant, observed conversion count
k, rate p = k/n. The lab's `BinomialDistribution` gives exact PMF/CDF values to answer
'what's the probability of seeing at least this conversion count under the control rate?'
— a Fisher-style exact comparison that doesn't rely on large-sample normality. For
arrival-like metrics — signups per hour, errors per session — the `PoissonDistribution`
with lambda = historical rate answers 'how surprising is today's count?', which is a
better anomaly alert than a z-score on counts because counts are integer and skewed.
And when sample sizes grow large, the same binomial CDF, now smooth via the beta route,
feeds the z-test machinery of later labs. The lab is the toolkit; the experiment design
questions — which metric, which model, which comparison — are the analysis."

**Interviewer**: "What are the failure modes of this lab's approach, and how do you catch them?"

**Candidate**: "Four. Sampling with a bad seed or reused seed: you get identical 'random'
streams and fake independent experiments — the lab's `Random(42)` demos are explicitly
reproducible, which is a feature for demos and a bug if you forget to reseed in
production. Extreme parameters: binomial with p very close to 0 or 1 and large n — the
recurrence underflows or overflows, so the implementation routes through the beta
function; naive summing would die at n ~ 170 from factorial overflow. The exponential
sampler at rate 0 — division by zero — needs a guard. And treating the normal
approximation as valid for small n with skewed underlying data — the CLT's rate of
convergence is distribution-dependent, and the answer is always 'check the finite-sample
behavior with simulation before trusting the approximation'."
