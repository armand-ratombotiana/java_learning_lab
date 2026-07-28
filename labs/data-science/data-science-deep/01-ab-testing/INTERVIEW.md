# A/B Testing — Interview Questions

## Statistics

### Q1: Sample Size Calculation
**Q**: You're asked to design an A/B test for a new recommendation algorithm. Current conversion is 5%. The team wants to detect a 10% relative lift with 80% power at α=0.05. How many users per variant?

**A**: Using the normal approximation for proportions:
```
n = 2 * p_pooled * (1-p_pooled) * (z_α/2 + z_β)² / δ²
p_pooled = 0.0525, δ = 0.005
z_0.025 = 1.96, z_0.20 = 0.84
n ≈ 2 * 0.0525 * 0.9475 * (2.80)² / (0.005)² ≈ 156,000 per variant
```

### Q2: Peeking Problem
**Q**: An engineer checks the test every day and stops once p < 0.05. How does this affect the false positive rate?

**A**: The effective false positive rate balloons to ~25% (or higher) because each peek is a hypothesis test. Use sequential testing (always-valid p-values) or fix a single stopping time a priori.

### Q3: Simpson's Paradox
**Q**: Overall conversion is flat, but every subpopulation (mobile/web, country segments) shows positive lift. Explain.

**A**: This is Simpson's paradox — the treatment changes the traffic mix. If treatment increases traffic from a low-conversion segment, the weighted average can appear flat or negative even with positive per-segment effects. Control for segment proportions via stratification or regression adjustment.

### Q4: Metric Selection
**Q**: Your team proposes 20 success metrics for a single A/B test. What issues arise?

**A**: Multiple testing inflation — at α=0.05 with 20 independent metrics, P(at least one false positive) = 1 - 0.95²⁰ ≈ 64%. Apply Bonferroni (α/m) or Benjamini-Hochberg (FDR control). Pre-register primary and secondary metrics.

### Q5: Variance Reduction
**Q**: How would you reduce the variance of your metric without increasing sample size?

**A**: CUPED (using pre-experiment covariate), stratified sampling, post-stratification, or control variates. CUPED typically reduces variance by 20-50% when pre-experiment data correlates with the metric (ρ > 0.3).

## System Design

### Q6: Design an Experimentation Platform
**Q**: Design an internal A/B testing platform serving 100+ experiments simultaneously.

**A**: Key components: randomization service (consistent hash bucket assignment), metric pipeline (hourly/daily aggregation), statistical engine (sequential testing, CUPED, multiple testing correction), experiment configuration (variants, start/end dates, targeting), dashboard (real-time and finalized results). Architecture: microservices with Kafka for event collection, Spark for aggregation, specialized stat engine avoiding shared experiment interference.

### Q7: Network Effects / Interference
**Q**: A social network wants to test a new feed algorithm. How do you handle interference between users (treatment users influence control users)?

**A**: Switch to cluster-randomized experiments (randomize at the social cluster level). Ego-network randomization. Difference-in-differences with time-series. For marketplace: switchback experiments (time-based randomization).

## Coding

### Q8: Implement p-value computation
```java
// Compute two-sided p-value from z-statistic using standard normal CDF
public double pValue(double z) {
    return 2.0 * (1.0 - normalCDF(Math.abs(z)));
}

private double normalCDF(double x) {
    // Abramowitz and Stegun approximation
    double t = 1.0 / (1.0 + 0.2316419 * Math.abs(x));
    double d = 0.3989422804014327; // 1/sqrt(2*pi)
    double p = d * Math.exp(-x * x / 2.0) *
        (t * (0.319381530 + t * (-0.356563782 + t * (1.781477937 +
         t * (-1.821255978 + t * 1.330274429)))));
    return x > 0 ? 1.0 - p : p;
}
```
