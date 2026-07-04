# Algebra

The study of mathematical symbols and the rules for manipulating them.

## Scope

- Linear equations and systems
- Quadratic equations and polynomials
- Inequalities, absolute value
- Functions, domain, range, composition
- Matrix algebra

## Java Implementation

```java
public class Algebra {
    public static double[] solveLinear(double a, double b) {
        if (a == 0) throw new IllegalArgumentException("No unique solution");
        return new double[]{-b / a};
    }

    public static double[] solveQuadratic(double a, double b, double c) {
        double d = b * b - 4 * a * c;
        if (d < 0) return new double[]{};
        if (d == 0) return new double[]{-b / (2 * a)};
        double sqrtD = Math.sqrt(d);
        return new double[]{(-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a)};
    }
}
```
