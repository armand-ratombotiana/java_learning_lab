# Mock Interview: Bayesian Statistics

**Interviewer**: Why use Bayesian methods for A/B testing instead of frequentist?

**Candidate**: Three reasons: (1) Direct probability interpretation — "95% chance B > A" vs frequentist's "reject H0 at α=0.05". (2) Can incorporate prior information from previous experiments. (3) Sequential analysis with no peeking penalty — you can monitor and stop at any time without inflating error rates. The downside is sensitivity to prior specification, but with weakly informative priors (Beta(1,1)) results typically align with frequentist methods for large samples.

**Interviewer**: How do you choose between equal-tailed and HPD credible intervals?

**Candidate**: Equal-tailed intervals (2.5% and 97.5% quantiles) are simpler and invariant to monotonic transformations. HPD intervals are narrower for multimodal distributions but not transformation-invariant. For unimodal symmetric posteriors (common with large samples), they're nearly identical. I use equal-tailed by default, HPD when the posterior is skewed and I need the shortest interval.

**Interviewer**: Walk me through implementing MCMC for a logistic regression.

**Candidate**: Target distribution is the posterior: P(β | y, X) ∝ P(y | X, β) * P(β). Log-posterior = Σ[y_i * log(σ(X_iβ)) + (1-y_i) * log(1-σ(X_iβ))] - 0.5 * β'β / σ²_prior + constant. I'd use Metropolis-Hastings with a multivariate normal proposal centered at the current β with covariance estimated from the Hessian at the mode (or use NUTS/HMC for efficiency). I'd run 4 chains, check R̂ < 1.1, ensure ESS > 400, and inspect trace plots.

**Interviewer**: What is the "prior-likelihood conflict" and how do you detect it?

**Candidate**: When the prior assigns very low density to the region supported by the data. This appears as: (1) the posterior is far from both prior and MLE (compression artifact), (2) effective sample size drops, (3) MCMC mixing is poor. Detect via prior-posterior overlap checks or by comparing the posterior to the likelihood alone (using a flat prior alternative). Solutions: relax the prior, or check for data coding errors.

**Interviewer**: Let's code. Implement the Metropolis-Hastings sampler for a simple normal mean problem.

**Candidate**:
```java
public List<Double> mhNormalMean(double[] data, double mu0, double sigma0, double step, int iter, int burn) {
    List<Double> samples = new ArrayList<>();
    double current = mean(data);
    double logPrior = (c) -> -0.5 * Math.pow((c - mu0) / sigma0, 2);
    double logLikelihood = (mu) -> {
        double s = 0;
        for (double v : data) s += Math.pow(v - mu, 2);
        return -data.length / 2.0 * Math.log(2 * Math.PI) - s / 2.0;
    };
    double logCurrent = logPrior.apply(current) + logLikelihood.apply(current);
    
    for (int i = 0; i < iter; i++) {
        double proposal = current + step * rng.nextGaussian();
        double logProp = logPrior.apply(proposal) + logLikelihood.apply(proposal);
        if (Math.log(rng.nextDouble()) < logProp - logCurrent) {
            current = proposal;
            logCurrent = logProp;
        }
        if (i >= burn) samples.add(current);
    }
    return samples;
}
```
