# Code Deep Dive: Probability and Statistics

```java
package com.mathacademy.probability;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ProbabilityCalculator {
    
    public static class RandomVariable {
        double mean, variance;
        public RandomVariable(double mean, double variance) {
            this.mean = mean; this.variance = variance;
        }
        public double getMean() { return mean; }
        public double getVariance() { return variance; }
        public double getStdDev() { return Math.sqrt(variance); }
    }
    
    public static class ProbabilityDistribution {
        Map<Double, Double> pmf = new HashMap<>();
        
        public void add(double value, double probability) {
            pmf.put(value, pmf.getOrDefault(value, 0.0) + probability);
        }
        
        public double expectedValue() {
            return pmf.entrySet().stream()
                .mapToDouble(e -> e.getKey() * e.getValue()).sum();
        }
        
        public double variance() {
            double mean = expectedValue();
            return pmf.entrySet().stream()
                .mapToDouble(e -> Math.pow(e.getKey() - mean, 2) * e.getValue()).sum();
        }
        
        public double cdf(double x) {
            return pmf.entrySet().stream()
                .filter(e -> e.getKey() <= x)
                .mapToDouble(Map.Entry::getValue).sum();
        }
    }
    
    public static double uniformProbability(int a, int b, int k) {
        return (k >= a && k <= b) ? 1.0 / (b - a + 1) : 0;
    }
    
    public static double binomial(int n, int k, double p) {
        return combinations(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }
    
    public static double poisson(double lambda, int k) {
        return Math.exp(-lambda) * Math.pow(lambda, k) / factorial(k);
    }
    
    public static double normalPDF(double x, double mean, double stdDev) {
        double z = (x - mean) / stdDev;
        return Math.exp(-0.5 * z * z) / (stdDev * Math.sqrt(2 * Math.PI));
    }
    
    public static double normalCDF(double x, double mean, double stdDev) {
        return 0.5 * (1 + erf((x - mean) / (stdDev * Math.sqrt(2))));
    }
    
    private static double erf(double z) {
        double t = 1 / (1 + 0.5 * Math.abs(z));
        double tau = t * Math.exp(-z * z - 1.26551223 + 
            t * (1.00002368 + t * (0.37409196 + t * (0.09678418 + 
            t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + 
            t * (1.48851587 + t * (-0.82215223 + t * 0.17087277)))))))));
        return z >= 0 ? 1 - tau : tau - 1;
    }
    
    public static double exponentialPDF(double x, double lambda) {
        return x < 0 ? 0 : lambda * Math.exp(-lambda * x);
    }
    
    public static double bayesTheorem(double pA, double pBgivenA, double pB) {
        return pBgivenA * pA / pB;
    }
    
    public static long combinations(int n, int k) {
        if (k > n) return 0;
        if (k > n - k) k = n - k;
        long result = 1;
        for (int i = 0; i < k; i++) result = result * (n - i) / (i + 1);
        return result;
    }
    
    public static long factorial(int n) {
        if (n <= 1) return 1;
        long result = 1;
        for (int i = 2; i <= n; i++) result *= i;
        return result;
    }
    
    public static double[] generateRandomSample(int n, double min, double max) {
        Random random = ThreadLocalRandom.current();
        double[] sample = new double[n];
        for (int i = 0; i < n; i++) sample[i] = min + random.nextDouble() * (max - min);
        return sample;
    }
    
    public static double sampleMean(double[] data) {
        return Arrays.stream(data).sum() / data.length;
    }
    
    public static double sampleVariance(double[] data) {
        double mean = sampleMean(data);
        double sumSq = 0;
        for (double x : data) sumSq += (x - mean) * (x - mean);
        return sumSq / (data.length - 1);
    }
}
```