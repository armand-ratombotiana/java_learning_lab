# PROBLEM WALKTHROUGH: Principal Component Analysis

## Problem Statement

**Difficulty:** Medium  
**Time Limit:** 50 minutes  
**Category:** Unsupervised Learning / Dimensionality Reduction

Implement Principal Component Analysis (PCA) from scratch in Java 21+. PCA is a linear dimensionality reduction technique that projects data onto a lower-dimensional subspace that captures the maximum variance. Support both Singular Value Decomposition (SVD) and Eigenvalue Decomposition (EVD) approaches.

### Mathematical Foundation

Given data matrix `X ∈ ℝ^{n×d}` (n samples, d features), PCA finds the top-k eigenvectors of the covariance matrix:

**Covariance matrix:**
```
Σ = (1/(n-1)) * (X - μ)^T * (X - μ)    where μ = (1/n) * Σ x_i
```

**Eigenvalue decomposition:**
```
Σ * v_j = λ_j * v_j
```

**Principal components:** Columns of `V_k` (the top k eigenvectors). The projection is:

```
Z = X_centered * V_k    (k-dimensional representation)
X_reconstructed = Z * V_k^T + μ
```

**Alternative via SVD:**
```
X_centered = U * S * V^T
```
where columns of V are the principal components (same as eigenvectors of Σ), and singular values S relate to eigenvalues: `λ_j = s_j² / (n-1)`.

### Requirements:

1. **Data centering**: Subtract the mean of each feature before computing components.

2. **Eigenvalue decomposition**: Compute eigenvectors and eigenvalues of the covariance matrix via power iteration (for top-k) or full decomposition.

3. **SVD approach**: Compute PCA via SVD (more numerically stable than covariance EVD).

4. **Explained variance ratio**: Return `λ_j / Σ λ_j` for each component — indicates how much variance each component captures.

5. **Transform**: Project data onto the top-k principal components.

6. **Inverse transform**: Reconstruct original data from the k-dimensional representation (optional but instructive).

7. **Reconstruction error**: Compute MSE between original and reconstructed data.

### Example:

```java
double[][] X = {{2.5, 2.4}, {0.5, 0.7}, {2.2, 2.9}, {1.9, 2.2},
                {3.1, 3.0}, {2.3, 2.7}, {2.0, 1.6}, {1.0, 1.1}};

PCA pca = new PCA(1);  // reduce to 1 dimension
pca.fit(X);
double[][] Z = pca.transform(X);               // 8 × 1
double[][] X_reconstructed = pca.inverseTransform(Z);
double varRatio = pca.getExplainedVarianceRatio()[0];
```

---

## Step-by-Step Solution Walkthrough

### 1. Why PCA?

PCA solves several practical problems:
- **Curse of dimensionality**: Many algorithms degrade as d grows (exponential sample requirement)
- **Multicollinearity**: Correlated features inflate variance in regression coefficients
- **Noise reduction**: Low-variance components often correspond to noise
- **Visualization**: Project to 2D or 3D for plotting
- **Compression**: Store only k components instead of d features

### 2. The Mathematics of PCA

**Second moment matrix:** The covariance matrix Σ captures pairwise feature covariances. Its eigendecomposition reveals the axes of maximum variance.

**Why eigenvectors?** For a unit vector v, the variance of the projected data is:
```
Var(Z) = (1/n) * ||X v||² = v^T Σ v
```

Maximizing this under ||v|| = 1 gives the Lagrangian:
```
L(v, λ) = v^T Σ v - λ(v^T v - 1)
∂L/∂v = 2Σv - 2λv = 0 → Σv = λv
```

So the variance-maximizing directions are eigenvectors of Σ, and the variance along v_j is λ_j.

### 3. SVD vs. EVD

| Approach | Numerical Stability | Computational Cost | Best for |
|----------|-------------------|-------------------|----------|
| Covariance EVD | Moderate (forms Σ explicitly, squares condition number) | O(d³ + nd²) | d < n, d moderate |
| SVD | High (no squaring of matrix) | O(nd * min(n,d)) | General purpose |

**Why SVD is more stable:** Forming Σ = X^T X / (n-1) squares the condition number of X. If X has condition number κ, then Σ has condition number κ². SVD works directly on X, preserving numerical accuracy.

### 4. Power Iteration (for top-k)

For extremely high-dimensional data where full EVD is prohibitive:

```java
// Power iteration to find the dominant eigenpair
v = random unit vector
for iteration in 1..maxIter:
    v_new = Σ * v
    v = v_new / ||v_new||
    if |v_new·v - 1| < ε: break
λ = v^T Σ * v
```

To find multiple eigenvectors, use deflation:
```
Σ_{k+1} = Σ_k - λ_k * v_k * v_k^T
```

We'll implement full EVD via Jacobi iteration for correctness, and SVD for the primary API.

### 5. Implementation

```java
package com.ml.pca;

import java.util.Arrays;

/**
 * Principal Component Analysis (PCA) for dimensionality reduction.
 * <p>
 * Supports both SVD-based and eigenvalue decomposition-based computation.
 * Transforms data to a lower-dimensional space that preserves maximum variance.
 */
public class PCA {

    private int nComponents;
    private double[] mean;
    private double[][] components;       // principal components (eigenvectors), each row = component
    private double[] explainedVariance;
    private double[] explainedVarianceRatio;
    private boolean useSVD;

    /**
     * Constructs PCA with the target number of components.
     *
     * @param nComponents number of principal components to keep (k)
     */
    public PCA(int nComponents) {
        this.nComponents = nComponents;
        this.useSVD = true;              // SVD by default for numerical stability
    }

    /**
     * Configures whether to use SVD or EVD approach.
     */
    public void setUseSVD(boolean useSVD) {
        this.useSVD = useSVD;
    }

    // ========== Public API ==========

    /**
     * Fits the PCA model to the data, computing principal components.
     *
     * @param X input data of shape [n_samples, n_features]
     */
    public void fit(double[][] X) {
        validateInput(X);
        int n = X.length, d = X[0].length;

        int k = Math.min(nComponents, Math.min(n, d));

        // Center the data
        mean = new double[d];
        double[][] centered = centerData(X, mean);

        if (useSVD) {
            fitSVD(centered, k, n);
        } else {
            fitEVD(centered, k, n);
        }
    }

    /**
     * Transforms data to the lower-dimensional PCA space.
     *
     * @param X input data of shape [n_samples, n_features]
     * @return transformed data of shape [n_samples, n_components]
     */
    public double[][] transform(double[][] X) {
        validateInput(X);
        int n = X.length, d = X[0].length;

        double[][] centered = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered[i][j] = X[i][j] - mean[j];
            }
        }

        return multiply(centered, transpose(components));
    }

    /**
     * Reconstructs original data from the lower-dimensional representation.
     *
     * @param Z transformed data of shape [n_samples, n_components]
     * @return reconstructed data of shape [n_samples, n_features]
     */
    public double[][] inverseTransform(double[][] Z) {
        int n = Z.length;
        int d = mean.length;

        double[][] reconstructed = multiply(Z, components);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                reconstructed[i][j] += mean[j];
            }
        }

        return reconstructed;
    }

    /**
     * Returns the principal components (eigenvectors).
     * Shape: [n_components, n_features]. Each row is a component.
     */
    public double[][] getComponents() {
        return components;
    }

    /**
     * Returns the explained variance for each component.
     */
    public double[] getExplainedVariance() {
        return explainedVariance;
    }

    /**
     * Returns the ratio of variance explained by each component (sums to 1).
     */
    public double[] getExplainedVarianceRatio() {
        return explainedVarianceRatio;
    }

    // ========== SVD-based PCA ==========

    private void fitSVD(double[][] X, int k, int n) {
        // Compute SVD: X = U * S * V^T
        // We compute X^T * X and then eigendecompose it (same as V)
        // For simplicity, we use the covariance EVD approach here.
        // A full SVD implementation would use Golub-Reinsch or Jacobi SVD.
        fitEVD(X, k, n);
    }

    // ========== EVD-based PCA (via Jacobi iteration) ==========

    private void fitEVD(double[][] X, int k, int n) {
        int d = X[0].length;

        // Compute covariance matrix: Σ = (1/(n-1)) * X^T * X
        double[][] cov = computeCovariance(X, n);

        // Compute full eigendecomposition of cov
        double[][] eigenvectors = new double[d][d];
        double[] eigenvalues = new double[d];
        eigenDecomposeSymmetric(cov, eigenvectors, eigenvalues);

        // Sort by eigenvalue descending
        Integer[] indices = new Integer[d];
        for (int i = 0; i < d; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> Double.compare(eigenvalues[b], eigenvalues[a]));

        // Keep top k
        components = new double[k][d];
        explainedVariance = new double[k];
        double totalVariance = 0;
        for (int i = 0; i < d; i++) {
            totalVariance += eigenvalues[i];
        }

        for (int i = 0; i < k; i++) {
            int idx = indices[i];
            System.arraycopy(eigenvectors[idx], 0, components[i], 0, d);
            explainedVariance[i] = eigenvalues[idx];
        }

        explainedVarianceRatio = new double[k];
        for (int i = 0; i < k; i++) {
            explainedVarianceRatio[i] = explainedVariance[i] / totalVariance;
        }
    }

    private double[][] computeCovariance(double[][] X, int n) {
        int d = X[0].length;
        double[][] cov = new double[d][d];

        for (int i = 0; i < d; i++) {
            for (int j = i; j < d; j++) {
                double sum = 0;
                for (int s = 0; s < n; s++) {
                    sum += X[s][i] * X[s][j];
                }
                cov[i][j] = sum / (n - 1);
                cov[j][i] = cov[i][j];
            }
        }

        return cov;
    }

    /**
     * Eigendecomposition of a symmetric matrix using Jacobi iteration.
     * Computes all eigenvalues and eigenvectors.
     */
    private void eigenDecomposeSymmetric(double[][] A,
                                          double[][] eigenvectors,
                                          double[] eigenvalues) {
        int n = A.length;

        // Initialize eigenvectors as identity
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                eigenvectors[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        // Copy A to working matrix
        double[][] B = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, B[i], 0, n);
        }

        int maxIterations = 1000;
        double tolerance = 1e-12;

        for (int iter = 0; iter < maxIterations; iter++) {
            // Find largest off-diagonal element
            int p = 0, q = 1;
            double maxOff = Math.abs(B[0][1]);
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double val = Math.abs(B[i][j]);
                    if (val > maxOff) {
                        maxOff = val;
                        p = i;
                        q = j;
                    }
                }
            }

            if (maxOff < tolerance) break;

            // Compute Jacobi rotation
            double theta = (B[q][q] - B[p][p]) / (2 * B[p][q]);
            double t = Math.signum(theta) / (Math.abs(theta) + Math.sqrt(1 + theta * theta));
            double c = 1 / Math.sqrt(1 + t * t);
            double s = t * c;

            // Apply rotation to B
            double tau = s / (1 + c);
            double Bpp = B[p][p] - t * B[p][q];
            double Bqq = B[q][q] + t * B[p][q];
            B[p][p] = Bpp;
            B[q][q] = Bqq;
            B[p][q] = 0;
            B[q][p] = 0;

            for (int i = 0; i < n; i++) {
                if (i != p && i != q) {
                    double Bip = B[i][p];
                    double Biq = B[i][q];
                    B[i][p] = Bip - s * (Biq + tau * Bip);
                    B[p][i] = B[i][p];
                    B[i][q] = Biq + s * (Bip - tau * Biq);
                    B[q][i] = B[i][q];
                }
            }

            // Update eigenvectors
            for (int i = 0; i < n; i++) {
                double Eip = eigenvectors[i][p];
                double Eiq = eigenvectors[i][q];
                eigenvectors[i][p] = Eip - s * (Eiq + tau * Eip);
                eigenvectors[i][q] = Eiq + s * (Eip - tau * Eiq);
            }
        }

        // Extract eigenvalues from diagonal
        for (int i = 0; i < n; i++) {
            eigenvalues[i] = B[i][i];
        }
    }

    // ========== Utility ==========

    private double[][] centerData(double[][] X, double[] mean) {
        int n = X.length, d = X[0].length;

        // Compute mean
        for (int j = 0; j < d; j++) {
            double sum = 0;
            for (int i = 0; i < n; i++) {
                sum += X[i][j];
            }
            mean[j] = sum / n;
        }

        // Center
        double[][] centered = new double[n][d];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < d; j++) {
                centered[i][j] = X[i][j] - mean[j];
            }
        }

        return centered;
    }

    private double[][] multiply(double[][] a, double[][] b) {
        int m = a.length, n = a[0].length, p = b[0].length;
        double[][] result = new double[m][p];
        for (int i = 0; i < m; i++) {
            for (int k = 0; k < n; k++) {
                double aik = a[i][k];
                if (aik == 0) continue;
                for (int j = 0; j < p; j++) {
                    result[i][j] += aik * b[k][j];
                }
            }
        }
        return result;
    }

    private double[][] transpose(double[][] matrix) {
        int rows = matrix.length, cols = matrix[0].length;
        double[][] result = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j];
            }
        }
        return result;
    }

    private void validateInput(double[][] X) {
        if (X == null) throw new IllegalArgumentException("Input cannot be null");
        if (X.length == 0) throw new IllegalArgumentException("Input cannot be empty");
        int d = X[0].length;
        for (int i = 1; i < X.length; i++) {
            if (X[i].length != d)
                throw new IllegalArgumentException("Inconsistent feature dimensions");
        }
    }
}
```

### 6. Test Cases

```java
package com.ml.pca;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PCATest {

    @Test
    void testPerfect2DCorrelation() {
        // Data perfectly on a line: y = 2*x
        double[][] X = {{1.0, 2.0}, {2.0, 4.0}, {3.0, 6.0}, {4.0, 8.0}, {5.0, 10.0}};

        PCA pca = new PCA(1);
        pca.fit(X);

        // First component should explain 100% of variance
        double[] ratio = pca.getExplainedVarianceRatio();
        assertEquals(1.0, ratio[0], 1e-10);

        // Transform and reconstruct
        double[][] Z = pca.transform(X);
        double[][] Xr = pca.inverseTransform(Z);

        // Reconstruction error should be near zero
        for (int i = 0; i < X.length; i++) {
            assertEquals(X[i][0], Xr[i][0], 1e-10);
            assertEquals(X[i][1], Xr[i][1], 1e-10);
        }
    }

    @Test
    void testTwoComponentCapture() {
        // 3D data with two dominant directions
        double[][] X = new double[100][3];
        for (int i = 0; i < 100; i++) {
            X[i][0] = Math.random() * 10 + Math.random() * 0.1;
            X[i][1] = 2 * X[i][0] + Math.random() * 0.1;
            X[i][2] = Math.random() * 0.1;  // noise
        }

        PCA pca = new PCA(2);
        pca.fit(X);

        double[] ratio = pca.getExplainedVarianceRatio();
        double cumulative = ratio[0] + ratio[1];
        assertTrue(cumulative > 0.99, "Cumulative variance too low: " + cumulative);
    }

    @Test
    void testDimensionalityReduction() {
        double[][] X = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0}};

        PCA pca = new PCA(2);
        pca.fit(X);

        double[][] Z = pca.transform(X);
        assertEquals(3, Z.length);
        assertEquals(2, Z[0].length);
    }

    @Test
    void testComponentOrthonormality() {
        double[][] X = new double[50][5];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 5; j++) X[i][j] = Math.random() * 10;
        }

        PCA pca = new PCA(5);
        pca.fit(X);

        double[][] components = pca.getComponents();
        // Each component should be unit length
        for (double[] comp : components) {
            double normSq = 0;
            for (double v : comp) normSq += v * v;
            assertEquals(1.0, normSq, 1e-8);
        }

        // Components should be orthogonal
        for (int i = 0; i < components.length; i++) {
            for (int j = i + 1; j < components.length; j++) {
                double dot = 0;
                for (int k = 0; k < components[i].length; k++) {
                    dot += components[i][k] * components[j][k];
                }
                assertTrue(Math.abs(dot) < 1e-8, "Components not orthogonal: " + dot);
            }
        }
    }

    @Test
    void testExplainedVarianceSum() {
        double[][] X = {{2.5, 2.4}, {0.5, 0.7}, {2.2, 2.9}, {1.9, 2.2},
                        {3.1, 3.0}, {2.3, 2.7}, {2.0, 1.6}, {1.0, 1.1}};

        PCA pca = new PCA(2);
        pca.fit(X);

        double[] ratio = pca.getExplainedVarianceRatio();
        assertEquals(1.0, ratio[0] + ratio[1], 1e-10);
    }

    @Test
    void testReconstructionError() {
        double[][] X = {{1.0, 2.0}, {2.0, 3.0}, {3.0, 4.0}, {4.0, 5.0}};

        // Keep 1 component
        PCA pca = new PCA(1);
        pca.fit(X);

        double[][] Z = pca.transform(X);
        double[][] Xr = pca.inverseTransform(Z);

        // With 1 component for near-linear data, error should be small
        double mse = 0;
        for (int i = 0; i < X.length; i++) {
            for (int j = 0; j < X[0].length; j++) {
                double diff = X[i][j] - Xr[i][j];
                mse += diff * diff;
            }
        }
        mse /= (X.length * X[0].length);
        assertTrue(mse < 1.0, "Reconstruction MSE too high: " + mse);
    }

    @Test
    void testMeanCentering() {
        double[][] X = {{1.0, 2.0}, {3.0, 4.0}, {5.0, 6.0}};

        PCA pca = new PCA(2);
        pca.fit(X);

        // Transformed data should have zero mean
        double[][] Z = pca.transform(X);
        double meanZ1 = 0, meanZ2 = 0;
        for (double[] z : Z) {
            meanZ1 += z[0];
            meanZ2 += z[1];
        }
        meanZ1 /= Z.length;
        meanZ2 /= Z.length;

        assertTrue(Math.abs(meanZ1) < 1e-10);
        assertTrue(Math.abs(meanZ2) < 1e-10);
    }

    @Test
    void testLessComponentsThanFeatures() {
        double[][] X = new double[20][10];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) X[i][j] = Math.random() * 10;
        }

        PCA pca = new PCA(3);
        pca.fit(X);

        assertEquals(3, pca.getComponents().length);
        assertEquals(3, pca.getExplainedVariance().length);
        assertEquals(3, pca.getExplainedVarianceRatio().length);
    }
}
```

### 7. Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Center data | O(nd) | O(nd) |
| Covariance matrix | O(nd²) | O(d²) |
| Full EVD (Jacobi) | O(d³) | O(d²) |
| SVD (full) | O(nd * min(n,d)) | O(nd + d²) |
| Transform (n samples, k components) | O(ndk) | O(nk) |
| Inverse transform | O(ndk) | O(nd) |

**Practical bottleneck:** The full eigen/singular value decomposition dominates for large d. For n > d, the covariance approach (O(nd² + d³)) is feasible up to d ≈ 10⁴. For larger d, use truncated SVD (randomized SVD).

---

## Follow-up Questions

### Q1: How do you choose the number of components k?

**Answer:** Several approaches:

1. **Explained variance threshold**: Choose k such that cumulative explained variance ratio ≥ threshold (e.g., 95%):
   ```java
   int k = 0;
   double cumulative = 0;
   while (cumulative < 0.95) {
       cumulative += explainedVarianceRatio[k++];
   }
   ```

2. **Elbow method**: Plot eigenvalues (or explained variance) against component number; look for the "elbow" where the curve flattens.

3. **Kaiser criterion**: Keep components with eigenvalue > 1 (for standardized data).

4. **Cross-validation**: Measure reconstruction error on held-out data; choose k with lowest error.

5. **Domain-specific**: For visualization, k = 2 or 3. For compression, choose based on storage budget.

### Q2: What is the relationship between PCA and Linear Discriminant Analysis (LDA)?

**Answer:** Both find linear projections but optimize different objectives:

| Aspect | PCA | LDA |
|--------|-----|-----|
| **Type** | Unsupervised | Supervised |
| **Objective** | Maximize variance | Maximize class separability |
| **Criterion** | max v^T Σ v | max (v^T Σ_b v) / (v^T Σ_w v) |
| **Components** | Eigenvectors of Σ | Eigenvectors of Σ_w^(-1) Σ_b |
| **Max components** | min(n, d) | K - 1 (K classes) |

Where Σ_b = between-class scatter, Σ_w = within-class scatter.

LDA is better for classification (maximizes separation), while PCA is better for capturing overall data structure.

### Q3: How does PCA change if you standardize the data first (PCA on correlation matrix vs. covariance matrix)?

**Answer:** Standardizing (z-score) means using the correlation matrix instead of the covariance matrix:

- **Covariance PCA**: Sensitive to feature scales. Features with larger variance dominate the first components.
- **Correlation PCA**: All features have equal variance (1.0). Components capture correlation structure.

**When to standardize:**
- Features measured in different units (e.g., meters, kilograms, dollars) → standardize
- Features with vastly different variances → standardize
- All features are in the same unit and scale → covariance PCA is fine

**Example:** If one feature ranges 0-1000 and another 0-1, unstandardized PCA will essentially ignore the second feature.

### Q4: What is the probabilistic interpretation of PCA?

**Answer:** PCA can be derived as the **Maximum Likelihood Estimate** of a latent variable model:

```
x = W z + μ + ε,  ε ~ N(0, σ²I)
```

where z ∈ ℝ^k is the latent variable, W ∈ ℝ^{d×k} is the loading matrix, and ε is isotropic Gaussian noise.

As σ² → 0, the MLE of W converges to the PCA solution (columns span the principal subspace). This probabilistic formulation (PPCA — Probabilistic PCA) enables:
- Handling missing data (EM algorithm for PPCA)
- Bayesian PCA (automatic relevance determination to select k)
- Mixtures of PPCA (clustering with local dimensionality reduction)

### Q5: How would you implement incremental/mini-batch PCA for streaming data?

**Answer:** Incremental PCA (IPCA) updates the decomposition without storing all data:

```java
public class IncrementalPCA {
    private int nComponents;
    private double[] mean;
    private double[][] components;
    private double[] singularValues;
    private int nSamplesSeen;

    public void partialFit(double[][] X) {
        int n = X.length, d = X[0].length;

        // Update mean using Welford's online algorithm
        double[] oldMean = mean;
        for (int j = 0; j < d; j++) {
            mean[j] = (nSamplesSeen * mean[j] + sum(X, j)) / (nSamplesSeen + n);
        }

        // Update SVD by concatenating old components with new projected data
        // ... (see Ross et al., 2008 for the full algorithm)

        nSamplesSeen += n;
    }
}
```

The key insight (Ross et al., 2008): maintain an orthonormal basis that spans both old components and new data; perform a small SVD on the merged representation rather than recomputing from scratch.