# Mock Interview: Eigenvalues & SVD

**Topic:** Explain SVD and its applications in ML — PCA, recommendation, compression

## Core Questions

### Q1: What is the Singular Value Decomposition (SVD)?

**Answer:**
For any $A \in \mathbb{R}^{m \times n}$ (rank $r$):

$A = U \Sigma V^T$

- $U \in \mathbb{R}^{m \times r}$: Left singular vectors (columns = eigenvectors of $AA^T$)
- $\Sigma \in \mathbb{R}^{r \times r}$: Diagonal matrix of singular values $\sigma_1 \ge \sigma_2 \ge \cdots \ge \sigma_r > 0$
- $V \in \mathbb{R}^{n \times r}$: Right singular vectors (columns = eigenvectors of $A^T A$)

**Key properties:**
- $U^T U = I$, $V^T V = I$
- $\sigma_i = \sqrt{\lambda_i}$ where $\lambda_i$ are eigenvalues of $A^T A$
- $\|A\|_F^2 = \sum \sigma_i^2$
- $\|A\|_2 = \sigma_1$ (spectral norm)

### Q2: How is SVD used for PCA?

**Answer:**
Given centered data matrix $X \in \mathbb{R}^{n \times d}$:

PCA via SVD: $X = U \Sigma V^T$

- **Principal components:** Columns of $V$ (eigenvectors of $X^T X$)
- **Projected data (scores):** $T = XV = U \Sigma$
- **Variance explained:** $\sigma_i^2 / \sum \sigma_j^2$

**Why SVD over eigendecomposition of $X^T X$?**
- Numerically stable (avoids squaring condition number)
- Handles $n < d$ naturally
- Avoids computing $X^T X$ explicitly ($O(nd^2)$ vs $O(n^2 d)$)

### Q3: Applications of SVD in ML.

**Answer:**
1. **Dimensionality reduction (PCA):** Keep top $k$ components, project data
2. **Recommender systems:** $R \approx U \Sigma V^T$. User $i$'s predicted rating for item $j$ is $U_{i,:} \cdot \Sigma \cdot V_{j,:}^T$. SVD for collaborative filtering predicts missing ratings.
3. **Matrix completion:** Nuclear norm minimization $\min \|X\|_*$ s.t. observed entries match
4. **Image compression:** Keep top $k$ singular values: $A \approx U_k \Sigma_k V_k^T$ (storage reduces from $mn$ to $k(m+n)$)
5. **Latent semantic analysis (LSA):** Term-document matrix SVD → topic vectors
6. **Low-rank approximation:** Remove noise, extract dominant patterns
7. **Condition number analysis:** $\kappa = \sigma_{\max}/\sigma_{\min}$ — stability of linear systems

### Q4: Left vs. right singular vectors.

**Answer:**
- **Left singular vectors $U$:** Columns correspond to row-space patterns. In PCA, these are the normalized projected data points.
- **Right singular vectors $V$:** Columns correspond to column-space patterns. In PCA, these are the principal component directions.

**Eigenvalue decomposition vs. SVD:**
| Eigenvalue | SVD |
|-----------|-----|
| Square matrices only | Any matrix |
| $A = Q\Lambda Q^{-1}$ | $A = U\Sigma V^T$ |
| May not exist (non-diagonalizable) | Always exists |
| Not unique (ordering/scale) | Unique up to singular vector signs |

## Advanced

- **Truncated SVD:** Only compute top $k$ singular values — use randomized SVD or power iteration for large matrices
- **Randomized SVD:** $O(mn \log k)$ — project matrix to low-dim subspace, compute exact SVD on smaller matrix
- **Relationship to four fundamental subspaces:** $U$ spans column space, $V$ spans row space, nullspaces from discarded singular vectors
- **Conditioning and regularization:** Truncating small singular values stabilizes inverse problems (ridge regression as smooth truncation)
