# LeetCode Pattern Cheatsheet for ML Interviews

ML-related LeetCode patterns, company frequency analysis, and practice problems organized by topic.

---

## 1. Math for ML Patterns

### Matrix Operations

| Pattern | Frequency | Companies | Description |
|---------|-----------|-----------|-------------|
| Matrix Multiplication | High | Google, NVIDIA, Apple | Multiply matrices, optimize with cache |
| Matrix Transpose | Medium | All | In-place transpose, cache-friendly |
| Rotation/Transform | Medium | Google, Apple | 2D matrix operations |
| Sparse Matrix | Medium | Google, Meta | Compressed storage, operations |
| Matrix Search | High | Google, Meta, Amazon | Search in sorted matrix |

**Practice Problems**:
```
1. Set Matrix Zeroes (LC 73) - Google, Meta
2. Spiral Matrix (LC 54) - Google, Apple
3. Rotate Image (LC 48) - Google, Meta, Amazon
4. Search a 2D Matrix II (LC 240) - Google, Meta
5. Sparse Matrix Multiplication (LC 311) - Google, Facebook
6. Matrix Diagonal Sum (LC 1572) - Google
7. Rotating the Box (LC 1861) - Google
8. Transpose Matrix (LC 867) - Apple
```

### Probability & Statistics

| Pattern | Frequency | Companies | Description |
|---------|-----------|-----------|-------------|
| Random Sampling | High | Google, Meta | Reservoir sampling, weighted random |
| Probability DP | Medium | Google, OpenAI | DP with probabilities |
| Expected Value | Medium | Google, Quant roles | Expected number of trials |
| Distribution Sampling | Medium | Apple, NVIDIA | Sampling from distributions |
| Random Shuffle | High | All | Fisher-Yates, biased shuffle |

**Practice Problems**:
```
1. Random Pick Index (LC 398) - Facebook
2. Random Pick with Weight (LC 528) - Google, Facebook
3. Random Flip Matrix (LC 519) - Google
4. Random Point in Non-overlapping Rectangles (LC 497) - Google
5. Shuffle an Array (LC 384) - Meta, Apple
6. Generate Random Point in a Circle (LC 478) - Google
7. Implement Rand10() Using Rand7() (LC 470) - Google, Meta
8. Random Pick with Blacklist (LC 710) - Google
```

### Statistics Problems

| Problem | Type | Companies |
|---------|------|-----------|
| Median of Two Sorted Arrays (LC 4) | Hard | Google, Meta, Amazon |
| Find Median from Data Stream (LC 295) | Hard | Google, Meta, Apple |
| Moving Average from Data Stream (LC 346) | Easy | Google |
| Statistics from a Large Sample (LC 1093) | Medium | Apple |
| Probability of a Two Boxes Having The Same Number of Distinct Balls (LC 1467) | Hard | Google |

### Linear Algebra in Coding

- **Eigenvalue/Eigenvector**: Preprocessing, PCA
- **Vector Similarity**: Cosine similarity, dot product
- **Norm Computation**: L1, L2, infinity norm
- **Orthogonality**: Gram-Schmidt, orthogonal projection

```
// Cosine similarity implementation pattern
public double cosineSimilarity(Map<String, Integer> v1, Map<String, Integer> v2) {
    double dotProduct = 0.0;
    double norm1 = 0.0;
    double norm2 = 0.0;
    for (String key : v1.keySet()) {
        dotProduct += (double) v1.get(key) * v2.getOrDefault(key, 0);
        norm1 += Math.pow(v1.get(key), 2);
    }
    for (int val : v2.values()) {
        norm2 += Math.pow(val, 2);
    }
    return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
}
```

---

## 2. ML Model Implementation Patterns

### Regression Patterns

| Pattern | Description | Interview Tips |
|---------|-------------|----------------|
| Linear regression OLS | Closed form via normal equation | Matrix inversion stability |
| Gradient descent | Iterative optimization | Learning rate, convergence |
| Regularized regression | L1 (Lasso), L2 (Ridge) | Sparsity, feature selection |
| Polynomial regression | Feature expansion | Overfitting with degree |
| Weighted regression | Sample importance | Heteroscedasticity handling |

**Common Interview Coding**:
```
1. Implement linear regression (closed form + gradient descent)
2. Implement ridge regression with SVD
3. Implement logistic regression with regularization
4. Implement polynomial feature expansion
5. Implement softmax regression
6. Implement locally weighted regression (LOESS)
```

### Classification Patterns

| Pattern | Description | When to Use |
|---------|-------------|-------------|
| Logistic regression | Linear decision boundary | Baseline, binary classification |
| Decision tree | Non-linear, interpretable | Tabular data, feature importance |
| Naive Bayes | Probabilistic, independent features | Text classification, fast |
| SVM | Max margin classifier | High dimensional, clear margin |
| KNN | Lazy learner, non-parametric | Low dimension, small data |

**Common Interview Coding**:
```
1. Implement Naive Bayes classifier (Gaussian, Multinomial)
2. Implement k-nearest neighbors with KD-tree
3. Implement SVM (primal or dual form, simplified)
4. Implement perceptron algorithm
5. Implement one-vs-all or one-vs-one multi-class
```

### Clustering Patterns

| Pattern | Algorithm | Complexity | Use Case |
|---------|-----------|------------|----------|
| Centroid-based | K-Means, K-Medoids | O(n k d) i | Spherical clusters |
| Density-based | DBSCAN, OPTICS | O(n log n) | Arbitrary shapes, noise |
| Hierarchical | Agglomerative, Divisive | O(n^2 log n) | Hierarchy discovery |
| Distribution-based | GMM, EM | O(n k d) | Soft assignments |
| Graph-based | Spectral clustering | O(n^3) | Non-convex clusters |

**Common Interview Coding**:
```
1. Implement K-means with K-means++ initialization
2. Implement DBSCAN (region query, expansion)
3. Implement hierarchical agglomerative clustering
4. Implement Gaussian Mixture Model with EM
5. Implement spectral clustering (simplified)
```

---

## 3. Feature Engineering Patterns

### Numerical Feature Patterns

| Pattern | Description | Code Pattern |
|---------|-------------|--------------|
| Normalization | Min-max scaling to [0,1] | `(x - min) / (max - min)` |
| Standardization | Zero mean, unit variance | `(x - mean) / std` |
| Robust scaling | Median-based, outlier robust | `(x - median) / IQR` |
| Log transform | Handle skewed distributions | `log(1 + x)` |
| Power transform | Box-Cox, Yeo-Johnson | `boxcox(x, lambda)` |
| Binning | Discretize continuous features | Equal width, equal frequency |
| Interaction features | Feature crosses | `x1 * x2`, `x1 / x2` |
| Polynomial features | Non-linear expansion | `[1, x, x^2, x^3, ...]` |

```
// Min-max normalization
double normalize(double val, double min, double max) {
    if (max == min) return 0.0;
    return (val - min) / (max - min);
}

// Standardization
double standardize(double val, double mean, double std) {
    return (val - mean) / std;
}
```

### Categorical Feature Patterns

| Pattern | Method | Cardinality |
|---------|--------|-------------|
| One-hot encoding | Binary columns per category | Low (< 20) |
| Label encoding | Integer mapping | High, ordinal |
| Target encoding | Mean target per category | High, non-ordinal |
| Frequency encoding | Count/frequency per category | High, any |
| Embedding | Learned low-dim vector | Very high (> 1000) |
| Hash encoding | Feature hashing | Very high, memory constrained |

### Text Feature Patterns

| Pattern | Method | Example |
|---------|--------|---------|
| Bag of words | Count vectorizer | `[0, 2, 1, 0, ...]` |
| TF-IDF | Term frequency * inverse doc freq | `tf * log(N/df)` |
| N-grams | Sequence of n tokens | bi-grams, tri-grams |
| Word embeddings | Pre-trained vectors | Word2Vec, GloVe, FastText |
| Sentence embeddings | Transformer encoders | BERT, SBERT, Instructor |
| Character features | N-gram characters | Subword information |
| POS tags | Part of speech | Grammatical features |

### Image Feature Patterns

| Pattern | Description | Application |
|---------|-------------|-------------|
| Color histograms | Distribution of pixel values | Image retrieval |
| HOG | Histogram of oriented gradients | Object detection |
| SIFT/SURF | Scale-invariant feature transform | Keypoint matching |
| CNN features | Deep features from ConvNets | Transfer learning |
| Pixel statistics | Mean, std, skewness per channel | Simple classification |

### Feature Selection Patterns

| Pattern | Method | When to Use |
|---------|--------|-------------|
| Filter methods | Correlation, chi-square, mutual info | Quick preprocessing |
| Wrapper methods | RFE, forward/backward selection | Small feature sets |
| Embedded methods | L1 regularization, tree importance | Built into training |
| Variance threshold | Remove low variance | Constant features |
| Correlation threshold | Remove highly correlated | Multicollinearity |
| PCA | Unsupervised dimensionality reduction | Dense, correlated features |

---

## 4. Evaluation and Optimization Patterns

### Model Selection Patterns

| Pattern | Method | Code |
|---------|--------|------|
| Hold-out | Train/val/test split | `sklearn.model_selection.train_test_split` |
| K-fold CV | Rotating validation sets | `sklearn.model_selection.KFold` |
| Stratified CV | Preserve class distribution | `sklearn.model_selection.StratifiedKFold` |
| Group CV | Non-i.i.d. samples grouped | `sklearn.model_selection.GroupKFold` |
| Time series CV | Temporal order preservation | `sklearn.model_selection.TimeSeriesSplit` |
| Nested CV | Unbiased performance estimate | Outer CV for evaluation, inner for tuning |

### Hyperparameter Optimization

| Pattern | Search Method | Pros/Cons |
|---------|---------------|-----------|
| Grid search | Exhaustive over all combos | Simple, exponential cost |
| Random search | Random sampling of params | Better for high dimension |
| Bayesian opt | Gaussian process model | Efficient, sequential |
| Hyperband | Adaptive resource allocation | Multi-fidelity |
| Population-based | Evolutionary optimization | Parallel, robust |

### Optimization Patterns

| Pattern | Description | When to Use |
|---------|-------------|-------------|
| Gradient descent | Full batch gradient | Small dataset |
| Mini-batch SGD | Stochastic on mini-batches | Large dataset, defaults |
| Momentum | Accelerated SGD | Escaping local minima |
| Adam | Adaptive moment estimation | Default optimizer |
| AdaGrad | Adaptive per-parameter | Sparse features |
| RMSProp | Root mean square prop | Non-stationary |
| L-BFGS | Quasi-Newton method | Small data, smooth |

### Convergence Monitoring

```python
# Early stopping pattern
class EarlyStopping:
    def __init__(self, patience=5, min_delta=1e-4):
        self.patience = patience
        self.min_delta = min_delta
        self.counter = 0
        self.best_score = None
        self.early_stop = False

    def step(self, val_loss):
        if self.best_score is None:
            self.best_score = val_loss
        elif val_loss > self.best_score - self.min_delta:
            self.counter += 1
            if self.counter >= self.patience:
                self.early_stop = True
        else:
            self.best_score = val_loss
            self.counter = 0
        return self.early_stop
```

---

## 5. Company Frequency Analysis

### Problem Frequency by Company

| Pattern | Google | Meta | Amazon | Apple | Microsoft | NVIDIA |
|---------|--------|------|--------|-------|-----------|--------|
| Matrix Operations | 15 | 8 | 10 | 12 | 7 | 20 |
| DP | 25 | 30 | 20 | 15 | 18 | 10 |
| Graph | 20 | 18 | 15 | 10 | 12 | 8 |
| String | 18 | 22 | 20 | 15 | 15 | 5 |
| Tree | 15 | 12 | 18 | 10 | 14 | 5 |
| Hash Map/Set | 20 | 25 | 22 | 12 | 16 | 8 |
| Two Pointers | 12 | 15 | 18 | 8 | 10 | 5 |
| Binary Search | 18 | 12 | 15 | 10 | 12 | 8 |
| Stack/Queue | 10 | 8 | 12 | 6 | 8 | 5 |
| Heap | 12 | 10 | 8 | 8 | 6 | 5 |
| Union Find | 8 | 5 | 6 | 3 | 4 | 2 |
| Trie | 6 | 8 | 5 | 4 | 5 | 2 |
| Sorting | 10 | 12 | 15 | 8 | 10 | 6 |
| Math | 15 | 10 | 12 | 10 | 8 | 15 |
| Bit Manipulation | 5 | 3 | 4 | 6 | 3 | 10 |

*Numbers represent approximate count of distinct problems asked (higher = more frequent)*

### Top 50 Problems for ML Interviews

#### Must-Know (Frequency: Very High)

| # | Problem | Companies | Pattern |
|---|---------|-----------|---------|
| 1 | Two Sum (LC 1) | All | Hash Map |
| 2 | Merge Intervals (LC 56) | Google, Meta, Amazon | Sorting + Intervals |
| 3 | LRU Cache (LC 146) | Google, Meta, Amazon | Design |
| 4 | Binary Search (LC 704) | All | Binary Search |
| 5 | Kth Largest Element (LC 215) | Google, Meta, Amazon | Quickselect/Heap |
| 6 | Rotate Image (LC 48) | Google, Meta, Apple | Matrix |
| 7 | Spiral Matrix (LC 54) | Google, Apple, Microsoft | Matrix |
| 8 | Search in Rotated Sorted Array (LC 33) | Meta, Amazon | Binary Search |
| 9 | Generate Parentheses (LC 22) | Google, Meta | Backtracking |
| 10 | Longest Substring Without Repeating (LC 3) | All | Sliding Window |
| 11 | Number of Islands (LC 200) | All | DFS/BFS |
| 12 | Word Search (LC 79) | Meta, Amazon | Backtracking |
| 13 | Course Schedule (LC 207) | Google, Meta, Amazon | Topological Sort |
| 14 | Design Add and Search Words (LC 211) | Google, Meta | Trie |
| 15 | Minimum Window Substring (LC 76) | Google, Meta, Apple | Sliding Window |

#### Data Science / ML Specific

| # | Problem | Companies | Pattern |
|---|---------|-----------|---------|
| 16 | Moving Average from Data Stream (LC 346) | Google | Queue |
| 17 | Dot Product of Two Sparse Vectors (LC 1570) | Meta, Google | Two Pointers |
| 18 | Design Hit Counter (LC 362) | Google, Meta | Queue, Design |
| 19 | Find Median from Data Stream (LC 295) | Google, Meta, Apple | Two Heaps |
| 20 | Random Pick with Weight (LC 528) | Google, Meta | Prefix Sum |
| 21 | Shuffle an Array (LC 384) | Meta, Apple | Fisher-Yates |
| 22 | Top K Frequent Elements (LC 347) | All | Heap, Bucket Sort |
| 23 | Sparse Matrix Multiplication (LC 311) | Google, Meta | Matrix |
| 24 | Queue Reconstruction by Height (LC 406) | Google | Greedy |
| 25 | Valid Sudoku (LC 36) | Apple, Amazon | Hash Set |

#### Algorithm Mastery

| # | Problem | Companies | Pattern |
|---|---------|-----------|---------|
| 26 | Maximum Subarray (LC 53) | All | Kadane's Algorithm |
| 27 | Coin Change (LC 322) | Google, Meta, Amazon | DP |
| 28 | Longest Palindromic Substring (LC 5) | All | DP, Expand Around |
| 29 | Decode Ways (LC 91) | Google, Meta | DP |
| 30 | Longest Increasing Subsequence (LC 300) | Google, Meta | DP, Patience Sort |
| 31 | Climbing Stairs (LC 70) | All | DP (Fibonacci) |
| 32 | House Robber (LC 198) | Google, Meta | DP |
| 33 | Best Time to Buy and Sell Stock (LC 121) | All | One Pass |
| 34 | Product of Array Except Self (LC 238) | Meta, Amazon, Apple | Prefix/Suffix |
| 35 | Container With Most Water (LC 11) | Meta, Amazon | Two Pointers |

#### Trees and Graphs

| # | Problem | Companies | Pattern |
|---|---------|-----------|---------|
| 36 | Binary Tree Level Order Traversal (LC 102) | All | BFS |
| 37 | Validate Binary Search Tree (LC 98) | All | Inorder, DFS |
| 38 | Serialize and Deserialize Binary Tree (LC 297) | Google, Meta, Amazon | DFS/BFS |
| 39 | Word Ladder (LC 127) | Google, Meta, Amazon | BFS |
| 40 | Alien Dictionary (LC 269) | Google, Meta, Apple | Topological Sort |
| 41 | Clone Graph (LC 133) | Meta, Amazon | DFS/BFS, Hash Map |
| 42 | Pacific Atlantic Water Flow (LC 417) | Google, Meta | DFS |
| 43 | Graph Valid Tree (LC 261) | Google, Meta | Union Find, DFS |
| 44 | Walls and Gates (LC 286) | Google, Meta, Apple | BFS |
| 45 | Number of Connected Components (LC 323) | Meta, Amazon | Union Find, DFS |

#### Hard Problems for Senior Roles

| # | Problem | Companies | Pattern |
|---|---------|-----------|---------|
| 46 | Merge k Sorted Lists (LC 23) | Google, Meta, Amazon | Divide & Conquer, Heap |
| 47 | Median of Two Sorted Arrays (LC 4) | Google, Meta | Binary Search |
| 48 | Trapping Rain Water (LC 42) | All | Two Pointers, Stack |
| 49 | Sudoku Solver (LC 37) | Google, Apple | Backtracking |
| 50 | Basic Calculator (LC 224) | Google, Meta, Amazon | Stack |

---

## 6. Time Complexity Cheatsheet

### Common ML Algorithm Complexities

| Algorithm | Training | Inference | Memory |
|-----------|----------|-----------|--------|
| Linear Regression | O(n d^2) | O(d) | O(d) |
| Logistic Regression | O(n d i) | O(d) | O(d) |
| Decision Tree (CART) | O(n log n d) | O(log n) | O(n) |
| Random Forest | O(n log n d t) | O(t log n) | O(t n) |
| KNN | O(1) | O(n d) | O(n d) |
| K-Means | O(n k d i) | O(k d) | O(k d) |
| SVM (RBF) | O(n^2 d) | O(n d) | O(n^2) |
| Neural Network | O(n d L) | O(d L) | O(d L) |
| Transformer | O(n L^2) | O(L^2) | O(L^2) |
| PCA | O(n d^2) | O(d k) | O(d^2) |

*n = samples, d = features, i = iterations, k = clusters/neighbors, t = trees, L = layers/sequence length*

### Data Structure Operations

| Data Structure | Access | Search | Insert | Delete |
|----------------|--------|--------|--------|--------|
| Array | O(1) | O(n) | O(n) | O(n) |
| Stack/Queue | O(1) | O(n) | O(1) | O(1) |
| Linked List | O(n) | O(n) | O(1) | O(1) |
| Hash Table | O(1) avg | O(1) avg | O(1) avg | O(1) avg |
| BST | O(log n) | O(log n) | O(log n) | O(log n) |
| Heap | O(1) for min | O(log n) | O(log n) | O(log n) |
| Trie | O(k) | O(k) | O(k) | O(k) |
| Adjacency Matrix | O(1) | O(1) | O(1) | O(1) |
| Adjacency List | O(1) | O(d) | O(1) | O(d) |

---

## 7. System Design Coding Patterns

### Common Coding Patterns for ML Design

```python
# Pipeline pattern
class MLPipeline:
    def __init__(self, steps):
        self.steps = steps  # List of (name, transformer)

    def fit_transform(self, X, y=None):
        for name, transformer in self.steps:
            X = transformer.fit_transform(X, y)
        return X

    def transform(self, X):
        for name, transformer in self.steps:
            X = transformer.transform(X)
        return X


# Online learning pattern (streaming)
class OnlineLearner:
    def __init__(self, learning_rate=0.01):
        self.weights = None
        self.lr = learning_rate
        self.n_features = None

    def partial_fit(self, x, y):
        if self.weights is None:
            self.n_features = len(x)
            self.weights = np.zeros(self.n_features)
        prediction = self._predict(x)
        gradient = self._gradient(x, y, prediction)
        self.weights -= self.lr * gradient
        return self._loss(y, prediction)


# Sliding window pattern
class SlidingWindowMetrics:
    def __init__(self, window_size):
        self.window = []
        self.window_size = window_size

    def add(self, value):
        self.window.append(value)
        if len(self.window) > self.window_size:
            self.window.pop(0)

    def mean(self):
        return sum(self.window) / len(self.window)

    def variance(self):
        mean = self.mean()
        return sum((x - mean) ** 2 for x in self.window) / len(self.window)
```

---

## 8. Quick Reference: Common ML Code Snippets

```python
# Train/val/test split
def train_val_test_split(X, y, val_size=0.1, test_size=0.2, random_state=42):
    np.random.seed(random_state)
    indices = np.random.permutation(len(X))
    test_end = int(len(X) * test_size)
    val_end = test_end + int(len(X) * val_size)

    test_idx = indices[:test_end]
    val_idx = indices[test_end:val_end]
    train_idx = indices[val_end:]

    return (X[train_idx], X[val_idx], X[test_idx],
            y[train_idx], y[val_idx], y[test_idx])


# Batch generator
def batch_generator(X, y, batch_size=32, shuffle=True):
    n = len(X)
    indices = np.arange(n)
    if shuffle:
        np.random.shuffle(indices)
    for start in range(0, n, batch_size):
        batch_idx = indices[start:start + batch_size]
        yield X[batch_idx], y[batch_idx]


# Confusion matrix computation
def confusion_matrix(y_true, y_pred, num_classes):
    cm = np.zeros((num_classes, num_classes), dtype=int)
    for t, p in zip(y_true, y_pred):
        cm[t][p] += 1
    return cm


# One-hot encoding
def one_hot_encode(y, num_classes):
    return np.eye(num_classes)[y]


# Standard scaler
def standard_scaler(X, mean=None, std=None):
    if mean is None:
        mean = np.mean(X, axis=0)
        std = np.std(X, axis=0)
    return (X - mean) / (std + 1e-10), mean, std
```
