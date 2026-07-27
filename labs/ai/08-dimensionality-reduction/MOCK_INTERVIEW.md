# Mock Interview: Dimensionality Reduction

**Topic:** PCA vs t-SNE vs UMAP — when to use each in ML pipelines

## Core Questions

### Q1: Compare PCA, t-SNE, and UMAP.

**Answer:**
| Aspect | PCA | t-SNE | UMAP |
|--------|-----|-------|------|
| **Type** | Linear | Non-linear | Non-linear |
| **Speed** | Very fast ($O(n d^2)$) | Slow ($O(n^2)$) | Fast ($O(n \log n)$) |
| **Deterministic** | Yes | No (random init) | No (but more stable) |
| **Global structure** | Preserves | Poor | Good |
| **Local structure** | Poor | Excellent | Excellent |
| **Perplexity/n_neighbors** | None | 5-50 | 15-200 |
| **Interpretability** | High (components) | Low | Low |
| **Scalability** | $> 10^6$ points | $< 10^4$ points | $> 10^6$ points |

### Q2: How does t-SNE work?

**Answer:**
t-SNE minimizes KL divergence between pairwise similarities in high-D and low-D.

**High-D similarity:**
$p_{j|i} = \frac{\exp(-\|x_i - x_j\|^2 / 2\sigma_i^2)}{\sum_{k \ne i} \exp(-\|x_i - x_k\|^2 / 2\sigma_i^2)}$
$p_{ij} = (p_{j|i} + p_{i|j}) / 2n$

**Low-D similarity (heavy-tailed):**
$q_{ij} = \frac{(1 + \|y_i - y_j\|^2)^{-1}}{\sum_{k \ne l} (1 + \|y_k - y_l\|^2)^{-1}}$

**Loss:** $KL(P \parallel Q) = \sum_{i \ne j} p_{ij} \log \frac{p_{ij}}{q_{ij}}$

**Perplexity:** Controls $\sigma_i$ — balances attention between local and global structure.

### Q3: How does UMAP work?

**Answer:**
1. Build $k$-nearest neighbor graph (high-D)
2. Create fuzzy simplicial set representation
3. Optimize low-D embedding to minimize cross-entropy between fuzzy set representations

**Key differences from t-SNE:**
- Uses $k$-NN (faster) vs. Gaussian kernel perplexity
- Uses cross-entropy instead of KL (preserves global structure better)
- Symmetric: both attractive and repulsive forces
- Theoretical foundation in topological data analysis

### Q4: When to use each?

**Answer:**
**Use PCA when:**
- Need interpretable components (loadings)
- Preprocessing before other ML models
- Linear structure is sufficient
- Large datasets (> 100K samples)
- Need inverse transform (reconstruction)

**Use t-SNE when:**
- Exploring small to medium datasets
- Visualizing clusters with strong local structure
- Publication-quality visualizations
- Dataset < 10K points (or use Barnes-Hut approximation)

**Use UMAP when:**
- Large datasets (> 10K points)
- Need both local and global structure preservation
- Need faster than t-SNE with comparable quality
- Embedding as feature input to downstream models
- Want to embed new test points (UMAP has transform)

### Q5: Pitfalls to watch for.

**Answer:**
- **t-SNE cluster size means nothing:** Compactness in t-SNE doesn't correspond to variance
- **Distance in embedding isn't meaningful:** Only relative neighborhood structure
- **Multiple runs give different results:** Always run several times
- **Perplexity sensitivity:** Bad choice can create false structure
- **PCA before t-SNE/UMAP:** Common practice to reduce noise, speed up
- **Don't use t-SNE for feature extraction:** No out-of-sample transform (parametric t-SNE exists but limited)
