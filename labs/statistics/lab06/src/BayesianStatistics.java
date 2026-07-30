package com.statistics.lab06;

/**
 * Performs Bayesian inference using conjugate priors.
 * Supports Beta-Binomial model, posterior summary statistics,
 * credible intervals, and Bayesian A/B testing via Monte Carlo.
 */
public final class BayesianStatistics {

    private BayesianStatistics() {
    }

    /**
     * Holds the parameters and mean of a Beta posterior distribution.
     */
    public record BetaPosterior(double alpha, double beta, double mean) {
        @Override
        public String toString() {
            return String.format("Beta(%.1f, %.1f), mean = %.4f", alpha, beta, mean);
        }
    }

    /**
     * Computes the log of the Beta function using
     * log-Gamma approximation (Lanczos).
     */
    public static double logBeta(double a, double b) {
        return logGamma(a) + logGamma(b) - logGamma(a + b);
    }

    /**
     * Lanczos approximation for log-Gamma.
     */
    public static double logGamma(double x) {
        double[] coef = {
            76.18009172947146, -86.50532032941677,
            24.01409824083091, -1.231739572450155,
            0.1208650973866179e-2, -0.5395239384953e-5
        };
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j < 6; j++) {
            y += 1;
            ser += coef[j] / y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    /**
     * Computes the Beta PDF value at x.
     */
    public static double betaPdf(double x, double a, double b) {
        if (x < 0 || x > 1) return 0;
        return Math.exp((a - 1) * Math.log(x) + (b - 1) * Math.log(1 - x) - logBeta(a, b));
    }

    /**
     * Computes the Beta CDF using regularized incomplete beta function (continued fraction).
     */
    public static double betaCdf(double x, double a, double b) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return regularizedIncompleteBeta(x, a, b);
    }

    /**
     * Regularized incomplete beta function I_x(a, b) via continued fraction.
     */
    public static double regularizedIncompleteBeta(double x, double a, double b) {
        if (x == 0 || x == 1) return x;
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b)
            + a * Math.log(x) + b * Math.log(1 - x));
        if (x < (a + 1) / (a + b + 2)) {
            return bt * continuedFractionBeta(x, a, b) / a;
        } else {
            return 1 - bt * continuedFractionBeta(1 - x, b, a) / b;
        }
    }

    private static double continuedFractionBeta(double x, double a, double b) {
        int maxIter = 200;
        double eps = 3e-12;
        double qab = a + b;
        double qap = a + 1;
        double qam = a - 1;
        double c = 1.0;
        double d = 1.0 - qab * x / qap;
        if (Math.abs(d) < eps) d = eps;
        d = 1.0 / d;
        double h = d;
        for (int m = 1; m <= maxIter; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < eps) d = eps;
            c = 1.0 + aa / c;
            if (Math.abs(c) < eps) c = eps;
            d = 1.0 / d;
            h *= d * c;
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1.0 + aa * d;
            if (Math.abs(d) < eps) d = eps;
            c = 1.0 + aa / c;
            if (Math.abs(c) < eps) c = eps;
            d = 1.0 / d;
            double del = d * c;
            h *= del;
            if (Math.abs(del - 1.0) < eps) break;
        }
        return h;
    }

    /**
     * Returns posterior parameters for Beta-Binomial conjugate model.
     *
     * @param priorAlpha  prior Alpha parameter
     * @param priorBeta   prior Beta parameter
     * @param successes   number of successes observed
     * @param trials      total number of trials
     * @return posterior summary
     */
    public static BetaPosterior betaBinomialPosterior(int priorAlpha, int priorBeta,
                                                       int successes, int trials) {
        double postAlpha = priorAlpha + successes;
        double postBeta = priorBeta + (trials - successes);
        double mean = postAlpha / (postAlpha + postBeta);
        return new BetaPosterior(postAlpha, postBeta, mean);
    }

    /**
     * Estimates a 95% equal-tailed credible interval for a Beta posterior.
     *
     * @param postAlpha posterior alpha
     * @param postBeta  posterior beta
     * @return array [lower, upper] of the 95% credible interval
     */
    public static double[] credibleInterval95(double postAlpha, double postBeta) {
        double lo = 0.0, hi = 1.0;
        // Binary search for lower bound (2.5th percentile)
        for (int i = 0; i < 50; i++) {
            double mid = (lo + hi) / 2;
            if (betaCdf(mid, postAlpha, postBeta) < 0.025) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        double lower = (lo + hi) / 2;
        lo = 0.0;
        hi = 1.0;
        // Binary search for upper bound (97.5th percentile)
        for (int i = 0; i < 50; i++) {
            double mid = (lo + hi) / 2;
            if (betaCdf(mid, postAlpha, postBeta) < 0.975) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        double upper = (lo + hi) / 2;
        return new double[]{lower, upper};
    }

    /**
     * Performs Bayesian A/B testing via Monte Carlo.
     *
     * @param aSuccesses conversions for version A
     * @param aTrials    visitors for version A
     * @param bSuccesses conversions for version B
     * @param bTrials    visitors for version B
     * @param samples    number of Monte Carlo samples
     * @return probability that version B is better than version A
     */
    public static double abTestProbability(int aSuccesses, int aTrials,
                                            int bSuccesses, int bTrials,
                                            int samples) {
        BetaPosterior postA = betaBinomialPosterior(1, 1, aSuccesses, aTrials);
        BetaPosterior postB = betaBinomialPosterior(1, 1, bSuccesses, bTrials);
        int count = 0;
        java.util.Random rng = new java.util.Random(42);
        for (int i = 0; i < samples; i++) {
            double thetaA = sampleBeta(postA.alpha(), postA.beta(), rng);
            double thetaB = sampleBeta(postB.alpha(), postB.beta(), rng);
            if (thetaB > thetaA) count++;
        }
        return (double) count / samples;
    }

    /**
     * Generates a sample from Beta(a, b) using the transform method
     * (via Gamma variates).
     */
    public static double sampleBeta(double a, double b, java.util.Random rng) {
        double ga = sampleGamma(a, rng);
        double gb = sampleGamma(b, rng);
        return ga / (ga + gb);
    }

    /**
     * Generates a sample from Gamma(shape, scale=1) using Marsaglia-Tsang.
     */
    public static double sampleGamma(double shape, java.util.Random rng) {
        if (shape < 1) {
            double u = rng.nextDouble();
            return sampleGamma(1.0 + shape, rng) * Math.pow(u, 1.0 / shape);
        }
        double d = shape - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);
        while (true) {
            double x, v;
            do {
                x = rng.nextGaussian();
                v = 1.0 + c * x;
            } while (v <= 0);
            v = v * v * v;
            double u = rng.nextDouble();
            double x2 = x * x;
            if (u < 1.0 - 0.0331 * x2 * x2) {
                return d * v;
            }
            if (Math.log(u) < 0.5 * x2 + d * (1.0 - v + Math.log(v))) {
                return d * v;
            }
        }
    }

    /**
     * Runs test cases for Bayesian statistics.
     */
    public static void main(String[] args) {
        System.out.println("=== Beta-Binomial Conjugate ===");
        BetaPosterior post = betaBinomialPosterior(1, 1, 8, 10);
        System.out.println("Prior: Beta(1,1) uniform");
        System.out.println("Data:  8 heads in 10 tosses");
        System.out.println("Posterior: " + post);

        double[] ci = credibleInterval95(post.alpha(), post.beta());
        System.out.printf("95%% Credible Interval: [%.4f, %.4f]%n", ci[0], ci[1]);

        System.out.println("\n=== Beta PDF/CDF Checks ===");
        System.out.printf("Beta(2,5) PDF at 0.2: %.6f%n", betaPdf(0.2, 2, 5));
        System.out.printf("Beta(2,5) CDF at 0.5: %.6f%n", betaCdf(0.5, 2, 5));

        System.out.println("\n=== Bayesian A/B Testing ===");
        int aSuc = 100, aTri = 1000;
        int bSuc = 120, bTri = 1000;
        double prob = abTestProbability(aSuc, aTri, bSuc, bTri, 100000);
        System.out.printf("A: %d/%d, B: %d/%d%n", aSuc, aTri, bSuc, bTri);
        System.out.printf("P(B > A) = %.4f%n", prob);

        BetaPosterior postA = betaBinomialPosterior(1, 1, aSuc, aTri);
        BetaPosterior postB = betaBinomialPosterior(1, 1, bSuc, bTri);
        System.out.println("A posterior: " + postA);
        System.out.println("B posterior: " + postB);

        double[] ciA = credibleInterval95(postA.alpha(), postA.beta());
        double[] ciB = credibleInterval95(postB.alpha(), postB.beta());
        System.out.printf("A 95%% CI: [%.4f, %.4f]%n", ciA[0], ciA[1]);
        System.out.printf("B 95%% CI: [%.4f, %.4f]%n", ciB[0], ciB[1]);
    }
}
