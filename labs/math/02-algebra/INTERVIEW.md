# Interview Questions on Algebra

## Easy

1. Implement `pow(x, n)` — power function.
2. Check if three points are collinear.
3. Rotate a matrix 90 degrees.

## Medium

4. Solve "Valid Sudoku" — constraint satisfaction.
5. Multiply two polynomials represented as linked lists.
6. Find the $k$th smallest element in a sorted matrix.
7. Spiral matrix traversal.

## Hard

8. Solve linear equations with Gaussian elimination.
9. Design a sparse matrix library.
10. Implement Strassen's matrix multiplication.
11. Polynomial multiplication with FFT.

## Java: Power Function

```java
public double pow(double x, int n) {
    if (n == 0) return 1;
    if (n < 0) return 1 / pow(x, -n);
    double half = pow(x, n / 2);
    return (n % 2 == 0) ? half * half : x * half * half;
}
```

## Java: Matrix Rotation

```java
public void rotate(int[][] matrix) {
    int n = matrix.length;
    // Transpose
    for (int i = 0; i < n; i++)
        for (int j = i; j < n; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[j][i];
            matrix[j][i] = temp;
        }
    // Reverse each row
    for (int i = 0; i < n; i++)
        for (int j = 0; j < n / 2; j++) {
            int temp = matrix[i][j];
            matrix[i][j] = matrix[i][n - 1 - j];
            matrix[i][n - 1 - j] = temp;
        }
}
```
