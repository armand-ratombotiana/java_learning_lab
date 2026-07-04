# Internals of Probability

## Pseudorandom Number Generators

### Linear Congruential Generator (LCG)

$$
X_{n+1} = (a X_n + c) \mod m
$$

```java
// Java's Random uses a 48-bit LCG (modified)
public class SimpleLCG {
    private long seed;

    public SimpleLCG(long seed) { this.seed = seed; }

    public int nextInt() {
        seed = (seed * 25214903917L + 11L) & ((1L << 48) - 1);
        return (int) (seed >>> 16);
    }
}
```

### SplittableRandom (Java 8+)

Uses the SplitMix algorithm — faster and supports parallel splitting.

## Common Distributions

| Distribution | PMF/PDF | Parameters | Mean | Variance |
|-------------|---------|------------|------|----------|
| Uniform | $\frac{1}{b-a}$ | $a, b$ | $\frac{a+b}{2}$ | $\frac{(b-a)^2}{12}$ |
| Normal | $\frac{1}{\sigma\sqrt{2\pi}} e^{-\frac{(x-\mu)^2}{2\sigma^2}}$ | $\mu, \sigma$ | $\mu$ | $\sigma^2$ |
| Binomial | $\binom{n}{k} p^k (1-p)^{n-k}$ | $n, p$ | $np$ | $np(1-p)$ |
| Poisson | $\frac{\lambda^k e^{-\lambda}}{k!}$ | $\lambda$ | $\lambda$ | $\lambda$ |
| Exponential | $\lambda e^{-\lambda x}$ | $\lambda$ | $1/\lambda$ | $1/\lambda^2$ |

## Box-Muller Transform

Generate normal random variables from uniform:

```java
public static double[] generateNormal(Random rng) {
    double u = rng.nextDouble();
    double v = rng.nextDouble();
    return new double[]{
        Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v),
        Math.sqrt(-2 * Math.log(u)) * Math.sin(2 * Math.PI * v)
    };
}
```
