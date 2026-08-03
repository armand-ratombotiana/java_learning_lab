# Lab 08: Mock Interview — Experimental Design

**Role**: Data Scientist / ML Engineer
**Duration**: 60 minutes
**Focus**: Sample size, power, factorial designs, 2^k experiments, interactions, normal quantiles

---

**Interviewer**: "Walk me through the experimental design toolkit in this lab."

**Candidate**: "Three interlocking pieces. Sizing — `sampleSizeMeans` and
`sampleSizeProportions` — answers 'how many units do I need?' for means and for rates,
given the effect I care about, alpha, and power. The quantile machinery —
`normInv` (Acklam's algorithm), `normCdf` (via `erfc`), and the round-trip checks —
is what every sizing formula consumes: z_alpha/2 and z_beta are inverse-normal calls.
And factorial design — `factorial2` for 2^2 and `factorial3` for 2^3 — answers 'what
does each factor and each interaction do?' in one efficient experiment. The demo runs
all of it end to end: the conversion test needs 5567 per variant (p1 = 0.080, p2 =
0.095), the session-duration test needs 393 per group, the z round-trips come back to
six decimals, and the 2^2 factorial on the booking page yields effect A = 6.00,
effect B = 4.00, and interaction AB = 4.00."

**Interviewer**: "Derive the sample size formula for comparing two means."

**Candidate**: "The two-sample z-test rejects when |z| > z_alpha/2, where z = (X-bar1 -
X-bar2) / (sigma·sqrt(2/n)) with equal n per group. Under the alternative with mean
difference delta, the statistic is normal with mean delta/(sigma·sqrt(2/n)) = d·sqrt(n/2)
and unit variance, where d = delta/sigma is Cohen's d. Power 1 - beta means the
rejection threshold sits at the (1 - beta) quantile of the alternative distribution:
z_alpha/2 - d·sqrt(n/2) = -z_beta, which solves to n = 2·(z_alpha/2 + z_beta)^2/d^2.
The lab's `sampleSizeMeans(2.0, 10.0, 0.05, 0.20)` = 392.44 → 393 per group applies the
formula directly, and `sampleSizeProportions` replaces the variance term with the
Bernoulli variance, p1·(1-p1) + p2·(1-p2), giving 5566.60 → 5567. The inverse-square
lesson is right there: halving delta quadruples n."

**Interviewer**: "What is the 2^2 factorial design, and what does the lab's analysis compute?"

**Candidate**: "Two factors, A and B, each at two levels — low and high — run in all four
combinations, so the design matrix is the corners of a square: (A-,B-), (A+,B-), (A-,B+),
(A+,B+). The analysis computes three orthogonal contrasts. Effect A: the average
response at A+ minus the average at A- — (mean[B-] + mean[B+])/2 at A+ minus the same at
A-, i.e. the main effect of A averaged over B's levels. Effect B is the mirror. The
interaction AB: does A's effect depend on B's level? — (mean(A+B+) - mean(A-B+)) -
(mean(A+B-) - mean(A-B-)), the difference of differences. The lab's demo on means
{10, 14, 12, 20}: effect A = 6.00, effect B = 4.00, interaction AB = 4.00. The
interaction is not optional information: with AB = 4, reporting only the main effects
misdescribes the system."

**Interviewer**: "Walk through the lab's factorial computations on {10, 14, 12, 20}."

**Candidate**: "Arranged in standard order: A- B- = 10, A+ B- = 14, A- B+ = 12, A+ B+ =
20. Effect A = (14 + 20)/2 - (10 + 12)/2 = 17 - 11 = 6.00: turning A on adds 6 on
average. Effect B = (12 + 20)/2 - (10 + 14)/2 = 16 - 12 = 4.00. Interaction AB = (20 -
12) - (14 - 10) = 8 - 4 = 4.00: A's effect is 4 when B is off but 8 when B is on — the
two factors amplify each other. The lab's `factorial2` computes exactly these three
contrasts and returns them as the record Factorial2Result(effectA, effectB,
interactionAB). The business read for the booking page: ship both A+ and B+ together —
the interaction means shipping either alone forfeits the combined gain."

**Interviewer**: "What is a main effect when an interaction is present?"

**Candidate**: "A main effect is an average over the other factor's levels — and that
averaging is exactly why main effects can mislead when interactions are real. In the
demo, effect A = 6.00 is the *average* of A's effect at B- (4) and at B+ (8); neither
number is 6, and 'A adds 6' is true only on average. The interaction says the truth is
conditional: A adds 4 with the old review snippet and 8 with the new one. The rules of
thumb: if the interaction is large relative to the main effects, describe the system
conditionally — 'A+ B+ gives 20, every other combination is ≤ 14'; if the interaction is
small, the main effects are clean summaries. The lab's 2^3 demo shows the same
structure with more factors — effects A = 6.50, B = 4.50, C = 1.50, AB = 2.50 — where
C's main effect matters less than the AB interaction."

**Interviewer**: "How does the 2^3 design extend the 2^2, and what are its contrasts?"

**Candidate**: "Three factors, all combinations — 8 runs, the corners of a cube. The
analysis computes 7 orthogonal contrasts: three main effects (A, B, C), three two-factor
interactions (AB, AC, BC), and one three-factor interaction (ABC). The lab's
`factorial3` applies the standard sign matrix — each row is one run, each column one
contrast, with ±1 entries — and divides each contrast sum by 2^(k-1) = 4 to get the
effects. The demo on means {5, 9, 7, 15, 6, 10, 8, 18}: A = 6.50, B = 4.50, C = 1.50,
AB = 2.50, AC = 0.50, BC = 0.50, ABC = 0.50. The read: A and B are the drivers, AB
matters, and C with its small main effect and tiny interactions is a candidate to drop
or ignore — the 8 runs bought the ability to say that about every combination
simultaneously."

**Interviewer**: "What is the difference between a factorial design and testing factors one at a time?"

**Candidate**: "One-at-a-time testing changes one factor while holding the rest fixed —
it measures each effect at one arbitrary point of the other factors, so it cannot see
interactions at all (the demo's AB = 4 is invisible to it), and it needs as many
experiments as factors. A factorial design varies all factors together, and the magic
is the orthogonality: because the design is balanced, each contrast is measured
averaging over all levels of the others, and a 2^k design estimates all k main effects
plus all interactions in exactly 2^k runs. The cost comparison is the whole argument: 3
factors one-at-a-time is 6-7 runs measuring 3 things; a single 2^3 is 8 runs measuring
7 things including interactions. The extra runs are free information — the experiment
that tests factors individually is the wasteful one."

**Interviewer**: "Walk through the `normInv` round-trip checks and why they matter."

**Candidate**: "`normInv(p)` is the inverse standard normal CDF — Acklam's algorithm:
three-piece rational approximation (lower, central, upper regions) plus one Halley-style
refinement step. The lab's demo runs the round-trip: for each z in {-1.96, -1.645, 0,
1.645, 1.96}, compute p = normCdf(z), then z' = normInv(p) — and z' comes back to six
decimals every time: -1.960000, -1.645000, 0.000000, 1.645000, 1.960000. Why it
matters: every formula in this lab — sample sizes, power, MDE — is a function of
z_alpha/2 and z_beta, so one bad quantile corrupts every downstream number. The
round-trip is a cheap, complete verification of both directions at once, and the
specific values chosen are the critical values the tests actually use."

**Interviewer**: "The conversion test needs 5567 per variant for a 1.5-point lift. Why so many, and is that reasonable?"

**Candidate**: "Because the variance is the Bernoulli variance p(1-p) — near 0.08-0.095
that's about 0.073-0.086 — and the effect is tiny relative to that noise: the signal is
1.5 percentage points on a ~9% base. The formula, n = (z_alpha/2 + z_beta)^2·(p1(1-p1) +
p2(1-p2))/(p1-p2)^2, is dominated by the squared difference in the denominator — 0.015^2
= 0.000225 — and the inverse-square law does the rest: a 3-point lift would need a
quarter of the sample. And yes, it's reasonable: conversion experiments on real products
routinely run hundreds of thousands of users precisely because the effects worth
shipping are small. The honest alternative: ask what lift is worth shipping — the MDE
at your traffic budget — and if it's 0.5 points, either grow the sample or accept the
test can't answer."

**Interviewer**: "What is blocking, and why does it matter in this lab's designs?"

**Candidate**: "Blocking is grouping experimental units into homogeneous sets — regions,
days, cohorts, account sizes — so that known nuisance variation is controlled rather
than left to inflate the error. The effect: the block differences are removed from the
error term, the F-tests and contrasts get more power, and the design protects against
confounding — a test run across regions where one region's event hits one variant would
otherwise masquerade as a treatment effect. It's the experimental-design answer to the
clustering warnings in the hypothesis-testing labs: if users within a region behave
alike, they are not independent units, and blocking formalizes that structure. The
factorial lab's fixed-run designs assume clean homogeneous runs; in production, the
same contrast arithmetic runs within blocks, and the block sums are pooled at the end."

**Interviewer**: "How do you choose alpha, power, and the target effect in a real product experiment?"

**Candidate**: "The target effect comes first and is a business decision: the smallest
effect worth shipping — the MDE — converts 'we expect a 2% lift' into a number the
sizing formula needs; use the historical effect size or the ROI break-even point. Power
is a risk budget: 80% is the convention, but the honest number depends on the cost of
missing a real effect — safety-critical changes deserve 90%+; exploratory tests can
accept 70%. Alpha is the false-positive budget: 0.05 standard, 0.01 for changes that are
expensive to roll back, 0.10 for cheap high-traffic tests. The process matters more than
the numbers: write the sizing down *before* the experiment — alpha, power, MDE, n — and
compare the planned n with the traffic you can actually deliver; if they don't match,
the plan is the thing that changes, not the analysis after the fact."

**Interviewer**: "The 2^3 demo shows C with a small main effect. How do you decide whether to drop a factor?"

**Candidate**: "The decision is about the contrast structure, not the significance alone.
C's main effect is 1.50 and its interactions are all 0.50 — C is a nearly additive,
weak factor. Dropping it from the model makes the remaining estimates *more* precise,
because its tiny contribution goes into the error term that the A, B, and AB tests
consume — a resource trade, not a free lunch. The opposite case is the cautionary one:
a factor with a small main effect but a large interaction can't be dropped, because it
matters *conditionally* — the demo's AB = 2.50 shows B's story is incomplete without A.
The general rule: drop factors only when both their main effect and their interactions
are small; keep anything whose conditional behavior matters, and re-run the reduced
design (a 2^2 in this case) to confirm the estimates stabilize. That confirmation run
is where the sample-size formulas return."
