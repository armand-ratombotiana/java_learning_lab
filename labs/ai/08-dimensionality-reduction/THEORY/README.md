# Dimensionality Reduction - Theory

## 1. Principal Component Analysis (PCA)

### Goal
Find orthogonal directions of maximum variance.

### Algorithm
1. Center data (subtract mean)
2. Compute covariance matrix
3. Find eigenvectors (principal components)
4. Project onto top k components

### SVD Connection
```
X = UΣVᵀ
V contains principal directions
```

### Variance Explained
```
Var(k) = Σᵢ₌₁ᵏ λᵢ / Σᵢ λᵢ
```

## 2. Linear Discriminant Analysis (LDA)

### Goal
Maximize class separability.

### Between-class scatter:
```
S_B = Σᵢ nᵢ(μᵢ - μ)(μᵢ - μ)ᵀ
```

### Within-class scatter:
```
S_W = Σᵢ Σₓ∈Cᵢ (x - μᵢ)(x - μᵢ)ᵀ
```

### Solution
```
W = S_W⁻¹S_B
```

## 3. t-SNE

### Non-linear Embedding
- Compute pairwise similarities
- Minimize KL divergence between high-dim and low-dim distributions

### Parameters
- Perplexity: balances local/global
- Learning rate
- Iterations

## 4. UMAP

### Topological Approach
- Construct fuzzy simplicial set
- Minimize cross-entropy between high and low-dim

### Advantages
- Faster than t-SNE
- Preserves global structure
- Handles larger datasets