# Mock Interview: Statistical Power

**Interviewer**: The product team wants to run an A/B test with 1000 users per variant to detect a 1% conversion lift from a baseline of 5%. Is this enough?

**Candidate**: Let me compute. Baseline p1=0.05, MDE=0.01 (p2=0.06). Using Cohen's h = 2*arcsin(sqrt(0.06)) - 2*arcsin(sqrt(0.05)) ≈ 0.044. For α=0.05 and power=0.80:
n = 2 * (1.96 + 0.84)² / (0.044)² ≈ 8100 per variant. The proposed 1000 users gives power around 18% — very underpowered. Even with 5000 users, power is only about 60%.

**Interviewer**: What if they accept a 2% absolute lift detection instead of 1%?

**Candidate**: With MDE=0.02, h≈0.089, n ≈ 1970 per variant. Still under 1000, but closer. For 1000 users, the detectable effect at 80% power is roughly h = (1.96+0.84)/sqrt(1000/2) ≈ 0.125, which corresponds to about a 2.8% absolute lift. They need to set more realistic expectations or increase sample size.

**Interviewer**: The team can't get more traffic. What alternatives do we have?

**Candidate**: (1) CUPED — use pre-experiment covariates to reduce variance by 20-50%, effectively increasing power. (2) Switch to a continuous metric instead of binary (e.g., revenue per user instead of conversion) — continuous outcomes often have higher power. (3) Use a sequential test with alpha spending — can stop early if effect is large, potentially saving sample. (4) Consider a Bayesian approach with an informative prior from historical data. (5) Accept that this effect size requires more data and use the results as a directional signal, not a confirmatory test.

**Interviewer**: Let's code. Write a function to compute power for a two-sample t-test given d, n, and α.

**Candidate**:
```java
public double powerTTest(double d, int n, double alpha) {
    double df = 2.0 * n - 2.0;
    double ncp = d * Math.sqrt(n / 2.0);
    double tCrit = studentTQuantile(1.0 - alpha / 2.0, df);
    return 1.0 - nonCentralTCdf(tCrit, df, ncp) + nonCentralTCdf(-tCrit, df, ncp);
}
```

**Interviewer**: What is the non-centrality parameter and why does it matter?

**Candidate**: The ncp determines the shift of the test statistic distribution under the alternative hypothesis. For a two-sample t-test, ncp = d * √(n/2). A larger |ncp| means more separation between the null and alternative distributions, hence higher power. It directly connects effect size and sample size: power increases with both d and n because they both increase the ncp.
