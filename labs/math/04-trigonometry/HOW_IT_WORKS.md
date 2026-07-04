# How Trigonometry Works

## Core Functions

### Sine and Cosine (Unit Circle Definition)

$$
\sin\theta = \frac{y}{r} \quad \cos\theta = \frac{x}{r} \quad r = \sqrt{x^2 + y^2}
$$

### Key Identities

**Pythagorean:**
$$
\sin^2\theta + \cos^2\theta = 1
$$

**Addition formulas:**
$$
\sin(A \pm B) = \sin A \cos B \pm \cos A \sin B
$$
$$
\cos(A \pm B) = \cos A \cos B \mp \sin A \sin B
$$

**Law of Sines:**
$$
\frac{a}{\sin A} = \frac{b}{\sin B} = \frac{c}{\sin C}
$$

**Law of Cosines:**
$$
c^2 = a^2 + b^2 - 2ab \cos C
$$

## In Java: Computing $\sin(x)$ via Taylor Series

```java
public static double sinTaylor(double x, int terms) {
    double sum = x;
    double term = x;
    for (int n = 1; n < terms; n++) {
        term *= -x * x / ((2 * n) * (2 * n + 1));
        sum += term;
    }
    return sum;
}
```

Java's `Math.sin()` uses optimized hardware instructions or polynomial approximations (CORDIC or minimax), not Taylor series.
