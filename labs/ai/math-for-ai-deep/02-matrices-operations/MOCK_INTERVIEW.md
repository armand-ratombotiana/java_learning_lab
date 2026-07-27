# Mock Interview: Matrix Operations

**Topic:** Matrix multiplication optimization, understanding through ML

## Core Questions

### Q1: Explain different ways to view matrix multiplication.

**Answer:**
$C = AB$ where $A \in \mathbb{R}^{m \times n}$, $B \in \mathbb{R}^{n \times p}$:

1. **Dot product view:** $C_{ij} = A_{i,:} \cdot B_{:,j}$ — inner product of row $i$ of $A$ with column $j$ of $B$

2. **Column view:** $C_{:,j} = A \cdot B_{:,j}$ — each column of $C$ is a linear combination of columns of $A$ weighted by $B_{:,j}$

3. **Row view:** $C_{i,:} = A_{i,:} \cdot B$ — each row of $C$ is a linear combination of rows of $B$

4. **Sum of rank-1 matrices:** $C = \sum_{k=1}^n A_{:,k} \cdot B_{k,:}$

**ML intuition:** View 2 is useful for understanding how neural net layers transform feature representations. View 4 is key for SVD and low-rank approximations.

### Q2: How would you optimize matrix multiplication?

**Answer:**
**Algorithmic:**
- **Strassen's algorithm:** $O(n^{2.807})$ — recursive divide-and-conquer with 7 multiplications instead of 8
- **Coppersmith-Winograd:** $O(n^{2.376})$ — theoretical, impractical
- **Blocked/tiled multiplication:** Exploit cache hierarchy, operate on blocks fitting in L1/L2 cache

**Hardware-aware:**
- **SIMD vectorization:** Process multiple elements per instruction
- **GPU tiling:** Use shared memory, cooperatively load tiles
- **Loop reordering:** $i \to k \to j$ ordering for optimal cache usage (row-major)
- **BLAS libraries:** GotoBLAS/OpenBLAS, Intel MKL, cuBLAS — highly optimized assembly

**Memory:**
- Avoid transposition overhead; pack matrices in contiguous memory
- Prefetch data before CPU needs it

### Q3: Matrix operations in ML — intuitive understanding.

**Answer:**
| ML Operation | Matrix Form | Intuition |
|-------------|------------|-----------|
| **Linear layer** | $Y = XW$ | $W$ rotates + scales the input space |
| **Attention** | $A = \text{softmax}(QK^T)V$ | $QK^T$ computes pairwise similarities, $AV$ aggregates values |
| **Covariance** | $\Sigma = \frac{1}{n} X^T X$ | Measures co-variation between features |
| **Kernel matrix** | $K_{ij} = k(x_i, x_j)$ | Pairwise similarities define embedding geometry |
| **Gradient** | $\nabla_W L = \frac{1}{n} X^T (y - \hat{y})$ | Error signal propagated back via transpose |

### Q4: Explain the trace and its uses in ML.

**Answer:**
$\text{Tr}(A) = \sum_i A_{ii}$

**Properties:**
- $\text{Tr}(AB) = \text{Tr}(BA)$ (cyclic)
- $\text{Tr}(A^T A) = \|A\|_F^2$ (Frobenius norm)

**Uses in ML:**
- Regularization: trace norm encourages low-rank solutions
- $\text{Tr}(X^T L X)$ in graph Laplacian regularization measures smoothness
- Computing gradients of matrix functions via trace identities

## Advanced

- **Matrix calculus:** $\frac{\partial}{\partial X} \text{Tr}(AX) = A^T$
- **Kronecker product:** $A \otimes B$ — used in deep learning for efficient computation of large matrices
- **Low-rank approximation:** Eckart-Young theorem — SVD gives best rank-$k$ approximation in Frobenius norm
