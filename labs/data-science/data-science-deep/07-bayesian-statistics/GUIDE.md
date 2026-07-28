# Bayesian Statistics Guide

## 1. Conjugate Models

### Beta-Binomial (conversion rate estimation)

```java
public record BetaBinomial(double alpha, double beta) {
    public BetaBinomial posterior(int successes, int failures) {
        return new BetaBinomial(alpha + successes, beta + failures);
    }
    
    public double posteriorMean() {
        return alpha / (alpha + beta);
    }
    
    public double[] credibleInterval(double credibility) {
        double lo = betaQuantile((1.0 - credibility) / 2.0, alpha, beta);
        double hi = betaQuantile(1.0 - (1.0 - credibility) / 2.0, alpha, beta);
        return new double[]{lo, hi};
    }
}
```

### Normal-Normal (mean estimation)

```java
public record NormalNormal(double priorMean, double priorVar) {
    public PosteriorNormal posterior(double[] data, double likelihoodVar) {
        int n = data.length;
        double sampleMean = Arrays.stream(data).average().orElseThrow();
        double postVar = 1.0 / (1.0 / priorVar + n / likelihoodVar);
        double postMean = postVar * (priorMean / priorVar + n * sampleMean / likelihoodVar);
        return new PosteriorNormal(postMean, postVar);
    }
    
    public record PosteriorNormal(double mean, double variance) {}
}
```

## 2. MCMC: Metropolis-Hastings

```java
public class MetropolisHastings {
    private final DoubleUnaryOperator logTarget;
    private final Random rng;
    private final double stepSize;
    
    public MetropolisHastings(DoubleUnaryOperator logTarget, double stepSize) {
        this.logTarget = logTarget;
        this.stepSize = stepSize;
        this.rng = new Random(42L);
    }
    
    public List<Double> sample(int iterations, double initial, int burnIn, int thinning) {
        List<Double> samples = new ArrayList<>();
        double current = initial;
        double logCurrent = logTarget.applyAsDouble(current);
        int accepted = 0;
        
        for (int i = 0; i < iterations; i++) {
            double proposal = current + stepSize * rng.nextGaussian();
            double logProposal = logTarget.applyAsDouble(proposal);
            double logR = logProposal - logCurrent;
            
            if (Math.log(rng.nextDouble()) < logR) {
                current = proposal;
                logCurrent = logProposal;
                accepted++;
            }
            
            if (i >= burnIn && (i - burnIn) % thinning == 0) {
                samples.add(current);
            }
        }
        return samples;
    }
}
```

## 3. Credible Intervals

```java
public record CredibleInterval(double lower, double upper, double probability) {
    // Equal-tailed interval (ETI)
    public static CredibleInterval equalTailed(double[] samples, double prob) {
        double[] sorted = Arrays.copyOf(samples, samples.length);
        Arrays.sort(sorted);
        int n = samples.length;
        int lowerIdx = (int) ((1.0 - prob) / 2.0 * n);
        int upperIdx = (int) ((1.0 + prob) / 2.0 * n);
        return new CredibleInterval(sorted[lowerIdx], sorted[upperIdx], prob);
    }
    
    // Highest Posterior Density (HPD) interval
    public static CredibleInterval hpd(double[] samples, double prob) {
        double[] sorted = Arrays.copyOf(samples, samples.length);
        Arrays.sort(sorted);
        int n = samples.length;
        int intervalSize = (int) (prob * n);
        double minWidth = Double.MAX_VALUE;
        int bestStart = 0;
        for (int i = 0; i <= n - intervalSize; i++) {
            double width = sorted[i + intervalSize - 1] - sorted[i];
            if (width < minWidth) {
                minWidth = width;
                bestStart = i;
            }
        }
        return new CredibleInterval(sorted[bestStart], sorted[bestStart + intervalSize - 1], prob);
    }
}
```

## 4. Bayes Factors

```java
public record BayesFactor(double bf10) {
    // BF10 > 3: moderate evidence for H1
    // BF10 > 10: strong evidence for H1
    // BF10 > 100: decisive evidence for H1
    
    public static BayesFactor fromPosteriorOdds(double posteriorOdds, double priorOdds) {
        return new BayesFactor(posteriorOdds / priorOdds);
    }
    
    public static BayesFactor savageDickey(double priorDensity, double posteriorDensity) {
        return new BayesFactor(posteriorDensity / priorDensity);
    }
}
```

## 5. WAIC (Watanabe-Akaike Information Criterion)

```java
public record WAIC(double waic) {
    public static WAIC compute(double[][] logLikelihoods) {
        // logLikelihoods[s][i] = log p(y_i | θ_s)
        int n = logLikelihoods[0].length;
        int S = logLikelihoods.length;
        
        double lppd = 0; // log pointwise predictive density
        double pWAIC = 0; // effective number of parameters
        
        for (int i = 0; i < n; i++) {
            double sum = 0;
            double var = 0;
            for (int s = 0; s < S; s++) {
                double ll = logLikelihoods[s][i];
                sum += Math.exp(ll);
                var += ll * ll;
            }
            lppd += Math.log(sum / S);
            double meanLl = sum;
            pWAIC += var / S - meanLl * meanLl;
        }
        
        double waic = -2.0 * (lppd - pWAIC);
        return new WAIC(waic);
    }
}
```
