package com.statistics.lab08;

/**
 * Provides tools for experimental design: sample size calculation,
 * factorial design analysis (2^k), and standard normal quantile
 * functions.
 */
public final class ExperimentalDesign {

    private ExperimentalDesign() {
    }

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
     * Rational approximations for the inverse normal CDF (z-scores).
     * Based on Peter Acklam's algorithm.
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
        // Refine with Newton-Raphson
        double e = 0.5 * erfc(-x / Math.sqrt(2)) - p;
        double u = e * Math.sqrt(2 * Math.PI) * Math.exp(x * x / 2);
        x -= u / (1 + x * u / 2);
        return x;
    }

    /**
     * Standard normal CDF using complementary error function.
     */
    public static double normCdf(double z) {
        return 0.5 * erfc(-z / Math.sqrt(2));
    }

    /**
     * Calculates required sample size per group for comparing two means.
     *
     * @param delta minimum detectable difference in means
     * @param sigma pooled standard deviation
     * @param alpha significance level (Type I error)
     * @param beta  Type II error (power = 1 - beta)
     * @return sample size per group
     */
    public static double sampleSizeMeans(double delta, double sigma,
                                          double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        return 2 * Math.pow(zAlpha2 + zBeta, 2) * sigma * sigma / (delta * delta);
    }

    /**
     * Calculates required sample size per group for comparing two proportions.
     *
     * @param p1    expected proportion in group 1
     * @param p2    expected proportion in group 2
     * @param alpha significance level
     * @param beta  Type II error
     * @return sample size per group
     */
    public static double sampleSizeProportions(double p1, double p2,
                                                double alpha, double beta) {
        double zAlpha2 = normInv(1 - alpha / 2);
        double zBeta = normInv(1 - beta);
        double pBar = (p1 + p2) / 2;
        // Unpooled version
        double num = Math.pow(zAlpha2 + zBeta, 2) * (p1 * (1 - p1) + p2 * (1 - p2));
        double den = (p1 - p2) * (p1 - p2);
        return num / den;
    }

    /**
     * Represents a 2-factor factorial design result.
     */
    public record Factorial2Result(double effectA, double effectB,
                                    double interactionAB) {
    }

    /**
     * Analyzes a 2² factorial design.
     * Design matrix order: (--), (+-), (-+), (++)
     *
     * @param means response means for each factor combination
     *              in standard order: [A-,B-], [A+,B-], [A-,B+], [A+,B+]
     * @return main effects and interaction
     */
    public static Factorial2Result factorial2(double[] means) {
        if (means.length != 4) {
            throw new IllegalArgumentException("Need exactly 4 means for 2^2 design");
        }
        double mA = (means[1] + means[3]) / 2 - (means[0] + means[2]) / 2;
        double mB = (means[2] + means[3]) / 2 - (means[0] + means[1]) / 2;
        double mAB = (means[3] - means[2]) - (means[1] - means[0]);
        return new Factorial2Result(mA, mB, mAB);
    }

    /**
     * Analyzes a 2³ factorial design.
     * Standard order: (---), (+--), (-+-), (++-), (--+), (+-+), (-++), (+++)
     *
     * @param means response means in standard order
     * @return array of 7 effects: [A, B, C, AB, AC, BC, ABC]
     */
    public static double[] factorial3(double[] means) {
        if (means.length != 8) {
            throw new IllegalArgumentException("Need exactly 8 means for 2^3 design");
        }
        // Contrast coefficients for 2^3 design
        int[][] signs = {
            {-1, -1, -1,  1,  1,  1, -1},
            { 1, -1, -1, -1, -1,  1,  1},
            {-1,  1, -1, -1,  1, -1,  1},
            { 1,  1, -1,  1, -1, -1, -1},
            {-1, -1,  1,  1, -1, -1,  1},
            { 1, -1,  1, -1,  1, -1, -1},
            {-1,  1,  1, -1, -1,  1, -1},
            { 1,  1,  1,  1,  1,  1,  1}
        };
        double[] effects = new double[7];
        for (int j = 0; j < 7; j++) {
            double sum = 0;
            for (int i = 0; i < 8; i++) {
                sum += signs[i][j] * means[i];
            }
            effects[j] = sum / 4; // divisor = 2^(k-1) = 4
        }
        return effects;
    }

    /**
     * Runs test cases for experimental design.
     */
    public static void main(String[] args) {
        System.out.println("=== Sample Size for Means ===");
        double delta = 5.0;
        double sigma = 10.0;
        double alpha = 0.05;
        double beta = 0.20;
        double n = sampleSizeMeans(delta, sigma, alpha, beta);
        System.out.printf("Delta=%.1f, Sigma=%.1f, Alpha=%.2f, Beta=%.2f%n",
            delta, sigma, alpha, beta);
        System.out.printf("Sample size per group: %.2f -> %d%n", n, (int) Math.ceil(n));

        System.out.println("\n=== Sample Size for Proportions ===");
        double p1 = 0.10, p2 = 0.30;
        double nProp = sampleSizeProportions(p1, p2, alpha, beta);
        System.out.printf("p1=%.2f, p2=%.2f, Alpha=%.2f, Beta=%.2f%n", p1, p2, alpha, beta);
        System.out.printf("Sample size per group: %.2f -> %d%n", nProp, (int) Math.ceil(nProp));

        System.out.println("\n=== Norm Inv/Cdf Checks ===");
        double[] zs = {-1.96, -1.645, 0, 1.645, 1.96};
        for (double z : zs) {
            double p = normCdf(z);
            double zBack = normInv(p);
            System.out.printf("z=%.3f -> p=%.6f -> z'=%.6f%n", z, p, zBack);
        }

        System.out.println("\n=== 2² Factorial Design ===");
        // Example: A=drug dose, B=delivery method
        // Means: [low/low, high/low, low/high, high/high]
        double[] f2means = {10, 14, 12, 20};
        Factorial2Result f2 = factorial2(f2means);
        System.out.printf("Means: [%.0f, %.0f, %.0f, %.0f]%n",
            f2means[0], f2means[1], f2means[2], f2means[3]);
        System.out.printf("Effect A (drug):     %.2f%n", f2.effectA());
        System.out.printf("Effect B (delivery): %.2f%n", f2.effectB());
        System.out.printf("Interaction AB:      %.2f%n", f2.interactionAB());

        System.out.println("\n=== 2³ Factorial Design ===");
        double[] f3means = {5, 9, 7, 15, 6, 10, 8, 18};
        double[] f3 = factorial3(f3means);
        String[] labels = {"A", "B", "C", "AB", "AC", "BC", "ABC"};
        for (int i = 0; i < labels.length; i++) {
            System.out.printf("Effect %s: %.2f%n", labels[i], f3[i]);
        }
    }
}
