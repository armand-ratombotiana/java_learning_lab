# Problem Walkthrough: Matrix Operations & Multiplication

## Problem Statement

**Interview Problem: Implement a Comprehensive Matrix Operations Library**

Design and implement a `MatrixOps` class that supports fundamental matrix operations for machine learning applications:

1. **Matrix Multiplication** — Standard O(n³) and Strassen's O(n^2.807) algorithms
2. **Transpose** — In-place and copy-based
3. **Determinant** — Using LU decomposition
4. **Inverse** — Using Gauss-Jordan elimination
5. **Trace** — Sum of diagonal elements
6. **Rank** — Using Gaussian elimination with partial pivoting

**Constraints:**
- Matrices represented as `double[][]` (row-major)
- Support square and rectangular matrices where applicable
- Handle singular matrices gracefully (throw `SingularMatrixException`)
- Use partial pivoting for numerical stability
- Tolerance for zero detection: 1e-10

**Example:**
```java
double[][] A = {{1, 2}, {3, 4}};
double[][] B = {{5, 6}, {7, 8}};
double[][] C = MatrixOps.multiply(A, B); // {{19, 22}, {43, 50}}
double det = MatrixOps.determinant(A); // -2.0
double[][] inv = MatrixOps.inverse(A); // {{-2, 1}, {1.5, -0.5}}
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Matrix Definition

An **m × n matrix** A is a rectangular array of elements arranged in m rows and n columns:

A = [aᵢⱼ] for i ∈ {1,...,m}, j ∈ {1,...,n}

#### 1.2 Matrix Multiplication

**Standard definition**: (AB)ᵢⱼ = Σₖ Aᵢₖ · Bₖⱼ

For A ∈ ℝ^{m×p}, B ∈ ℝ^{p×n}, result C ∈ ℝ^{m×n}.

**Properties:**
- Associative: (AB)C = A(BC)
- Distributive: A(B + C) = AB + AC
- Not commutative: AB ≠ BA in general
- Transpose: (AB)^T = B^T A^T

#### 1.3 Strassen's Algorithm

Divide each matrix into 4 quadrants of size n/2:
```
A = | A11  A12 |    B = | B11  B12 |
    | A21  A22 |        | B21  B22 |
```

Compute 7 products instead of 8:
- M₁ = (A11 + A22)(B11 + B22)
- M₂ = (A21 + A22)B11
- M₃ = A11(B12 − B22)
- M₄ = A22(B21 − B11)
- M₅ = (A11 + A12)B22
- M₆ = (A21 − A11)(B11 + B12)
- M₇ = (A12 − A22)(B21 + B22)

Result quadrants:
- C11 = M₁ + M₄ − M₅ + M₇
- C12 = M₃ + M₅
- C21 = M₂ + M₄
- C22 = M₁ − M₂ + M₃ + M₆

**Complexity**: O(n^{log₂7}) ≈ O(n^{2.807}) compared to O(n³) for naive.

#### 1.4 Transpose

(A^T)ᵢⱼ = Aⱼᵢ for i ∈ {1,...,m}, j ∈ {1,...,n}

Properties:
- (A^T)^T = A
- (AB)^T = B^T A^T
- det(A^T) = det(A) for square matrices

#### 1.5 Determinant

For a square matrix A ∈ ℝ^{n×n}:
- det(A) = Σⱼ (−1)^{1+j} · A_{1j} · det(M_{1j}) (Laplace expansion, O(n!))
- Computed efficiently via LU decomposition: det(A) = det(P)·det(L)·det(U) = (−1)^p · Πᵢ Uᵢᵢ

Where p is the number of row swaps in partial pivoting.

#### 1.6 LU Decomposition

Factor A = P·L·U where:
- P: permutation matrix (row reordering)
- L: unit lower triangular (diagonal = 1)
- U: upper triangular

**Doolittle's method**: L has 1s on diagonal. Solve row by row:
- For each column k: compute U[k][j] for j ≥ k, then L[i][k] for i > k

#### 1.7 Inverse via Gauss-Jordan

Augment A with identity: [A | I]
Apply row operations until A becomes I; the right side becomes A^{-1}.

**Row operations:**
1. Swap rows
2. Scale row by non-zero scalar
3. Add multiple of one row to another

**Singularity**: If a zero pivot is encountered with no non-zero below to swap, the matrix is singular (no inverse).

#### 1.8 Rank

Maximum number of linearly independent rows (or columns). Compute via Gaussian elimination with partial pivoting; count non-zero pivots after elimination.

---

### 2. Algorithm Design

#### 2.1 Standard Multiplication — O(m·p·n)

```java
for i in 0..m:
  for j in 0..n:
    sum = 0
    for k in 0..p:
      sum += A[i][k] * B[k][j]
    C[i][j] = sum
```

**Cache optimization:** Loop ordering matters. i-k-j ordering (as above) uses spatial locality for A and C but not B. For large matrices, tiling/blocking improves cache utilization.

#### 2.2 Strassen Multiplication — O(n^{log₂7})

Recursive divide-and-conquer with base case switching to naive at n ≤ 64 (tunable threshold).

**Challenges:**
- Memory overhead from creating submatrices
- Numerical instability for large matrices
- Only works for n that are powers of 2 (pad with zeros)

#### 2.3 Transpose — O(m·n)

Simple double loop swapping indices.

**In-place for square matrix**: swap A[i][j] ↔ A[j][i] for i < j.
**Out-of-place**: create new matrix of size n×m.

#### 2.4 Determinant via LU — O(n³)

1. Factor A = P·L·U with partial pivoting
2. Track number of row swaps p
3. det = (−1)^p × Π U[i][i]
4. If any diagonal of U is zero, det = 0

#### 2.5 Inverse via Gauss-Jordan — O(n³)

1. Create augmented matrix [A | I]
2. For each column k:
   a. Find pivot row (max |A[i][k]| for i ≥ k)
   b. Swap current row with pivot row
   c. Divide entire row by pivot value
   d. Eliminate column k from all other rows
3. Extract inverse from right half

#### 2.6 Rank — O(m·n·min(m,n))

Forward elimination phase only. Count non-zero rows.

---

### 3. Java Implementation

```java
package com.ml.linalg;

import java.util.Objects;

/**
 * Comprehensive matrix operations for machine learning.
 * 
 * <p>Supports multiplication (naive and Strassen), transpose,
 * determinant via LU decomposition, inverse via Gauss-Jordan,
 * trace, and rank computation with partial pivoting.</p>
 * 
 * @since 1.0
 */
public final class MatrixOps {

    public static final double EPSILON = 1e-10;

    /** Threshold below which Strassen switches to naive multiplication. */
    private static final int STRASSEN_THRESHOLD = 64;

    private MatrixOps() {}

    // ==================== MULTIPLICATION ====================

    /**
     * Standard matrix multiplication: C = A × B.
     * 
     * <p>Uses i-k-j loop ordering for cache efficiency.</p>
     * 
     * @param A left matrix of shape m×p
     * @param B right matrix of shape p×n
     * @return product matrix of shape m×n
     * @throws IllegalArgumentException if inner dimensions mismatch
     */
    public static double[][] multiply(double[][] A, double[][] B) {
        validate(A);
        validate(B);
        int m = A.length;
        int p = A[0].length;
        int n = B[0].length;
        if (p != B.length) {
            throw new IllegalArgumentException(
                "Dimension mismatch: A cols=" + p + ", B rows=" + B.length);
        }
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            double[] aRow = A[i];
            double[] cRow = C[i];
            for (int k = 0; k < p; k++) {
                double aik = aRow[k];
                if (aik == 0.0) continue; // skip zero entries
                double[] bRow = B[k];
                for (int j = 0; j < n; j++) {
                    cRow[j] += aik * bRow[j];
                }
            }
        }
        return C;
    }

    /**
     * Strassen's matrix multiplication with O(n^{2.807}) complexity.
     * Falls back to naive for n &le; threshold.
     * Pads matrices to nearest power of 2 if needed.
     */
    public static double[][] strassenMultiply(double[][] A, double[][] B) {
        validate(A);
        validate(B);
        int m = A.length;
        int p = A[0].length;
        int n = B[0].length;
        if (p != B.length) {
            throw new IllegalArgumentException("Dimension mismatch for Strassen");
        }
        // Pad to square power-of-2
        int maxDim = Math.max(Math.max(m, p), n);
        int size = 1;
        while (size < maxDim) {
            size <<= 1;
        }
        double[][] Apad = padMatrix(A, size, size);
        double[][] Bpad = padMatrix(B, size, size);
        double[][] Cpad = strassenRecursive(Apad, Bpad);
        // Extract result
        double[][] C = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(Cpad[i], 0, C[i], 0, n);
        }
        return C;
    }

    private static double[][] strassenRecursive(double[][] A, double[][] B) {
        int n = A.length;
        if (n <= STRASSEN_THRESHOLD) {
            return multiply(A, B);
        }
        if (!isPowerOfTwo(n)) {
            int newSize = nextPowerOfTwo(n);
            A = padMatrix(A, newSize, newSize);
            B = padMatrix(B, newSize, newSize);
            n = newSize;
        }
        int k = n / 2;

        double[][] A11 = submatrix(A, 0, 0, k, k);
        double[][] A12 = submatrix(A, 0, k, k, k);
        double[][] A21 = submatrix(A, k, 0, k, k);
        double[][] A22 = submatrix(A, k, k, k, k);

        double[][] B11 = submatrix(B, 0, 0, k, k);
        double[][] B12 = submatrix(B, 0, k, k, k);
        double[][] B21 = submatrix(B, k, 0, k, k);
        double[][] B22 = submatrix(B, k, k, k, k);

        // 7 Strassen products
        double[][] M1 = strassenRecursive(add(A11, A22), add(B11, B22));
        double[][] M2 = strassenRecursive(add(A21, A22), B11);
        double[][] M3 = strassenRecursive(A11, sub(B12, B22));
        double[][] M4 = strassenRecursive(A22, sub(B21, B11));
        double[][] M5 = strassenRecursive(add(A11, A12), B22);
        double[][] M6 = strassenRecursive(sub(A21, A11), add(B11, B12));
        double[][] M7 = strassenRecursive(sub(A12, A22), add(B21, B22));

        // Combine results
        double[][] C11 = add(sub(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(sub(add(M1, M3), M2), M6);

        return compose(C11, C12, C21, C22);
    }

    // ==================== TRANSPOSE ====================

    /**
     * Computes the transpose: A^T where (A^T)[j][i] = A[i][j].
     */
    public static double[][] transpose(double[][] A) {
        validate(A);
        int m = A.length;
        int n = A[0].length;
        double[][] T = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                T[j][i] = A[i][j];
            }
        }
        return T;
    }

    /**
     * In-place transpose for square matrices.
     */
    public static void transposeInPlace(double[][] A) {
        validate(A);
        int n = A.length;
        if (A[0].length != n) {
            throw new IllegalArgumentException("In-place transpose requires square matrix");
        }
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double tmp = A[i][j];
                A[i][j] = A[j][i];
                A[j][i] = tmp;
            }
        }
    }

    // ==================== TRACE ====================

    /**
     * Trace: sum of diagonal elements tr(A) = Σᵢ A[i][i].
     */
    public static double trace(double[][] A) {
        validate(A);
        int m = A.length;
        int n = A[0].length;
        int min = Math.min(m, n);
        double sum = 0.0;
        for (int i = 0; i < min; i++) {
            sum += A[i][i];
        }
        return sum;
    }

    // ==================== LU DECOMPOSITION ====================

    /**
     * Computes PA = LU with partial pivoting.
     * 
     * @param A input matrix (square)
     * @param L output unit lower triangular (set to null to allocate)
     * @param U output upper triangular
     * @param P output permutation matrix
     * @return number of row swaps modulo 2
     */
    public static int luDecomposition(double[][] A, double[][] L, double[][] U, double[][] P) {
        int n = A.length;
        // Initialize
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                U[i][j] = A[i][j];
                L[i][j] = (i == j) ? 1.0 : 0.0;
                P[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        int swaps = 0;
        for (int k = 0; k < n - 1; k++) {
            // Partial pivoting
            int pivot = k;
            double maxVal = Math.abs(U[k][k]);
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(U[i][k]) > maxVal) {
                    maxVal = Math.abs(U[i][k]);
                    pivot = i;
                }
            }
            if (maxVal < EPSILON) continue; // singular
            if (pivot != k) {
                swapRows(U, k, pivot);
                swapRows(P, k, pivot);
                swaps++;
                // Swap L rows up to column k-1 (already triangular)
                for (int j = 0; j < k; j++) {
                    double tmp = L[k][j];
                    L[k][j] = L[pivot][j];
                    L[pivot][j] = tmp;
                }
            }
            // Elimination
            for (int i = k + 1; i < n; i++) {
                L[i][k] = U[i][k] / U[k][k];
                for (int j = k; j < n; j++) {
                    U[i][j] -= L[i][k] * U[k][j];
                }
            }
        }
        return swaps;
    }

    // ==================== DETERMINANT ====================

    /**
     * Determinant via LU decomposition.
     * det(A) = (-1)^p × Πᵢ U[i][i]
     */
    public static double determinant(double[][] A) {
        validateSquare(A);
        int n = A.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];
        double[][] P = new double[n][n];
        int swaps = luDecomposition(A, L, U, P);
        double det = (swaps % 2 == 0) ? 1.0 : -1.0;
        for (int i = 0; i < n; i++) {
            det *= U[i][i];
            if (Math.abs(U[i][i]) < EPSILON) return 0.0;
        }
        return det;
    }

    // ==================== INVERSE ====================

    /**
     * Matrix inverse via Gauss-Jordan elimination.
     * 
     * @throws SingularMatrixException if the matrix is singular
     */
    public static double[][] inverse(double[][] A) {
        validateSquare(A);
        int n = A.length;
        // Augmented matrix [A | I]
        double[][] aug = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                aug[i][j] = A[i][j];
            }
            aug[i][n + i] = 1.0;
        }

        for (int k = 0; k < n; k++) {
            // Partial pivoting
            int pivot = k;
            double maxVal = Math.abs(aug[k][k]);
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(aug[i][k]) > maxVal) {
                    maxVal = Math.abs(aug[i][k]);
                    pivot = i;
                }
            }
            if (maxVal < EPSILON) {
                throw new SingularMatrixException("Matrix is singular");
            }
            if (pivot != k) {
                swapRows(aug, k, pivot);
            }
            // Scale pivot row
            double pivotVal = aug[k][k];
            for (int j = k; j < 2 * n; j++) {
                aug[k][j] /= pivotVal;
            }
            // Eliminate column k from all other rows
            for (int i = 0; i < n; i++) {
                if (i == k) continue;
                double factor = aug[i][k];
                for (int j = k; j < 2 * n; j++) {
                    aug[i][j] -= factor * aug[k][j];
                }
            }
        }

        // Extract inverse
        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(aug[i], n, inv[i], 0, n);
        }
        return inv;
    }

    // ==================== RANK ====================

    /**
     * Rank via Gaussian elimination with partial pivoting.
     */
    public static int rank(double[][] A) {
        validate(A);
        int m = A.length;
        int n = A[0].length;
        double[][] copy = new double[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(A[i], 0, copy[i], 0, n);
        }

        int rank = 0;
        for (int col = 0, row = 0; col < n && row < m; col++) {
            // Find pivot
            int pivot = row;
            double maxVal = Math.abs(copy[row][col]);
            for (int i = row + 1; i < m; i++) {
                if (Math.abs(copy[i][col]) > maxVal) {
                    maxVal = Math.abs(copy[i][col]);
                    pivot = i;
                }
            }
            if (maxVal < EPSILON) continue;
            if (pivot != row) {
                swapRows(copy, row, pivot);
            }
            // Eliminate below
            for (int i = row + 1; i < m; i++) {
                double factor = copy[i][col] / copy[row][col];
                for (int j = col; j < n; j++) {
                    copy[i][j] -= factor * copy[row][j];
                }
            }
            rank++;
            row++;
        }
        return rank;
    }

    // ==================== HELPER METHODS ====================

    private static double[][] add(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] + B[i][j];
            }
        }
        return C;
    }

    private static double[][] sub(double[][] A, double[][] B) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C[i][j] = A[i][j] - B[i][j];
            }
        }
        return C;
    }

    private static double[][] submatrix(double[][] A, int row, int col, int rows, int cols) {
        double[][] sub = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(A[row + i], col, sub[i], 0, cols);
        }
        return sub;
    }

    private static double[][] compose(double[][] C11, double[][] C12,
                                       double[][] C21, double[][] C22) {
        int k = C11.length;
        int n = 2 * k;
        double[][] C = new double[n][n];
        for (int i = 0; i < k; i++) {
            System.arraycopy(C11[i], 0, C[i], 0, k);
            System.arraycopy(C12[i], 0, C[i], k, k);
        }
        for (int i = 0; i < k; i++) {
            System.arraycopy(C21[i], 0, C[k + i], 0, k);
            System.arraycopy(C22[i], 0, C[k + i], k, k);
        }
        return C;
    }

    private static double[][] padMatrix(double[][] A, int newRows, int newCols) {
        double[][] P = new double[newRows][newCols];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, P[i], 0, A[0].length);
        }
        return P;
    }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    private static int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    private static void swapRows(double[][] M, int i, int j) {
        double[] tmp = M[i];
        M[i] = M[j];
        M[j] = tmp;
    }

    private static void validate(double[][] A) {
        Objects.requireNonNull(A, "Matrix must not be null");
        if (A.length == 0) {
            throw new IllegalArgumentException("Matrix must have at least one row");
        }
        for (double[] row : A) {
            Objects.requireNonNull(row, "Matrix row must not be null");
            if (A[0] != null && row.length != A[0].length) {
                throw new IllegalArgumentException("Inconsistent row lengths");
            }
        }
    }

    private static void validateSquare(double[][] A) {
        validate(A);
        if (A.length != A[0].length) {
            throw new IllegalArgumentException("Matrix must be square");
        }
    }

    /**
     * Exception thrown when attempting to invert a singular matrix.
     */
    public static class SingularMatrixException extends RuntimeException {
        public SingularMatrixException(String message) {
            super(message);
        }
    }
}
```

---

### 4. Test Cases

```java
package com.ml.linalg;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatrixOpsTest {

    private static final double DELTA = 1e-9;

    @Test
    void testMultiply() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] B = {{5, 6}, {7, 8}};
        double[][] C = MatrixOps.multiply(A, B);
        assertArrayEquals(new double[]{19, 22}, C[0], DELTA);
        assertArrayEquals(new double[]{43, 50}, C[1], DELTA);
    }

    @Test
    void testMultiplyRectangular() {
        double[][] A = {{1, 2, 3}, {4, 5, 6}}; // 2×3
        double[][] B = {{7, 8}, {9, 10}, {11, 12}}; // 3×2
        double[][] C = MatrixOps.multiply(A, B); // 2×2
        // C[0][0] = 1·7 + 2·9 + 3·11 = 58
        assertEquals(58.0, C[0][0], DELTA);
        assertEquals(64.0, C[0][1], DELTA);
        assertEquals(139.0, C[1][0], DELTA);
        assertEquals(154.0, C[1][1], DELTA);
    }

    @Test
    void testStrassenMultiply() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] B = {{5, 6}, {7, 8}};
        double[][] C_naive = MatrixOps.multiply(A, B);
        double[][] C_strassen = MatrixOps.strassenMultiply(A, B);
        for (int i = 0; i < 2; i++) {
            assertArrayEquals(C_naive[i], C_strassen[i], DELTA);
        }
    }

    @Test
    void testTranspose() {
        double[][] A = {{1, 2, 3}, {4, 5, 6}};
        double[][] T = MatrixOps.transpose(A);
        assertEquals(3, T.length);
        assertEquals(2, T[0].length);
        assertEquals(1, T[0][0], DELTA);
        assertEquals(4, T[0][1], DELTA);
        assertEquals(2, T[1][0], DELTA);
        assertEquals(6, T[2][1], DELTA);
    }

    @Test
    void testTransposeInPlace() {
        double[][] A = {{1, 2}, {3, 4}};
        MatrixOps.transposeInPlace(A);
        assertEquals(1, A[0][0], DELTA);
        assertEquals(3, A[0][1], DELTA);
        assertEquals(2, A[1][0], DELTA);
        assertEquals(4, A[1][1], DELTA);
    }

    @Test
    void testTrace() {
        double[][] A = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        assertEquals(15.0, MatrixOps.trace(A), DELTA);
        assertEquals(5.0, MatrixOps.trace(new double[][]{{1, 2}, {3, 4}}), DELTA);
    }

    @Test
    void testDeterminant() {
        assertEquals(-2.0, MatrixOps.determinant(new double[][]{{1, 2}, {3, 4}}), DELTA);
        assertEquals(0.0, MatrixOps.determinant(new double[][]{{1, 2}, {2, 4}}), DELTA);
        assertEquals(1.0, MatrixOps.determinant(new double[][]{{1, 0}, {0, 1}}), DELTA);
        double det = MatrixOps.determinant(
            new double[][]{{2, 4, 5}, {1, 2, 3}, {3, 7, 8}});
        assertEquals(-1.0, det, DELTA);
    }

    @Test
    void testInverse() {
        double[][] A = {{1, 2}, {3, 4}};
        double[][] inv = MatrixOps.inverse(A);
        double[][] I = MatrixOps.multiply(A, inv);
        assertEquals(1.0, I[0][0], DELTA);
        assertEquals(0.0, I[0][1], DELTA);
        assertEquals(0.0, I[1][0], DELTA);
        assertEquals(1.0, I[1][1], DELTA);
    }

    @Test
    void testInverseSingular() {
        assertThrows(MatrixOps.SingularMatrixException.class,
            () -> MatrixOps.inverse(new double[][]{{1, 2}, {2, 4}}));
    }

    @Test
    void testRank() {
        assertEquals(2, MatrixOps.rank(new double[][]{{1, 2}, {3, 4}}));
        assertEquals(1, MatrixOps.rank(new double[][]{{1, 2}, {2, 4}}));
        assertEquals(0, MatrixOps.rank(new double[][]{{0, 0}, {0, 0}}));
        double[][] rectangular = {{1, 2, 3}, {4, 5, 6}};
        assertEquals(2, MatrixOps.rank(rectangular));
    }

    @Test
    void testIdentityTimesInverse() {
        double[][] A = {{4, 7}, {2, 6}};
        double[][] inv = MatrixOps.inverse(A);
        double[][] product = MatrixOps.multiply(inv, A);
        assertEquals(1.0, product[0][0], DELTA);
        assertEquals(0.0, product[0][1], DELTA);
        assertEquals(0.0, product[1][0], DELTA);
        assertEquals(1.0, product[1][1], DELTA);
    }
}
```

---

### 5. Complexity Analysis

**Time Complexity:**

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| Standard Multiply | O(m·p·n) | For m×p × p×n |
| Strassen | O(n^{2.807}) | Square matrices, recursive |
| Transpose | O(m·n) | Single pass |
| Transpose In-Place | O(n²) | Square only |
| Determinant (LU) | O(n³) | LU decomposition |
| Inverse (Gauss-Jordan) | O(n³) | 2n × n augmented matrix |
| Rank | O(m·n·min(m,n)) | Gaussian elimination |
| Trace | O(min(m,n)) | Diagonal only |

**Space Complexity:**

| Operation | Auxiliary Space |
|-----------|----------------|
| Standard Multiply | O(m·n) result |
| Strassen | O(n² log n) recursion |
| Transpose | O(m·n) result |
| LU Decomposition | O(n²) for L, U, P |
| Inverse | O(n²) augmented + result |
| Rank | O(m·n) copy |

**Numerical Stability:**

- **Partial pivoting** prevents division by small pivots, bounding error growth.
- LU decomposition has backward error of O(n·ε·‖A‖) with partial pivoting.
- Strassen is less stable than naive (relative error ≈ (log₂ n)·ε).
- For ill-conditioned matrices (large condition number), results degrade gracefully.

---

### 6. Follow-Up Questions

**Q1: Compare Strassen vs naive multiplication. When would you use each?**

Strassen is asymptotically faster (O(n^{2.807}) vs O(n³)) but:
- Higher constant factors (matrix creation overhead)
- Less numerically stable
- Requires square matrices, ideally power-of-2

Use naive for: n < 64, non-square matrices, where numerical accuracy is critical.
Use Strassen for: large square matrices (n > 1024), where speed is prioritized.

Modern BLAS implementations use naive for n ≤ 512 and a hybrid approach (blocked + Strassen) for larger matrices. The crossover point varies by hardware due to cache hierarchy.

**Q2: How does cache friendliness affect matrix multiplication performance?**

Matrix multiplication is memory-bound for moderate sizes. Key optimization strategies:

- **Loop tiling (blocking)**: Process sub-blocks that fit in L1/L2 cache
- **Loop ordering**: i-k-j performs best (A read sequentially, B read repeatedly)
- **SIMD vectorization**: Process multiple floats/cycle
- **Prefetching**: Software prefetch of upcoming cache lines

Typical speedups from cache optimization: 5-10× over naive triple loop.

**Q3: What is the condition number and why does it matter for inverse?**

cond(A) = ‖A‖·‖A^{-1}‖

A large condition number means A is ill-conditioned: small perturbations in input cause large changes in output. For solving Ax = b, the relative error in x is bounded by cond(A) × (relative error in A and b).

Matrices with condition number > 1/√(machine_epsilon) are effectively singular. For double precision (ε ≈ 2.2e-16), cond > 1e8 indicates serious numerical issues.

**Q4: How would you implement block matrix multiplication?**

Divide matrices into blocks that fit in L2 cache:
```
For each block row i:
  For each block col j:
    For each block k:
      C[i][j] += A[i][k] × B[k][j]  // block multiply
```

Block size tuned to cache size (typically 64-256 for modern CPUs). Each block multiplication uses the standard triple loop for good cache locality.

**Q5: What is the Sherman-Morrison formula?**

For rank-1 updates: (A + uv^T)^{-1} = A^{-1} − (A^{-1}uv^T A^{-1}) / (1 + v^T A^{-1}u)

This allows incremental updates to the inverse in O(n²) instead of recomputing from scratch in O(n³). Useful in recursive least squares and online learning.

**Q6: How does Gaussian elimination with partial pivoting ensure numerical stability?**

Without pivoting, dividing by a small pivot amplifies rounding errors catastrophically. Partial pivoting selects the row with the largest |U[i][k]| as the pivot, ensuring the multiplier L[i][k] = U[i][k]/U[k][k] ≤ 1. This bounds the growth of elements in U (Wilkinson's backward error analysis).

**Q7: Describe the relationship between determinant, volume, and linear independence.**

|det(A)| = volume of the parallelepiped spanned by the columns of A.

- det(A) = 0 → columns linearly dependent → zero volume → matrix singular
- |det(A)| = 1 → unit volume → matrix is orthogonal (up to scaling)

This geometric interpretation explains why determinant appears in change-of-variables for probability density functions and in multivariate calculus.

---

### 7. Applications in Machine Learning

- **Linear regression**: β = (X^T X)^{-1} X^T y (normal equations)
- **PCA**: Eigenvalue decomposition of covariance matrix Σ = (1/n)X^T X
- **Neural networks**: Forward pass is repeated matrix multiplication
- **Attention mechanisms**: Attention(Q,K,V) = softmax(QK^T / √dₖ)V
- **Recommendation systems**: Matrix factorization for collaborative filtering
