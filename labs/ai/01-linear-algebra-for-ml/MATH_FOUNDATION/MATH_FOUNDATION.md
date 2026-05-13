# Linear Algebra - Mathematical Foundations

## Table of Contents
1. [Vector Mathematics](#1-vector-mathematics)
2. [Matrix Theory](#2-matrix-theory)
3. [Determinants and Inverses](#3-determinants-and-inverses)
4. [Eigenvalue Theory](#4-eigenvalue-theory)
5. [Singular Value Decomposition](#5-singular-value-decomposition)
6. [Matrix Calculus](#6-matrix-calculus)
7. [Special Identities](#7-special-identities)
8. [Proofs and Derivations](#8-proofs-and-derivations)

---

## 1. Vector Mathematics

### 1.1 Vector Addition and Scalar Multiplication

**Axioms of Vector Space**:
For vectors **u**, **v**, **w** in V and scalars α, β in ℝ:

1. **Closure under addition**: **u** + **v** ∈ V
2. **Commutativity**: **u** + **v** = **v** + **u**
3. **Associativity**: (**u** + **v**) + **w** = **u** + (**v** + **w**)
4. **Identity**: ∃ **0** ∈ V: **0** + **v** = **v**
5. **Inverse**: ∃ **-v** ∈ V: **v** + (-**v**) = **0**
6. **Closure under scalar multiplication**: α**v** ∈ V
7. **Distributivity**: α(**u** + **v**) = α**u** + α**v**
8. **Distributivity**: (α + β)**v** = α**v** + β**v**
9. **Associativity**: (αβ)**v** = α(β**v**)
10. **Identity**: 1**v** = **v**

### 1.2 Dot Product (Inner Product)

**Definition**: For **a**, **b** ∈ ℝⁿ:
$$\mathbf{a} \cdot \mathbf{b} = \sum_{i=1}^{n} a_i b_i = \|\mathbf{a}\| \|\mathbf{b}\| \cos\theta$$

**Properties**:
- Symmetric: **a** · **b** = **b** · **a**
- Bilinear: (α**a** + β**b**) · **c** = α(**a** · **c**) + β(**b** · **c**)
- Positive definite: **v** · **v** ≥ 0, equals 0 only if **v** = **0**

**Cauchy-Schwarz Inequality**:
$$|\mathbf{a} \cdot \mathbf{b}| \leq \|\mathbf{a}\| \|\mathbf{b}\|$$

**Proof**:
Consider function f(t) = (a + tb) · (a + tb) ≥ 0
Expanding: a·a + 2t(a·b) + t²(b·b) ≥ 0
This quadratic in t has discriminant ≤ 0:
(2(a·b))² - 4(a·a)(b·b) ≤ 0
4(a·b)² ≤ 4(a·a)(b·b)
(a·b)² ≤ (a·a)(b·b)
|a·b| ≤ ||a|| · ||b|| ∎

**Triangle Inequality**:
$$\|\mathbf{a} + \mathbf{b}\| \leq \|\mathbf{a}\| + \|\mathbf{b}\|$$

**Proof**:
||a + b||² = (a + b)·(a + b) = ||a||² + 2(a·b) + ||b||²
≤ ||a||² + 2||a|| ||b|| + ||b||² = (||a|| + ||b||)²
Taking square root: ||a + b|| ≤ ||a|| + ||b|| ∎

### 1.3 Cross Product (3D)

**Definition**: For **a**, **b** ∈ ℝ³:
$$\mathbf{a} \times \mathbf{b} = \begin{pmatrix} a_2 b_3 - a_3 b_2 \\ a_3 b_1 - a_1 b_3 \\ a_1 b_2 - a_2 b_1 \end{pmatrix}$$

**Properties**:
- Anti-commutative: **a** × **b** = -(**b** × **a**)
- Perpendicular: (**a** × **b**) · **a** = 0 and (**a** × **b**) · **b** = 0
- Magnitude: ||**a** × **b**|| = ||a|| ||b|| sin θ (area of parallelogram)

**Scalar Triple Product**:
$$[\mathbf{a}, \mathbf{b}, \mathbf{c}] = \mathbf{a} \cdot (\mathbf{b} \times \mathbf{c}) = \det\begin{pmatrix} a_1 & a_2 & a_3 \\ b_1 & b_2 & b_3 \\ c_1 & c_2 & c_3 \end{pmatrix}$$

**Geometric Interpretation**: Volume of parallelepiped defined by **a**, **b**, **c**

### 1.4 Vector Norms

**L^p Norm Definition**:
$$\|\mathbf{v}\|_p = \left(\sum_{i=1}^{n} |v_i|^p\right)^{1/p}$$

**Special Cases**:

| Norm | Formula | Name |
|------|---------|------|
| L0 | count(v_i ≠ 0) | Sparsity count |
| L1 | Σ|v_i| | Manhattan/Taxicab |
| L2 | √(Σv_i²) | Euclidean |
| L∞ | max|v_i| | Chebyshev/Max |

**p-norm Properties**:
1. Positive homogeneity: ||α**v**|| = |α|·||**v**||
2. Triangle inequality: ||**u** + **v**|| ≤ ||**u**|| + ||**v**||
3. Definiteness: ||**v**|| = 0 ⟺ **v** = **0**

**Unit Balls**:
- L1: Diamond (rotated square)
- L2: Sphere
- L∞: Cube

### 1.5 Linear Independence and Span

**Linear Combination**: **v** = Σ cᵢ **uᵢ** for scalars cᵢ

**Span**: span{S} = {Σ cᵢ **uᵢ** : cᵢ ∈ ℝ, **uᵢ** ∈ S}

**Linear Independence**: S is linearly independent if:
$$\sum c_i \mathbf{u}_i = \mathbf{0} \implies c_i = 0 \quad \forall i$$

**Equivalent Condition**: No vector in S can be written as a linear combination of others

**Theorem**: Maximum size of linearly independent set in ℝⁿ is n

### 1.6 Basis and Dimension

**Basis**: B is a basis of V if:
1. B is linearly independent
2. span(B) = V

**Dimension**: dim(V) = |B| for any basis B

**Standard Basis** in ℝⁿ:
$$\hat{e}_i = (0, 0, ..., 1, ..., 0) \text{ with 1 at position } i$$

**Coordinates**: For basis B = {**v₁**, ..., **vₙ**}:
$$\mathbf{x} = c_1 \mathbf{v}_1 + ... + c_n \mathbf{v}_n \implies [\mathbf{x}]_B = \begin{pmatrix} c_1 \\ \vdots \\ c_n \end{pmatrix}$$

---

## 2. Matrix Theory

### 2.1 Matrix Operations

**Addition**: (A + B)ᵢⱼ = Aᵢⱼ + Bᵢⱼ

**Scalar Multiplication**: (αA)ᵢⱼ = αAᵢⱼ

**Transpose**: (Aᵀ)ᵢⱼ = Aⱼᵢ

**Properties**:
- (Aᵀ)ᵀ = A
- (A + B)ᵀ = Aᵀ + Bᵀ
- (αA)ᵀ = αAᵀ
- (AB)ᵀ = BᵀAᵀ

**Trace**: tr(A) = Σᵢ Aᵢᵢ

**Properties**:
- tr(A + B) = tr(A) + tr(B)
- tr(αA) = α tr(A)
- tr(AB) = tr(BA)
- tr(Aᵀ) = tr(A)

### 2.2 Matrix Multiplication

**Definition**: For A ∈ ℝᵐˣᵏ, B ∈ ℝᵏˣⁿ:
$$(AB)_{ij} = \sum_{p=1}^{k} A_{ip} B_{pj}$$

**Properties**:
- Not commutative: AB ≠ BA generally
- Associative: (AB)C = A(BC)
- Distributive: A(B + C) = AB + AC
- (AB)ᵀ = BᵀAᵀ

**Element-wise (Hadamard)**: (A ∘ B)ᵢⱼ = Aᵢⱼ · Bᵢⱼ

**Kronecker Product**: A ⊗ B produces block matrix

### 2.3 Types of Matrices

**Square Matrix**: m = n

**Symmetric Matrix**: A = Aᵀ

**Skew-symmetric Matrix**: Aᵀ = -A

**Diagonal Matrix**: Aᵢⱼ = 0 for i ≠ j

**Identity Matrix**: Iᵢⱼ = δᵢⱼ (Kronecker delta)

**Orthogonal Matrix**: QᵀQ = QQᵀ = I
- Preserves dot products: (Q**x**) · (Q**y**) = **x** · **y**
- Preserves norms: ||Q**x**|| = ||**x**||
- Columns are orthonormal

**Positive Definite Matrix**: **x**ᵀA**x** > 0 for all **x** ≠ **0**
- All eigenvalues positive
- Cholesky decomposition exists

**Idempotent Matrix**: A² = A

### 2.4 Block Matrices

**Partitioning**: A can be divided into blocks Aᵢⱼ

**Block Multiplication**:
$$\begin{pmatrix} A_{11} & A_{12} \\ A_{21} & A_{22} \end{pmatrix} \begin{pmatrix} B_{11} & B_{12} \\ B_{21} & B_{22} \end{pmatrix} = \begin{pmatrix} A_{11}B_{11} + A_{12}B_{21} & A_{11}B_{12} + A_{12}B_{22} \\ A_{21}B_{11} + A_{22}B_{21} & A_{21}B_{12} + A_{22}B_{22} \end{pmatrix}$$

**Block Diagonal**:
$$A = \text{diag}(A_1, A_2, ..., A_k) = \begin{pmatrix} A_1 & 0 & \cdots & 0 \\ 0 & A_2 & \cdots & 0 \\ \vdots & \vdots & \ddots & \vdots \\ 0 & 0 & \cdots & A_k \end{pmatrix}$$

Properties:
- det(A) = Π det(Aᵢ)
- A⁻¹ = diag(A₁⁻¹, A₂⁻¹, ..., Aₖ⁻¹) if all Aᵢ invertible

---

## 3. Determinants and Inverses

### 3.1 Determinant Definition

**2×2 case**:
$$\det\begin{pmatrix} a & b \\ c & d \end{pmatrix} = ad - bc$$

**3×3 case (Sarrus' Rule)**:
$$\det(A) = aei + bfg + cdh - ceg - bdi - afh$$

**General Definition (Leibniz Formula)**:
$$\det(A) = \sum_{\sigma \in S_n} \text{sgn}(\sigma) \prod_{i=1}^{n} a_{i,\sigma(i)}$$

where S_n is set of all permutations of {1, ..., n}

**Laplace Expansion** (cofactor expansion):
$$\det(A) = \sum_{j=1}^{n} (-1)^{i+j} a_{ij} \det(M_{ij})$$
for fixed row i, or similarly for columns.

### 3.2 Determinant Properties

1. **Linear in each row** (with others fixed)
2. **Sign change** on row swap
3. **Zero rows/columns** ⟹ det = 0
4. **Identical rows** ⟹ det = 0
5. **Triangular**: det = product of diagonal elements
6. **det(I)** = 1
7. **det(Aᵀ)** = det(A)
8. **det(AB)** = det(A)det(B)
9. **det(A⁻¹)** = 1/det(A) if A invertible

### 3.3 Matrix Inverse

**Definition**: A⁻¹ satisfies AA⁻¹ = A⁻¹A = I

**Necessary and Sufficient Condition**: A invertible ⟺ det(A) ≠ 0

**Adjugate Method**:
$$A^{-1} = \frac{1}{\det(A)} \text{adj}(A)$$

where adj(A) = Cᵀ, the transpose of cofactor matrix.

**Cofactor**: Cᵢⱼ = (-1)ⁱ⁺ʲ det(Mᵢⱼ)

### 3.4 Solving Linear Systems

**Cramer's Rule** (theoretical, not practical):
For Ax = b, where A is invertible:
$$x_i = \frac{\det(A_i)}{\det(A)}$$
where Aᵢ is A with column i replaced by b.

**Gaussian Elimination** (practical method):
1. Form augmented matrix [A | b]
2. Apply row operations to get upper triangular
3. Back-substitute

### 3.5 Sherman-Morrison Formula

For invertible A and rank-1 update:
$$(A + \mathbf{u}\mathbf{v}^T)^{-1} = A^{-1} - \frac{A^{-1}\mathbf{u}\mathbf{v}^T A^{-1}}{1 + \mathbf{v}^T A^{-1}\mathbf{u}}$$

**Proof**:
Let B = A + **uv**ᵀ. Want to find X such that BX = I.
Assume X = A⁻¹ + **pq**ᵀ.
Then BX = (A + **uv**ᵀ)(A⁻¹ + **pq**ᵀ) = I + **vp**ᵀ + A⁻¹**uq**ᵀ + **uv**ᵀA⁻¹**uq**ᵀ
Set = I: need **vp**ᵀ + A⁻¹**uq**ᵀ + **uv**ᵀA⁻¹**uq**ᵀ = 0
Choose p = -A⁻¹**u**, then **vp**ᵀ = -**v**A⁻¹**u**ᵀ
Also **u**(**v**ᵀA⁻¹**u**) = (1 + **v**ᵀA⁻¹**u**)**u**
This leads to q = -Aᵀ⁻¹**v**/(1 + **v**ᵀA⁻¹**u**) ∎

### 3.6 Woodbury Matrix Identity

$$(A + UCV)^{-1} = A^{-1} - A^{-1}U(C^{-1} + VA^{-1}U)^{-1} VA^{-1}$$

**Special Case** (rank-k update):
$$(A + \sum_{i=1}^{k} \mathbf{u}_i \mathbf{v}_i^T)^{-1} = A^{-1} - A^{-1}U(I + V^TA^{-1}U)^{-1}V^TA^{-1}$$

where U, V are n×k matrices with columns **uᵢ**, **vᵢ**.

---

## 4. Eigenvalue Theory

### 4.1 Eigenvalues and Eigenvectors

**Definition**: For A ∈ ℝⁿˣⁿ, **v** ≠ **0**, λ such that:
$$A\mathbf{v} = \lambda \mathbf{v}$$

- λ is an **eigenvalue**
- **v** is an **eigenvector**

**Characteristic Polynomial**:
$$p(\lambda) = \det(A - \lambda I)$$

**Eigenvalues are roots** of p(λ) = 0.

**Properties**:
- Sum of eigenvalues = trace(A)
- Product of eigenvalues = det(A)
- Number of eigenvalues (counting multiplicity) = n

### 4.2 Characteristic Polynomial

For 2×2:
$$A = \begin{pmatrix} a & b \\ c & d \end{pmatrix}$$
$$p(\lambda) = \det\begin{pmatrix} a-\lambda & b \\ c & d-\lambda \end{pmatrix} = (a-\lambda)(d-\lambda) - bc$$
$$= \lambda^2 - (a+d)\lambda + (ad-bc)$$

**For symmetric matrix**: All eigenvalues are real.

**Proof sketch**: For symmetric A, vᵀAv ∈ ℝ. If Av = λv, then ṽ = conj(v) satisfies:
v̄ᵀAv = ṽᵀ(λv) = λv̄ᵀv ∈ ℝ
For complex λ = α + iβ: (Av̄)ᵀv = λv̄ᵀv
Subtract conjugate: (A - λI)ᵀv = 0, hence λ = λ̄, so β = 0 ∎

### 4.3 Diagonalization

**A is diagonalizable** if ∃ invertible P such that:
$$A = PDP^{-1}$$

where D is diagonal with eigenvalues.

**P's columns are eigenvectors**.

**Condition**: A has n linearly independent eigenvectors.

**Geometric Interpretation**: Diagonalization finds basis where A acts as scaling.

### 4.4 Spectral Theorem

**Real Symmetric Matrices**: A = QΛQᵀ where Q is orthogonal (Qᵀ = Q⁻¹).

**Proof**:
1. All eigenvalues are real
2. Eigenvectors corresponding to different eigenvalues are orthogonal
3. For repeated eigenvalues, can choose orthonormal basis of eigenspace
4. Q has orthonormal columns, Λ diagonal with eigenvalues ∎

### 4.5 Power Iteration

**Algorithm for dominant eigenvector**:
```
v₀ = random unit vector
repeat:
    v_{k+1} = Av_k
    v_{k+1} = v_{k+1} / ||v_{k+1}||
    λ_{k+1} = v_kᵀ A v_k
until convergence
```

**Convergence**: v_k → eigenvector for largest |λ|

**Rate**: |λ₂/λ₁|ᵏ, where λ₁ > |λ₂| ≥ ...

### 4.6 QR Algorithm

**For computing all eigenvalues**:
```
A₀ = A
repeat for k = 0, 1, 2, ...:
    QR = qr(A_k)  (orthogonal decomposition)
    A_{k+1} = RQ
until A_k converges to quasi-triangular form
```

**Properties**:
- A_{k+1} is orthogonally similar to A_k: A_{k+1} = Q_kᵀ A_k Q_k
- A_k → quasi-upper-triangular with eigenvalues on diagonal

### 4.7 Applications

**Matrix Powers** (diagonalizable):
$$A^k = P D^k P^{-1} = P \text{diag}(\lambda_1^k, ..., \lambda_n^k) P^{-1}$$

**Computing Aᵏv** without full powers:
$$A^k \mathbf{v} = \sum_{i=1}^{n} c_i \lambda_i^k \mathbf{v}_i$$
where cᵢ from eigenvector decomposition.

**Stability Analysis** (discrete dynamical systems):
If all |λᵢ| < 1, then Aᵏ → 0 as k → ∞

---

## 5. Singular Value Decomposition

### 5.1 SVD Definition

**For any** A ∈ ℝᵐˣⁿ:
$$A = U \Sigma V^T$$

- U ∈ ℝᵐˣᵐ: columns are left singular vectors (eigenvectors of AAᵀ)
- Σ ∈ ℝᵐˣⁿ: diagonal matrix σ₁ ≥ σ₂ ≥ ... ≥ 0 (singular values)
- V ∈ ℝⁿˣⁿ: columns are right singular vectors (eigenvectors of AᵀA)

**Singular values** are √(eigenvalues of AᵀA or AAᵀ).

### 5.2 Geometry of SVD

**Unit sphere** in ℝⁿ maps to **ellipsoid** in ℝᵐ.

- σ₁ = longest semi-axis
- σᵣ = shortest non-zero semi-axis
- vᵢ = direction of axis i in domain
- uᵢ = direction of axis i in codomain

**Row space** = span of rows = span of first r columns of V (r = rank)
**Column space** = span of columns = span of first r columns of U

### 5.3 Truncated SVD

**Best rank-k approximation**:
$$A_k = U_k \Sigma_k V_k^T = \sum_{i=1}^{k} \sigma_i \mathbf{u}_i \mathbf{v}_i^T$$

**Optimality** (Eckart-Young theorem):
$$\|A - A_k\|_F = \sqrt{\sum_{i=k+1}^{r} \sigma_i^2} = \min_{\tilde{A}: \text{rank}(\tilde{A})=k} \|A - \tilde{A}\|_F$$

### 5.4 Pseudoinverse

**Moore-Penrose Pseudoinverse**:
$$A^+ = V \Sigma^+ U^T$$

where Σ⁺ has reciprocal of non-zero singular values:
$$\Sigma^+_{ii} = \begin{cases} 1/\sigma_i & \text{if } \sigma_i > 0 \\ 0 & \text{otherwise} \end{cases}$$

**Solution to least squares**:
$$\mathbf{x}^* = A^+ \mathbf{b}$$

This minimizes ||A**x** - **b**||₂.

### 5.5 SVD and Eigenvalue Relationships

**AᵀA = V ΣᵀΣ Vᵀ**:
- Eigenvalues of AᵀA = σᵢ²
- Eigenvectors of AᵀA = columns of V

**AAᵀ = U ΣΣᵀ Uᵀ**:
- Eigenvalues of AAᵀ = σᵢ²  
- Eigenvectors of AAᵀ = columns of U

### 5.6 Norms via SVD

| Norm | Formula |
|------|---------|
| Spectral (2-norm) | ||A||₂ = σ₁ |
| Frobenius | ||A||_F = √(Σσᵢ²) |
| Nuclear | ||A||_* = Σσᵢ |
| Trace | tr(A) = Σ eigenvalues |

**Relationships**:
- ||A||_F² = ||A||₂² + ... + ||A||ₙ² = Σσᵢ²
- ||A||_* ≥ ||A||_F ≥ ||A||₂

---

## 6. Matrix Calculus

### 6.1 Derivatives with Respect to Vector

**Scalar function** f(**x**): ℝⁿ → ℝ

**Gradient**:
$$\nabla f(\mathbf{x}) = \begin{pmatrix} \frac{\partial f}{\partial x_1} \\ \frac{\partial f}{\partial x_2} \\ \vdots \\ \frac{\partial f}{\partial x_n} \end{pmatrix}$$

**Properties**:
- ∇(f + g) = ∇f + ∇g
- ∇(αf) = α∇f
- ∇(fg) = f∇g + g∇f (product rule)
- ∇(f/g) = (g∇f - f∇g)/g²

### 6.2 Hessian Matrix

**Second derivatives**:
$$H = \nabla^2 f = \begin{pmatrix} \frac{\partial^2 f}{\partial x_1^2} & \frac{\partial^2 f}{\partial x_1 \partial x_2} & \cdots \\ \frac{\partial^2 f}{\partial x_2 \partial x_1} & \frac{\partial^2 f}{\partial x_2^2} & \cdots \\ \vdots & \vdots & \ddots \end{pmatrix}$$

**Symmetry** (if continuous second partials): Hᵀ = H

**Second-order Taylor expansion**:
$$f(\mathbf{x} + \Delta\mathbf{x}) \approx f(\mathbf{x}) + \nabla f^T \Delta\mathbf{x} + \frac{1}{2} \Delta\mathbf{x}^T H \Delta\mathbf{x}$$

### 6.3 Matrix Derivatives

**Derivative of scalar with respect to matrix**:

$$\frac{\partial f}{\partial X_{ij}} = \text{element }(i,j) \text{ of } \frac{\partial f}{\partial X}$$

**Key Rules**:

1. $\frac{\partial}{\partial X} \text{tr}(AX) = A^T$

2. $\frac{\partial}{\partial X} \text{tr}(X^T A) = A$

3. $\frac{\partial}{\partial X} \text{tr}(AXB) = A^T B^T$

4. $\frac{\partial}{\partial X} \text{tr}(AX^T B) = BA$

5. $\frac{\partial}{\partial X} \log\det(X) = (X^{-1})^T$

6. $\frac{\partial}{\partial X} \|AX - B\|_F^2 = 2A^T(AX - B)$

### 6.4 Common Gradient Formulas

| Function | Gradient |
|----------|----------|
| **a**ᵀ**x** | **a** |
| **x**ᵀA**x** | (A + Aᵀ)**x** |
| ||**x** - **a**||² | 2(**x** - **a**) |
| ||X||_F² | 2X |
| tr(XᵀX) | 2X |
| tr(XᵀAX) | (A + Aᵀ)X |

### 6.5 Chain Rule

**For composition**: y = f(g(**x**))

$$\nabla_{\mathbf{x}} y = (J_g)^T \nabla_y f$$

where J_g is the Jacobian of g.

**Matrix chain rule**: For Y = f(X):
$$\frac{\partial y}{\partial X_{ij}} = \text{tr}\left(\left(\frac{\partial y}{\partial Y}\right)^T \frac{\partial Y}{\partial X_{ij}}\right)$$

### 6.6 Vector-Jacobian Products

**Efficient computation** in backpropagation:

$$\mathbf{g}^T \frac{\partial \mathbf{f}}{\partial \mathbf{x}} = \left(\frac{\partial \mathbf{f}}{\partial \mathbf{x}}\right)^T \mathbf{g}$$

For linear transform y = Ax:
$$\mathbf{g}^T \frac{\partial \mathbf{y}}{\partial \mathbf{x}} = \mathbf{g}^T A$$

---

## 7. Special Identities

### 7.1 Matrix Inversion Lemma

**Woodbury Identity** (derived from block matrix inversion):
$$(A + UCV)^{-1} = A^{-1} - A^{-1}U(C^{-1} + VA^{-1}U)^{-1}VA^{-1}$$

**Special cases**:

**Sherman-Morrison** (rank-1):
$$(A + \mathbf{u}\mathbf{v}^T)^{-1} = A^{-1} - \frac{A^{-1}\mathbf{u}\mathbf{v}^TA^{-1}}{1 + \mathbf{v}^TA^{-1}\mathbf{u}}$$

**Binomial inverse theorem**:
$$(A + \mathbf{u}\mathbf{v}^T)^{-1} = A^{-1} - \frac{A^{-1}\mathbf{u}\mathbf{v}^TA^{-1}}{\mathbf{v}^TA^{-1}\mathbf{u}}$$

### 7.2 Block Matrix Identities

**Block inverse** (for partitioned matrices):
$$\begin{pmatrix} A & B \\ C & D \end{pmatrix}^{-1} = \begin{pmatrix} A^{-1} + A^{-1}BS^{-1}CA^{-1} & -A^{-1}BS^{-1} \\ -S^{-1}CA^{-1} & S^{-1} \end{pmatrix}$$

where S = D - CA⁻¹B (Schur complement of A).

**Also**:
$$\begin{pmatrix} A & B \\ C & D \end{pmatrix}^{-1} = \begin{pmatrix} S_D^{-1} & -S_D^{-1}BD^{-1} \\ -D^{-1}CS_D^{-1} & D^{-1} + D^{-1}CS_D^{-1}BD^{-1} \end{pmatrix}$$

where S_D = A - BD⁻¹C (Schur complement of D).

### 7.3 Kronecker Products

**Definition**: For A ∈ ℝᵐˣⁿ, B ∈ ℝᵖˣᵍ:
$$A \otimes B = \begin{pmatrix} a_{11}B & a_{12}B & \cdots & a_{1n}B \\ a_{21}B & a_{22}B & \cdots & a_{2n}B \\ \vdots & \vdots & \ddots & \vdots \\ a_{m1}B & a_{m2}B & \cdots & a_{mn}B \end{pmatrix} \in \mathbb{R}^{mp \times nq}$$

**Properties**:
- (A ⊗ B)(C ⊗ D) = (AC) ⊗ (BD) when compatible
- (A ⊗ B)⁻¹ = A⁻¹ ⊗ B⁻¹
- (A ⊗ B)ᵀ = Aᵀ ⊗ Bᵀ
- tr(A ⊗ B) = tr(A)tr(B)
- det(A ⊗ B) = det(A)ᵖ det(B)ᵐ

### 7.4 Vectorization

**vec(X)** stacks columns of X into a single vector.

**Key identity**:
$$\text{vec}(AXB) = (B^T \otimes A) \text{vec}(X)$$

**Application**: Converts matrix equations to vector equations.

### 7.5 Trace Identities

- tr(A + B) = tr(A) + tr(B)
- tr(AB) = tr(BA) (cyclic)
- tr(ABC) = tr(BCA) = tr(CAB)
- tr(Aᵀ) = tr(A)
- tr(AAᵀ) = ||A||_F²

### 7.6 Norm Inequalities

**Matrix norms**:
1. ||A||₂ ≤ ||A||_F ≤ √n ||A||₂
2. ||A||_F ≤ ||A||_* ≤ √r ||A||_F (r = rank)
3. ||A + B||_F ≤ ||A||_F + ||B||_F
4. ||AB||_F ≤ ||A||₂ ||B||_F
5. ||AB||_F ≤ ||A||_F ||B||₂

---

## 8. Proofs and Derivations

### 8.1 Least Squares Normal Equations

**Problem**: Minimize ||A**x** - **b**||₂²

**Solution**: Set gradient to zero:
$$\nabla_{\mathbf{x}} \|A\mathbf{x} - \mathbf{b}\|_2^2 = 2A^T(A\mathbf{x} - \mathbf{b}) = \mathbf{0}$$

**Normal equations**:
$$A^T A \mathbf{x} = A^T \mathbf{b}$$

**Solution**:
$$\mathbf{x} = (A^T A)^{-1} A^T \mathbf{b} = A^+ \mathbf{b}$$

**Conditions**: 
- If A has full column rank → unique solution
- If A is not full rank → minimum norm solution via pseudoinverse

### 8.2 PCA Derivation

**Goal**: Find orthogonal projection that maximizes variance.

**Data matrix** X ∈ ℝⁿˣᵈ (n samples, d features)

**Centered**: X̃ = X - μ (μ = mean)

**Variance along direction w**:
$$V(\mathbf{w}) = \frac{1}{n-1} \|\tilde{X}\mathbf{w}\|^2 = \frac{1}{n-1} \mathbf{w}^T \tilde{X}^T \tilde{X} \mathbf{w}$$

**Constraint**: ||**w**|| = 1

**Lagrangian**:
$$\mathcal{L} = \mathbf{w}^T S \mathbf{w} - \lambda(\mathbf{w}^T \mathbf{w} - 1)$$

where S = X̃ᵀX̃/(n-1) is sample covariance.

**Optimality condition**:
$$\frac{\partial \mathcal{L}}{\partial \mathbf{w}} = 2S\mathbf{w} - 2\lambda\mathbf{w} = 0$$
$$S\mathbf{w} = \lambda \mathbf{w}$$

**Conclusion**: **w** must be eigenvector of covariance matrix S!

**Interpretation**:
- λ = variance along **w**
- Maximize variance → largest eigenvalue
- Principal components = eigenvectors sorted by eigenvalue

### 8.3 SVD-PCA Connection

**Covariance matrix**: S = XᵀX/(n-1)

**SVD of X**: X = UΣVᵀ

**Covariance**: S = VΣ²Vᵀ/(n-1)

**Eigenvectors of S**: V (right singular vectors of X)

**Eigenvalues**: σᵢ²/(n-1)

### 8.4 Eigenvalue Properties Proof

**Sum of eigenvalues = trace**:

From characteristic polynomial:
$$p(\lambda) = (-1)^n \lambda^n + (-1)^{n-1} \text{tr}(A) \lambda^{n-1} + ... + \det(A)$$

Coefficient of λⁿ⁻¹ = -trace(A) = -Σλᵢ

Therefore: Σλᵢ = trace(A) ∎

**Product of eigenvalues = determinant**:

p(0) = det(-A) = (-1)ⁿ det(A) = (-1)ⁿ Πλᵢ

Therefore: det(A) = Πλᵢ ∎

### 8.5 Cauchy Interlace Theorem

**Statement**: If B is a principal m×m submatrix of A ∈ ℝⁿˣⁿ, then:
$$\lambda_k(A) \leq \lambda_k(B) \leq \lambda_{k+n-m}(A)$$
for k = 1, ..., m

**Interpretation**: Eigenvalues of submatrix interlace with full matrix.

**Application**: Adding rows/columns changes eigenvalues predictably.

### 8.6 Sherman-Weyl Inequalities

**For singular values**: λᵢ(AAᵀ) = σᵢ²(A)

**Weyl's theorem**: For A, B symmetric:
$$\lambda_i(A) + \lambda_j(B) \leq \lambda_{i+j}(A + B) \leq \lambda_i(A) + \lambda_n(B)$$

**Application**: Perturbation bounds for eigenvalues.

---

## Practice Problems

### Level 1: Computation

1. Compute eigenvalues of [[2, 1], [1, 2]]
2. Find SVD of [[3, 1], [1, 3]]
3. Verify A⁻¹A = I for 3×3 matrix
4. Compute condition number of [[1, 2], [2, 4.01]]

### Level 2: Proofs

1. Prove (AB)⁻¹ = B⁻¹A⁻¹
2. Show det(A) = 0 iff A is singular
3. Prove Cauchy-Schwarz using determinants
4. Derive gradient of ||Ax - b||²

### Level 3: Applications

1. Show how SVD solves least squares
2. Derive PCA from eigenvectors
3. Explain why Cholesky requires SPD
4. Compute eigenvalues of covariance matrix

---

## Summary

This mathematical foundation covers:

- **Vector spaces**: Inner products, norms, orthogonalization
- **Matrix algebra**: All operations, special matrices
- **Determinants**: Properties, computation, inverse formulas
- **Eigenvalues**: Theory, algorithms, applications
- **SVD**: Full and truncated decompositions
- **Calculus**: Gradients, Hessians, matrix derivatives
- **Special identities**: Woodbury, Sherman-Morrison, Kronecker

These form the mathematical backbone for all ML algorithms.