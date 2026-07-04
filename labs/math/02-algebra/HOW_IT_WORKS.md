# How Algebra Works

## Core Operations

### Solving $ax + b = 0$

$$
x = -\frac{b}{a}
$$

### Quadratic Formula

For $ax^2 + bx + c = 0$:

$$
x = \frac{-b \pm \sqrt{b^2 - 4ac}}{2a}
$$

### Systems of Linear Equations

Matrix form: $A\mathbf{x} = \mathbf{b}$

Solve via Gaussian elimination, Cramer's rule, or matrix inversion ($\mathbf{x} = A^{-1}\mathbf{b}$).

## In Java: Gaussian Elimination

```java
public static double[] solveLinearSystem(double[][] A, double[] b) {
    int n = b.length;
    double[][] augmented = new double[n][n + 1];
    for (int i = 0; i < n; i++) {
        System.arraycopy(A[i], 0, augmented[i], 0, n);
        augmented[i][n] = b[i];
    }
    // Forward elimination
    for (int col = 0; col < n; col++) {
        for (int row = col + 1; row < n; row++) {
            double factor = augmented[row][col] / augmented[col][col];
            for (int j = col; j <= n; j++)
                augmented[row][j] -= factor * augmented[col][j];
        }
    }
    // Back substitution
    double[] x = new double[n];
    for (int i = n - 1; i >= 0; i--) {
        double sum = augmented[i][n];
        for (int j = i + 1; j < n; j++)
            sum -= augmented[i][j] * x[j];
        x[i] = sum / augmented[i][i];
    }
    return x;
}
```
