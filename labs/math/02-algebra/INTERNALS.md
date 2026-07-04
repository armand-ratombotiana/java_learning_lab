# Internals of Algebra

## Polynomial Representation

A polynomial $P(x) = a_n x^n + a_{n-1} x^{n-1} + \dots + a_0$ is stored as coefficient array `[a_0, a_1, ..., a_n]` (ascending degree).

```java
public class Polynomial {
    private final double[] coeffs; // coeffs[k] = coefficient of x^k

    public Polynomial(double[] coeffs) {
        this.coeffs = coeffs.clone();
    }

    public double evaluate(double x) {
        // Horner's method: O(n)
        double result = 0;
        for (int i = coeffs.length - 1; i >= 0; i--)
            result = result * x + coeffs[i];
        return result;
    }
}
```

## Matrix Multiplication

$C_{ij} = \sum_{k=1}^{n} A_{ik} B_{kj}$

```java
public static double[][] multiply(double[][] A, double[][] B) {
    int m = A.length, n = B[0].length, p = B.length;
    double[][] C = new double[m][n];
    for (int i = 0; i < m; i++)
        for (int k = 0; k < p; k++)
            for (int j = 0; j < n; j++)
                C[i][j] += A[i][k] * B[k][j];
    return C;
}
```

## Polynomial Multiplication (Convolution)

$(A \cdot B)_k = \sum_{i=0}^{k} a_i b_{k-i}$

Optimized with FFT in $O(n \log n)$.
