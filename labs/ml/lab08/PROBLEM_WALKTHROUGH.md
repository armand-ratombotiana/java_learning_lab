# Problem Walkthrough: Principal Component Analysis

## Problem 1: Face-Embedding Compression — Company: Meta

### Interview Scenario
"You're at Meta. The face-recognition service stores a 4-D feature vector per
face, and storage and search latency are hurting. The hypothesis: the embeddings
are effectively lower-dimensional — most of the variance is in one direction.
Before changing any storage schema, prove it with PCA: show the eigenvalues, the
explained-variance breakdown, and the projected 2-D coordinates."

### The Problem
Compress 10 face embeddings from 4-D to 2-D with PCA. It must: (1) Center the
data, (2) Build the sample covariance matrix, (3) Extract the top two
eigenvectors via power iteration and deflation, (4) Report each component's
eigenvalue and cumulative explained variance so the 2-D choice is justified,
(5) Project the centered data onto the components and print the 10×2 result.

### Solution Walkthrough
- Step 1: Encode 10 faces × 4 synthetic embedding dimensions — the lab's dataset.
- Step 2: `center(X)` subtracts column means; `covariances(Xc)` forms the 4×4
  sample covariance with the n−1 divisor.
- Step 3: `powerIterate(cov, 1000)` finds PC1; the Rayleigh quotient gives its
  eigenvalue 2.0450 — 95.35% of total variance (trace = 2.1450).
- Step 4: `deflate(cov, ev, lambda)` subtracts λ·vvᵀ; the second iteration finds
  PC2 at λ = 0.0614 — cumulative 98.21%.
- Step 5: `project(Xc, components)` produces the 10×2 cloud; the first column
  spans −2.29..+2.09, the second only −0.36..+0.48 — visually a line, confirming
  effective dimensionality near 1.

### Code
```java
package com.ml.lab08;

import java.util.Random;

/**
 * Meta-style face-embedding compressor (PCA).
 * <p>
 * Reduces 4-D face feature vectors to 2-D with the Lab 08 pipeline —
 * center, covariance, power iteration, deflation, projection — and
 * additionally reports cumulative explained variance so the component
 * count is justified, not guessed.
 */
public class FaceEmbeddingPCA {

    public static double[][] center(double[][] X) {
        int n = X.length, m = X[0].length;
        double[][] c = new double[n][m];
        double[] mean = new double[m];
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) mean[j] += X[i][j];
            mean[j] /= n;
        }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < m; j++)
                c[i][j] = X[i][j] - mean[j];
        return c;
    }

    public static double[][] covariances(double[][] Xc) {
        int n = Xc.length, m = Xc[0].length;
        double[][] cov = new double[m][m];
        for (int i = 0; i < m; i++)
            for (int j = 0; j <= i; j++) {
                double s = 0;
                for (int k = 0; k < n; k++) s += Xc[k][i] * Xc[k][j];
                cov[i][j] = s / (n - 1);
                cov[j][i] = s / (n - 1);
            }
        return cov;
    }

    public static double[] powerIterate(double[][] A, int iters) {
        int n = A.length;
        double[] v = new double[n];
        Random rng = new Random(42);
        for (int i = 0; i < n; i++) v[i] = rng.nextDouble();
        for (int iter = 0; iter < iters; iter++) {
            double[] Av = new double[n];
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    Av[i] += A[i][j] * v[j];
            double norm = 0;
            for (double x : Av) norm += x * x;
            norm = Math.sqrt(norm);
            if (norm < 1e-12) break;
            for (int i = 0; i < n; i++) v[i] = Av[i] / norm;
        }
        return v;
    }

    public static double[][] deflate(double[][] A, double[] eigenvec, double eigenval) {
        int n = A.length;
        double[][] R = new double[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                R[i][j] = A[i][j] - eigenval * eigenvec[i] * eigenvec[j];
        return R;
    }

    public static double[][] project(double[][] Xc, double[][] components) {
        int n = Xc.length, k = components.length;
        double[][] proj = new double[n][k];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++)
                for (int d = 0; d < Xc[0].length; d++)
                    proj[i][j] += Xc[i][d] * components[j][d];
        return proj;
    }

    // Rayleigh quotient: approximate eigenvalue for the dominant direction.
    public static double eigenval(double[][] A, double[] v) {
        double lambda = 0;
        for (int i = 0; i < v.length; i++) {
            double rowDot = 0;
            for (int j = 0; j < v.length; j++) rowDot += A[i][j] * v[j];
            lambda += v[i] * rowDot;
        }
        return lambda;
    }

    public static void main(String[] args) {
        System.out.println("=== Face-Embedding PCA ===");

        // 10 faces x 4 embedding dimensions (synthetic)
        double[][] X = {
            {2.5, 2.4, 3.1, 1.2},
            {0.5, 0.7, 1.0, 0.8},
            {2.2, 2.9, 2.8, 1.5},
            {1.9, 2.2, 3.0, 1.1},
            {3.1, 3.0, 3.5, 1.8},
            {2.3, 2.7, 3.2, 1.4},
            {2.0, 1.6, 2.4, 0.9},
            {1.0, 1.1, 1.8, 0.7},
            {1.5, 1.6, 2.2, 1.0},
            {1.1, 0.9, 1.5, 0.6}
        };

        double[][] Xc = center(X);
        double[][] cov = covariances(Xc);

        double trace = 0;
        for (int i = 0; i < cov.length; i++) trace += cov[i][i];

        double[][] components = new double[2][X[0].length];
        double[][] A = cov;
        double explained = 0;
        for (int k = 0; k < 2; k++) {
            double[] ev = powerIterate(A, 1000);
            components[k] = ev;
            double lambda = eigenval(A, ev);
            explained += lambda / trace;
            System.out.printf("PC%d: lambda=%.4f  explained=%.4f (cumulative %.4f)%n",
                    k + 1, lambda, lambda / trace, explained);
            A = deflate(A, ev, lambda);
        }

        double[][] proj = project(Xc, components);
        System.out.println("Projected data (10 x 2):");
        for (double[] row : proj) {
            System.out.printf("  [%.4f, %.4f]%n", row[0], row[1]);
        }
        System.out.printf("Shape: %d x %d -> %d x %d%n",
                X.length, X[0].length, proj.length, proj[0].length);
    }
}
```

### Expected Output
```
=== Face-Embedding PCA ===
PC1: lambda=2.0450  explained=0.9535 (cumulative 0.9535)
PC2: lambda=0.0614  explained=0.0286 (cumulative 0.9821)
Projected data (10 x 2):
  [1.0441, -0.2347]
  [-2.2930, 0.3004]
  [1.0784, 0.4803]
  [0.5257, -0.0710]
  [2.0854, -0.0241]
  [1.2161, 0.1088]
  [-0.1551, -0.3634]
  [-1.3675, -0.0474]
  [-0.5111, 0.0076]
  [-1.6230, -0.1564]
Shape: 10 x 4 -> 10 x 2
```

---

## Problem 2: Netflix Movie-Rating Compression — Company: Netflix

### Interview Scenario
"You're at Netflix. The content team models movies by two metadata ratios —
action share and drama share — and wants to see the covariance structure before
deciding whether one dimension is enough for the recommendation feature."

### The Problem
Run the PCA front-end on 3 movies and: (1) Center the data, (2) Build the
2×2 covariance matrix, (3) Read the structure — the features are perfectly
anti-correlated, (4) Argue why one component will capture nearly everything.

### Solution Walkthrough
- Step 1: 3 movies as `(action %, drama %)` — action and drama shares sum to
  100, so they move in exact opposition.
- Step 2: `center` yields the zero-sum deviations [-5,5], [15,-15], [-10,10].
- Step 3: `covariances` gives [[175, -175], [-175, 175]] — diagonal +175,
  off-diagonal −175: the anti-correlation, captured in the covariance matrix
  itself.
- Step 4: The matrix is rank-1-like — the second eigenvalue will be near zero,
  so PC1 carries all the structure.

### Code
```java
// 3 movies x 2 rating-dimensions (action %, drama %)
double[][] X = {{60, 40}, {80, 20}, {55, 45}};
double[][] Xc = FaceEmbeddingPCA.center(X);
double[][] cov = FaceEmbeddingPCA.covariances(Xc);
System.out.println("Centered data:");
for (double[] r : Xc) System.out.println("  " + Arrays.toString(r));
System.out.println("Covariance:");
for (double[] r : cov) System.out.println("  " + Arrays.toString(r));
```

### Expected Output
```
Centered data:
  [-5.0, 5.0]
  [15.0, -15.0]
  [-10.0, 10.0]
Covariance:
  [175.0, -175.0]
  [-175.0, 175.0]
```

---

## Problem 3: Spotify Feature-Axis Verification — Company: Spotify

### Interview Scenario
"You're at Spotify. An audio feature pair has known variances — 2.0 on the x-axis
and 1.0 on the y-axis, uncorrelated. Verify the PCA machinery returns the
obvious answer: the dominant direction is the x-axis, and it explains 2/3 of the
variance."

### The Problem
Verify power iteration and eigenvalue math on a diagonal covariance and:
(1) Find the dominant eigenvector, (2) Compute its eigenvalue, (3) Report the
explained-variance share, (4) Confirm the answer matches theory.

### Solution Walkthrough
- Step 1: Covariance [[2, 0], [0, 1]] — diagonal, so its eigenvectors are the
  coordinate axes by inspection.
- Step 2: `powerIterate(cov, 5000)` converges to [1.0, 0.0] — the x-axis.
- Step 3: Rayleigh quotient gives λ = 2.0; explained = 2.0/3.0 = 0.6667,
  matching the theory for a diagonal matrix.

### Code
```java
// Covariance of two audio features: x-axis variance 2.0, y-axis 1.0
double[][] cov = {{2.0, 0.0}, {0.0, 1.0}};
double[] v = FaceEmbeddingPCA.powerIterate(cov, 5000);
System.out.println("Dominant eigenvector: " + Arrays.toString(v));
double lambda = FaceEmbeddingPCA.eigenval(cov, v);
System.out.printf("Dominant eigenvalue: %.4f%n", lambda);
System.out.printf("Explained by PC1: %.4f%n", lambda / (2.0 + 1.0));
```

### Expected Output
```
Dominant eigenvector: [1.0, 0.0]
Dominant eigenvalue: 2.0000
Explained by PC1: 0.6667
```
