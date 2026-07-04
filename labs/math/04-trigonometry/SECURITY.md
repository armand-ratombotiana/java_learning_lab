# Security in Trigonometry

## Signal Processing Attacks

### Aliasing

When sampling below the Nyquist rate ($f_s < 2f_{\text{max}}$), high frequencies masquerade as low frequencies. Attackers can exploit this to hide data.

### Side-Channel via Angle Computation

Timing of `atan2` can leak information about coordinates. Use constant-time implementations in cryptographic contexts.

## Trigonometric Functions in Cryptography

### Elliptic Curve Cryptography

Point addition uses tangent lines — directly trigonometric:

$$
\lambda = \frac{3x_1^2 + a}{2y_1} \quad \text{(point doubling)}
$$

### Gaussian Noise Generation

```java
// Box-Muller transform uses sin/cos
public static double gaussian(Random rng) {
    double u = rng.nextDouble();
    double v = rng.nextDouble();
    return Math.sqrt(-2 * Math.log(u)) * Math.cos(2 * Math.PI * v);
}
```

## Safe Domain Checks

Always validate arguments to inverse trig functions to prevent NaN-based attacks.
