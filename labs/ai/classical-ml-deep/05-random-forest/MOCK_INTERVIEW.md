# Mock Interview: Random Forest

**Topic:** Explain random forest — bagging, feature sampling, OOB, vs gradient boosting

## Core Questions

### Q1: How does random forest work?

**Answer:**
Random Forest is an ensemble of decision trees trained with two sources of randomness:

1. **Bagging (Bootstrap Aggregating):** Each tree is trained on a bootstrap sample (with replacement) of the training data.
2. **Feature sampling:** At each split, only a random subset of $m$ features is considered (typically $m = \sqrt{d}$ for classification, $d/3$ for regression).

Prediction: majority vote (classification) or average (regression).

### Q2: Why does random forest work so well?

**Answer:**
Ensemble reduces variance with minimal bias increase.

For $B$ identically distributed trees with pairwise correlation $\rho$ and variance $\sigma^2$:
$\text{Var}(\text{average}) = \rho\sigma^2 + \frac{1-\rho}{B}\sigma^2$

- Bagging reduces the $\frac{1-\rho}{B}$ term (independent bootstrap noise)
- Feature sampling reduces $\rho$ (trees are less correlated)
- As $B \to \infty$, variance $\to \rho\sigma^2$ (never zero, limited by correlation)

Low bias: individual trees are nearly unbiased (deep trees), ensemble preserves this.

### Q3: Explain OOB error.

**Answer:**
Each bootstrap sample uses ~63.2% of data. Left-out 36.8% is out-of-bag (OOB).

For each training point, predict using only trees where it was OOB. Aggregate OOB predictions give unbiased error estimate without a separate validation set.

OOB error closely matches $k$-fold cross-validation performance.

### Q4: Random Forest vs. Gradient Boosting.

| Aspect | Random Forest | Gradient Boosting |
|--------|--------------|-------------------|
| **Training** | Parallel (trees independent) | Sequential (trees depend on previous) |
| **Goal** | Reduce variance | Reduce bias |
| **Trees** | Deep, low bias | Shallow (stumps), high bias |
| **Overfitting** | Less prone | More prone (needs regularization) |
| **Tuning** | Fewer hyperparameters | Many (lr, n_estimators, subsample, etc.) |
| **Performance** | Good out-of-box | Often better with tuning |
| **Categorical features** | Handles naturally | Needs encoding |

| Gradient boosting tends to outperform random forest on structured/tabular data with careful tuning. Random forest is more robust and easier to train.

## Advanced

- **Extremely Randomized Trees (ExtraTrees):** Randomize split threshold too (pick random threshold instead of best), further reduces variance
- **Proximity matrix:** Count how often two points land in same leaf — useful for unsupervised learning, missing value imputation
- **Variable importance:** Mean decrease in impurity (MDI) or permutation importance
