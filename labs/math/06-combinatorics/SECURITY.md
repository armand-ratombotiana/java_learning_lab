# Security in Combinatorics

## Password Security

The number of possible passwords of length $L$ from an alphabet of size $A$:

$$
\text{space} = A^L
$$

```java
public static double passwordEntropy(int alphabetSize, int length) {
    long space = (long) Math.pow(alphabetSize, length);
    return log2(space); // entropy in bits
}
```

## Birthday Attack

Based on the birthday paradox: with $\sqrt{2 \cdot 2^n}$ random inputs, there's a 50% chance of collision in an $n$-bit hash.

$$
P(\text{collision}) \approx 1 - e^{-\frac{k(k-1)}{2N}}
$$

## Combinatorial Explosion in Attacks

- **Brute force**: try all possible keys
- **Dictionary attack**: try common passwords (combinations of words)
- **Rainbow tables**: precomputed hash chains using reduction functions

## Mitigation

Use large key spaces ($\ge 2^{128}$), salt passwords, and rate-limit login attempts.

```java
// Estimating crack time
double combinations = Math.pow(26, 8); // 8-char lowercase
double seconds = combinations / 1e9; // 1 billion guesses/sec
```
