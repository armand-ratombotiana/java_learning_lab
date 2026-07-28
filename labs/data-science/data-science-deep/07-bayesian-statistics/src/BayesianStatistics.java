package com.datascience.deep.lab07;

import java.util.*;
import java.util.function.DoubleUnaryOperator;

public final class BayesianStatistics {

    // -- Beta-Binomial --

    public record BetaBinomial(double alpha, double beta) {
        public BetaBinomial posterior(int successes, int failures) {
            return new BetaBinomial(alpha + successes, beta + failures);
        }

        public double posteriorMean() { return alpha / (alpha + beta); }

        public double[] credibleInterval(double credibility) {
            double lo = betaQuantile((1.0 - credibility) / 2.0, alpha, beta);
            double hi = betaQuantile(1.0 - (1.0 - credibility) / 2.0, alpha, beta);
            return new double[]{lo, hi};
        }

        public double probGreaterThan(double threshold) {
            return 1.0 - incompleteBeta(alpha, beta, threshold);
        }
    }

    // -- Normal-Normal --

    public record NormalPrior(double mean, double var) {
        public NormalPosterior posterior(double[] data, double likelihoodVar) {
            int n = data.length;
            double sampleMean = Arrays.stream(data).average().orElseThrow();
            double postVar = 1.0 / (1.0 / var + n / likelihoodVar);
            double postMean = postVar * (mean / var + n * sampleMean / likelihoodVar);
            return new NormalPosterior(postMean, postVar);
        }
    }

    public record NormalPosterior(double mean, double var) {}

    // -- Metropolis-Hastings --

    public static class MetropolisHastings {
        private final DoubleUnaryOperator logTarget;
        private final Random rng;
        private final double stepSize;

        public MetropolisHastings(DoubleUnaryOperator logTarget, double stepSize) {
            this.logTarget = logTarget;
            this.stepSize = stepSize;
            this.rng = new Random(42L);
        }

        public List<Double> sample(int iterations, double initial, int burnIn) {
            List<Double> samples = new ArrayList<>();
            double current = initial;
            double logCurrent = logTarget.applyAsDouble(current);

            for (int i = 0; i < iterations; i++) {
                double proposal = current + stepSize * rng.nextGaussian();
                double logProposal = logTarget.applyAsDouble(proposal);
                if (Math.log(rng.nextDouble()) < logProposal - logCurrent) {
                    current = proposal;
                    logCurrent = logProposal;
                }
                if (i >= burnIn) samples.add(current);
            }
            return samples;
        }
    }

    // -- Gibbs Sampler for Normal Model --

    public record GibbsNormalResult(double[] mu, double[] sigma2) {}

    public static GibbsNormalResult gibbsNormal(double[] data, double mu0, double tau0Sq,
                                                double alpha, double beta, int iterations) {
        Random rng = new Random(42L);
        int n = data.length;
        double[] mu = new double[iterations];
        double[] sigma2 = new double[iterations];
        mu[0] = Arrays.stream(data).average().orElseThrow();
        double dataVar = Arrays.stream(data).map(v -> Math.pow(v - mu[0], 2)).sum() / (n - 1);
        sigma2[0] = dataVar > 0 ? dataVar : 1.0;
        double sumData = Arrays.stream(data).sum();

        for (int t = 1; t < iterations; t++) {
            double postVar = 1.0 / (1.0 / tau0Sq + n / sigma2[t - 1]);
            double postMean = postVar * (mu0 / tau0Sq + sumData / sigma2[t - 1]);
            mu[t] = postMean + Math.sqrt(postVar) * rng.nextGaussian();

            double ss = 0;
            for (double v : data) ss += Math.pow(v - mu[t], 2);
            double shape = alpha + n / 2.0;
            double rate = beta + ss / 2.0;
            sigma2[t] = 1.0 / gammaSample(rng, shape, rate);
        }
        return new GibbsNormalResult(mu, sigma2);
    }

    // -- Bayesian A/B Test --

    public record ABPosterior(double probBetter, double expectedLoss,
                              double alphaA, double betaA, double alphaB, double betaB) {
        public double probLiftGreaterThan(double threshold) {
            int count = 0, n = 50000;
            Random rng = new Random();
            for (int i = 0; i < n; i++) {
                double pA = betaSample(rng, alphaA, betaA);
                double pB = betaSample(rng, alphaB, betaB);
                if ((pB - pA) / pA > threshold) count++;
            }
            return (double) count / n;
        }
    }

    public static ABPosterior bayesianABTest(int succA, int totalA, int succB, int totalB) {
        double alphaPrior = 1, betaPrior = 1;
        double alphaA = alphaPrior + succA, betaA = betaPrior + totalA - succA;
        double alphaB = alphaPrior + succB, betaB = betaPrior + totalB - succB;

        Random rng = new Random(42L);
        int nSamples = 100000;
        int countBetter = 0;
        double sumLoss = 0;
        for (int i = 0; i < nSamples; i++) {
            double pA = betaSample(rng, alphaA, betaA);
            double pB = betaSample(rng, alphaB, betaB);
            if (pB > pA) countBetter++;
            if (pA > pB) sumLoss += pA - pB;
        }
        return new ABPosterior((double) countBetter / nSamples, sumLoss / nSamples,
                               alphaA, betaA, alphaB, betaB);
    }

    // -- Credible Intervals --

    public record CredibleInterval(double lower, double upper, double probability) {
        public static CredibleInterval equalTailed(double[] samples, double prob) {
            double[] sorted = Arrays.copyOf(samples, samples.length);
            Arrays.sort(sorted);
            int n = samples.length;
            int lowerIdx = (int) ((1.0 - prob) / 2.0 * n);
            int upperIdx = (int) ((1.0 + prob) / 2.0 * n);
            return new CredibleInterval(sorted[lowerIdx], sorted[upperIdx], prob);
        }

        public static CredibleInterval hpd(double[] samples, double prob) {
            double[] sorted = Arrays.copyOf(samples, samples.length);
            Arrays.sort(sorted);
            int n = samples.length;
            int intervalSize = (int) (prob * n);
            double minWidth = Double.MAX_VALUE;
            int bestStart = 0;
            for (int i = 0; i <= n - intervalSize; i++) {
                double width = sorted[i + intervalSize - 1] - sorted[i];
                if (width < minWidth) { minWidth = width; bestStart = i; }
            }
            return new CredibleInterval(sorted[bestStart], sorted[bestStart + intervalSize - 1], prob);
        }
    }

    // -- Utility: Random samplers and distributions --

    private static double betaSample(Random rng, double alpha, double beta) {
        double x = gammaSample(rng, alpha, 1.0);
        double y = gammaSample(rng, beta, 1.0);
        return x / (x + y);
    }

    private static double gammaSample(Random rng, double shape, double rate) {
        if (shape < 1) {
            double u = rng.nextDouble();
            return gammaSample(rng, shape + 1, rate) * Math.pow(u, 1.0 / shape);
        }
        double d = shape - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);
        while (true) {
            double x = rng.nextGaussian();
            double v = 1.0 + c * x;
            if (v <= 0) continue;
            v = v * v * v;
            double u = rng.nextDouble();
            if (u < 1.0 - 0.0331 * (x * x) * (x * x)) return d * v / rate;
            if (Math.log(u) < 0.5 * x * x + d * (1.0 - v + Math.log(v))) return d * v / rate;
        }
    }

    private static double betaQuantile(double p, double a, double b) {
        double lo = 0, hi = 1;
        for (int iter = 0; iter < 50; iter++) {
            double mid = (lo + hi) / 2;
            double cdf = incompleteBeta(a, b, mid);
            if (cdf < p) lo = mid;
            else hi = mid;
        }
        return (lo + hi) / 2;
    }

    private static double incompleteBeta(double a, double b, double x) {
        if (x < 0 || x > 1) return 0;
        if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1) / (a + b + 2)) return bt * betaCF(a, b, x) / a;
        return 1.0 - bt * betaCF(b, a, 1.0 - x) / b;
    }

    private static double betaCF(double a, double b, double x) {
        int maxIter = 200; double eps = 3e-12;
        double qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double c = 1.0, d = 1.0 - qab * x / qap;
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1.0 / d; double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d; if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c; if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d; h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d; if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1.0 + aa / c; if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1.0 / d; double del = d * c; h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return h;
    }

    private static double logGamma(double x) {
        double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                        -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x, tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) ser += cof[j] / ++y;
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }
}
