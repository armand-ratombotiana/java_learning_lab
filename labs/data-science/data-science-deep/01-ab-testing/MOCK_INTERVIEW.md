# Mock Interview: A/B Testing

**Interviewer**: Let's start with a scenario. Our product team wants to test a new checkout flow that they believe will increase purchase conversion from 8% to 9%. How many users do we need?

**Candidate**: We need to specify our significance level and power. Assuming α=0.05 and 80% power, and using a two-sided test:

n = 2 * (0.085 * 0.915) * (1.96 + 0.84)² / (0.01)² ≈ 62,350 per variant.

**Interviewer**: The team insists they need to check results weekly to avoid running a "bad" experiment too long. What do you advise?

**Candidate**: This creates the peeking problem. Each weekly check inflates the Type I error rate. With 4 peeks, the effective α rises from 5% to about 14%. I'd recommend a sequential testing approach using always-valid p-values or an alpha spending function (e.g., Pocock or O'Brien-Fleming boundaries).

**Interviewer**: Let's say after running the test, overall conversion is flat. But when we segment by new vs returning users, both segments show a 1% lift. What happened?

**Candidate**: Classic Simpson's paradox. The treatment likely shifted traffic composition — maybe the new flow disproportionately attracted returning users (who have higher baseline conversion). The overall metric is a weighted average, and if the treatment group has a larger share of the lower-converting segment, the overall effect can cancel out. Stratify the analysis and report per-segment results with proper weighting.

**Interviewer**: How would you implement the statistical engine in production?

**Candidate**: I'd build it as a stateless microservice receiving experiment summaries and returning test results. Key components: a metric registry defining how to aggregate (mean, proportion, quantile), a variance reduction module applying CUPED with pre-experiment covariates, a sequential testing module for continuous monitoring, and a multiple testing correction module. Results are cached until the experiment reaches the pre-registered sample size.

**Interviewer**: What about guardrail metrics — metrics that shouldn't regress?

**Candidate**: Guardrail metrics (e.g., page load time, error rate) are tested simultaneously. We use non-inferiority tests with a pre-specified boundary. For example, we test H₀: δ ≤ -margin vs H₁: δ > -margin. We apply Holm-Bonferroni correction across guardrails to control FWER while maintaining more power than standard Bonferroni.

**Interviewer**: Let's code. Implement `requiredSampleSize` in Java.

**Candidate**:
```java
public static int requiredSampleSize(double baseline, double mde, double alpha, double power) {
    double zAlpha = normalQuantile(1.0 - alpha / 2.0);
    double zBeta = normalQuantile(power);
    double pAvg = baseline + mde / 2.0;
    return (int) Math.ceil(
        2.0 * pAvg * (1.0 - pAvg) * Math.pow(zAlpha + zBeta, 2) / (mde * mde)
    );
}
```

**Interviewer**: What's one subtlety of this formula?

**Candidate**: It assumes equal group sizes and uses the pooled variance under the alternative. For unequal allocation (e.g., 90-10 treatment-control split), the variance term changes: instead of 2/p(1-p), it becomes (1/r + 1/(1-r))/p(1-p) where r is the treatment fraction. Unequal allocation increases total sample needed but can be useful when treatment is expensive or risky.
