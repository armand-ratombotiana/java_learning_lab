# Trigonometry

The study of relationships between angles and sides of triangles, and periodic functions.

## Scope

- Unit circle, radians vs degrees
- Sine, cosine, tangent and their inverses
- Trigonometric identities
- Law of sines, law of cosines
- Periodic functions, Fourier series

## Java Implementation

```java
public class Trigonometry {
    public static double[] solveTriangle(double a, double b, double C) {
        // Given two sides and included angle (SAS)
        double c = Math.sqrt(a * a + b * b - 2 * a * b * Math.cos(C));
        double A = Math.asin(a * Math.sin(C) / c);
        double B = Math.PI - A - C;
        return new double[]{c, A, B};
    }
}
```
