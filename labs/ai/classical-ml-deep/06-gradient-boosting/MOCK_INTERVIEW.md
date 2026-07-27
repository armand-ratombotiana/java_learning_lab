# Mock Interview: Gradient Boosting

**Topic:** Implement gradient boosting from scratch for regression

## Core Questions

### Q1: Explain gradient boosting conceptually.

**Answer:**
Gradient boosting builds an ensemble sequentially, where each new model fits the **residuals** (negative gradient) of the previous ensemble.

$F_0(x) = \text{argmin}_\gamma \sum L(y_i, \gamma)$ (constant model)

For $m = 1$ to $M$:
1. Compute pseudo-residuals: $r_i = -\left[\frac{\partial L(y_i, F(x_i))}{\partial F(x_i)}\right]_{F=F_{m-1}}$
2. Fit weak learner $h_m(x)$ to residuals $\{(x_i, r_i)\}$
3. Find optimal step: $\gamma_m = \text{argmin}_\gamma \sum L(y_i, F_{m-1}(x_i) + \gamma h_m(x_i))$
4. Update: $F_m(x) = F_{m-1}(x) + \eta \gamma_m h_m(x)$

### Q2: Implement from scratch for regression with MSE.

```python
class GradientBoostingRegressor:
    def __init__(self, n_estimators=100, lr=0.1, max_depth=3):
        self.n_estimators = n_estimators
        self.lr = lr
        self.max_depth = max_depth
        self.trees = []

    def fit(self, X, y):
        # Initialize with mean
        self.F0 = np.mean(y)
        Fm = np.full(len(y), self.F0)

        for _ in range(self.n_estimators):
            residuals = y - Fm  # negative gradient of MSE: -(y - F) = residual
            tree = DecisionTree(max_depth=self.max_depth)
            tree.fit(X, residuals)
            preds = tree.predict(X)
            Fm += self.lr * preds
            self.trees.append(tree)

    def predict(self, X):
        pred = np.full(X.shape[0], self.F0)
        for tree in self.trees:
            pred += self.lr * tree.predict(X)
        return pred
```

### Q3: What are the key hyperparameters and their effects?

| Parameter | Effect |
|-----------|--------|
| **n_estimators** | More trees = lower bias, risk of overfitting |
| **learning_rate** | Shrinks contribution of each tree. Lower $\eta$ needs more trees |
| **max_depth** | Deeper trees = lower bias, higher variance. Typically 3-6 |
| **subsample** | Stochastic GB — use fraction of data per iteration (reduces overfitting) |
| **min_samples_leaf** | Prevents fitting noise in leaves |

**Trade-off:** $\eta$ and $n\_estimators$ are inversely related. Lower $\eta$ requires proportionally more trees.

### Q4: Explain regularization in gradient boosting.

**Answer:**
- **Shrinkage (learning rate):** $\eta < 1$, typically 0.01-0.1
- **Subsampling:** Row sampling per iteration (stochastic gradient boosting)
- **Column sampling:** Feature subsampling at tree/ split level
- **Pruning:** Limit tree depth, min samples per leaf
- **Early stopping:** Monitor validation loss and stop when it degrades

## Advanced

- **Gradient Boosting for Classification:** Fit trees to residuals for each class in multiclass; use softmax + cross-entropy loss
- **XGBoost improvements:** Regularized objective, approximate split finding, column block for parallel, cache-aware access, sparsity aware
- **LightGBM:** Gradient-based One-Side Sampling (GOSS), Exclusive Feature Bundling (EFB), leaf-wise tree growth
- **CatBoost:** Ordered boosting, categorical feature handling, symmetric trees
