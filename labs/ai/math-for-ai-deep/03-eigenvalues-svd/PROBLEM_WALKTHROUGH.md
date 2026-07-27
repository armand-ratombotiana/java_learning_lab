# Problem Walkthrough: Eigenvalue Decomposition & SVD

## Problem Statement

**Interview Problem: Implement Power Iteration for Eigenvalues and a Simplified SVD**

You are building a dimensionality reduction library. Implement algorithms to compute eigenvalues, eigenvectors, and singular values of matrices:

1. **Power Iteration** — Find the dominant eigenvalue and its eigenvector
2. **Rayleigh Quotient Iteration** — Accelerated eigenvalue convergence
3. **Deflation** — Find all eigenvalues by removing known eigenvectors
4. **Simplified SVD** — Compute singular values via eigenvalue decomposition of A^T A

**Constraints:**
- Real, square, diagonalizable matrices
- Handle symmetric matrices (guaranteed real eigenvalues)
- Maximum iterations: 1000 (power iteration), 100 (Rayleigh)
- Convergence tolerance: 1e-10
- Detect and report non-convergence

**Example:**
```java
double[][] A = {{4, 1}, {1, 3}};
var eigen = EigenDecomposition.powerIteration(A);
// Dominant eigenvalue ≈ 5.0 (exact: (7+√5)/2)
// Eigenvector ≈ [0.8507, 0.5257]

double[][] B = {{2, 0}, {0, 3}};
double[] svals = SVD.computeSingularValues(B); // {3, 2}
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Eigenvalue Definition

For a square matrix A ∈ ℝ^{n×n}, λ is an eigenvalue and v ≠ 0 is the corresponding eigenvector if:

A·v = λ·v

**Characteristic polynomial**: p(λ) = det(A − λI) = 0

**Properties:**
- tr(A) = Σᵢ λᵢ
- det(A) = Πᵢ λᵢ
- If A is symmetric: λᵢ ∈ ℝ, eigenvectors are orthogonal
- (A − λI) is singular (has non-trivial null space)

#### 1.2 Power Iteration

Finds the **dominant eigenvalue** (largest |λ|) and its eigenvector.

**Algorithm:**
1. Start with random unit vector v⁰
2. For k = 1, 2, ...:
   - w = A·v^{(k-1)}
   - v^k = w / ‖w‖
   - λ^k = (v^k)^T A v^k (Rayleigh quotient)
3. Stop when |λ^k − λ^{(k-1)}| < ε or ‖A·v^k − λ^k·v^k‖ < ε

**Convergence rate**: O(|λ₂/λ₁|^k) where λ₁ is dominant, λ₂ is second-largest.

#### 1.3 Rayleigh Quotient

For any vector x ≠ 0:
R(x) = (x^T A x) / (x^T x)

**Properties:**
- R(x) ∈ [λ_min, λ_max] for symmetric A
- At the eigenvector, R(vᵢ) = λᵢ
- ∇R(x) = 0 at eigenvectors (stationary points)

#### 1.4 Rayleigh Quotient Iteration

Accelerates convergence by updating the shift at each step:
1. Start with initial guess μ⁰
2. For k = 1, 2, ...:
   - Solve (A − μ^{(k-1)}I)·w = v^{(k-1)}
   - v^k = w / ‖w‖
   - μ^k = R(v^k)
3. Cubic convergence for symmetric matrices

#### 1.5 Deflation (Hotelling's)

To find subsequent eigenvalues after finding λ₁, v₁:

**Wielandt deflation**: A' = A − λ₁·v₁·v₁^T

This shifts the known eigenvalue to 0 while preserving other eigenvalues and eigenvectors.

#### 1.6 Singular Value Decomposition (SVD)

For any A ∈ ℝ^{m×n} (m ≥ n):
A = U·Σ·V^T

Where:
- U ∈ ℝ^{m×m}: left singular vectors (orthonormal columns)
- Σ ∈ ℝ^{m×n}: diagonal with σ₁ ≥ σ₂ ≥ ... ≥ σᵣ ≥ 0 (singular values)
- V ∈ ℝ^{n×n}: right singular vectors (orthonormal columns)

**Relationship to eigenvalues:**
- σᵢ² = λᵢ(A^T A) = λᵢ(A A^T)
- Columns of V are eigenvectors of A^T A
- Columns of U are eigenvectors of A A^T

#### 1.7 Simplified SVD Computation

For A ∈ ℝ^{m×n}:
1. Compute B = A^T A (n×n symmetric positive semi-definite)
2. Find eigenvalues λᵢ and eigenvectors vᵢ of B
3. σᵢ = √λᵢ
4. uᵢ = A·vᵢ / σᵢ

---

### 2. Algorithm Design

#### 2.1 Power Iteration

**Input**: A (n×n), maxIter, tolerance
**Output**: (λ, v)

```
v = randomUnitVector(n)
λ = 0
for iter in 1..maxIter:
  w = A·v
  v_new = w / ‖w‖₂
  λ_new = v_new^T · (A·v_new)
  if |λ_new − λ| < tol:
    break
  v = v_new
  λ = λ_new
return (λ, v)
```

**Numerical consideration**: If A·v converges to 0 (rare for random initial vectors), the matrix may have λ = 0 dominant.

#### 2.2 Rayleigh Quotient Iteration

```
v = randomUnitVector(n)
μ = R(v)
for iter in 1..maxIter:
  Solve (A − μI)·w = v  // linear system
  v = w / ‖w‖₂
  μ_new = R(v)
  if |μ_new − μ| < tol:
    break
  μ = μ_new
return (μ, v)
```

The linear solve (A − μI)·w = v costs O(n³). Since we need to solve at each step, this is expensive for large matrices. For tridiagonal matrices (as after Hessenberg reduction), solve is O(n).

#### 2.3 Deflation

Given (λ₁, v₁):
```
A' = A − λ₁·v₁·v₁^T
```

Apply power iteration on A' to find λ₂, v₂.

#### 2.4 SVD

```
B = A^T · A  // n×n, O(mn²)
allEigenvalues = computeAllEigenvalues(B)
for i in 0..n-1:
  σᵢ = √(max(0, λᵢ))
  vᵢ = eigenvector of B for λᵢ
  uᵢ = A·vᵢ / σᵢ
```

---

### 3. Java Implementation

```java
package com.ml.linalg;

import java.util.Arrays;
import java.util.Objects;

/**
 * Eigenvalue decomposition and Singular Value Decomposition utilities.
 * 
 * <p>Provides power iteration (dominant eigenvalue), Rayleigh quotient
 * iteration (accelerated convergence), deflation for finding multiple
 * eigenvalues, and simplified SVD via A^T A eigenvalue decomposition.</p>
 * 
 * @since 1.0
 */
public final class EigenDecomposition {

    /** Convergence tolerance. */
    public static final double EPSILON = 1e-10;

    /** Maximum iterations for power iteration. */
    private static final int MAX_POWER_ITER = 1000;

    /** Maximum iterations for Rayleigh quotient iteration. */
    private static final int MAX_RAYLEIGH_ITER = 100;

    /** Small value to avoid singular SVD division. */
    private static final double MIN_SINGULAR_VALUE = 1e-14;

    private EigenDecomposition() {}

    /**
     * Result container for eigenvalue-eigenvector pairs.
     */
    public static class EigenPair {
        private final double eigenvalue;
        private final double[] eigenvector;

        public EigenPair(double eigenvalue, double[] eigenvector) {
            this.eigenvalue = eigenvalue;
            this.eigenvector = Objects.requireNonNull(eigenvector);
        }

        public double eigenvalue() { return eigenvalue; }
        public double[] eigenvector() { return eigenvector; }
    }

    /**
     * Computes the dominant eigenvalue and eigenvector using power iteration.
     * 
     * <p>Power iteration converges to the eigenvalue with largest absolute
     * value at rate O(|λ₂/λ₁|^k). For symmetric matrices, the Rayleigh
     * quotient gives a quadratic approximation.</p>
     * 
     * @param A square matrix
     * @return EigenPair containing the dominant eigenvalue and unit eigenvector
     * @throws IllegalArgumentException if A is not square
     * @throws ConvergenceException if the method does not converge
     */
    public static EigenPair powerIteration(double[][] A) {
        validateSquare(A);
        int n = A.length;
        double[] v = randomUnitVector(n);
        double lambda = 0.0;

        for (int iter = 0; iter < MAX_POWER_ITER; iter++) {
            // w = A·v
            double[] w = multiplyMatrixVector(A, v);
            double normW = l2Norm(w);
            if (normW < EPSILON) {
                throw new ConvergenceException(
                    "Power iteration converged to zero vector at iteration " + iter);
            }
            // Normalize
            double[] vNew = scale(w, 1.0 / normW);
            // Rayleigh quotient for eigenvalue estimate
            double lambdaNew = rayleighQuotient(A, vNew);

            if (Math.abs(lambdaNew - lambda) < EPSILON) {
                return new EigenPair(lambdaNew, vNew);
            }
            v = vNew;
            lambda = lambdaNew;
        }
        throw new ConvergenceException(
            "Power iteration failed to converge within " + MAX_POWER_ITER + " iterations");
    }

    /**
     * Computes eigenvalue/eigenvector using Rayleigh quotient iteration.
     * 
     * <p>This method has cubic convergence for symmetric matrices but requires
     * solving a linear system at each iteration (O(n³)).</p>
     */
    public static EigenPair rayleighQuotientIteration(double[][] A) {
        validateSquare(A);
        int n = A.length;
        double[] v = randomUnitVector(n);
        double mu = rayleighQuotient(A, v);

        for (int iter = 0; iter < MAX_RAYLEIGH_ITER; iter++) {
            // Solve (A - μI)·w = v
            double[][] shifted = new double[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    shifted[i][j] = A[i][j];
                }
                shifted[i][i] -= mu;
            }
            double[] w = solveLinearSystem(shifted, v);
            double normW = l2Norm(w);
            if (normW < EPSILON) {
                // v is already the eigenvector
                return new EigenPair(mu, v);
            }
            double[] vNew = scale(w, 1.0 / normW);
            double muNew = rayleighQuotient(A, vNew);

            if (Math.abs(muNew - mu) < EPSILON) {
                return new EigenPair(muNew, vNew);
            }
            v = vNew;
            mu = muNew;
        }
        throw new ConvergenceException(
            "Rayleigh quotient iteration failed to converge");
    }

    /**
     * Finds all eigenvalues and eigenvectors of a symmetric matrix via
     * power iteration with deflation.
     * 
     * <p>Uses Wielandt deflation: after finding (λ₁, v₁), the matrix
     * is deflated to A' = A − λ₁·v₁·v₁^T, removing the known eigenvalue.</p>
     */
    public static EigenPair[] allEigenvalues(double[][] A) {
        validateSquare(A);
        int n = A.length;
        EigenPair[] results = new EigenPair[n];
        double[][] current = copyMatrix(A);

        for (int k = 0; k < n; k++) {
            try {
                results[k] = powerIteration(current);
            } catch (ConvergenceException e) {
                // Remaining eigenvalues may be complex or close together
                break;
            }
            // Deflate: A' = A − λ·v·v^T
            double lambda = results[k].eigenvalue();
            double[] v = results[k].eigenvector();
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    current[i][j] -= lambda * v[i] * v[j];
                }
            }
        }
        return results;
    }

    /**
     * Computes the Rayleigh quotient R(x) = (x^T A x) / (x^T x).
     * 
     * <p>For symmetric A, the Rayleigh quotient is bounded between
     * the minimum and maximum eigenvalues of A.</p>
     */
    public static double rayleighQuotient(double[][] A, double[] x) {
        double[] Ax = multiplyMatrixVector(A, x);
        double num = dotProduct(x, Ax);
        double den = dotProduct(x, x);
        if (den < EPSILON) {
            throw new ArithmeticException("Zero vector in Rayleigh quotient");
        }
        return num / den;
    }

    // ==================== SVD ====================

    /**
     * Simplified SVD via eigenvalue decomposition of A^T A.
     * 
     * <p>For A ∈ ℝ^{m×n}, computes σᵢ = √(λᵢ(A^T A)) and the
     * corresponding singular vectors.</p>
     * 
     * @param A input matrix (m ≥ n)
     * @return SVDResult containing U, S, V^T
     */
    public static SVDResult svd(double[][] A) {
        validate(A);
        int m = A.length;
        int n = A[0].length;
        if (m < n) {
            // For tall matrices, compute SVD of A^T and transpose
            SVDResult result = svd(MatrixOps.transpose(A));
            return new SVDResult(result.V, result.S, result.U);
        }

        // B = A^T A (n×n symmetric positive semi-definite)
        double[][] AT = MatrixOps.transpose(A);
        double[][] B = MatrixOps.multiply(AT, A);

        // Find all eigenvalues/vectors of B
        EigenPair[] eigenPairs = allEigenvalues(B);

        // Sort eigenvalues descending
        Arrays.sort(eigenPairs, (a, b) ->
            Double.compare(b.eigenvalue(), a.eigenvalue()));

        int rank = 0;
        for (EigenPair ep : eigenPairs) {
            if (ep != null && ep.eigenvalue() > EPSILON) rank++;
            else break;
        }

        double[] S = new double[rank];
        double[][] U = new double[m][rank];
        double[][] V = new double[n][rank];

        int idx = 0;
        for (int i = 0; i < n && idx < rank; i++) {
            if (eigenPairs[i] == null) break;
            double sigma = Math.sqrt(Math.max(0.0, eigenPairs[i].eigenvalue()));
            if (sigma < MIN_SINGULAR_VALUE) break;

            S[idx] = sigma;
            double[] v = eigenPairs[i].eigenvector();
            for (int j = 0; j < n; j++) {
                V[j][idx] = v[j];
            }
            // u = A·v / σ
            double[] u = multiplyMatrixVector(A, v);
            u = scale(u, 1.0 / sigma);
            for (int j = 0; j < m; j++) {
                U[j][idx] = u[j];
            }
            idx++;
        }

        // Truncate to rank
        if (idx < rank) {
            S = Arrays.copyOf(S, idx);
            U = Arrays.copyOf(U, idx);
            V = Arrays.copyOf(V, idx);
        }

        return new SVDResult(U, S, V);
    }

    /**
     * Result container for SVD: A = U·diag(S)·V^T.
     */
    public static class SVDResult {
        private final double[][] U;
        private final double[] S;
        private final double[][] V;

        public SVDResult(double[][] U, double[] S, double[][] V) {
            this.U = U;
            this.S = S;
            this.V = V;
        }

        public double[][] U() { return U; }
        public double[] S() { return S; }
        public double[][] V() { return V; }

        /**
         * Reconstructs the original matrix: A = U·diag(S)·V^T.
         */
        public double[][] reconstruct() {
            int m = U.length;
            int n = V.length;
            int k = S.length;
            double[][] A = new double[m][n];
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    double sum = 0.0;
                    for (int s = 0; s < k; s++) {
                        sum += U[i][s] * S[s] * V[j][s];
                    }
                    A[i][j] = sum;
                }
            }
            return A;
        }
    }

    /**
     * Exception for convergence failures.
     */
    public static class ConvergenceException extends RuntimeException {
        public ConvergenceException(String message) {
            super(message);
        }
    }

    // ==================== HELPER METHODS ====================

    private static double[] randomUnitVector(int n) {
        double[] v = new double[n];
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            v[i] = Math.random() * 2.0 - 1.0;
            sum += v[i] * v[i];
        }
        double norm = Math.sqrt(sum);
        for (int i = 0; i < n; i++) {
            v[i] /= norm;
        }
        return v;
    }

    private static double[] multiplyMatrixVector(double[][] A, double[] x) {
        int n = A.length;
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            double[] row = A[i];
            for (int j = 0; j < n; j++) {
                sum += row[j] * x[j];
            }
            y[i] = sum;
        }
        return y;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double l2Norm(double[] v) {
        double sum = 0.0;
        for (double vi : v) {
            sum += vi * vi;
        }
        return Math.sqrt(sum);
    }

    private static double[] scale(double[] v, double s) {
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] * s;
        }
        return result;
    }

    /**
     * Solves the linear system A·x = b using Gaussian elimination
     * with partial pivoting. Assumes A is non-singular.
     */
    private static double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        double[][] aug = new double[n][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, aug[i], 0, n);
            aug[i][n] = b[i];
        }

        for (int k = 0; k < n; k++) {
            int pivot = k;
            double maxVal = Math.abs(aug[k][k]);
            for (int i = k + 1; i < n; i++) {
                if (Math.abs(aug[i][k]) > maxVal) {
                    maxVal = Math.abs(aug[i][k]);
                    pivot = i;
                }
            }
            if (maxVal < EPSILON) continue;
            if (pivot != k) {
                double[] tmp = aug[k];
                aug[k] = aug[pivot];
                aug[pivot] = tmp;
            }
            double pivotVal = aug[k][k];
            for (int j = k; j <= n; j++) {
                aug[k][j] /= pivotVal;
            }
            for (int i = 0; i < n; i++) {
                if (i == k) continue;
                double factor = aug[i][k];
                for (int j = k; j <= n; j++) {
                    aug[i][j] -= factor * aug[k][j];
                }
            }
        }

        double[] x = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = aug[i][n];
        }
        return x;
    }

    private static double[][] copyMatrix(double[][] A) {
        int n = A.length;
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, C[i], 0, n);
        }
        return C;
    }

    private static void validate(double[][] A) {
        Objects.requireNonNull(A);
        if (A.length == 0) throw new IllegalArgumentException("Empty matrix");
        for (double[] row : A) {
            Objects.requireNonNull(row);
        }
    }

    private static void validateSquare(double[][] A) {
        validate(A);
        int n = A.length;
        for (double[] row : A) {
            if (row.length != n) {
                throw new IllegalArgumentException("Matrix must be square");
            }
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

class EigenDecompositionTest {

    private static final double DELTA = 1e-8;

    @Test
    void testPowerIterationDominant() {
        double[][] A = {{4, 1}, {1, 3}};
        var result = EigenDecomposition.powerIteration(A);
        // Eigenvalues are (7 ± √5)/2 ≈ 5.618, 2.382
        assertEquals(5.61803398875, result.eigenvalue(), 1e-4);
        // Verify A·v = λ·v
        double[] Av = multiply(A, result.eigenvector());
        double[] lambdaV = scale(result.eigenvector(), result.eigenvalue());
        assertArrayEquals(lambdaV, Av, 1e-8);
    }

    @Test
    void testPowerIterationWithDeflation() {
        double[][] A = {{4, 1}, {1, 3}};
        var e1 = EigenDecomposition.powerIteration(A);
        // Deflate and find second eigenvalue
        double lambda1 = e1.eigenvalue();
        double[] v1 = e1.eigenvector();
        double[][] deflated = new double[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                deflated[i][j] = A[i][j] - lambda1 * v1[i] * v1[j];
            }
        }
        var e2 = EigenDecomposition.powerIteration(deflated);
        assertEquals(2.38196601125, e2.eigenvalue(), 1e-4);
    }

    @Test
    void testAllEigenvalues() {
        double[][] A = {{4, 1, 0}, {1, 3, 1}, {0, 1, 2}};
        var pairs = EigenDecomposition.allEigenvalues(A);
        assertNotNull(pairs[0]);
        assertNotNull(pairs[1]);
        // Verify each eigenpair
        for (var pair : pairs) {
            if (pair == null) break;
            double[] Av = multiply(A, pair.eigenvector());
            double[] lv = scale(pair.eigenvector(), pair.eigenvalue());
            assertArrayEquals(lv, Av, 1e-6);
        }
    }

    @Test
    void testRayleighQuotient() {
        double[][] A = {{3, 0}, {0, 5}};
        double[] x = {0, 1};
        assertEquals(5.0, EigenDecomposition.rayleighQuotient(A, x), DELTA);
        double[] x2 = {1, 0};
        assertEquals(3.0, EigenDecomposition.rayleighQuotient(A, x2), DELTA);
    }

    @Test
    void testRayleighQuotientIteration() {
        double[][] A = {{4, 1}, {1, 3}};
        var result = EigenDecomposition.rayleighQuotientIteration(A);
        assertEquals(5.61803398875, result.eigenvalue(), 1e-8);
    }

    @Test
    void testSVD() {
        double[][] A = {{1, 0}, {0, 2}, {0, 0}}; // 3×2
        var svd = EigenDecomposition.svd(A);
        // Singular values: 2, 1
        assertEquals(2, svd.S().length);
        assertEquals(2.0, svd.S()[0], DELTA);
        assertEquals(1.0, svd.S()[1], DELTA);
        // Reconstruct
        double[][] recon = svd.reconstruct();
        for (int i = 0; i < A.length; i++) {
            assertArrayEquals(A[i], recon[i], DELTA);
        }
    }

    @Test
    void testSVDToyMatrix() {
        double[][] A = {{2, 0}, {0, 3}};
        var svd = EigenDecomposition.svd(A);
        assertEquals(3.0, svd.S()[0], DELTA);
        assertEquals(2.0, svd.S()[1], DELTA);
    }

    @Test
    void testEigenvectorVerification() {
        double[][] A = {{1, 2}, {3, 4}};
        var result = EigenDecomposition.powerIteration(A);
        double[] Av = multiply(A, result.eigenvector());
        double[] lambdaV = scale(result.eigenvector(), result.eigenvalue());
        for (int i = 0; i < Av.length; i++) {
            assertEquals(lambdaV[i], Av[i], 1e-8);
        }
    }

    @Test
    void testConvergenceException() {
        double[][] singular = {{1, 2}, {2, 4}};
        assertThrows(EigenDecomposition.ConvergenceException.class,
            () -> EigenDecomposition.powerIteration(singular));
    }

    @Test
    void testSVDReconstruction() {
        double[][] A = {{1, 2}, {3, 4}, {5, 6}};
        var svd = EigenDecomposition.svd(A);
        double[][] recon = svd.reconstruct();
        for (int i = 0; i < A.length; i++) {
            assertArrayEquals(A[i], recon[i], 1e-8);
        }
    }

    // Helpers
    private double[] multiply(double[][] A, double[] x) {
        int n = A.length;
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            y[i] = sum;
        }
        return y;
    }

    private double[] scale(double[] v, double s) {
        double[] r = new double[v.length];
        for (int i = 0; i < v.length; i++) r[i] = v[i] * s;
        return r;
    }
}
```

---

### 5. Complexity Analysis

**Time Complexity:**

| Operation | Complexity | Notes |
|-----------|-----------|-------|
| Power Iteration (per iter) | O(n²) | Matrix-vector multiply |
| Power Iteration (total) | O(n² · log(1/ε) / log(|λ₂/λ₁|)) | Convergence depends on eigengap |
| Rayleigh Quotient Iteration | O(n³) per iter | Linear solve at each step |
| Deflation | O(n²) | Outer product subtraction |
| All Eigenvalues (deflation) | O(k·n²) per eigenvalue | k = iter per eigenvalue |
| SVD (via A^T A) | O(m·n² + n³) | A^T A + eigendecomposition |
| SVD Reconstruction | O(m·n·k) | k = rank |

**Space Complexity:**

| Operation | Auxiliary Space |
|-----------|----------------|
| Power Iteration | O(n) vectors |
| Rayleigh | O(n²) for shifted matrix |
| All Eigenvalues | O(n²) for deflated matrix |
| SVD | O(m² + n²) for U, V |

**Convergence Analysis:**

Power iteration convergence rate depends on the **eigengap** γ = |λ₁/λ₂|:
- After k iterations: error ≈ |λ₂/λ₁|^k
- For closely spaced eigenvalues (γ ≈ 1), convergence is slow
- Rayleigh quotient iteration has cubic convergence for symmetric matrices
- Deflation propagates errors: eigenvector error in earlier pairs affects later ones

---

### 6. Follow-Up Questions

**Q1: When does power iteration fail?**

1. **Equal magnitude eigenvalues** (|λ₁| = |λ₂|): The method converges to a linear combination of the two eigenvectors.
2. **Complex eigenvalues**: Power iteration oscillates without converging.
3. **Zero eigenvalue dominant**: The matrix-vector product converges to zero.
4. **Initial vector orthogonal to dominant eigenvector**: The method never sees that eigenvector (rare with random initialization).

**Q2: How does the QR algorithm improve on power iteration?**

The QR algorithm simultaneously finds all eigenvalues:
1. Compute A₀ = A
2. For k = 1, 2, ...: factor A_{k-1} = QₖRₖ, then Aₖ = RₖQₖ
3. Aₖ converges to upper triangular (Schur form) with eigenvalues on diagonal

Convergence is accelerated by shifts (Wilkinson shift, Rayleigh shift). The practical QR algorithm uses Hessenberg reduction (O(n³)) followed by implicit QR steps (O(n²) each). This is the standard method used by LAPACK.

**Q3: What is the relationship between SVD and PCA?**

PCA finds directions of maximum variance in data X (n×d, n centered):

1. Covariance matrix: C = X^T X / (n-1)
2. PCs are eigenvectors of C (which are right singular vectors of X)
3. Explained variance = σᵢ² / Σⱼ σⱼ²

SVD of X directly gives: XV = UΣ, where columns of V are loadings, columns of U are principal component scores.

**Q4: How can you compute the smallest eigenvalue of a matrix?**

Use power iteration on A^{-1} (inverse iteration):
1. Solve A·w = v^{(k-1)} at each step
2. Converges to eigenvalue closest to 0
3. With shift μ: (A − μI)^{-1} converges to eigenvalue closest to μ

**Q5: What is the Courant-Fischer min-max principle?**

For symmetric A with eigenvalues λ₁ ≥ λ₂ ≥ ... ≥ λₙ:

λₖ = min_{dim(S)=n-k+1} max_{x∈S, x≠0} R(x)
λₖ = max_{dim(S)=k} min_{x∈S, x≠0} R(x)

This characterizes eigenvalues as optima of the Rayleigh quotient over subspaces, providing the theoretical foundation for many eigenvalue algorithms.

**Q6: How does the condition number relate to SVD?**

For A ∈ ℝ^{m×n}:
cond(A) = σ_max / σ_min

- Large condition number → ill-conditioned
- cond(A) = 1 → A is orthogonal (perfectly conditioned)
- cond(A^T A) = cond(A)² → normal equations square the condition number

In linear regression, the condition number of the design matrix determines the sensitivity of β̂ to measurement errors.

**Q7: Explain the Eckart-Young theorem.**

For A with SVD UΣV^T, the best rank-k approximation in the Frobenius norm is:

Aₖ = UₖΣₖVₖ^T

where Uₖ, Vₖ contain the first k columns and Σₖ contains the top k singular values.

‖A − Aₖ‖_F² = Σ_{i=k+1}^r σᵢ²

This is the foundation of dimensionality reduction: SVD provides the optimal low-rank approximation. The approximation error equals the sum of squared discarded singular values.

---

### 7. Applications in Machine Learning

| Application | Decomposition | Purpose |
|-------------|--------------|---------|
| PCA | Eigendecomposition of covariance | Dimensionality reduction |
| SVD for recommendation | Truncated SVD of user-item matrix | Collaborative filtering |
| Spectral clustering | Eigendecomposition of Laplacian | Graph partitioning |
| t-SNE initialization | SVD of data matrix | Visualization initialization |
| Word embeddings | SVD of co-occurrence matrix | Latent semantic analysis |
| Neural network compression | SVD of weight matrices | Model size reduction |
| PageRank | Dominant eigenvector of Google matrix | Web ranking |
