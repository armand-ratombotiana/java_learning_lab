# Interview Questions on Probability

## Easy

1. Given a fair coin, what's the probability of exactly 3 heads in 5 flips?
2. Roll two dice: probability of sum > 7?
3. What's the expected value of a fair die?

## Medium

4. Implement the Monty Hall problem and verify switching wins 2/3 of the time.
5. Given a biased coin that lands heads with probability $p$, make a fair coin.
6. Reservoir sampling: select $k$ items uniformly from a stream of unknown size.
7. Compute $P(A \mid B)$ given $P(A)$, $P(B)$, $P(B \mid A)$.

## Hard

8. Implement a Markov Chain Monte Carlo (MCMC) sampler.
9. Design and implement a distributed random number generator.
10. Implement the Gumbel-max trick for sampling from categorical distribution.
11. Bayesian inference for a change-point detection problem.

## Java: Fair Coin from Biased Coin

```java
public static class BiasedCoin {
    private final double p;
    private final Random rng;
    public BiasedCoin(double p, Random rng) {
        this.p = p; this.rng = rng;
    }
    public boolean flip() { return rng.nextDouble() < p; }
}

public static boolean fairFlip(BiasedCoin coin) {
    // von Neumann's trick
    boolean a = coin.flip();
    boolean b = coin.flip();
    if (a && !b) return true;  // HT
    if (!a && b) return false; // TH
    return fairFlip(coin);
}
```

## Java: Reservoir Sampling

```java
public static <T> List<T> reservoirSample(Stream<T> stream, int k) {
    List<T> reservoir = new ArrayList<>(k);
    Random rng = new Random();
    Iterator<T> it = stream.iterator();
    for (int i = 0; i < k && it.hasNext(); i++)
        reservoir.add(it.next());
    int i = k;
    while (it.hasNext()) {
        T item = it.next();
        int j = rng.nextInt(++i);
        if (j < k) reservoir.set(j, item);
    }
    return reservoir;
}
```
