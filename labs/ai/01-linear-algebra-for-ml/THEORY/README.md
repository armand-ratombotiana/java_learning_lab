# Linear Algebra for Machine Learning - Theory

## Overview
Linear algebra is the foundation of machine learning. It provides the mathematical framework for representing data, performing computations, and understanding algorithms.

## 1. Vectors

### Definition
A vector is an ordered list of numbers that represents a point in n-dimensional space.

### Properties
- **Magnitude**: Length of the vector
- **Direction**: Orientation in space
- **Operations**: Addition, scalar multiplication, dot product, cross product

### Types of Vectors
- **Column vectors**: Vertical arrangement
- **Row vectors**: Horizontal arrangement
- **Unit vectors**: Magnitude of 1
- **Zero vectors**: All elements are zero

### Applications in ML
- Feature vectors in classification
- Word embeddings
- Gradient vectors in optimization

## 2. Matrices

### Definition
A matrix is a 2D array of numbers arranged in rows and columns.

### Types of Matrices
- **Square matrix**: Same number of rows and columns
- **Diagonal matrix**: Non-zero elements only on diagonal
- **Identity matrix**: Diagonal elements are 1
- **Symmetric matrix**: A = A^T
- **Orthogonal matrix**: A^T * A = I
- **Sparse matrix**: Most elements are zero

### Matrix Operations
- Addition/Subtraction (element-wise)
- Scalar multiplication
- Matrix multiplication
- Transpose
- Inverse
- Determinant

### Applications in ML
- Dataset representation (rows = samples, columns = features)
- Linear transformations
- Weight matrices in neural networks
- Covariance matrices

## 3. Eigenvalues and Eigenvectors

### Definition
For a square matrix A, if there exists a non-zero vector v and scalar λ such that:
```
A * v = λ * v
```
Then v is an eigenvector and λ is an eigenvalue.

### Properties
- Characteristic equation: det(A - λI) = 0
- Trace = sum of eigenvalues
- Determinant = product of eigenvalues
- Number of eigenvalues = dimension of matrix

### Applications in ML
- PCA (Principal Component Analysis)
- Spectral clustering
- PageRank algorithm
- Dimensionality reduction

## 4. Singular Value Decomposition (SVD)

### Definition
SVD decomposes any matrix into three matrices:
```
A = U * Σ * V^T
```
- U: Left singular vectors (orthogonal)
- Σ: Diagonal matrix of singular values
- V: Right singular vectors (orthogonal)

### Applications in ML
- Image compression
- Recommender systems
- Natural language processing (Latent Semantic Analysis)
- Data noise reduction

## 5. Matrix Decompositions

### LU Decomposition
A = L * U where L is lower triangular and U is upper triangular.

### QR Decomposition
A = Q * R where Q is orthogonal and R is upper triangular.

### Cholesky Decomposition
A = L * L^T for symmetric positive-definite matrices.

## 6. Vector Spaces

### Definition
A set of vectors closed under addition and scalar multiplication.

### Key Concepts
- **Basis**: Set of linearly independent vectors that span the space
- **Dimension**: Number of vectors in the basis
- **Subspace**: A vector space contained within another
- **Orthogonality**: Perpendicular vectors

### Applications
- Feature space analysis
- Kernel methods in SVM
- Manifold learning

## 7. Linear Transformations

### Definition
A function that preserves vector addition and scalar multiplication.

### Properties
- Can be represented as matrices
- Preserve lines and planes
- May change angles and lengths

### Applications
- Data preprocessing
- Feature engineering
- Neural network layers