# Interview Questions: Experimentation

## Conceptual

1. **What is the difference between statistical significance and practical significance?**
   - Statistical: p < α (the effect is likely real, not due to chance)
   - Practical: the effect size is large enough to matter for business decisions
   - A tiny effect can be statistically significant with enough sample size

2. **Explain Simpson's paradox in the context of A/B testing.**
   - When the aggregate result shows one direction but all subgroups show the opposite. E.g., overall conversion is higher for treatment, but within every device type (mobile, desktop, tablet), control is higher. This happens when the treatment changes the mix of users across device types.

3. **You run an A/B test and get p = 0.04. What do you do?**
   - Check multiple testing correction (is this the primary metric?)
   - Check for peeking (was the test stopped early?)
   - Check practical significance (is the effect big enough to matter?)
   - Check for sample ratio mismatch (are the groups balanced?)
   - If all checks pass, consider it a borderline result and run a follow-up test

## Coding

4. **Implement a function that computes the p-value of a two-sample t-test in Java.**

```java
public static double twoSampleTTest(double[] group1, double[] group2) {
    int n1 = group1.length, n2 = group2.length;
    double m1 = mean(group1), m2 = mean(group2);
    double v1 = variance(group1, m1), v2 = variance(group2, m2);
    double t = (m1 - m2) / Math.sqrt(v1/n1 + v2/n2);
    double df = welchSatterthwaite(v1, n1, v2, n2);  // approximate degrees of freedom
    return 2 * (1 - tCDF(Math.abs(t), df));  // two-sided
}
```

5. **How would you detect that a significant A/B test result is actually a false positive?**
   - Re-run the experiment (replication)
   - Check for peeking (sequential testing adjustment)
   - Check for sample ratio mismatch
   - Check the novelty effect (run longer to see if effect persists)
   - Check for instrumentation errors (is the tracking correct?)
   - Cross-check with a different metric that should also move

6. **Design an experimentation platform for a company with 100M users and 200 concurrent experiments.**
   - Assignment: hash-based, O(1), 3-layer architecture
   - Metrics: real-time streaming pipeline (Kafka + Flink)
   - Analysis: CUPED + sequential testing + FDR correction
   - Dashboard: real-time per-experiment view with auto-stop rules
   - Registry: all experiments logged with decisions
