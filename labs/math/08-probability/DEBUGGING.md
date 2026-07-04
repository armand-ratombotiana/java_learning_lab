# Debugging Probability in Java

## Common Issues

### Non-Uniform Random Numbers

```java
// WRONG: using remainder creates bias
int biased = rng.nextInt() % n;

// RIGHT: rejection sampling
int unbiased = rng.nextInt(n);

// Actually, nextInt(n) already does rejection internally
```

### Random Seed Reuse

```java
// WRONG: new Random() each time has same seed (based on time)
for (int i = 0; i < 100; i++) {
    Random r = new Random(); // same seed if fast enough!
    System.out.println(r.nextDouble()); // same values!
}

// RIGHT: reuse one Random instance
Random rng = new Random();
for (int i = 0; i < 100; i++)
    System.out.println(rng.nextDouble());
```

### Thread Safety of Random

`java.util.Random` is thread-safe internally (CAS on seed), but for high throughput, use `ThreadLocalRandom`:

```java
double val = ThreadLocalRandom.current().nextDouble();
```

## Debugging Checklist

- [ ] Random range correct? (0 to n-1, not 1 to n)
- [ ] Independent events or conditional?
- [ ] Did you apply Bayes' theorem correctly?
- [ ] Events mutually exclusive or overlapping?
- [ ] Sample size large enough for Law of Large Numbers?
- [ ] Random seed deterministic or truly random?
