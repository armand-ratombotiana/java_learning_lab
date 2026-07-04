# Security in Geometry

## Computational Geometry Attacks

### Polygon Bomb

A degenerate polygon with many collinear points causes $O(n^2)$ or $O(n^3)$ behavior in naive algorithms. Use monotone chains or randomized algorithms.

### Floating-Point Exploitation

An attacker can craft coordinates that exploit floating-point edge cases:

```java
// NaN/Infinity propagation
Point a = new Point(Double.NaN, 0);
Point b = new Point(0, 0);
double d = a.distanceTo(b); // NaN!
```

## Safe Input Handling

```java
public static boolean isValidCoordinate(double value) {
    return Double.isFinite(value); // rejects NaN, +-Infinity
}
```

## Geometry in Cryptography

- **Elliptic Curve Cryptography (ECC)**: point addition on elliptic curves over finite fields
- **Lattice-based cryptography**: geometric structures resistant to quantum attacks
