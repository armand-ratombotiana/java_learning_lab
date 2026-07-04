# Architecture of Probability

## Java Random Hierarchy

```
java.util.Random (seed-based, LCG)
├── java.security.SecureRandom (FIPS 140-2)
├── ThreadLocalRandom (thread-local, fast)
└── SplittableRandom (splittable, parallel)
```

## Probability Framework Architecture

```java
interface Distribution {
    double pdf(double x);
    double cdf(double x);
    double sample(Random rng);
    double mean();
    double variance();
}

interface RandomProcess {
    Distribution getStationaryDistribution();
    List<Double> simulate(Random rng, int steps);
}
```

## Simulation Pipeline

```
Model → Random Input → Compute Output → Aggregate Statistics
                                         ↓
                                   Confidence Intervals
```

## Bayesian Inference Pipeline

```
Prior P(H) → Evidence E → Likelihood P(E|H) → Posterior P(H|E)
                                                    ↓
                                           New Prior for next update
```
