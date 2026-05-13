# Linear Algebra - Exercises

## Exercise 1: Vector Operations
```python
# Given vectors
v1 = np.array([1, 2, 3, 4])
v2 = np.array([5, 6, 7, 8])

# Task 1: Compute dot product
# Task 2: Compute cosine similarity
# Task 3: Normalize both vectors (L2 norm = 1)
# Task 4: Find projection of v1 onto v2

def exercise1():
    v1 = np.array([1, 2, 3, 4], dtype=float)
    v2 = np.array([5, 6, 7, 8], dtype=float)

    # 1. Dot product
    dot = np.dot(v1, v2)

    # 2. Cosine similarity
    cos_sim = dot / (np.linalg.norm(v1) * np.linalg.norm(v2))

    # 3. Normalize vectors
    v1_normalized = v1 / np.linalg.norm(v1)
    v2_normalized = v2 / np.linalg.norm(v2)

    # 4. Projection of v1 onto v2
    projection = (np.dot(v1, v2) / np.dot(v2, v2)) * v2

    return dot, cos_sim, v1_normalized, v2_normalized, projection
```

## Exercise 2: Matrix Operations
```python
# Given matrix A and vector b
A = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 10]])
b = np.array([1, 2, 3])

# Task 1: Check if A is invertible (compute determinant)
# Task 2: Solve Ax = b
# Task 3: Compute A^T * A
# Task 4: Find eigenvalues and eigenvectors

def exercise2():
    A = np.array([[1, 2, 3], [4, 5, 6], [7, 8, 10]], dtype=float)
    b = np.array([1, 2, 3], dtype=float)

    # 1. Determinant
    det = np.linalg.det(A)

    # 2. Solve system
    x = np.linalg.solve(A, b)

    # 3. A^T * A
    AtA = A.T @ A

    # 4. Eigenvalues and eigenvectors
    eigenvalues, eigenvectors = np.linalg.eig(A)

    return det, x, AtA, eigenvalues, eigenvectors
```

## Exercise 3: SVD and Dimensionality Reduction
```python
# Given a data matrix
data = np.random.randn(100, 50)

# Task 1: Perform full SVD
# Task 2: Keep top 10 components and reconstruct
# Task 3: Compute compression ratio

def exercise3():
    data = np.random.randn(100, 50)

    # 1. Full SVD
    U, S, Vt = np.linalg.svd(data, full_matrices=False)

    # 2. Reconstruction with top 10 components
    k = 10
    data_reconstructed = U[:, :k] @ np.diag(S[:k]) @ Vt[:k, :]

    # 3. Compression ratio
    original_size = 100 * 50
    compressed_size = 100 * k + k + k * 50
    ratio = compressed_size / original_size

    return data_reconstructed, ratio
```

## Exercise 4: Matrix Properties
```python
# Compute condition number and classify matrices

def classify_matrices():
    A = np.array([[1, 2], [2, 4]])  # Singular
    B = np.array([[2, 1], [1, 2]])  # Positive definite
    C = np.array([[1, 0], [0, 1]])  # Identity

    results = {}
    for name, M in [('A', A), ('B', B), ('C', C)]:
        det = np.linalg.det(M)
        cond = np.linalg.cond(M)
        eigenvalues = np.linalg.eigvals(M)
        is_pos_def = all(eigenvalues > 0)
        results[name] = {'det': det, 'cond': cond, 'pos_def': is_pos_def}

    return results
```

## Exercise 5: Linear Transformation
```python
# Create and apply linear transformations

def linear_transformations():
    # Define transformation matrix
    T = np.array([[1, 0.5], [0.5, 1]])

    # Apply to unit circle points
    angles = np.linspace(0, 2*np.pi, 100)
    points = np.array([np.cos(angles), np.sin(angles)])
    transformed = T @ points

    return transformed
```

## Advanced Challenges
1. Implement your own matrix multiplication
2. Compute SVD from scratch using power iteration
3. Implement QR decomposition (Gram-Schmidt)
4. Build a simple PCA from singular vectors