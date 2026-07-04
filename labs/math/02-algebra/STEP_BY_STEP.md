# Step-by-Step: Algebra in Java

## Solving Quadratic Equation

```java
public static Complex[] solveQuadratic(double a, double b, double c) {
    double discriminant = b * b - 4 * a * c;
    if (discriminant >= 0) {
        double sqrtD = Math.sqrt(discriminant);
        return new Complex[]{
            new Complex((-b + sqrtD) / (2 * a), 0),
            new Complex((-b - sqrtD) / (2 * a), 0)
        };
    } else {
        double real = -b / (2 * a);
        double imag = Math.sqrt(-discriminant) / (2 * a);
        return new Complex[]{
            new Complex(real, imag),
            new Complex(real, -imag)
        };
    }
}
```

## Polynomial Evaluation (Horner's Method)

```java
// Evaluate 2x^3 - 3x^2 + 4x - 5
// coeffs = [-5, 4, -3, 2]  (ascending degree)
public static double hornersMethod(double[] coeffs, double x) {
    double result = 0;
    for (int i = coeffs.length - 1; i >= 0; i--)
        result = result * x + coeffs[i];
    return result;
}
```

## Matrix Transpose

```java
public static double[][] transpose(double[][] matrix) {
    int rows = matrix.length, cols = matrix[0].length;
    double[][] result = new double[cols][rows];
    for (int i = 0; i < rows; i++)
        for (int j = 0; j < cols; j++)
            result[j][i] = matrix[i][j];
    return result;
}
```
