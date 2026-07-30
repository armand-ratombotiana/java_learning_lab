package com.statistics.lab10;

/**
 * Computes statistical power, effect sizes (Cohen's d),
 * sample size requirements, and minimum detectable effect (MDE)
 * for two-sample mean comparisons.
 * <p>
 * Uses the standard normal distribution for approximations.
 */
public final class StatisticalPower {

    private StatisticalPower() {
    }

    // -----------------------------------------------------------------
    // Normal distribution helpers
    // -----------------------------------------------------------------

    /**
     * Complementary error function using Horner-form rational approximation.
     */
    public static double erfc(double x) {
        double z = Math.abs(x);
        double t = 1.0 / (1.0 + 0.5 * z);
        double r = t * Math.exp(-z * z - 1.26551223 +
            t * (1.00002368 +
            t * (0.37409196 +
            t * (0.09678418 +
            t * (-0.18628806 +
            t * (0.27886807 +
            t * (-1.13520398 +
            t * (1.48851587 +
            t * (-0.82215223 +
            t * (0.17087277))))))))));
        return x >= 0 ? r : 2 - r;
    }

    /**
     * Standard normal CDF using complementary error function.
     */
    public static double normCdf(double z) {
        return 0.5 * erfc(-z / Math.sqrt(2));
    }

    /**
     * Inverse normal CDF (Acklam rational approximation).
     */
    public static double normInv(double p) {
        if (p <= 0 || p >= 1) {
            throw new IllegalArgumentException("p must be in (0,1)");
        }
        double[] a = {-3.969683028665376e1, 2.209460984245205e2,
                      -2.759285104469687e2, 1.383577518672690e2,
                      -3.066479806614716e1, 2.506628277459239};
        double[] b = {-5.447609879822406e1, 1.615858368580409e2,
                      -1.556989798598866e2, 6.680131188771972e1,
                      -1.328068155288572e1};
        double[] c = {-7.784894002430293e-3, -3.223964580411365e-1,
                      -2.400758277161838, -2.549732539343734,
                      4.374664141464968, 2.938163982698783};
        double[] d = {7.784695709041462e-3, 3.224671290700398e-1,
                      2.445134137142996, 3.754408661907416};
        double pLow = 0.02425;
        double pHigh = 1 - pLow;
        double x;
        if (p < pLow) {
            double q = Math.sqrt(-2 * Math.log(p));
            x = (((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])
                / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
        } else if (p <= pHigh) {
            double q = p - 0.5;
            double r = q * q;
            x = (((((a[0] * r + a[1]) * r + a[2]) * r + a[3]) * r + a[4]) * r + a[5]) * q
                / (((((b[0] * r + b[1]) * r + b[2]) * r + b[3]) * r + b[4]) * r + 1);
        } else {
            double q = Math.sqrt(-2 * Math.log(1 - p));
            x = -(((((c[0] * q + c[1]) * q + c[2]) * q + c[3]) * q + c[4]) * q + c[5])
                / ((((d[0] * q + d[1]) * q + d[2]) * q + d[3]) * q + 1);
        }
        double e = 0.5 * erfc(-x / Math.sqrt(2)) - p;
        double u = e * Math.sqrt(2 * Math.PI) * Math.exp(x * x / 2);
        x -= u / (1 + x * u / 2);
        return x;
    }

    // -----------------------------------------------------------------
    // Cohen's d
    // -----------------------------------------------------------------

    /**
     * Computes Cohen's d (standardized mean difference).
     *
     * @param mean1        mean of group 1
     * @param mean2        mean of group 2
     * @param pooledStdDev pooled standard deviation
     * @return Cohen's d
     */
    public static double cohensD(double mean1, double mean2, double pooledStdDev) {
        return (mean1 - mean2) / pooledStdDev;
    }

    /**
     * Computes the pooled standard deviation from two groups.
     *
     * @param std1 standard deviation of group 1
     * @param n1   size of group 1
     * @param std2 standard deviation of group 2
     * @param n2   size of group 2
     * @return pooled standard deviation
     */
    public static double pooledStdDev(double std1, int n1, double std2, int n2) {
        double num = (n1 - 1) * std1 * std1 + (n2 - 1) * std2 * std2;
        double den = n1 + n2 - 2;
        return Math.sqrt(num / den);
    }

    // -----------------------------------------------------------------
    // Power for two-sample means
    // -----------------------------------------------------------------

    /**
     * Calculates the statistical power for a two-sample mean comparison
     * (two-sided test).
     *
     * @param delta     true difference in means (|μ₁ - μ₂|)
     * @param sigma     pooled standard deviation
     * @param nPerGroup sample size per group
     * @param alpha     significance level
     * @return statistical power (0 to 1)
     */
    public static double powerTwoSampleMeans(double delta, double sigma,
                                              int nPerGroup, double alpha) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double se = sigma * Math.sqrt(2.0 / nPerGroup);
        double z = Math.abs(delta) / se - zAlpha2;
        return normCdf(z);
    }

    /**
     * Calculates power from a standardized effect size (Cohen's d).
     *
     * @param d          Cohen's d
     * @param nPerGroup  sample size per group
     * @param alpha      significance level
     * @return statistical power
     */
    public static double powerFromCohensD(double d, int nPerGroup, double alpha) {
        return powerTwoSampleMeans(d, 1.0, nPerGroup, alpha);
    }

    // -----------------------------------------------------------------
    // Minimum Detectable Effect (MDE)
    // -----------------------------------------------------------------

    /**
     * Computes the minimum detectable effect (MDE) in raw units
     * for a two-sample mean test.
     *
     * @param sigma     pooled standard deviation
     * @param nPerGroup sample size per group
     * @param alpha     significance level
     * @param beta      Type II error (power = 1 - beta)
     * @return minimum detectable difference in means
     */
    public static double minimumDetectableEffect(double sigma, int nPerGroup,
                                                  double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return (zAlpha2 + zBeta) * sigma * Math.sqrt(2.0 / nPerGroup);
    }

    // -----------------------------------------------------------------
    // Sample size determination
    // -----------------------------------------------------------------

    /**
     * Calculates required sample size per group for a two-sample mean test.
     *
     * @param delta minimum detectable difference
     * @param sigma pooled standard deviation
     * @param alpha significance level
     * @param beta  Type II error
     * @return required sample size per group
     */
    public static double sampleSizePerGroup(double delta, double sigma,
                                             double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return 2 * Math.pow(zAlpha2 + zBeta, 2) * sigma * sigma / (delta * delta);
    }

    /**
     * Calculates required sample size per group from Cohen's d.
     *
     * @param d     Cohen's d
     * @param alpha significance level
     * @param beta  Type II error
     * @return required sample size per group
     */
    public static double sampleSizeFromCohensD(double d, double alpha, double beta) {
        return sampleSizePerGroup(d, 1.0, alpha, beta);
    }

    /**
     * Runs test cases for statistical power and effect size.
     */
    public static void main(String[] args) {
        System.out.println("=== Cohen's d ===");
        double d = cohensD(100, 90, 15);
        System.out.printf("Mean1=100, Mean2=90, SD=15 -> d = %.4f (medium effect)%n", d);
        System.out.printf("Interpretation: |d|=%.2f is '%s'%n", Math.abs(d),
            interpretCohensD(d));

        double pooledSd = pooledStdDev(10, 30, 12, 30);
        System.out.printf("Pooled SD (sd1=10,n1=30; sd2=12,n2=30): %.4f%n", pooledSd);

        System.out.println("\n=== Statistical Power ===");
        // Classic: d=0.5, n=64, alpha=0.05 should give ~0.80 power
        double power1 = powerTwoSampleMeans(5, 10, 64, 0.05);
        System.out.printf("Delta=5, Sigma=10, n=64, Alpha=0.05 -> Power = %.4f%n", power1);

        double power2 = powerFromCohensD(0.5, 64, 0.05);
        System.out.printf("d=0.5, n=64, Alpha=0.05 -> Power = %.4f (should be ~0.80)%n", power2);

        double power3 = powerFromCohensD(0.8, 26, 0.05);
        System.out.printf("d=0.8, n=26, Alpha=0.05 -> Power = %.4f (should be ~0.80)%n", power3);

        // Small effect
        double power4 = powerFromCohensD(0.2, 100, 0.05);
        System.out.printf("d=0.2, n=100, Alpha=0.05 -> Power = %.4f%n", power4);

        System.out.println("\n=== Minimum Detectable Effect ===");
        double mde = minimumDetectableEffect(10, 100, 0.05, 0.20);
        System.out.printf("Sigma=10, n=100, Alpha=0.05, Power=0.80 -> MDE = %.4f%n", mde);

        double mde2 = minimumDetectableEffect(10, 200, 0.05, 0.20);
        System.out.printf("Sigma=10, n=200, Alpha=0.05, Power=0.80 -> MDE = %.4f%n", mde2);

        System.out.println("\n=== Sample Size Determination ===");
        double n1 = sampleSizePerGroup(5, 10, 0.05, 0.20);
        System.out.printf("Delta=5, Sigma=10, Alpha=0.05, Power=0.80 -> n = %.2f (%d)%n",
            n1, (int) Math.ceil(n1));

        double n2 = sampleSizeFromCohensD(0.5, 0.05, 0.20);
        System.out.printf("d=0.5, Alpha=0.05, Power=0.80 -> n = %.2f (%d)%n",
            n2, (int) Math.ceil(n2));

        double n3 = sampleSizeFromCohensD(0.2, 0.05, 0.20);
        System.out.printf("d=0.2, Alpha=0.05, Power=0.80 -> n = %.2f (%d)%n",
            n3, (int) Math.ceil(n3));

        System.out.println("\n=== Power Curves (varying n) ===");
        System.out.println("d=0.5, Alpha=0.05:");
        for (int n = 10; n <= 100; n += 10) {
            double p = powerFromCohensD(0.5, n, 0.05);
            System.out.printf("  n=%3d -> Power=%.4f%n", n, p);
        }

        System.out.println("\n=== Normal CDF/Inv Checks ===");
        double[] zs = {-1.96, -1.645, 0, 1.645, 1.96};
        for (double z : zs) {
            double p = normCdf(z);
            double zBack = normInv(p);
            System.out.printf("z=%.3f -> p=%.6f -> z'=%.6f%n", z, p, zBack);
        }
    }

    /**
     * Returns a qualitative interpretation of Cohen's d.
     */
    public static String interpretCohensD(double d) {
        double ad = Math.abs(d);
        if (ad < 0.2) return "negligible";
        if (ad < 0.5) return "small";
        if (ad < 0.8) return "medium";
        return "large";
    }
}
