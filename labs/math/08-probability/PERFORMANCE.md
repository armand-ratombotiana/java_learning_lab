# Probability Performance

## Random Number Generation Speed

| Generator | Throughput | Quality |
|-----------|-----------|---------|
| `java.util.Random` | ~50M/s | Medium (48-bit LCG) |
| `ThreadLocalRandom` | ~100M/s | Medium |
| `SplittableRandom` | ~200M/s | Good (SplitMix) |
| `SecureRandom` | ~1M/s | Best (cryptographic) |

## Monte Carlo Sampling

```java
// Parallel Monte Carlo with ThreadLocalRandom
IntStream.range(0, 1_000_000).parallel()
    .mapToDouble(i -> ThreadLocalRandom.current().nextDouble())
    .filter(x -> x < 0.5)
    .count();
```

## Rejection Sampling Efficiency

For normal distribution, Box-Muller generates 2 samples directly vs rejection sampling which may waste many uniforms.

## Precomputed PDFs

For performance-critical applications, precompute probability values:

```java
double[] normalPDF = new double[1000];
double step = 6.0 / 1000;
for (int i = 0; i < 1000; i++) {
    double x = -3 + i * step;
    normalPDF[i] = Math.exp(-x*x/2) / Math.sqrt(2 * Math.PI);
}
```
