# Statistical Distributions Code Deep Dive

This lab provides a pure Java implementation of samplers and probability calculators for common distributions.

## 💻 Pure Java Implementation

```java file="labs/data-science/02-eda/statistical-distributions/SOLUTION/DistributionCalculators.java"
package datascience.eda;

import java.util.Random;

/**
 * A fundamental implementation of statistical distribution calculations.
 */
public class DistributionCalculators {

    private static final Random random = new Random();

    /**
     * Bernoulli Sampler: Returns 1 with probability p, else 0.
     */
    public static int sampleBernoulli(double p) {
        return random.nextDouble() < p ? 1 : 0;
    }

    /**
     * Normal (Gaussian) Sampler: Uses the Box-Muller transform.
     */
    public static double sampleNormal(double mean, double stdDev) {
        // Java's nextGaussian() returns a value from N(0, 1)
        return mean + (random.nextGaussian() * stdDev);
    }

    /**
     * Poisson PDF: Probability of exactly k events.
     */
    public static double poissonPDF(int k, double lambda) {
        return (Math.pow(lambda, k) * Math.exp(-lambda)) / factorial(k);
    }

    /**
     * Binomial PDF: Probability of exactly k successes in n trials.
     */
    public static double binomialPDF(int k, int n, double p) {
        return combinations(n, k) * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    // --- Math Helpers ---

    private static long factorial(int n) {
        long res = 1;
        for (int i = 2; i <= n; i++) res *= i;
        return res;
    }

    private static long combinations(int n, int k) {
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    public static void main(String[] args) {
        // 1. Normal Distribution
        double height = sampleNormal(170, 10);
        System.out.printf("Sampled Height: %.2f cm\n", height);

        // 2. Poisson Distribution
        // If 5 customers arrive per hour on average, what's probability of exactly 3?
        double p3 = poissonPDF(3, 5.0);
        System.out.printf("P(X=3 | lambda=5): %.4f\n", p3);

        // 3. Binomial Distribution
        // Probability of 7 heads in 10 flips (p=0.5)
        double heads7 = binomialPDF(7, 10, 0.5);
        System.out.printf("P(7 Heads in 10 flips): %.4f\n", heads7);
    }
}
```

## 🔍 Key Takeaways
1. **Box-Muller Transform**: Java's `Random.nextGaussian()` uses the Box-Muller transform under the hood. It takes two uniform random numbers and transforms them into a normally distributed number using sine and cosine functions.
2. **Factorial Limits**: Notice that the `factorial` method uses a `long`. This will overflow very quickly (at $n=21$). In real data science libraries (like Apache Commons Math), factorials are calculated using the Gamma function and log-space to handle large numbers.
3. **Precision**: For small probabilities, always consider working in log-space (as seen in the Naive Bayes lab) to avoid floating-point underflow.