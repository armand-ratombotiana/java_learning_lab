# Linear Algebra - Code Deep Dive

## Using NumPy for Linear Algebra

```python
import numpy as np
from numpy import linalg as la

# === VECTORS ===
# Create vectors
v1 = np.array([1, 2, 3])
v2 = np.array([4, 5, 6])

# Vector operations
dot_product = np.dot(v1, v2)  # 32
cross_product = np.cross(v1, v2)  # [-3, 6, -3]
magnitude = la.norm(v1)  # 3.741

# === MATRICES ===
# Create matrices
A = np.array([[1, 2], [3, 4]])
B = np.array([[5, 6], [7, 8]])

# Matrix operations
C = np.matmul(A, B)  # Matrix multiplication
C = A @ B  # Same as above

# Transpose
A_T = A.T

# Inverse
A_inv = la.inv(A)  # Only if invertible

# Determinant
det_A = la.det(A)  # -2.0

# === EIGENVALUES & EIGENVECTORS ===
eigenvalues, eigenvectors = la.eig(A)
# eigenvalues: array([-0.372, 5.372])
# eigenvectors: columns are eigenvectors

# For symmetric matrices
eigenvalues, eigenvectors = la.eigh(A)  # More stable

# === SVD ===
U, S, Vt = la.svd(A)
# U: left singular vectors
# S: singular values (diagonal)
# Vt: right singular vectors (transposed)

# Full SVD
U_full, S_full, Vt_full = la.svd(A, full_matrices=True)

# Truncated SVD (dimensionality reduction)
U_trunc, S_trunc, Vt_trunc = la.svd(A, full_matrices=False)
k = 1  # Keep only top k components
A_approx = U_trunc[:, :k] @ np.diag(S_trunc[:k]) @ Vt_trunc[:k, :]

# === MATRIX DECOMPOSITIONS ===
# LU Decomposition
from scipy.linalg import lu
P, L, U = lu(A)

# QR Decomposition
Q, R = la.qr(A)

# Cholesky (for symmetric positive-definite)
A_sym = A @ A.T  # Make it positive definite
L = la.cholesky(A_sym)

# === SOLVE LINEAR SYSTEMS ===
# Ax = b
b = np.array([1, 2])
x = la.solve(A, b)  # Direct solution
x = la.lstsq(A, b)  # Least squares solution

# === PSEUDOINVERSE ===
A_pinv = la.pinv(A)  # Moore-Penrose pseudoinverse

# === CONDITION NUMBER ===
cond = la.cond(A)  # Measures numerical stability

# === NORM COMPUTATION ===
l1_norm = la.norm(A, 1)  # Column sum
l2_norm = la.norm(A, 2)  # Spectral norm
inf_norm = la.norm(A, np.inf)  # Row sum
fro_norm = la.norm(A, 'fro')  # Frobenius norm
```

## Using SciPy for Advanced Operations

```python
from scipy import sparse
from scipy.sparse.linalg import svds

# Sparse SVD (for large matrices)
U, s, Vt = svds(sparse.csr_matrix(A), k=2)

# Linear algebra with sparse matrices
from scipy.sparse import linalg as spla
```

## Using JAX for GPU Acceleration

```python
import jax.numpy as jnp
from jax import jit

# GPU-accelerated operations
A_jax = jnp.array(A)
eigenvalues = jnp.linalg.eigvals(A_jax)

# JIT-compiled functions for speed
@jit
def matrix_op(A, b):
    return jnp.linalg.solve(A, b)
```

## Performance Comparison

```python
import time

# NumPy (CPU)
start = time.time()
for _ in range(100):
    la.inv(A)
print(f"NumPy: {time.time()-start:.4f}s")

# JAX (GPU if available)
A_jax = jnp.array(A)
start = time.time()
for _ in range(100):
    jnp.linalg.inv(A_jax)
print(f"JAX: {time.time()-start:.4f}s")
```

## Common Pitfalls

1. **Singular matrices**: Always check determinant/condition number
2. **Floating point precision**: Use `np.linalg.cond()` to check stability
3. **Memory**: For large matrices, use sparse representations
4. **Broadcasting**: Ensure shapes are compatible