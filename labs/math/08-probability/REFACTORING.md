# Refactoring Probability Code

## Extract Distribution Classes

```java
// BEFORE: scattered computation
double mean = n * p;
double variance = n * p * (1 - p);

// AFTER
public record Binomial(int n, double p) {
    public double mean() { return n * p; }
    public double variance() { return n * p * (1 - p); }
    public double pmf(int k) { return binomial(n, k) * Math.pow(p, k) * Math.pow(1-p, n-k); }
}
```

## Separate Randomness from Logic

```java
// BEFORE
class Simulation {
    Random rng = new Random();
    public void run() {
        if (rng.nextDouble() < 0.3) { ... }
    }
}

// AFTER: inject Random for testability
class Simulation {
    private final Random rng;
    Simulation(Random rng) { this.rng = rng; }
}
```

## Use Enum for Events

```java
public enum Coin { HEADS, TAILS;
    public static Coin flip(Random rng) {
        return rng.nextBoolean() ? HEADS : TAILS;
    }
}
```
