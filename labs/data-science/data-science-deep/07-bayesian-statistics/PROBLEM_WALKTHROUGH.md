# Problem Walkthrough: Bayesian A/B Test

## Problem
Implement a Bayesian A/B test comparing two conversion rates using Beta-Binomial conjugate model, with MCMC for a more complex hierarchical model.

## Step 1: Beta-Binomial Conjugate

```java
public class BetaBinomialABTest {
    private final double alphaPrior, betaPrior;
    
    public BetaBinomialABTest(double alphaPrior, double betaPrior) {
        this.alphaPrior = alphaPrior;
        this.betaPrior = betaPrior;
    }
    
    public ABPosterior analyze(int successesA, int totalA, int successesB, int totalB) {
        double alphaA = alphaPrior + successesA;
        double betaA = betaPrior + totalA - successesA;
        double alphaB = alphaPrior + successesB;
        double betaB = betaPrior + totalB - successesB;
        
        // P(pB > pA) via Monte Carlo
        int nSamples = 100000;
        int count = 0;
        for (int i = 0; i < nSamples; i++) {
            double pA = betaSample(rng, alphaA, betaA);
            double pB = betaSample(rng, alphaB, betaB);
            if (pB > pA) count++;
        }
        double probBetter = (double) count / nSamples;
        
        // Expected loss
        double expectedLoss = 0;
        for (int i = 0; i < nSamples; i++) {
            double pA = betaSample(rng, alphaA, betaA);
            double pB = betaSample(rng, alphaB, betaB);
            if (pA > pB) expectedLoss += pA - pB;
        }
        expectedLoss /= nSamples;
        
        return new ABPosterior(probBetter, expectedLoss, alphaA, betaA, alphaB, betaB);
    }
}
```

## Step 2: MCMC for Hierarchical Model

```java
public class HierarchicalBetaBinomial {
    // Multiple groups: each has its own rate, rates drawn from Beta(μ, κ)
    // μ ~ Beta(1,1), κ ~ Gamma(2, 0.1)
    
    public record HierarchicalResult(double[] groupRates, double mu, double kappa) {}
    
    public HierarchicalResult mcmc(int[][] successes, int[][] totals, int iterations) {
        int groups = successes.length;
        double[] rates = new double[groups];
        for (int j = 0; j < groups; j++) 
            rates[j] = (double) successes[j][0] / totals[j][0];
        double mu = mean(rates);
        double kappa = 5.0;
        
        for (int iter = 0; iter < iterations; iter++) {
            // Sample each rate | mu, kappa, data
            for (int j = 0; j < groups; j++) {
                int s = successes[j][0], t = totals[j][0];
                double alpha = mu * kappa + s;
                double beta = (1 - mu) * kappa + t - s;
                rates[j] = betaSample(rng, alpha, beta);
            }
            
            // Sample mu | rates, kappa
            double alphaMu = 1 + kappa * mean(rates);
            double betaMu = 1 + kappa * (1 - mean(rates));
            mu = betaSample(rng, alphaMu, betaMu);
            
            // Sample kappa | rates, mu (MH step)
            double proposal = kappa + rng.nextGaussian() * 0.5;
            if (proposal > 0) {
                double logRatio = logGamma(proposal) - logGamma(kappa) 
                    + groups * (logGamma(mu * proposal) + logGamma((1-mu) * proposal) 
                    - logGamma(mu * kappa) - logGamma((1-mu) * kappa))
                    + (proposal - kappa) * sum(log(rates))
                    + (proposal - kappa) * sum(log(1 - rates))
                    + Math.log(dGammaPdf(proposal, 2, 0.1) / dGammaPdf(kappa, 2, 0.1))
                    + 0.5 * Math.log(kappa / proposal); // Hastings correction
                if (Math.log(rng.nextDouble()) < logRatio) kappa = proposal;
            }
        }
        return new HierarchicalResult(rates, mu, kappa);
    }
}
```

## Step 3: Decision Rules

```java
public class BayesianDecision {
    public enum Action { LAUNCH, NOT_LAUNCH, CONTINUE }
    
    public Action recommend(ABPosterior post, double minEffect, double lossThreshold) {
        double probWorth = post.probLiftGreaterThan(minEffect);
        if (probWorth > 0.95) return Action.LAUNCH;
        if (post.expectedLoss() > lossThreshold) return Action.CONTINUE;
        return Action.NOT_LAUNCH;
    }
}
```

## Step 4: Posterior Predictive Check

```java
public class PosteriorPredictive {
    public double pValue(double[] observed, double[] posteriorParams) {
        // Compute discrepancy measure: chi-square statistic
        double observedDisc = computeDiscrepancy(observed);
        int extreme = 0;
        for (int s = 0; s < 10000; s++) {
            double[] simulated = generateReplicate(observed.length, posteriorParams);
            double simDisc = computeDiscrepancy(simulated);
            if (simDisc >= observedDisc) extreme++;
        }
        return (double) extreme / 10000; // Bayesian p-value near 0.5 indicates good fit
    }
}
```

## Step 5: Verification

```java
@Test
public void testBetaBinomial() {
    BetaBinomial prior = new BetaBinomial(1, 1);
    BetaBinomial posterior = prior.posterior(50, 950); // 5% conversion
    assertEquals(0.05, posterior.posteriorMean(), 0.001);
    double[] ci = posterior.credibleInterval(0.95);
    assertTrue(ci[0] < 0.05 && ci[1] > 0.05);
}

@Test
public void testABPosterior() {
    BetaBinomialABTest test = new BetaBinomialABTest(1, 1);
    ABPosterior post = test.analyze(50, 1000, 60, 1000);
    assertTrue(post.probBetter() > 0.8);
    assertTrue(post.expectedLoss() < 0.01);
}
```
