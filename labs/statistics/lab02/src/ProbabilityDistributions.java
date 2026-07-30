package com.statistics.lab02;

import java.util.Random;

/**
 * Provides PDF, CDF, and sampling methods for Normal, Binomial,
 * Poisson, and Exponential probability distributions.
 * <p>
 * All methods use double precision and the standard {@link Random} class.
 */
public final class ProbabilityDistributions {

    private static final Random RNG = new Random();

    private ProbabilityDistributions() {
    }

    // ──────────────────────── Normal Distribution ────────────────────────

    /**
     * Normal PDF: f(x) = (1/σ√(2π)) * e^(-(x-μ)²/(2σ²))
     */
    public static double normalPdf(double x, double mu, double sigma) {
        double z = (x - mu) / sigma;
        return Math.exp(-0.5 * z * z) / (sigma * Math.sqrt(2 * Math.PI));
    }

    /**
     * Normal CDF approximated using the error function.
     */
    public static double normalCdf(double x, double mu, double sigma) {
        double z = (x - mu) / sigma;
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }

    /**
     * Error function approximation using Horner's method.
     */
    private static double erf(double x) {
        double a1 = 0.254829592;
        double a2 = -0.284496736;
        double a3 = 1.421413741;
        double a4 = -1.453152027;
        double a5 = 1.061405429;
        double p = 0.3275911;
        int sign = (x < 0) ? -1 : 1;
        x = Math.abs(x);
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        return sign * y;
    }

    /**
     * Generates a standard normal sample using Box-Muller transform.
     */
    public static double normalSample() {
        double u1 = RNG.nextDouble();
        double u2 = RNG.nextDouble();
        return Math.sqrt(-2 * Math.log(u1)) * Math.cos(2 * Math.PI * u2);
    }

    /**
     * Generates a normal sample with given mean and standard deviation.
     */
    public static double normalSample(double mu, double sigma) {
        return mu + sigma * normalSample();
    }

    // ──────────────────────── Binomial Distribution ────────────────────────

    /**
     * Binomial PMF: P(X=k) = C(n,k) * p^k * (1-p)^(n-k)
     */
    public static double binomialPmf(int k, int n, double p) {
        if (k < 0 || k > n) {
            return 0;
        }
        return binomialCoefficient(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    /**
     * Binomial CDF: P(X ≤ k) = Σᵢ₌₀ᵏ C(n,i) pⁱ (1-p)ⁿ⁻ⁱ
     */
    public static double binomialCdf(int k, int n, double p) {
        double sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += binomialPmf(i, n, p);
        }
        return sum;
    }

    /**
     * Binomial coefficient C(n,k) computed iteratively to avoid overflow.
     */
    private static double binomialCoefficient(int n, int k) {
        if (k < 0 || k > n) {
            return 0;
        }
        if (k > n - k) {
            k = n - k;
        }
        double result = 1;
        for (int i = 1; i <= k; i++) {
            result = result * (n - k + i) / i;
        }
        return result;
    }

    /**
     * Generates a binomial sample by simulating n Bernoulli trials.
     */
    public static int binomialSample(int n, double p) {
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (RNG.nextDouble() < p) {
                count++;
            }
        }
        return count;
    }

    // ──────────────────────── Poisson Distribution ────────────────────────

    /**
     * Poisson PMF: P(X=k) = λ^k * e^(-λ) / k!
     */
    public static double poissonPmf(int k, double lambda) {
        if (k < 0) {
            return 0;
        }
        return Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
    }

    /**
     * Poisson CDF: P(X ≤ k) = Σᵢ₌₀ᵏ λⁱ e^(-λ) / i!
     */
    public static double poissonCdf(int k, double lambda) {
        double sum = 0;
        for (int i = 0; i <= k; i++) {
            sum += poissonPmf(i, lambda);
        }
        return sum;
    }

    /**
     * Factorial using double to avoid overflow.
     */
    private static double factorial(int n) {
        if (n <= 1) {
            return 1;
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    /**
     * Generates a Poisson sample using the exponential inter-arrival method.
     */
    public static int poissonSample(double lambda) {
        double el = Math.exp(-lambda);
        double product = 1;
        int k = 0;
        do {
            product *= RNG.nextDouble();
            k++;
        } while (product > el);
        return k - 1;
    }

    // ──────────────────────── Exponential Distribution ────────────────────────

    /**
     * Exponential PDF: f(x) = λe^(-λx)
     */
    public static double exponentialPdf(double x, double lambda) {
        if (x < 0) {
            return 0;
        }
        return lambda * Math.exp(-lambda * x);
    }

    /**
     * Exponential CDF: F(x) = 1 - e^(-λx)
     */
    public static double exponentialCdf(double x, double lambda) {
        if (x < 0) {
            return 0;
        }
        return 1 - Math.exp(-lambda * x);
    }

    /**
     * Generates an exponential sample using inverse transform.
     */
    public static double exponentialSample(double lambda) {
        double u = RNG.nextDouble();
        return -Math.log(1 - u) / lambda;
    }

    // ──────────────────────── Main (test cases) ────────────────────────

    /**
     * Runs test cases for all distribution methods.
     */
    public static void main(String[] args) {
        System.out.println("=== Normal Distribution (μ=0, σ=1) ===");
        for (double x = -2; x <= 2; x += 0.5) {
            System.out.printf("x=%.1f  PDF=%.6f  CDF=%.6f%n", x, normalPdf(x, 0, 1), normalCdf(x, 0, 1));
        }
        System.out.println("Samples: ");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  %.4f", normalSample(0, 1));
        }
        System.out.println();

        System.out.println("\n=== Binomial Distribution (n=10, p=0.5) ===");
        for (int k = 0; k <= 10; k++) {
            System.out.printf("k=%2d  PMF=%.6f  CDF=%.6f%n", k, binomialPmf(k, 10, 0.5), binomialCdf(k, 10, 0.5));
        }
        System.out.println("Samples: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(binomialSample(10, 0.5) + " ");
        }
        System.out.println();

        System.out.println("\n=== Poisson Distribution (λ=3) ===");
        for (int k = 0; k <= 10; k++) {
            System.out.printf("k=%2d  PMF=%.6f  CDF=%.6f%n", k, poissonPmf(k, 3), poissonCdf(k, 3));
        }
        System.out.println("Samples: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(poissonSample(3) + " ");
        }
        System.out.println();

        System.out.println("\n=== Exponential Distribution (λ=2) ===");
        for (double x = 0; x <= 2; x += 0.25) {
            System.out.printf("x=%.2f  PDF=%.6f  CDF=%.6f%n", x, exponentialPdf(x, 2), exponentialCdf(x, 2));
        }
        System.out.println("Samples: ");
        for (int i = 0; i < 5; i++) {
            System.out.printf("  %.4f", exponentialSample(2));
        }
        System.out.println();
    }
}
