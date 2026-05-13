# Linear Algebra for ML - Mathematical Foundations

## 1. Vector Operations

### Dot Product
```
a · b = Σᵢ aᵢ * bᵢ = ||a|| * ||b|| * cos(θ)
```
- Measures similarity between vectors
- Used in cosine similarity, projections

### Cross Product (3D only)
```
a × b = (a₂b₃ - a₃b₂, a₃b₁ - a₁b₃, a₁b₂ - a₂b₁)
```
- Produces perpendicular vector
- Used in computing normals

### Vector Norms
- **L1 (Manhattan)**: ||x||₁ = Σ|xᵢ|
- **L2 (Euclidean)**: ||x||₂ = √(Σxᵢ²)
- **L∞ (Infinity)**: ||x||∞ = max|xᵢ|
- **p-norm**: ||x||ₚ = (Σ|xᵢ|ᵖ)^(1/p)

## 2. Matrix Operations

### Matrix Multiplication
```
C = A * B
Cᵢⱼ = Σₖ Aᵢₖ * Bₖⱼ
```
- Not commutative: AB ≠ BA
- Associative: (AB)C = A(BC)
- Distributive: A(B + C) = AB + AC

### Matrix Inverse
```
A * A⁻¹ = I
A⁻¹ = adj(A) / det(A)
```
- Only exists for non-singular matrices
- Used in solving linear systems

### Determinant
```
det(A) = Σⱼ(-1)ⁱ⁺ⱼ * aᵢⱼ * det(Mᵢⱼ)
```
- Volume scaling factor
- det(A) = 0 means singular (non-invertible)

## 3. Eigenvalue Computation

### Characteristic Polynomial
```
det(A - λI) = 0
```
For 2x2 matrix:
```
| a-λ  b   | = (a-λ)(d-λ) - bc = 0
| c   d-λ  |
λ² - (a+d)λ + (ad-bc) = 0
```

### Power Iteration (for largest eigenvalue)
```
v_(k+1) = A * v_k
v_(k+1) = v_(k+1) / ||v_(k+1)||
```

### QR Algorithm
For finding all eigenvalues iteratively.

## 4. SVD Computation

### Full SVD
```
A ∈ ℝ^(m×n) → U ∈ ℝ^(m×m), Σ ∈ ℝ^(m×n), V ∈ ℝ^(n×n)
```

### Compact SVD
```
A ∈ ℝ^(m×n) → U ∈ ℝ^(m×r), Σ ∈ ℝ^(r×r), V ∈ ℝ^(n×r)
```
where r = rank(A)

### Truncated SVD (for dimensionality reduction)
Keep top-k singular values:
```
A ≈ U_k * Σ_k * V_k^T
```

## 5. Matrix Calculus

### Derivative of scalar w.r.t. vector
```
∂(wᵀx) / ∂x = w
∂(xᵀAx) / ∂x = (A + Aᵀ)x
```

### Gradient of matrix function
```
∂/∂X ||AX - B||² = 2Aᵀ(AX - B)
```

## 6. Special Matrix Identities

### Woodbury Identity
```
(A + UCV)⁻¹ = A⁻¹ - A⁻¹U(C⁻¹ + VA⁻¹U)⁻¹VA⁻¹
```

### Sherman-Morrison
```
(A + uvᵀ)⁻¹ = A⁻¹ - (A⁻¹uvᵀA⁻¹) / (1 + vᵀA⁻¹u)
```

### Matrix Inversion Lemma
Used in Kalman filters and online learning.

## 7. Practice Problems

1. Compute eigenvalues of [[2, 1], [1, 2]]
2. Find SVD of [[3, 1], [1, 3]]
3. Verify A*A⁻¹ = I for a 3x3 matrix
4. Compute condition number of a matrix