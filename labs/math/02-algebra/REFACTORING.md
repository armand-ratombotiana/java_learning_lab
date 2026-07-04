# Refactoring Algebra Code

## Extract Linear Algebra Operations

```java
// BEFORE
double[][] result = new double[n][n];
for (int i = 0; i < n; i++)
    for (int j = 0; j < n; j++)
        for (int k = 0; k < n; k++)
            result[i][j] += A[i][k] * B[k][j];

// AFTER
public class Matrix {
    public static Matrix multiply(Matrix a, Matrix b) { ... }
    public static Matrix transpose(Matrix a) { ... }
    public static double determinant(Matrix a) { ... }
    public static Matrix inverse(Matrix a) { ... }
}
```

## Replace Magic Math with Named Formulas

```java
// BEFORE
double x = (-b + Math.sqrt(b*b - 4*a*c)) / (2*a);

// AFTER
public static double[] quadraticRoots(double a, double b, double c) {
    double discriminant = b * b - 4 * a * c;
    if (discriminant < 0) return new double[0];
    double sqrtD = Math.sqrt(discriminant);
    return new double[]{(-b + sqrtD) / (2 * a), (-b - sqrtD) / (2 * a)};
}
```

## Use Records for Immutable Math Objects

```java
public record Vector(double x, double y) {
    public Vector add(Vector v) { return new Vector(x + v.x, y + v.y); }
    public double dot(Vector v) { return x * v.x + y * v.y; }
    public double magnitude() { return Math.sqrt(x * x + y * y); }
}
```
