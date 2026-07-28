# Mock Interview: Hypothesis Testing

**Interviewer**: You have conversion rates for two website designs (A and B), each with 10,000 users. How do you test if B is better?

**Candidate**: Since conversion is binary (purchased yes/no), I'd use a two-proportion z-test. H₀: pB - pA = 0. I'd check assumptions: samples are independent (random assignment), each user appears once, n*p > 5 and n*(1-p) > 5 for each group (satisfied with n=10k). If I also have revenue data (continuous), I'd use a Welch t-test for that metric.

**Interviewer**: The p-value is 0.06. How do you interpret this?

**Candidate**: At α = 0.05, we fail to reject the null. This doesn't mean the designs are equivalent — it means we don't have enough evidence to conclude a difference. I'd look at the effect size: what's the estimated lift and its 95% confidence interval? If the CI is [-0.1%, 2.1%], the data is consistent with both a small negative effect and a modest positive effect. The decision to launch should consider the expected value: if the CI includes the minimum detectable effect of practical significance, more data is needed.

**Interviewer**: Your product manager says "p = 0.06 means 94% chance B is better." Is that correct?

**Candidate**: No. p = 0.06 means: if the null hypothesis were true, we'd see a difference this large or larger in 6% of experiments. It's P(data | H₀), not P(H₀ | data). The misinterpretation is extremely common. I'd calculate a Bayesian posterior to give the PM the probability they want: P(pB > pA | data) using a Beta(1,1) prior. With n=10k, if pA=5% and pB=5.3%, the posterior P(pB > pA) might be ~92-93% — close but not the same as 1-p.

**Interviewer**: 10% of users see both designs (cookies are device-based, some users switch devices). How does this affect your analysis?

**Candidate**: This violates the independence assumption. Users with multiple exposures have correlated outcomes. I'd need to: (1) use a per-user analysis (aggregate all exposures per user with first-exposure or last-exposure mapping), or (2) model the correlation via cluster-robust standard errors (clustered by user ID), or (3) use a mixed effects model with random user intercepts. Ignoring this could inflate the Type I error rate because the effective sample size is smaller than the number of observations.

**Interviewer**: Let's code. Implement a function for a two-sample t-test.

**Candidate**:
```java
public record TTestResult(double t, double p, double df, double meanDiff, double ciLo, double ciHi) {
    public static TTestResult twoSample(double[] x, double[] y, double alpha) {
        int n1 = x.length, n2 = y.length;
        double m1 = mean(x), m2 = mean(y);
        double v1 = var(x), v2 = var(y);
        double se = Math.sqrt(v1/n1 + v2/n2);
        double t = (m1 - m2) / se;
        double df = Math.pow(v1/n1 + v2/n2, 2) / 
            (Math.pow(v1/n1, 2)/(n1-1) + Math.pow(v2/n2, 2)/(n2-1));
        double p = 2 * (1 - studentTCdf(Math.abs(t), df));
        double me = studentTQuantile(1 - alpha/2, df) * se;
        return new TTestResult(t, p, df, m1 - m2, (m1 - m2) - me, (m1 - m2) + me);
    }
}
```
