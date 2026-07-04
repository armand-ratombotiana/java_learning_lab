# Probability

The mathematical framework for quantifying uncertainty and randomness.

## Scope

- Sample spaces and events
- Conditional probability, Bayes' theorem
- Random variables, expectation, variance
- Common distributions: binomial, normal, Poisson
- Law of large numbers, central limit theorem

## Java Implementation

```java
public class Probability {
    private final Random random = new Random();

    public double uniform(double a, double b) {
        return a + random.nextDouble() * (b - a);
    }

    public int binomial(int n, double p) {
        int successes = 0;
        for (int i = 0; i < n; i++)
            if (random.nextDouble() < p) successes++;
        return successes;
    }
}
```
