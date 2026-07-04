# Algebra Performance

## Matrix Multiplication Optimization

### Standard $O(n^3)$ vs. Strassen $O(n^{2.81})$

```java
// Standard triple loop (best for small matrices, cache-friendly)
public static double[][] multiplyStandard(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++)
        for (int k = 0; k < n; k++)  // swap j/k for cache locality
            for (int j = 0; j < n; j++)
                C[i][j] += A[i][k] * B[k][j];
    return C;
}
```

### Cache-Blocking (Tiling)

Break matrices into blocks that fit in L1 cache for 10-100x speedup on large matrices.

## Polynomial Evaluation

Horner's method is optimal for sequential evaluation, but for many $x$ values, precompute powers:

```java
// Batch evaluation: precompute powers of x
private double[] powers;
public double evaluateFast(double x) {
    // Precompute x^0, x^1, ..., x^n
    powers[0] = 1;
    for (int i = 1; i <= degree; i++)
        powers[i] = powers[i-1] * x;
    // Dot product
    double sum = 0;
    for (int i = 0; i <= degree; i++)
        sum += coeffs[i] * powers[i];
    return sum;
}
```

## Use `java.util.concurrent` for Large Matrix Ops

Parallelize row operations across CPU cores using `ForkJoinPool` or parallel streams.
