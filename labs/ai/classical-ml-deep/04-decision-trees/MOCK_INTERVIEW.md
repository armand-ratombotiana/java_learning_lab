# Mock Interview: Decision Trees

**Topic:** Implement decision tree from scratch — entropy, Gini, pruning

## Core Questions

### Q1: Explain how a decision tree is built.

**Answer:**
Top-down greedy recursive partitioning:
1. Start with all data at root
2. For each feature, evaluate all split thresholds
3. Pick split that maximizes information gain (or minimizes impurity)
4. Recurse on child nodes
5. Stop when max depth / min samples / pure node

### Q2: Compare splitting criteria.

| Criterion | Formula | Range |
|-----------|---------|-------|
| **Entropy** | $-\sum p_k \log_2 p_k$ | $[0, \log_2 K]$ |
| **Gini** | $1 - \sum p_k^2$ | $[0, 1 - 1/K]$ |
| **Misclassification** | $1 - \max_k p_k$ | $[0, 1 - 1/K]$ |

Information Gain: $IG = H(\text{parent}) - \sum \frac{n_j}{n} H(\text{child}_j)$

**Gini vs. Entropy:** Gini is faster (no log), both give similar trees. Gini tends to isolate largest class, entropy tends to produce more balanced splits.

### Q3: Implement a decision tree from scratch.

```python
class DecisionTree:
    def __init__(self, max_depth=5, min_samples_split=2):
        self.max_depth = max_depth
        self.min_samples_split = min_samples_split
        self.tree = None

    def _gini(self, y):
        classes, counts = np.unique(y, return_counts=True)
        probs = counts / len(y)
        return 1 - np.sum(probs ** 2)

    def _split(self, X, y, feature, threshold):
        left = X[:, feature] <= threshold
        right = ~left
        return X[left], y[left], X[right], y[right]

    def _best_split(self, X, y):
        best_gain, best_feat, best_thresh = -1, None, None
        current_gini = self._gini(y)
        n = len(y)
        for feat in range(X.shape[1]):
            values = np.unique(X[:, feat])
            for thresh in (values[:-1] + values[1:]) / 2:
                X_l, y_l, X_r, y_r = self._split(X, y, feat, thresh)
                if len(y_l) == 0 or len(y_r) == 0:
                    continue
                gain = current_gini - (len(y_l)/n * self._gini(y_l) + len(y_r)/n * self._gini(y_r))
                if gain > best_gain:
                    best_gain, best_feat, best_thresh = gain, feat, thresh
        return best_feat, best_thresh

    def _build(self, X, y, depth):
        if depth >= self.max_depth or len(y) < self.min_samples_split or len(np.unique(y)) == 1:
            return {'type': 'leaf', 'value': np.bincount(y).argmax()}
        feat, thresh = self._best_split(X, y)
        if feat is None:
            return {'type': 'leaf', 'value': np.bincount(y).argmax()}
        X_l, y_l, X_r, y_r = self._split(X, y, feat, thresh)
        return {'type': 'node', 'feature': feat, 'threshold': thresh,
                'left': self._build(X_l, y_l, depth+1),
                'right': self._build(X_r, y_r, depth+1)}

    def fit(self, X, y):
        self.tree = self._build(X, y, 0)

    def _predict_one(self, x, node):
        if node['type'] == 'leaf':
            return node['value']
        if x[node['feature']] <= node['threshold']:
            return self._predict_one(x, node['left'])
        return self._predict_one(x, node['right'])

    def predict(self, X):
        return np.array([self._predict_one(x, self.tree) for x in X])
```

### Q4: Explain pruning strategies.

**Answer:**
- **Pre-pruning:** Stop early (max depth, min samples per leaf, min impurity decrease)
- **Post-pruning (CCP):** Build full tree then prune subtrees using cost-complexity pruning: $R_\alpha(T) = R(T) + \alpha|T|$ where $|T|$ is number of leaves. Trade off accuracy vs. complexity via $\alpha$.

## Advanced

- **Regression trees:** Predict mean of leaf; split minimizes MSE/Variance reduction
- **Handling missing data:** Surrogate splits, or treat missing as a separate category
- **Feature importance:** Total reduction in impurity weighted by samples reaching that feature
