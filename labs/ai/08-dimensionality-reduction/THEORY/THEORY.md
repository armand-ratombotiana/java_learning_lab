# Dimensionality Reduction - Complete Theory

## 1. Principal Component Analysis (PCA)

### 1.1 Goal
Find orthogonal directions of maximum variance

### 1.2 Algorithm
1. Center data: X' = X - mean
2. Compute covariance: S = X'^T X' / (n-1)
3. Eigendecomposition: S = Q Lambda Q^T
4. Project: Y = X' Q_k

### 1.3 SVD-based PCA
1. Compute SVD: X = U Sigma V^T
2. Principal components: columns of V
3. Scores: X V_k

### 1.4 Choosing K
- Explained variance > 95%
- Elbow in scree plot
- Cross-validation

## 2. Linear Discriminant Analysis (LDA)

### 2.1 Goal
Maximize between-class variance, minimize within-class

### 2.2 Between-class scatter
S_B = sum_k n_k (mu_k - mu)(mu_k - mu)^T

### 2.3 Within-class scatter
S_W = sum_k sum_{i in class k} (x_i - mu_k)(x_i - mu_k)^T

### 2.4 Solution
Maximize: trace((S_W)^{-1} S_B)

### 2.5 Limitations
- At most K-1 components
- Requires class labels

## 3. t-SNE

### 3.1 Stochastic Neighbor Embedding
- P_ij: probability of picking j as neighbor of i
- Q_ij: similar in low-dimensional space

### 3.2 KL Divergence
Minimize: KL(P || Q)

### 3.3 Perplexity
Controls effective number of neighbors

## 4. UMAP

### 4.1 Fuzzy simplicial sets
- Topological representation
- Cross-entropy minimization

### 4.2 Advantages
- Faster than t-SNE
- Preserves global structure
- Configurable neighbor count

## 5. Autoencoders

### 5.1 Architecture
Input -> Encoder -> Latent -> Decoder -> Output

### 5.2 Training
Minimize reconstruction error

### 5.3 Variants
- Denoising autoencoders
- Sparse autoencoders
- Variational autoencoders (VAE)

## Java Implementation

```java
public class PCA {
    public PCAResult fit(Matrix X, int nComponents) {
        // Center the data
        Vector mean = computeMean(X);
        Matrix centered = center(X, mean);
        
        // Compute covariance via SVD
        SVD.SVDResult svd = SVD.compute(centered);
        
        // Extract top k components
        Matrix components = extractComponents(svd, nComponents);
        
        // Project data
        Matrix scores = MatrixOperations.multiply(centered, components);
        
        return new PCAResult(components, scores, mean, svd.S);
    }
    
    public Matrix transform(Matrix X, PCAResult pca) {
        Matrix centered = center(X, pca.mean);
        return MatrixOperations.multiply(centered, pca.components);
    }
}
```