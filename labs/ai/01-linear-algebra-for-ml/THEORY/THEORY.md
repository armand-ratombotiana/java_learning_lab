# Linear Algebra for Machine Learning - Complete Theory

## Table of Contents
1. [Foundations of Linear Algebra](#1-foundations-of-linear-algebra)
2. [Vectors and Vector Spaces](#2-vectors-and-vector-spaces)
3. [Matrices and Matrix Operations](#3-matrices-and-matrix-operations)
4. [Systems of Linear Equations](#4-systems-of-linear-equations)
5. [Linear Transformations](#5-linear-transformations)
6. [Eigenvalues and Eigenvectors](#6-eigenvalues-and-eigenvectors)
7. [Singular Value Decomposition](#7-singular-value-decomposition)
8. [Matrix Decompositions](#8-matrix-decompositions)
9. [Vector Calculus for ML](#9-vector-calculus-for-ml)
10. [Applications in Machine Learning](#10-applications-in-machine-learning)

---

## 1. Foundations of Linear Algebra

### 1.1 What is Linear Algebra?

Linear algebra is the branch of mathematics concerning linear equations, linear functions, and their representations through matrices and vector spaces. In machine learning, it provides the mathematical framework for:

- **Data Representation**: Vectors and matrices for storing features and samples
- **Transformations**: Linear transformations for feature engineering
- **Optimization**: Matrix operations for gradient descent and backpropagation
- **Dimensionality Reduction**: Eigenvalue-based methods for PCA, SVD

### 1.2 Historical Context

Linear algebra has evolved from solving systems of linear equations (ancient mathematics) to a fundamental tool in modern computing and machine learning. Key milestones:

- **1693**: Leibniz introduces determinants
- **1750**: Cramer's rule for solving linear systems
- **1800s**: Gauss develops elimination method
- **1850s**: Cayley formalizes matrix algebra
- **1930s-40s**: von Neumann develops functional analysis foundations
- **1960s**: Sparse matrix techniques for computational efficiency
- **1990s-2000s**: Applications in machine learning and data science

### 1.3 Mathematical Prerequisites

Before diving into linear algebra, ensure you understand:

- **Real numbers (ℝ)**: The set of all real numbers
- **Functions**: Mappings from inputs to outputs
- **Basic calculus**: Derivatives and integrals (for later sections)
- **Set theory**: Basic operations on sets

---

## 2. Vectors and Vector Spaces

### 2.1 Definition of Vectors

A vector is an ordered tuple of numbers that represents both magnitude and direction in n-dimensional space.

**Notation**: 
- Bold lowercase: **v** or $\vec{v}$
- Component notation: $[v_1, v_2, ..., v_n]^T$
- Column vector: $\begin{pmatrix} v_1 \\ v_2 \\ \vdots \\ v_n \end{pmatrix}$

**Example**: A feature vector for a house with 3 features:
$$\vec{x} = \begin{pmatrix} 1500 \\ 3 \\ 20 \end{pmatrix} \text{(size, rooms, age)}$$

### 2.2 Types of Vectors

| Type | Definition | Example |
|------|------------|---------|
| Zero Vector | All components are 0 | **0** = [0, 0, 0] |
| Unit Vector | Magnitude = 1 | $\hat{i}$ = [1, 0, 0] |
| Row Vector | Horizontal arrangement | [1, 2, 3] |
| Column Vector | Vertical arrangement | [1, 2, 3]^T |
| Sparse Vector | Most components are zero | [1, 0, 0, 5, 0, 0] |

### 2.3 Vector Operations

#### Addition
$$\vec{a} + \vec{b} = [a_1 + b_1, a_2 + b_2, ..., a_n + b_n]^T$$

**Properties**:
- Commutative: $\vec{a} + \vec{b} = \vec{b} + \vec{a}$
- Associative: $(\vec{a} + \vec{b}) + \vec{c} = \vec{a} + (\vec{b} + \vec{c})$
- Identity: $\vec{a} + \vec{0} = \vec{a}$
- Inverse: $\vec{a} + (-\vec{a}) = \vec{0}$

#### Scalar Multiplication
$$c \cdot \vec{v} = [c \cdot v_1, c \cdot v_2, ..., c \cdot v_n]^T$$

#### Dot Product (Inner Product)
$$\vec{a} \cdot \vec{b} = \sum_{i=1}^{n} a_i b_i = \|\vec{a}\| \|\vec{b}\| \cos(\theta)$$

**Properties**:
- Commutative: $\vec{a} \cdot \vec{b} = \vec{b} \cdot \vec{a}$
- Distributive: $\vec{a} \cdot (\vec{b} + \vec{c}) = \vec{a} \cdot \vec{b} + \vec{a} \cdot \vec{c}$
- Scalar multiplication: $(c\vec{a}) \cdot \vec{b} = c(\vec{a} \cdot \vec{b})$

**ML Application**: Measuring similarity between vectors (word embeddings, document vectors)

#### Cross Product (3D only)
$$\vec{a} \times \vec{b} = \begin{pmatrix} a_2 b_3 - a_3 b_2 \\ a_3 b_1 - a_1 b_3 \\ a_1 b_2 - a_2 b_1 \end{pmatrix}$$

**Properties**:
- Produces a vector perpendicular to both $\vec{a}$ and $\vec{b}$
- Not commutative: $\vec{a} \times \vec{b} = -(\vec{b} \times \vec{a})$
- Magnitude: $\|\vec{a} \times \vec{b}\| = \|\vec{a}\| \|\vec{b}\| \sin(\theta)$

### 2.4 Vector Norms

A norm is a function that assigns a positive length to each vector.

#### L1 Norm (Manhattan/Taxicab)
$$\|\vec{x}\|_1 = \sum_{i=1}^{n} |x_i|$$

**Properties**:
- Sensitive to outliers
- Used in LASSO regularization (sparsity)
- Measures "city block" distance

#### L2 Norm (Euclidean)
$$\|\vec{x}\|_2 = \sqrt{\sum_{i=1}^{n} x_i^2}$$

**Properties**:
- Most common norm
- Used in Ridge regularization
- Invariant to rotations
- Measures straight-line distance

#### Lp Norm
$$\|\vec{x}\|_p = \left(\sum_{i=1}^{n} |x_i|^p\right)^{1/p}$$

#### L∞ Norm (Infinity)
$$\|\vec{x}\|_\infty = \max_i |x_i|$$

**Properties**:
- Used in max-pooling operations
- Robust to outliers up to certain threshold

### 2.5 Vector Spaces

A vector space V over a field F consists of:
1. A set of vectors (closure under addition and scalar multiplication)
2. An additive identity (zero vector)
3. Additive inverses for every vector

**Properties**:
- Closure under addition: $\vec{u}, \vec{v} \in V \Rightarrow \vec{u} + \vec{v} \in V$
- Closure under scalar multiplication: $\vec{v} \in V, c \in F \Rightarrow c\vec{v} \in V$

#### Subspaces
A subset W of a vector space V is a subspace if:
1. $\vec{0} \in W$
2. Closed under addition: $\vec{u}, \vec{w} \in W \Rightarrow \vec{u} + \vec{w} \in W$
3. Closed under scalar multiplication: $\vec{w} \in W, c \in F \Rightarrow c\vec{w} \in W$

### 2.6 Linear Independence and Span

#### Linear Combination
A vector $\vec{v}$ is a linear combination of vectors $\{\vec{v}_1, ..., \vec{v}_k\}$ if:
$$\vec{v} = c_1\vec{v}_1 + c_2\vec{v}_2 + ... + c_k\vec{v}_k$$

#### Span
The span of a set of vectors is the set of all linear combinations:
$$span\{\vec{v}_1, ..., \vec{v}_k\} = \{c_1\vec{v}_1 + ... + c_k\vec{v}_k : c_i \in \mathbb{R}\}$$

#### Linear Independence
Vectors $\{\vec{v}_1, ..., \vec{v}_k\}$ are linearly independent if:
$$c_1\vec{v}_1 + ... + c_k\vec{v}_k = \vec{0} \Rightarrow c_1 = c_2 = ... = c_k = 0$$

**Intuition**: No vector in the set can be written as a combination of the others.

**ML Application**: Feature selection - identify linearly independent features to avoid redundancy.

### 2.7 Basis and Dimension

#### Basis
A set of vectors B is a basis for vector space V if:
1. B is linearly independent
2. span(B) = V

**Standard Basis in ℝ^n**:
$$\hat{e}_1 = \begin{pmatrix} 1 \\ 0 \\ \vdots \\ 0 \end{pmatrix}, ..., \hat{e}_n = \begin{pmatrix} 0 \\ 0 \\ \vdots \\ 1 \end{pmatrix}$$

#### Dimension
The number of vectors in any basis of V (must be consistent).

**Examples**:
- ℝ^n has dimension n
- Space of all polynomials of degree ≤ 2 has dimension 3

### 2.8 Orthogonality

#### Orthogonal Vectors
$\vec{u}$ and $\vec{v}$ are orthogonal if $\vec{u} \cdot \vec{v} = 0$

#### Orthonormal Set
A set of vectors $\{\vec{v}_1, ..., \vec{v}_k\}$ is orthonormal if:
1. Each vector has unit norm: $\|\vec{v}_i\| = 1$
2. All pairs are orthogonal: $\vec{v}_i \cdot \vec{v}_j = 0$ for $i \neq j$

**Gram-Schmidt Process**: Convert any basis to an orthonormal basis.

**ML Application**: 
- Feature decorrelation in PCA
- Orthogonal weight matrices in neural networks

---

## 3. Matrices and Matrix Operations

### 3.1 Definition of Matrices

A matrix is a rectangular array of numbers arranged in rows and columns.

**Notation**: 
- Capital letters: A, B, X
- Element notation: $A_{ij}$ (row i, column j)
- $A \in \mathbb{R}^{m \times n}$ means m rows, n columns

**Example**: Dataset with 100 samples, 5 features
$$A \in \mathbb{R}^{100 \times 5} = \begin{pmatrix} a_{11} & a_{12} & a_{13} & a_{14} & a_{15} \\ a_{21} & a_{22} & a_{23} & a_{24} & a_{25} \\ \vdots & \vdots & \vdots & \vdots & \vdots \\ a_{100,1} & a_{100,2} & a_{100,3} & a_{100,4} & a_{100,5} \end{pmatrix}$$

### 3.2 Types of Matrices

| Type | Definition | Example |
|------|------------|---------|
| Row Vector | 1 × n matrix | [1, 2, 3] |
| Column Vector | n × 1 matrix | [1; 2; 3] |
| Square Matrix | m = n | 3 × 3 |
| Rectangular Matrix | m ≠ n | 4 × 3 |
| Diagonal Matrix | $A_{ij} = 0$ for i ≠ j | diag(1, 2, 3) |
| Identity Matrix | Diagonal = 1 | I_n |
| Zero Matrix | All elements = 0 | 0_{m×n} |
| Upper Triangular | $A_{ij} = 0$ for i > j | |
| Lower Triangular | $A_{ij} = 0$ for i < j | |
| Symmetric Matrix | A = A^T | A_{ij} = A_{ji} |
| Skew-Symmetric | A^T = -A | |
| Positive Definite | x^T A x > 0 ∀ x ≠ 0 | Covariance matrices |
| Orthogonal Matrix | A^T A = I | Rotation matrices |

### 3.3 Matrix Operations

#### Addition and Subtraction
Only defined for matrices of the same dimensions:
$$C_{ij} = A_{ij} + B_{ij}$$

**Properties**:
- Commutative: A + B = B + A
- Associative: (A + B) + C = A + (B + C)
- Identity: A + 0 = A

#### Scalar Multiplication
$$(cA)_{ij} = c \cdot A_{ij}$$

#### Matrix Multiplication
For A ∈ ℝ^{m×k} and B ∈ ℝ^{k×n}:
$$C = AB, \quad C_{ij} = \sum_{p=1}^{k} A_{ip} B_{pj}$$

**Important Properties**:
- Not commutative: AB ≠ BA generally
- Associative: (AB)C = A(BC)
- Distributive: A(B + C) = AB + AC
- Transpose: (AB)^T = B^T A^T

**Computational Complexity**: O(m·n·k) for standard multiplication

**ML Application**: Forward pass in neural networks: Y = XW + b

#### Element-wise Operations (Hadamard Product)
$$C_{ij} = A_{ij} \cdot B_{ij}$$

### 3.4 Transpose

The transpose of a matrix flips rows and columns:
$$(A^T)_{ij} = A_{ji}$$

**Properties**:
- (A^T)^T = A
- (A + B)^T = A^T + B^T
- (AB)^T = B^T A^T
- (cA)^T = cA^T

### 3.5 Trace

The trace is the sum of diagonal elements:
$$tr(A) = \sum_{i=1}^{n} A_{ii}$$

**Properties**:
- tr(A + B) = tr(A) + tr(B)
- tr(cA) = c·tr(A)
- tr(AB) = tr(BA)
- tr(A^T) = tr(A)

**ML Application**: Regularization term tr(W^T W) = ||W||_F²

### 3.6 Determinant

The determinant is a scalar value computed from a square matrix.

**For 2×2 matrix**:
$$det(A) = \begin{vmatrix} a & b \\ c & d \end{vmatrix} = ad - bc$$

**For n×n matrix** (Laplace expansion):
$$det(A) = \sum_{j=1}^{n} (-1)^{i+j} a_{ij} det(M_{ij})$$

where $M_{ij}$ is the minor (matrix after removing row i, column j).

**Properties**:
- det(I) = 1
- det(A^T) = det(A)
- det(AB) = det(A) · det(B)
- det(A^{-1}) = 1/det(A)
- det(cA) = c^n det(A) for n×n matrix
- A invertible ⟺ det(A) ≠ 0

**ML Application**: 
- Computing volume in changes of variables
- Evaluating model likelihoods

### 3.7 Matrix Inverse

A matrix A is invertible if there exists a matrix A^{-1} such that:
$$AA^{-1} = A^{-1}A = I$$

**Properties**:
- (A^{-1})^{-1} = A
- (AB)^{-1} = B^{-1}A^{-1}
- (A^{-1})^T = (A^T)^{-1} = A^{-T}

**Computation**:
1. Adjugate method: $A^{-1} = \frac{1}{det(A)} adj(A)$
2. Gaussian elimination
3. LU decomposition (for numerical stability)

**ML Application**: Solving normal equations in linear regression

### 3.8 Special Matrices

#### Identity Matrix
$$I_n = \begin{pmatrix} 1 & 0 & \cdots & 0 \\ 0 & 1 & \cdots & 0 \\ \vdots & \vdots & \ddots & \vdots \\ 0 & 0 & \cdots & 1 \end{pmatrix}$$

Properties:
- AI = IA = A
- Columns are standard basis vectors

#### Diagonal Matrix
$$D = diag(d_1, d_2, ..., d_n) = \begin{pmatrix} d_1 & 0 & \cdots & 0 \\ 0 & d_2 & \cdots & 0 \\ \vdots & \vdots & \ddots & \vdots \\ 0 & 0 & \cdots & d_n \end{pmatrix}$$

Properties:
- D^T = D
- D^{-1} = diag(1/d_1, 1/d_2, ..., 1/d_n)
- det(D) = ∏ d_i

#### Permutation Matrix
Square matrix with exactly one 1 in each row and column, 0 elsewhere.
- Multiplying by P permutes rows/columns
- P^T = P^{-1}

#### Block Matrices
Matrices partitioned into submatrices:
$$A = \begin{pmatrix} A_{11} & A_{12} \\ A_{21} & A_{22} \end{pmatrix}$$

Addition and multiplication follow similar rules with block-wise operations.

---

## 4. Systems of Linear Equations

### 4.1 Representation

A system of m linear equations in n unknowns:
$$\begin{aligned} a_{11}x_1 + a_{12}x_2 + ... + a_{1n}x_n &= b_1 \\ a_{21}x_1 + a_{22}x_2 + ... + a_{2n}x_n &= b_2 \\ ... \\ a_{m1}x_1 + a_{m2}x_2 + ... + a_{mn}x_n &= b_m \end{aligned}$$

**Matrix form**: Ax = b
$$A \in \mathbb{R}^{m \times n}, \quad x \in \mathbb{R}^{n}, \quad b \in \mathbb{R}^{m}$$

### 4.2 Solution Types

- **Unique solution**: Exactly one solution (m = n, A invertible)
- **No solution**: Inconsistent system
- **Infinitely many solutions**: Underdetermined system (m < n)

### 4.3 Solution Methods

#### Gaussian Elimination
Transform to row echelon form using elementary row operations.

**Elementary Row Operations**:
1. Swap two rows
2. Multiply a row by a non-zero scalar
3. Add a multiple of one row to another

#### Gauss-Jordan Elimination
Continue to reduced row echelon form (RREF).

#### LU Decomposition
A = LU where L is lower triangular, U is upper triangular.
- More efficient for multiple right-hand sides
- Used in solving linear systems repeatedly

### 4.4 Solution Existence (Rank Analysis)

- **Rank of A** = number of linearly independent rows
- **Augmented matrix rank** = rank of [A | b]

| Condition | Solution |
|-----------|----------|
| rank(A) = rank([A\|b]) = n | Unique solution |
| rank(A) = rank([A\|b]) < n | Infinite solutions |
| rank(A) < rank([A\|b]) | No solution |

### 4.5 Overdetermined and Underdetermined Systems

#### Overdetermined (m > n)
More equations than unknowns.
- Usually no exact solution
- Find best fit (least squares)

#### Underdetermined (m < n)
More unknowns than equations.
- Infinite solutions
- Find solution with additional constraints (e.g., minimum norm)

### 4.6 ML Application: Linear Regression

$$y = X\beta + \epsilon$$

Normal equations for OLS solution:
$$\beta = (X^T X)^{-1} X^T y$$

**Conditions**:
- X^T X must be invertible (full column rank)
- If not, use pseudoinverse or regularization

---

## 5. Linear Transformations

### 5.1 Definition

A linear transformation T: V → W satisfies:
1. T(u + v) = T(u) + T(v) (additivity)
2. T(cv) = cT(v) (homogeneity)

for all u, v ∈ V and c ∈ ℝ.

### 5.2 Matrix Representation

Every linear transformation T: ℝ^n → ℝ^m can be represented by a matrix A ∈ ℝ^{m×n}:
$$T(\vec{x}) = A\vec{x}$$

### 5.3 Common Transformations

#### Scaling
$$S = \begin{pmatrix} s_x & 0 \\ 0 & s_y \end{pmatrix}$$

#### Rotation
$$R(\theta) = \begin{pmatrix} \cos\theta & -\sin\theta \\ \sin\theta & \cos\theta \end{pmatrix}$$

#### Reflection
$$R_x = \begin{pmatrix} 1 & 0 \\ 0 & -1 \end{pmatrix} \text{ (x-axis)}$$

#### Shear
$$SH = \begin{pmatrix} 1 & k \\ 0 & 1 \end{pmatrix}$$

#### Projection
$$P = \begin{pmatrix} 1 & 0 \\ 0 & 0 \end{pmatrix} \text{ (onto x-axis)}$$

### 5.4 Properties Preserved by Linear Transformations

1. Origin maps to origin
2. Lines remain lines (may map to lines or points)
3. Parallel lines remain parallel
4. Linear combinations preserved: T(c₁v₁ + c₂v₂) = c₁T(v₁) + c₂T(v₂)

### 5.5 Change of Basis

For a basis B = {v₁, ..., vₙ}, any vector x can be written as:
$$\vec{x} = c_1\vec{v}_1 + ... + c_n\vec{v}_n$$

The coordinate vector [x]_B = [c₁, ..., cₙ]^T.

**Transition matrix**: P_B = [v₁, v₂, ..., vₙ]

### 5.6 Kernel and Image

#### Kernel (Null Space)
$$ker(T) = \{\vec{v} \in V : T(\vec{v}) = \vec{0}\}$$

#### Image (Range)
$$im(T) = \{T(\vec{v}) : \vec{v} \in V\}$$

**Dimension Theorem**: dim(ker(T)) + dim(im(T)) = dim(V)

### 5.7 ML Applications

- **Feature transformations**: PCA, autoencoders
- **Data augmentation**: Rotations, scaling in images
- **Embedding spaces**: Word2Vec, neural embeddings
- **Coordinate systems**: Different reference frames

---

## 6. Eigenvalues and Eigenvectors

### 6.1 Definition

For a square matrix A ∈ ℝ^{n×n}, a non-zero vector v and scalar λ such that:
$$Av = \lambda v$$

- v is an **eigenvector** (characteristic vector)
- λ is an **eigenvalue** (characteristic value)

**Intuition**: The transformation A only scales v, doesn't change its direction.

### 6.2 Characteristic Equation

$$Av = \lambda v \Rightarrow (A - \lambda I)v = 0$$

For non-trivial solution (v ≠ 0):
$$det(A - \lambda I) = 0$$

This is the **characteristic polynomial** of degree n.

### 6.3 Eigenvalue Properties

1. Sum of eigenvalues = trace(A)
$$\sum_{i=1}^{n} \lambda_i = tr(A)$$

2. Product of eigenvalues = det(A)
$$\prod_{i=1}^{n} \lambda_i = det(A)$$

3. Number of eigenvalues (counting multiplicity) = n

4. Eigenspace: {v : Av = λv} is a subspace

### 6.4 Diagonalization

A matrix A is **diagonalizable** if:
$$A = PDP^{-1}$$

where D is diagonal and P's columns are eigenvectors.

**Condition**: A has n linearly independent eigenvectors.

**Spectral Theorem**: 
- Real symmetric matrices always diagonalizable
- A = QΛQ^T where Q is orthogonal

### 6.5 Computing Eigenvalues

#### Direct Method (small matrices)
1. Compute characteristic polynomial
2. Find roots

#### Power Iteration (largest eigenvalue)
```
v₀ = random unit vector
repeat:
    v_{k+1} = Av_k
    v_{k+1} = v_{k+1} / ||v_{k+1}||
    λ_{k+1} = v_k^T Av_k
until convergence
```

#### QR Algorithm
Iterative method for all eigenvalues:
$$A_k = Q_k R_k, \quad A_{k+1} = R_k Q_k$$

Under certain conditions, A_k → diagonal matrix with eigenvalues.

### 6.6 Special Eigenvalue Cases

#### Defective Matrix
Not enough eigenvectors for diagonalization (requires Jordan form).

#### Positive Definite
All eigenvalues > 0.

#### Semidefinite
All eigenvalues ≥ 0.

#### Spectral Radius
$$\rho(A) = \max_i |\lambda_i|$$

**Important**: Powers of A converge based on spectral radius.

### 6.7 ML Applications

#### Principal Component Analysis (PCA)
- Eigenvectors of covariance matrix
- Directions of maximum variance
- Dimensionality reduction

#### Spectral Clustering
- Laplacian matrix eigenvectors
- Graph-based clustering

#### PageRank
- Dominant eigenvector of transition matrix
- Ranking nodes in a graph

#### Neural Networks
- Eigenvalue decay in weight matrices
- Spectral norm regularization
- Gradient flow analysis

---

## 7. Singular Value Decomposition (SVD)

### 7.1 Definition

For any matrix A ∈ ℝ^{m×n}, the SVD decomposes it as:
$$A = U \Sigma V^T$$

where:
- U ∈ ℝ^{m×m}: Left singular vectors (columns are orthonormal eigenvectors of AA^T)
- Σ ∈ ℝ^{m×n}: Diagonal matrix of singular values σ₁ ≥ σ₂ ≥ ... ≥ 0
- V ∈ ℝ^{n×n}: Right singular vectors (columns are orthonormal eigenvectors of A^T A)

### 7.2 Geometry of SVD

**Singular values** represent principal axes of an ellipsoid:
- A maps the unit sphere in ℝ^n to an ellipsoid in ℝ^m
- σ₁, σ₂, ..., σ_r are the lengths of the semi-axes
- v_i are directions in the input space
- u_i are directions in the output space

$$Av_i = \sigma_i u_i$$

### 7.3 Truncated SVD

Keep only top-k singular values for dimensionality reduction:
$$A \approx U_k \Sigma_k V_k^T$$

where:
- U_k ∈ ℝ^{m×k}
- Σ_k ∈ ℝ^{k×k}
- V_k ∈ ℝ^{n×k}

**Properties**:
- Best rank-k approximation in Frobenius norm
- A_k = argmin_{B: rank(B)=k} ||A - B||_F

### 7.4 Pseudoinverse (Moore-Penrose)

$$A^+ = V \Sigma^+ U^T$$

where Σ^+ has reciprocal of non-zero singular values:
$$\Sigma^+_{ii} = \begin{cases} 1/\sigma_i & \text{if } \sigma_i > 0 \\ 0 & \text{otherwise} \end{cases}$$

**Properties**:
- $A A^+ A = A$
- $A^+ A A^+ = A^+$
- $(A A^+)^T = A A^+$
- $(A^+ A)^T = A^+ A$

**Application**: Solve least squares problems
$$\vec{x} = A^+ \vec{b}$$

### 7.5 Matrix Norms via SVD

- **Spectral norm**: ||A||₂ = σ₁ (largest singular value)
- **Frobenius norm**: ||A||_F = √(Σσᵢ²)
- **Nuclear norm**: ||A||_* = Σσᵢ (sum of singular values)

### 7.6 ML Applications

#### Dimensionality Reduction
- Latent Semantic Analysis (LSA)
- Recommendation systems
- Image compression

#### Collaborative Filtering
- User-item matrix decomposition
- Matrix completion with low-rank assumption

#### Data Denoising
- Remove noise by truncating small singular values
- Keep top-k components preserving most variance

#### Pseudo-inverse in Linear Regression
- Handles singular X^T X
- Numerical stability

#### Condition Number
$$\kappa(A) = \frac{\sigma_{\max}}{\sigma_{\min}}$$
- Measures sensitivity of linear system solution
- High condition number → ill-conditioned

---

## 8. Matrix Decompositions

### 8.1 LU Decomposition

A = LU where:
- L: Lower triangular with unit diagonal
- U: Upper triangular

**Applications**:
- Solving Ax = b for multiple b
- Computing determinant: det(A) = det(L)·det(U) = ∏ diag(U)

**Pivot (PLU)**: PA = LU for numerical stability

### 8.2 Cholesky Decomposition

For symmetric positive definite A:
$$A = LL^T$$

where L is lower triangular with positive diagonal.

**Properties**:
- Unique for SPD matrices
- Faster than LU (half operations)
- Used in Kalman filters, least squares

### 8.3 QR Decomposition

A = QR where:
- Q: Orthogonal (Q^T Q = I)
- R: Upper triangular

**Applications**:
- Solving least squares
- Gram-Schmidt orthonormalization
- Eigenvalue algorithms (Householder reflections)

**Two methods**:
1. Gram-Schmidt (numerically unstable)
2. Householder reflections (stable)
3. Givens rotations (sparse-friendly)

### 8.4 Eigendecomposition

For diagonalizable A:
$$A = Q \Lambda Q^{-1}$$

where Q's columns are eigenvectors, Λ is diagonal with eigenvalues.

**Special case - Symmetric matrices**:
$$A = Q \Lambda Q^T$$
(Q orthogonal, Λ real)

### 8.5 Comparison of Decompositions

| Decomposition | Form | Conditions | Use Case |
|---------------|------|------------|----------|
| LU | A = LU | Square, invertible | Solve linear systems |
| Cholesky | A = LL^T | SPD | Faster solve, Kalman |
| QR | A = QR | Any | LSQ, orthonormalization |
| Eigendecomp | A = QΛQ^{-1} | Diagonalizable | Spectral analysis |
| SVD | A = UΣV^T | Any | Dimensionality reduction |

### 8.6 Block Matrix Operations

#### Block Diagonal
$$A = \begin{pmatrix} A_1 & 0 \\ 0 & A_2 \end{pmatrix}$$

Properties:
- det(A) = det(A₁)·det(A₂)
- A^k = diag(A₁^k, A₂^k)

#### Schur Complement
For block matrix:
$$A = \begin{pmatrix} P & Q \\ R & S \end{pmatrix}$$

Schur complement of P: S - RP^{-1}Q

**Application**: Matrix inversion lemma (Woodbury identity)

---

## 9. Vector Calculus for ML

### 9.1 Gradients

For f: ℝ^n → ℝ, gradient is:
$$\nabla f(\vec{x}) = \begin{pmatrix} \frac{\partial f}{\partial x_1} \\ \frac{\partial f}{\partial x_2} \\ \vdots \\ \frac{\partial f}{\partial x_n} \end{pmatrix}$$

**Properties**:
- Points in direction of steepest ascent
- ∇(f + g) = ∇f + ∇g
- ∇(cf) = c∇f
- ∇(fg) = f∇g + g∇f

### 9.2 Jacobian Matrix

For F: ℝ^n → ℝ^m:
$$J_F = \begin{pmatrix} \frac{\partial f_1}{\partial x_1} & \frac{\partial f_1}{\partial x_2} & \cdots & \frac{\partial f_1}{\partial x_n} \\ \frac{\partial f_2}{\partial x_1} & \frac{\partial f_2}{\partial x_2} & \cdots & \frac{\partial f_2}{\partial x_n} \\ \vdots & \vdots & \ddots & \vdots \\ \frac{\partial f_m}{\partial x_1} & \frac{\partial f_m}{\partial x_2} & \cdots & \frac{\partial f_m}{\partial x_n} \end{pmatrix}$$

### 9.3 Hessian Matrix

For f: ℝ^n → ℝ, second derivatives:
$$H = \nabla^2 f = \begin{pmatrix} \frac{\partial^2 f}{\partial x_1^2} & \frac{\partial^2 f}{\partial x_1 \partial x_2} & \cdots \\ \frac{\partial^2 f}{\partial x_2 \partial x_1} & \frac{\partial^2 f}{\partial x_2^2} & \cdots \\ \vdots & \vdots & \ddots \end{pmatrix}$$

**Properties**:
- Symmetric if second partials are continuous
- Used in Newton's method
- Positive definite → local minimum

### 9.4 Matrix Derivatives Rules

For A, B constant matrices, x, y vectors:

1. $\frac{\partial}{\partial \vec{x}} (A\vec{x}) = A^T$
2. $\frac{\partial}{\partial \vec{x}} (\vec{x}^T A) = A$
3. $\frac{\partial}{\partial \vec{x}} (\vec{x}^T A \vec{x}) = (A + A^T)\vec{x}$
4. $\frac{\partial}{\partial X} (tr(A X)) = A^T$
5. $\frac{\partial}{\partial X} (tr(X^T A)) = A$

### 9.5 Chain Rule

For compositions involving matrices:

$$\frac{\partial}{\partial \vec{x}} f(g(\vec{x})) = (J_g)^T \nabla f(g(\vec{x}))$$

**ML Application**: Backpropagation uses chain rule extensively.

### 9.6 Vector/Matrix Identities

1. ∇_x ||x - a||² = 2(x - a)
2. ∇_x x^T A x = (A + A^T)x
3. ∇_X log|X| = (X^{-1})^T
4. ∇_X tr(AX) = A^T

---

## 10. Applications in Machine Learning

### 10.1 Data Representation

#### Feature Matrices
- Rows = samples
- Columns = features
- X ∈ ℝ^{n×d}

#### One-hot Encoding
- Sparse matrix representation
- Used for categorical features

#### Embedding Matrices
- E ∈ ℝ^{vocab_size × embedding_dim}
- Maps discrete tokens to continuous vectors

### 10.2 Linear Models

#### Linear Regression
$$\hat{y} = X\beta$$
$$\beta = (X^T X)^{-1} X^T y$$

#### Logistic Regression
$$P(y=1|x) = \sigma(w^T x + b)$$

#### Support Vector Machines
$$f(x) = w^T \phi(x) + b$$

### 10.3 Neural Networks

#### Forward Pass
$$z^{(l)} = W^{(l)} a^{(l-1)} + b^{(l)}$$
$$a^{(l)} = \sigma(z^{(l)})$$

#### Parameter matrices
- W: weight matrix (n_l × n_{l-1})
- b: bias vector (n_l × 1)

#### Loss functions as matrix operations
- MSE: ||y - ŷ||²
- Cross-entropy: -y^T log(ŷ)

### 10.4 Dimensionality Reduction

#### PCA
1. Center data: X' = X - μ
2. Compute covariance: C = (X'^T X')/(n-1)
3. Eigendecomposition: C = QΛQ^T
4. Project: Y = X' Q_k

#### SVD-based
- Same as PCA using truncated SVD
- More numerically stable

### 10.5 Optimization

#### Gradient Descent
$$\theta_{t+1} = \theta_t - \alpha \nabla f(\theta_t)$$

#### Newton Method
$$\theta_{t+1} = \theta_t - H^{-1} \nabla f(\theta_t)$$

#### Conjugate Gradient
- Iterative for large sparse systems
- Uses matrix-vector products

### 10.6 Regularization

#### L2 (Ridge)
$$\mathcal{L}_{ridge} = \mathcal{L}_{original} + \lambda ||W||_F^2$$

#### L1 (Lasso)
$$\mathcal{L}_{lasso} = \mathcal{L}_{original} + \lambda ||W||_1$$

#### Elastic Net
$$\mathcal{L}_{elastic} = \mathcal{L}_{original} + \lambda_1 ||W||_1 + \lambda_2 ||W||_F^2$$

### 10.7 Matrix Factorization

#### Recommendation Systems
$$\hat{r}_{ui} = \mu + b_u + b_i + p_u^T q_i$$

#### Topic Models (NMF)
Find W, H such that V ≈ WH
- W: topics (k × vocab)
- H: document-topic distribution (docs × k)

### 10.8 Distance Metrics

#### Cosine Similarity
$$\cos(\vec{a}, \vec{b}) = \frac{\vec{a} \cdot \vec{b}}{||\vec{a}|| \cdot ||\vec{b}||}$$

#### Mahalanobis Distance
$$d_M(\vec{x}, \vec{y}) = \sqrt{(\vec{x} - \vec{y})^T S^{-1} (\vec{x} - \vec{y})}$$

### 10.9 Advanced Topics

#### Kronecker Products
Used in efficient matrix operations for structured neural networks.

#### Low-rank Approximations
- Random projections
- CUR decomposition
- Interpolative decomposition

#### Tensor Operations
- Higher-dimensional generalizations
- Reshaping, contracting, mode-n products

---

## Summary

Linear algebra is the mathematical language of machine learning. Key concepts to master:

1. **Vectors**: Operations, norms, orthogonalization
2. **Matrices**: Multiplication, inverses, decompositions
3. **Linear systems**: Solution methods, least squares
4. **Eigenvalues**: Spectral analysis, diagonalization
5. **SVD**: Low-rank approximations, pseudoinverse
6. **Calculus**: Gradients, Jacobians, Hessians

These foundations enable understanding and implementing:
- Neural network forward/backward passes
- Optimization algorithms
- Dimensionality reduction techniques
- Collaborative filtering systems

---

## Further Reading

1. **Linear Algebra Done Right** - Sheldon Axler
2. **Matrix Analysis** - Horn & Johnson
3. **Introduction to Linear Algebra** - Gilbert Strang
4. **Matrix Computations** - Golub & Van Loan
5. **Mathematics for Machine Learning** - Deisenroth et al.

---

## Practice Recommendations

1. Implement basic operations from scratch
2. Derive gradient expressions for linear models
3. Work through PCA and SVD calculations
4. Implement power iteration and QR algorithm
5. Apply matrix decompositions to real datasets