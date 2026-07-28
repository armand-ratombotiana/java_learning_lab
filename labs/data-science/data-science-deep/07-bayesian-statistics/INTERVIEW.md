# Bayesian Statistics — Interview Questions

### Q1: Prior Choice
**Q**: How do you choose a prior when there's no strong domain knowledge?

**A**: Use weakly informative priors (e.g., Beta(1,1) for proportions, Normal(0,10) for means, Half-Cauchy(0,5) for variances). These constrain parameters to reasonable ranges without strong influence on the posterior. Avoid flat improper priors in complex models (can lead to improper posteriors). Sensitivity analysis: vary the prior and check if conclusions change materially.

### Q2: Frequentist vs Bayesian
**Q**: A/B test: 1000 users per variant, conversion 5% vs 6%. Compare Bayesian vs frequentist analysis.

**A**: Frequentist: p ≈ 0.33, fail to reject H0. 95% CI for lift: [-0.8%, 2.8%]. Conclusion: inconclusive. Bayesian: Beta(1,1) prior → posterior Beta(51, 951) vs Beta(61, 941). P(pB > pA) ≈ 0.82, 95% credible interval for lift: [-0.6%, 2.6%]. Similar conclusions but Bayesian gives a direct probability: "82% chance treatment is better." The Bayesian interpretation is more actionable for business decisions.

### Q3: MCMC Diagnostics
**Q**: How do you check if your MCMC has converged?

**A**: Multiple diagnostics: (1) Trace plot — should look like "hairy caterpillars" with no trends. (2) Gelman-Rubin R̂ < 1.1 (run multiple chains). (3) Effective sample size (ESS) > 100 per parameter. (4) Autocorrelation — low autocorrelation means efficient sampling. (5) Geweke test — compare means from early vs late parts of chain. ESS = N / (1 + 2Σρ_k) where ρ_k are autocorrelations.

### Q4: Prior Predictive Checks
**Q**: What are prior predictive checks and why are they useful?

**A**: Simulate data from the prior distribution before seeing actual data. This checks whether your prior generates plausible data. If simulated datasets are wildly unrealistic (e.g., negative conversion rates), your prior is too strong or misspecified. It's a sanity check that helps you calibrate priors and detect modeling errors before fitting.

### Q5: Hierarchical Modeling
**Q**: When would you use a hierarchical (multi-level) Bayesian model?

**A**: When data has grouped structure (users in countries, patients in hospitals, items in categories). Hierarchical models share statistical strength across groups via a common prior distribution. Groups with few observations borrow information from groups with many observations (partial pooling). This provides more stable estimates for small groups and often outperforms both complete pooling (all groups same) and no pooling (each group independent).

## Coding

### Q6: Gibbs sampler for Normal model
```java
public record GibbsResult(double[][] samples, int iterations, int chain) {}

public GibbsResult gibbsNormal(double[] data, double mu0, double tau0Sq, double alpha, double beta, int iterations) {
    int n = data.length;
    double[] mu = new double[iterations];
    double[] sigma2 = new double[iterations];
    mu[0] = mean(data);
    sigma2[0] = var(data);
    
    for (int t = 1; t < iterations; t++) {
        // Sample mu | sigma2, data
        double postVar = 1.0 / (1.0 / tau0Sq + n / sigma2[t-1]);
        double postMean = postVar * (mu0 / tau0Sq + n * mean(data) / sigma2[t-1]);
        mu[t] = postMean + Math.sqrt(postVar) * rng.nextGaussian();
        
        // Sample sigma2 | mu, data
        double ss = 0;
        for (double v : data) ss += Math.pow(v - mu[t], 2);
        sigma2[t] = 1.0 / gammaSample(rng, alpha + n/2, beta + ss/2);
    }
    return new GibbsResult(new double[][]{mu, sigma2}, iterations, 1);
}
```
