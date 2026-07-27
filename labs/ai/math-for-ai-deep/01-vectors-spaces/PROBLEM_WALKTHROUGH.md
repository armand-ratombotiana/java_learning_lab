# Problem Walkthrough: Vector Operations & Spaces

## Problem Statement

**Interview Problem: Implement Core Vector Operations**

You are building a fundamental linear algebra library for a machine learning framework. Implement a utility class `VectorOps` that provides the following vector operations with double-precision floating-point arithmetic:

1. **Dot Product** — Compute the inner product of two vectors
2. **Cross Product** — Compute the cross product of two 3-dimensional vectors
3. **L1 Norm (Manhattan Norm)** — Sum of absolute values of vector components
4. **L2 Norm (Euclidean Norm)** — Square root of sum of squared components
5. **Unit Vector** — Normalize a vector to unit length
6. **Cosine Similarity** — Cosine of the angle between two vectors
7. **Orthogonality Check** — Determine if two vectors are orthogonal within a tolerance

**Constraints:**
- All vectors are represented as `double[]`
- Input validation: vectors must be non-null, non-empty, and of matching dimensions where required
- Handle edge cases: zero vectors, floating-point tolerance (epsilon = 1e-10)
- Operations must be numerically stable

**Example:**
```java
double[] v1 = {1.0, 2.0, 3.0};
double[] v2 = {4.0, 5.0, 6.0};
double dot = VectorOps.dotProduct(v1, v2); // 32.0
double[] cross = VectorOps.crossProduct(v1, v2); // {-3.0, 6.0, -3.0}
double l1 = VectorOps.l1Norm(v1); // 6.0
double l2 = VectorOps.l2Norm(v1); // ~3.7417
double cosim = VectorOps.cosineSimilarity(v1, v2); // ~0.9746
```

---

## Step-by-Step Solution Walkthrough

### 1. Mathematical Foundation

#### 1.1 Vector Space Axioms

A **vector space** over a field **F** (here ℝ) is a set **V** with two operations:
- **Vector addition**: `+` : V × V → V
- **Scalar multiplication**: `·` : F × V → V

Satisfying closure, associativity, commutativity, identity, inverse, distributivity, and compatibility axioms.

#### 1.2 Dot Product (Inner Product)

For vectors **a** = (a₁, a₂, ..., aₙ) and **b** = (b₁, b₂, ..., bₙ):

⟨**a**, **b**⟩ = ∑ᵢ aᵢ · bᵢ

**Properties:**
- Symmetric: ⟨a, b⟩ = ⟨b, a⟩
- Bilinear: Linear in each argument
- Positive definite: ⟨a, a⟩ ≥ 0 and = 0 iff a = 0
- **Cauchy-Schwarz Inequality**: |⟨a, b⟩| ≤ ‖a‖₂ · ‖b‖₂

#### 1.3 Norms

**L1 Norm** (Manhattan):
‖v‖₁ = ∑ᵢ |vᵢ|

**L2 Norm** (Euclidean):
‖v‖₂ = √(∑ᵢ vᵢ²)

**Properties of norms:**
- Positive definite: ‖v‖ ≥ 0, = 0 iff v = 0
- Homogeneous: ‖α·v‖ = |α|·‖v‖
- Triangle inequality: ‖u + v‖ ≤ ‖u‖ + ‖v‖

**Relationship**: ‖v‖₂ ≤ ‖v‖₁ ≤ √n · ‖v‖₂

#### 1.4 Unit Vectors

A **unit vector** is any vector with ‖v‖ = 1. To normalize:
û = v / ‖v‖₂

For the zero vector, normalization is undefined (division by zero).

#### 1.5 Cosine Similarity

cos(θ) = ⟨a, b⟩ / (‖a‖₂ · ‖b‖₂)

This measures the angle between two vectors, ranging from -1 (opposite direction) to 1 (same direction), with 0 indicating orthogonality.

#### 1.6 Cross Product (3D)

For **a** = (a₁, a₂, a₃) and **b** = (b₁, b₂, b₃):

**a** × **b** = (a₂b₃ − a₃b₂, a₃b₁ − a₁b₃, a₁b₂ − a₂b₁)

**Properties:**
- Anticommutative: a × b = −(b × a)
- Orthogonal to both a and b: ⟨a × b, a⟩ = ⟨a × b, b⟩ = 0
- ‖a × b‖ = ‖a‖ · ‖b‖ · sin(θ)

#### 1.7 Orthogonality

Two vectors are **orthogonal** if ⟨a, b⟩ = 0.

An **orthonormal set** has unit vectors that are pairwise orthogonal.

The **Gram-Schmidt process** converts a set of vectors to orthonormal form.

---

### 2. Algorithm Design

#### 2.1 Dot Product — O(n)

Simple loop accumulating the product of corresponding components.

**Numerical consideration**: Use Kahan summation for very large vectors to reduce floating-point error.

#### 2.2 L1 Norm — O(n)

Sum of absolute values.

#### 2.3 L2 Norm — O(n)

Compute sum of squares, return sqrt. Use `Math.hypot`-style approach or careful handling to avoid overflow for large components. The `Math.sqrt` of the sum-of-squares can underflow/overflow for extreme values. One mitigation: find max component, scale, compute, re-scale.

However, for typical ML vectors, direct computation is fine.

#### 2.4 Unit Vector — O(n)

Compute L2 norm, then divide each component by the norm.

**Edge case**: If norm is 0 (zero vector), the operation is undefined → throw `ArithmeticException`.

#### 2.5 Cosine Similarity — O(n)

Compute dot product and both L2 norms, then divide.

**Relation to Cauchy-Schwarz**: The inequality guarantees |cos θ| ≤ 1.

#### 2.6 Cross Product — O(1)

Just three multiplications and subtractions.

#### 2.7 Orthogonality Check — O(n)

Compute dot product, compare absolute value against epsilon.

---

### 3. Java Implementation

```java
package com.ml.linalg;

import java.util.Objects;

/**
 * Utility class providing core vector operations for machine learning.
 * 
 * <p>All operations assume double-precision floating-point arithmetic.
 * Methods validate inputs and handle edge cases including zero vectors,
 * dimension mismatches, and numerical tolerance.</p>
 * 
 * <p>Key mathematical properties leveraged:
 * <ul>
 *   <li>Cauchy-Schwarz inequality: |⟨a,b⟩| ≤ ‖a‖‖b‖</li>
 *   <li>L2 norm equivalence with cosine similarity</li>
 *   <li>Cross product orthogonality to both inputs</li>
 * </ul>
 * 
 * @since 1.0
 */
public final class VectorOps {

    /** Default epsilon for floating-point comparisons. */
    public static final double EPSILON = 1e-10;

    /** Required dimension for cross product operations. */
    private static final int CROSS_DIM = 3;

    /**
     * Private constructor to prevent instantiation.
     */
    private VectorOps() {
        // utility class
    }

    /**
     * Computes the dot product (inner product) of two vectors.
     * 
     * <p>The dot product is defined as ⟨a,b⟩ = Σᵢ aᵢ·bᵢ.
     * This satisfies symmetry, bilinearity, and positive-definiteness.</p>
     * 
     * @param a first vector, non-null and non-empty
     * @param b second vector, non-null, same length as a
     * @return the scalar dot product
     * @throws NullPointerException if either vector is null
     * @throws IllegalArgumentException if vectors are empty or length mismatch
     */
    public static double dotProduct(double[] a, double[] b) {
        validateVectors(a, b);
        int n = a.length;
        // Kahan summation for numerical accuracy
        double sum = 0.0;
        double compensation = 0.0;
        for (int i = 0; i < n; i++) {
            double y = a[i] * b[i] - compensation;
            double t = sum + y;
            compensation = (t - sum) - y;
            sum = t;
        }
        return sum;
    }

    /**
     * Computes the cross product of two 3-dimensional vectors.
     * 
     * <p>The cross product a × b produces a vector orthogonal to both a and b:
     * a × b = (a₂b₃ − a₃b₂, a₃b₁ − a₁b₃, a₁b₂ − a₂b₁).</p>
     * 
     * <p>Properties: anticommutative, distributive, ‖a×b‖ = ‖a‖‖b‖·sin(θ).</p>
     * 
     * @param a first 3D vector
     * @param b second 3D vector
     * @return a new 3-element array containing the cross product
     * @throws NullPointerException if either vector is null
     * @throws IllegalArgumentException if vectors are not length 3
     */
    public static double[] crossProduct(double[] a, double[] b) {
        validateVectors(a, b);
        if (a.length != CROSS_DIM || b.length != CROSS_DIM) {
            throw new IllegalArgumentException("Cross product requires 3D vectors");
        }
        return new double[] {
            a[1] * b[2] - a[2] * b[1], // x = a₂b₃ - a₃b₂
            a[2] * b[0] - a[0] * b[2], // y = a₃b₁ - a₁b₃
            a[0] * b[1] - a[1] * b[0]  // z = a₁b₂ - a₂b₁
        };
    }

    /**
     * Computes the L1 norm (Manhattan norm) of a vector.
     * 
     * <p>‖v‖₁ = Σᵢ |vᵢ|</p>
     * 
     * @param v input vector
     * @return the L1 norm
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if v is empty
     */
    public static double l1Norm(double[] v) {
        validateVector(v);
        double sum = 0.0;
        for (double vi : v) {
            sum += Math.abs(vi);
        }
        return sum;
    }

    /**
     * Computes the L2 norm (Euclidean norm) of a vector.
     * 
     * <p>‖v‖₂ = √(Σᵢ vᵢ²)</p>
     * 
     * @param v input vector
     * @return the L2 norm (non-negative)
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if v is empty
     */
    public static double l2Norm(double[] v) {
        validateVector(v);
        // Use scaling to avoid overflow/underflow for extreme values
        double max = 0.0;
        for (double vi : v) {
            double abs = Math.abs(vi);
            if (abs > max) {
                max = abs;
            }
        }
        if (max == 0.0) {
            return 0.0;
        }
        double sum = 0.0;
        for (double vi : v) {
            double scaled = vi / max;
            sum += scaled * scaled;
        }
        return max * Math.sqrt(sum);
    }

    /**
     * Normalizes a vector to unit length (L2 norm = 1).
     * 
     * <p>û = v / ‖v‖₂</p>
     * 
     * @param v input vector
     * @return a new normalized unit vector
     * @throws NullPointerException if v is null
     * @throws IllegalArgumentException if v is empty or zero-vector
     */
    public static double[] unitVector(double[] v) {
        validateVector(v);
        double norm = l2Norm(v);
        if (norm < EPSILON) {
            throw new ArithmeticException("Cannot normalize a zero vector");
        }
        double[] result = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            result[i] = v[i] / norm;
        }
        return result;
    }

    /**
     * Computes the cosine similarity between two vectors.
     * 
     * <p>cos θ = ⟨a,b⟩ / (‖a‖₂ · ‖b‖₂)</p>
     * 
     * <p>By the Cauchy-Schwarz inequality, |cos θ| ≤ 1.
     * Values near 1 indicate high similarity, near 0 indicate orthogonality,
     * near -1 indicate opposite directions.</p>
     * 
     * @param a first vector
     * @param b second vector
     * @return cosine similarity in range [-1, 1]
     * @throws NullPointerException if either vector is null
     * @throws IllegalArgumentException if vectors are empty, mismatch, or zero-norm
     */
    public static double cosineSimilarity(double[] a, double[] b) {
        validateVectors(a, b);
        double dot = dotProduct(a, b);
        double normA = l2Norm(a);
        double normB = l2Norm(b);
        if (normA < EPSILON || normB < EPSILON) {
            throw new ArithmeticException("Cannot compute cosine similarity with zero vector");
        }
        double similarity = dot / (normA * normB);
        // Clamp to [-1, 1] to handle floating-point rounding
        return Math.max(-1.0, Math.min(1.0, similarity));
    }

    /**
     * Checks if two vectors are orthogonal within the default tolerance.
     * 
     * <p>Vectors are orthogonal if |⟨a,b⟩| &lt; ε.</p>
     * 
     * @param a first vector
     * @param b second vector
     * @return true if the vectors are orthogonal
     * @throws NullPointerException if either vector is null
     * @throws IllegalArgumentException if vectors are empty or mismatch
     */
    public static boolean areOrthogonal(double[] a, double[] b) {
        return areOrthogonal(a, b, EPSILON);
    }

    /**
     * Checks if two vectors are orthogonal within the specified tolerance.
     * 
     * @param a first vector
     * @param b second vector
     * @param epsilon tolerance for the dot product check
     * @return true if |⟨a,b⟩| &lt; epsilon
     * @throws NullPointerException if a or b is null
     * @throws IllegalArgumentException if vectors are empty or mismatch
     */
    public static boolean areOrthogonal(double[] a, double[] b, double epsilon) {
        validateVectors(a, b);
        return Math.abs(dotProduct(a, b)) < epsilon;
    }

    /**
     * Computes the unit vector in the direction of the projection of a onto b.
     * 
     * <p>proj_b(a) = ⟨a,b̂⟩ · b̂ where b̂ = b / ‖b‖</p>
     * 
     * @param a vector to project
     * @param b vector onto which to project
     * @return the projection of a onto b
     */
    public static double[] projection(double[] a, double[] b) {
        validateVectors(a, b);
        double[] unitB = unitVector(b);
        double scalar = dotProduct(a, unitB);
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = scalar * unitB[i];
        }
        return result;
    }

    // ========== Validation ==========

    private static void validateVector(double[] v) {
        Objects.requireNonNull(v, "Vector must not be null");
        if (v.length == 0) {
            throw new IllegalArgumentException("Vector must not be empty");
        }
    }

    private static void validateVectors(double[] a, double[] b) {
        Objects.requireNonNull(a, "First vector must not be null");
        Objects.requireNonNull(b, "Second vector must not be null");
        if (a.length == 0 || b.length == 0) {
            throw new IllegalArgumentException("Vectors must not be empty");
        }
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                "Vector dimension mismatch: " + a.length + " vs " + b.length);
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

class VectorOpsTest {

    private static final double DELTA = 1e-10;

    @Test
    void testDotProduct() {
        double[] a = {1, 2, 3};
        double[] b = {4, 5, 6};
        assertEquals(32.0, VectorOps.dotProduct(a, b), DELTA);
        assertEquals(0.0, VectorOps.dotProduct(new double[]{1, 0}, new double[]{0, 1}), DELTA);
        assertEquals(14.0, VectorOps.dotProduct(new double[]{2, 3}, new double[]{4, 1}), DELTA);
    }

    @Test
    void testDotProductSymmetry() {
        double[] a = {3, -1, 7};
        double[] b = {2, 8, -4};
        assertEquals(VectorOps.dotProduct(a, b), VectorOps.dotProduct(b, a), DELTA);
    }

    @Test
    void testCrossProduct() {
        double[] a = {1, 2, 3};
        double[] b = {4, 5, 6};
        double[] result = VectorOps.crossProduct(a, b);
        assertArrayEquals(new double[]{-3, 6, -3}, result, DELTA);
        // anticommutative
        double[] reverse = VectorOps.crossProduct(b, a);
        assertArrayEquals(new double[]{3, -6, 3}, reverse, DELTA);
        // orthogonal to inputs
        assertEquals(0.0, VectorOps.dotProduct(a, result), DELTA);
        assertEquals(0.0, VectorOps.dotProduct(b, result), DELTA);
    }

    @Test
    void testCrossProductRequires3D() {
        assertThrows(IllegalArgumentException.class,
            () -> VectorOps.crossProduct(new double[]{1, 2}, new double[]{3, 4}));
    }

    @Test
    void testL1Norm() {
        assertEquals(6.0, VectorOps.l1Norm(new double[]{1, -2, 3}), DELTA);
        assertEquals(0.0, VectorOps.l1Norm(new double[]{0, 0, 0}), DELTA);
        assertEquals(10.0, VectorOps.l1Norm(new double[]{-5, -5}), DELTA);
    }

    @Test
    void testL2Norm() {
        assertEquals(3.7416573867739413, VectorOps.l2Norm(new double[]{1, 2, 3}), DELTA);
        assertEquals(0.0, VectorOps.l2Norm(new double[]{0, 0}), DELTA);
        assertEquals(5.0, VectorOps.l2Norm(new double[]{3, 4}), DELTA);
    }

    @Test
    void testUnitVector() {
        double[] v = {3, 4};
        double[] u = VectorOps.unitVector(v);
        assertEquals(1.0, VectorOps.l2Norm(u), DELTA);
        assertEquals(0.6, u[0], DELTA);
        assertEquals(0.8, u[1], DELTA);
    }

    @Test
    void testUnitVectorZeroVector() {
        assertThrows(ArithmeticException.class,
            () -> VectorOps.unitVector(new double[]{0, 0, 0}));
    }

    @Test
    void testCosineSimilarity() {
        double[] a = {1, 2, 3};
        double[] b = {4, 5, 6};
        // cos sim = (4+10+18)/(√14·√77) = 32/√1078 ≈ 0.9746
        double expected = 32.0 / Math.sqrt(1078);
        assertEquals(expected, VectorOps.cosineSimilarity(a, b), DELTA);
        // identical vectors
        assertEquals(1.0, VectorOps.cosineSimilarity(a, a), DELTA);
        // opposite vectors
        assertEquals(-1.0, VectorOps.cosineSimilarity(a, new double[]{-1, -2, -3}), DELTA);
        // orthogonal
        assertEquals(0.0, VectorOps.cosineSimilarity(new double[]{1, 0}, new double[]{0, 1}), DELTA);
    }

    @Test
    void testCosineSimilarityCauchySchwarz() {
        double[] a = {1.234, 5.678, -3.21, 9.87};
        double[] b = {-4.321, 7.654, 1.23, -6.54};
        double cosim = VectorOps.cosineSimilarity(a, b);
        assertTrue(Math.abs(cosim) <= 1.0, "Cauchy-Schwarz violated: " + cosim);
    }

    @Test
    void testOrthogonality() {
        assertTrue(VectorOps.areOrthogonal(new double[]{1, 0}, new double[]{0, 1}));
        assertTrue(VectorOps.areOrthogonal(new double[]{0, 0, 0}, new double[]{1, 2, 3}));
        assertFalse(VectorOps.areOrthogonal(new double[]{1, 2}, new double[]{3, 4}));
    }

    @Test
    void testNullInputs() {
        assertThrows(NullPointerException.class,
            () -> VectorOps.dotProduct(null, new double[]{1}));
        assertThrows(NullPointerException.class,
            () -> VectorOps.dotProduct(new double[]{1}, null));
    }

    @Test
    void testDimensionMismatch() {
        assertThrows(IllegalArgumentException.class,
            () -> VectorOps.dotProduct(new double[]{1, 2}, new double[]{1}));
    }

    @Test
    void testEmptyVectors() {
        assertThrows(IllegalArgumentException.class,
            () -> VectorOps.dotProduct(new double[0], new double[0]));
    }

    @Test
    void testProjection() {
        double[] a = {3, 4, 0};
        double[] b = {1, 0, 0};
        double[] proj = VectorOps.projection(a, b);
        assertArrayEquals(new double[]{3, 0, 0}, proj, DELTA);
        assertEquals(3.0, VectorOps.l2Norm(proj), DELTA);
    }
}
```

---

### 5. Complexity Analysis

**Time Complexity:**

| Operation | Time Complexity | Notes |
|-----------|---------------|-------|
| Dot Product | O(n) | Single pass, Kahan summation |
| Cross Product | O(1) | Fixed 3D, 3 multiplications + 3 subtractions |
| L1 Norm | O(n) | Single pass |
| L2 Norm | O(n) | Two passes (find max, compute scaled sum) |
| Unit Vector | O(n) | L2 norm (O(n)) + divide (O(n)) = O(n) |
| Cosine Similarity | O(n) | Dot product + 2 norms = O(n) |
| Orthogonality Check | O(n) | Dot product only |

**Space Complexity:**
- All operations use O(1) auxiliary space, except:
  - `unitVector` returns a new array: O(n)
  - `crossProduct` returns a new array: O(1) (3 elements)
  - `projection` returns a new array: O(n)

**Numerical Stability Analysis:**

| Operation | Risk | Mitigation |
|-----------|------|-----------|
| Dot product | Floating-point accumulation error | Kahan summation |
| L2 norm | Overflow/underflow for extreme values | Max-based scaling |
| Cosine similarity | Division by zero | Zero-vector check |
| Unit vector | Division by zero | Zero-vector check |
| Cross product | Minimal (only 3 ops) | None needed |
| Orthogonality | Tolerance-dependent | Configurable epsilon |

---

### 6. Follow-Up Questions

**Q1: How would you handle sparse vectors efficiently?**

For sparse vectors (e.g., bag-of-words features), store as `Map<Integer, Double>` (index → value). Compute dot product by iterating over the non-zero entries of the sparser vector and looking up indices in the denser vector. This reduces average-case complexity from O(n) to O(nnz) where nnz is the number of non-zero entries.

```java
public static double sparseDotProduct(Map<Integer, Double> a, double[] b) {
    double sum = 0.0;
    for (var entry : a.entrySet()) {
        sum += entry.getValue() * b[entry.getKey()];
    }
    return sum;
}
```

**Q2: Prove the Cauchy-Schwarz inequality.**

**Proof**: For any real t, consider ‖a − tb‖² ≥ 0:
- ‖a − tb‖² = ⟨a − tb, a − tb⟩ = ‖a‖² − 2t⟨a,b⟩ + t²‖b‖² ≥ 0

This is a quadratic in t. For it to be non-negative for all t, its discriminant must be ≤ 0:
- Δ = (2⟨a,b⟩)² − 4‖a‖²‖b‖² ≤ 0
- 4⟨a,b⟩² ≤ 4‖a‖²‖b‖²
- |⟨a,b⟩| ≤ ‖a‖·‖b‖

Equality holds when a and b are linearly dependent (a = tb for some scalar t).

**Q3: What's the relationship between cosine similarity and Euclidean distance?**

For unit vectors u, v:
‖u − v‖² = ‖u‖² + ‖v‖² − 2⟨u,v⟩ = 2 − 2cos(θ)

So ‖u − v‖² = 2(1 − cos(θ)). For unit vectors, cosine similarity and Euclidean distance are monotonically related. This relationship breaks for non-unit vectors.

**Q4: How would you parallelize vector operations for large-scale computation?**

Use Java's `parallelStream` or the Fork/Join framework. For dot product:
```java
public static double parallelDotProduct(double[] a, double[] b) {
    return IntStream.range(0, a.length)
        .parallel()
        .mapToDouble(i -> a[i] * b[i])
        .sum();
}
```

For very large vectors (millions of elements), consider partitioning the work across threads using `RecursiveTask` with a threshold of ~10K elements per task. On modern hardware with 16+ cores, this yields near-linear speedup.

**Q5: Can cosine similarity be negative? What does it mean?**

Yes. Cosine similarity ranges from −1 to 1:
- +1: Vectors point in the same direction (θ = 0°)
- 0: Vectors are orthogonal (θ = 90°)
- −1: Vectors point in opposite directions (θ = 180°)

In ML contexts (e.g., word embeddings), negative cosine similarity means the vectors encode opposite semantic concepts.

**Q6: What is the Gram-Schmidt process and how would you implement it?**

Given a set of vectors {v₁, v₂, ..., vₖ}, Gram-Schmidt produces orthonormal vectors {e₁, e₂, ..., eₖ}:
1. e₁ = v₁ / ‖v₁‖
2. For i = 2 to k: project_out = Σⱼ₌₁ⁱ⁻¹ ⟨vᵢ, eⱼ⟩ · eⱼ, then eᵢ = (vᵢ − project_out) / ‖vᵢ − project_out‖

Implementation handles numerical instability by re-orthogonalization (Gram-Schmidt twice).

**Q7: What is the relationship between L1 and L2 norms in high-dimensional spaces?**

In high dimensions, the ratio ‖v‖₁ / ‖v‖₂ ≈ √(2n/π) for random vectors from a standard normal distribution. This means L1 grows roughly as √n times L2. This has implications for regularization (L1 promotes sparsity, L2 promotes uniform shrinkage) and for distance metrics in high-dimensional spaces (curse of dimensionality).

---

### 7. Key Mathematical Theorems & Proofs

**Theorem: Cauchy-Schwarz Inequality**
|⟨a,b⟩| ≤ ‖a‖ ‖b‖

Proof via discriminant method as shown in Q2.

**Theorem: Triangle Inequality**
‖a + b‖ ≤ ‖a‖ + ‖b‖

Proof: ‖a + b‖² = ‖a‖² + ‖b‖² + 2⟨a,b⟩ ≤ ‖a‖² + ‖b‖² + 2‖a‖‖b‖ = (‖a‖ + ‖b‖)²

**Theorem: Polarization Identity**
⟨a,b⟩ = ¼(‖a + b‖² − ‖a − b‖²)

This allows recovering the inner product from the norm in Euclidean spaces.

**Theorem: Parallelogram Law**
‖a + b‖² + ‖a − b‖² = 2(‖a‖² + ‖b‖²)

A norm comes from an inner product iff it satisfies the parallelogram law.

---

### 8. Code Compilation and Usage

```bash
# Compile
javac -d out src/com/ml/linalg/VectorOps.java

# Run tests (assuming JUnit 5 on classpath)
javac -cp "lib/junit-jupiter-api.jar;out" -d out test/com/ml/linalg/VectorOpsTest.java
java -cp "lib/*;out" org.junit.jupiter.console.JUnitLauncher --select-class com.ml.linalg.VectorOpsTest
```
