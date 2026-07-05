package com.ai02;

import java.util.Random;

public class ProbabilityDistributions {
    private static final Random rand = new Random();

    public static double normalPDF(double x, double mean, double stddev) {
        double variance = stddev * stddev;
        return (1 / Math.sqrt(2 * Math.PI * variance))
            * Math.exp(-((x - mean) * (x - mean)) / (2 * variance));
    }

    public static double normalCDF(double x, double mean, double stddev) {
        return 0.5 * (1 + erf((x - mean) / (stddev * Math.sqrt(2))));
    }

    private static double erf(double x) {
        double t = 1 / (1 + 0.3275911 * Math.abs(x));
        double poly = 1 - t * (0.254829592 + t * (-0.284496736 + t * (1.421413741 + t * (-1.453152027 + t * 1.061405429))));
        return x >= 0 ? poly : -poly;
    }

    public static double bernoulliPMF(int k, double p) {
        return k == 1 ? p : (1 - p);
    }

    public static double binomialPMF(int k, int n, double p) {
        return comb(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    public static double uniformPDF(double x, double a, double b) {
        return (x >= a && x <= b) ? 1 / (b - a) : 0;
    }

    public static double exponentialPDF(double x, double lambda) {
        return x >= 0 ? lambda * Math.exp(-lambda * x) : 0;
    }

    public static double poissonPMF(int k, double lambda) {
        return Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
    }

    private static long comb(int n, int k) {
        if (k < 0 || k > n) return 0;
        long res = 1;
        if (k > n - k) k = n - k;
        for (int i = 0; i < k; i++)
            res = res * (n - i) / (i + 1);
        return res;
    }

    private static long factorial(int n) {
        long fact = 1;
        for (int i = 2; i <= n; i++) fact *= i;
        return fact;
    }

    public static void main(String[] args) {
        System.out.println("=== Probability Distributions Demo ===");
        System.out.println("Normal PDF at x=0 (mean=0, std=1): " + normalPDF(0, 0, 1));
        System.out.println("Normal CDF at x=1.96: " + normalCDF(1.96, 0, 1));
        System.out.println("Bernoulli PMF k=1 p=0.7: " + bernoulliPMF(1, 0.7));
        System.out.println("Binomial PMF k=3 n=10 p=0.5: " + binomialPMF(3, 10, 0.5));
        System.out.println("Uniform PDF x=0.5 [0,1]: " + uniformPDF(0.5, 0, 1));
        System.out.println("Exponential PDF x=0.5 lambda=1: " + exponentialPDF(0.5, 1));
        System.out.println("Poisson PMF k=2 lambda=1: " + poissonPMF(2, 1));
    }
}
