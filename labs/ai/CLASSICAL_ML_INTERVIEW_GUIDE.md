# Classical Machine Learning — Interview Preparation Guide

> AI Academy @ Java Learning Lab

---

## Table of Contents

1. [Regression](#regression)
2. [Tree-Based Methods](#tree-based-methods)
3. [Support Vector Machines](#support-vector-machines)
4. [Ensemble Methods](#ensemble-methods)
5. [Clustering](#clustering)
6. [Dimensionality Reduction](#dimensionality-reduction)
7. [Anomaly Detection](#anomaly-detection)
8. [Imbalanced Learning](#imbalanced-learning)

---

## Regression

### Linear Regression — OLS Closed Form

The Ordinary Least Squares (OLS) estimator minimizes the sum of squared residuals:

```
L(β) = ||y - Xβ||²
```

Taking the derivative and setting to zero:

```
∇L = -2Xᵀ(y - Xβ) = 0
XᵀXβ = Xᵀy
β̂ = (XᵀX)⁻¹Xᵀy
```

**Assumptions of Linear Regression:**

1. **Linearity**: The relationship between predictors and target is linear.
2. **Independence**: Observations are independent of each other.
3. **Homoscedasticity**: Constant variance of residuals (Var(ε) = σ²I).
4. **Normality**: Residuals are normally distributed (for inference, not estimation).
5. **No perfect multicollinearity**: XᵀX is invertible (full column rank).
6. **Exogeneity**: E[ε|X] = 0 (no omitted variable bias).

**Interpretation:**

- βⱼ: the expected change in y for a one-unit change in xⱼ, holding all other predictors constant.
- In a log-log model: βⱼ is the elasticity (percentage change in y per 1% change in xⱼ).
- In a log-level model: 100 × βⱼ is the approximate percentage change in y per unit change in xⱼ.

### Ridge, Lasso, ElasticNet

**Ridge (L2 regularization):**

```
β̂_ridge = argmin ||y - Xβ||² + λ||β||₂²
β̂_ridge = (XᵀX + λI)⁻¹Xᵀy
```

- Shrinks coefficients toward zero (never exactly zero).
- Handles multicollinearity well.
- Closed-form solution always exists (adds λI to make XᵀX invertible).

**Lasso (L1 regularization):**

```
β̂_lasso = argmin ||y - Xβ||² + λ||β||₁
```

- Performs feature selection (coefficients can become exactly zero).
- No closed-form; solved via coordinate descent or LARS.

**ElasticNet — combines both:**

```
β̂_en = argmin ||y - Xβ||² + λ₁||β||₁ + λ₂||β||₂²
```

- Useful when there are groups of correlated features (lasso picks one, elastic net picks groups).

**Bias-Variance Tradeoff:**

| Model | Bias | Variance |
|-------|------|----------|
| OLS | Low (unbiased) | High (overfits) |
| Ridge | Increases | Decreases |
| Lasso | Increases (more than ridge) | Decreases |
| Optimal λ | Balances both to minimize test error |

### Logistic Regression

**Odds and Log-Odds:**

```
Odds = p / (1 - p)
Logit(p) = log(p / (1 - p)) = Xβ
p = 1 / (1 + e^{-Xβ})  (sigmoid function)
```

**Decision Boundary:**

- If p ≥ 0.5, predict class 1; otherwise class 0.
- Decision boundary is linear in feature space: Xβ = 0.

**Multinomial Logistic Regression (Softmax):**

```
P(y = k | x) = exp(xβ_k) / Σⱼ exp(xβ_j)
```

- One-vs-rest approach vs. softmax regression.
- No closed form — solved via IRLS (iteratively reweighted least squares) or gradient descent.

**Loss Function: Log-Loss (Binary Cross-Entropy):**

```
J(β) = -[y log(p) + (1 - y) log(1 - p)]
```

### Evaluation Metrics

**MSE (Mean Squared Error):**

```
MSE = (1/n) Σ(yᵢ - ŷᵢ)²
```

- Penalizes large errors heavily; sensitive to outliers.

**MAE (Mean Absolute Error):**

```
MAE = (1/n) Σ|yᵢ - ŷᵢ|
```

- Robust to outliers; same units as target.

**R² (Coefficient of Determination):**

```
R² = 1 - SS_res / SS_tot = 1 - Σ(yᵢ - ŷᵢ)² / Σ(yᵢ - ȳ)²
```

- Proportion of variance explained by the model.
- Problem: always increases with more features (even useless ones).

**Adjusted R²:**

```
Adjusted R² = 1 - [(1 - R²)(n - 1) / (n - p - 1)]
```

- Penalizes for number of predictors p.
- Can decrease when useless features are added.

**AIC (Akaike Information Criterion):**

```
AIC = 2k - 2ln(L̂)
```

- Lower is better. Penalizes model complexity (k = number of parameters).
- For OLS: AIC = n ln(SS_res/n) + 2k (up to constant).

**BIC (Bayesian Information Criterion):**

```
BIC = k ln(n) - 2ln(L̂)
```

- Stronger penalty than AIC (k ln(n) vs 2k).
- Asymptotically consistent (picks true model as n → ∞).

**Log-Loss (Cross-Entropy):**

```
LogLoss = -(1/n) Σ[yᵢ log(pᵢ) + (1 - yᵢ)log(1 - pᵢ)]
```

- Proper scoring rule — minimized when predicted probabilities are calibrated.

### Interview Questions — Regression

**Q1: Derive the OLS estimator.**

Start with RSS = (y - Xβ)ᵀ(y - Xβ). Expand, differentiate, set to zero:
∂RSS/∂β = -2Xᵀy + 2XᵀXβ = 0 → XᵀXβ = Xᵀy → β̂ = (XᵀX)⁻¹Xᵀy.

**Q2: Why does ridge regression shrink coefficients but lasso sets them to zero?**

Ridge uses L2 penalty — constraint is a circle in parameter space; intersection with elliptical contours shrinks but rarely hits axis. Lasso uses L1 penalty — constraint is a diamond; corners lie on axes, so intersection often occurs at axes, zeroing coefficients.

**Q3: What is the bias-variance tradeoff in regularization?**

As λ increases, model simplicity increases → bias increases, variance decreases. Optimal λ minimizes expected test error = bias² + variance + irreducible error.

**Q4: How do you interpret a logit coefficient?**

A one-unit increase in xⱼ multiplies the odds of the event by e^{βⱼ}, holding all else constant.

**Q5: What happens if predictors are perfectly collinear?**

XᵀX is singular → no unique OLS solution. Ridge fixes this by adding λI.

**Q6: Compare AIC and BIC for model selection.**

AIC minimizes expected KL divergence; BIC is consistent (selects true model as n→∞). BIC penalizes complexity more heavily.

**Q7: Derive the gradient of log-loss for logistic regression.**

∂J/∂βⱼ = (1/n) Σ(pᵢ - yᵢ)xᵢⱼ.

**Q8: When would you use MAE over MSE?**

MAE is robust to outliers; MSE is differentiable and penalizes large errors more. Use MAE when outliers are expected but not informative; use MSE when Gaussian noise is reasonable.

**Q9: Explain adjusted R² and when to use it.**

Adjusted R² = 1 - (1 - R²)(n - 1)/(n - p - 1). Use it to compare models with different numbers of predictors; it only increases if a new feature improves the model more than expected by chance.

**Q10: How does ElasticNet handle groups of correlated features?**

ElasticNet combines L1 and L2 penalties. The L2 penalty encourages grouping effect (correlated features get similar coefficients), while L1 provides sparsity. This is useful when features come in groups (e.g., dummy-encoded categorical variables).

---

## Tree-Based Methods

### Decision Trees

**Splitting Criteria:**

**Classification — Gini Impurity:**

```
Gini(t) = 1 - Σₖ pₖ²
```

- Minimum at 0 (pure node), maximum when classes are equally distributed.
- For a binary split: Gini_split = (n_left/n) Gini_left + (n_right/n) Gini_right.

**Classification — Entropy / Information Gain:**

```
Entropy(t) = -Σₖ pₖ log₂(pₖ)
InfoGain = Entropy(parent) - Σ (n_child/n) Entropy(child)
```

- Information gain favors splits that create pure children.

**Regression — MSE Reduction:**

```
MSE(t) = (1/n_t) Σ (yᵢ - ȳ_t)²
MSE_reduction = MSE(parent) - Σ (n_child/n) MSE(child)
```

**Pruning:**

- **Pre-pruning**: max_depth, min_samples_split, min_samples_leaf, max_features.
- **Post-pruning (cost-complexity pruning)**: grow full tree, then prune subtrees using:

```
Cα(T) = R(T) + α|T|
```

- R(T) = misclassification rate (or MSE), |T| = number of terminal nodes, α = complexity parameter.
- Weakest-link pruning: iteratively remove the branch that gives the smallest per-node improvement.

**Max Depth:**
- Controls tree depth directly. Deeper trees overfit; shallow trees underfit.

### Random Forest

**Bagging (Bootstrap Aggregating):**

1. Sample B bootstrap datasets (with replacement) from the training data.
2. Train a decision tree on each bootstrap sample.
3. Average predictions (regression) or majority vote (classification).

**Why bagging reduces variance:** For i.i.d. variables with variance σ², the average of B variables has variance σ²/B. Trees are correlated though, so the variance reduction is limited.

**Feature Subsampling (Random Subspace Method):**

- At each split, consider only a random subset of features (typically √p for classification, p/3 for regression).
- This decorrelates the trees further — if all trees used all features, they'd make similar splits.
- Lower feature subsampling → more diverse trees → lower correlation → lower variance.

**OOB Error (Out-of-Bag):**

- Each tree is trained on ~63.2% of the data (1 - 1/e). The remaining ~36.8% is out-of-bag.
- For each observation, predict using only trees where that observation was OOB.
- OOB error is an unbiased estimate of test error — no need for a separate validation set.

**Properties:**

| Property | Value |
|----------|-------|
| Number of hyperparameters | Few (n_estimators, max_features, max_depth) |
| Parallelizable | Yes (each tree is independent) |
| Handles high-dimensional data | Yes (feature subsampling) |
| Overfitting risk | Low (asymptotically doesn't overfit with more trees) |

### Gradient Boosting

**General Framework:**

1. Initialize F₀(x) = argmin Σ L(yᵢ, γ).
2. For m = 1 to M:
   a. Compute pseudo-residuals: rᵢₘ = -∂L(yᵢ, F_{m-1}(xᵢ)) / ∂F_{m-1}(xᵢ).
   b. Fit a weak learner hₘ(x) to rᵢₘ.
   c. Compute optimal step size: γₘ = argmin Σ L(yᵢ, F_{m-1}(xᵢ) + γhₘ(xᵢ)).
   d. Update: Fₘ(x) = F_{m-1}(x) + ν · γₘ · hₘ(x).

- ν (learning rate) controls shrinkage — smaller ν requires more trees but generalizes better.

**AdaBoost (Adaptive Boosting):**

- Special case of forward stagewise additive modeling with exponential loss.
- Weights each sample; misclassified samples get higher weight in the next iteration.
- Final prediction = weighted majority vote of weak learners.

**XGBoost (Extreme Gradient Boosting):**

Key innovations over traditional gradient boosting:

1. **Regularized objective:**
   ```
   L = Σ L(yᵢ, ŷᵢ) + Σ Ω(fₖ)
   Ω(f) = γT + (1/2)λ||w||² + α|w|
   ```
   - T = number of leaves, w = leaf weights, γ = complexity cost, λ = L2, α = L1.

2. **Second-order approximation:**
   - Uses both gradient (gᵢ) and Hessian (hᵢ) for more accurate Newton-Raphson steps.
   ```
   L ≈ Σ [gᵢ fₘ(xᵢ) + ½ hᵢ fₘ(xᵢ)²] + Ω(fₘ)
   ```

3. **Column block structure:**
   - Features are pre-sorted and stored in compressed column (CSC) format as blocks.
   - Enables parallel computation of split finding across features.
   - Block structure is cached in memory for reuse across iterations.

4. **Sparsity awareness:**
   - Missing values or sparse data are handled by learning the default direction.
   - During training, instances with missing values are assigned to left or right child based on which direction reduces loss most.
   - This avoids imputation entirely.

5. **Weighted quantile sketch:**
   - Instead of enumerating all possible split values, XGBoost uses approximate split finding.
   - The weighted quantile sketch proposes candidate split points based on the distribution of Hessian weights.
   - Each candidate covers a percentile of the weighted data, ensuring splits are placed where data is dense.
   - Time complexity per tree: O(#features × #data × log(#candidates)) vs O(#features × #data × log(#data)) for exact greedy.

**LightGBM (Light Gradient Boosting Machine):**

Key innovations:

1. **GOSS (Gradient-Based One-Side Sampling):**
   - Instances with large gradients (underfitting) are more important.
   - Keep all instances with large gradients; randomly sample those with small gradients.
   - Amortization factor multiplies the sampled small-gradient instances to maintain the original data distribution.
   - Reduces data size without losing accuracy.

2. **EFB (Exclusive Feature Bundling):**
   - Many features are mutually exclusive (rarely non-zero simultaneously).
   - Bundle these features into a single feature to reduce dimensionality.
   - Uses a graph coloring problem to find the minimum number of bundles.
   - Enables handling of high-dimensional sparse data efficiently.

3. **Leaf-wise (best-first) tree growth:**
   - Grows the leaf with the largest loss reduction, not level by level.
   - More efficient — achieves lower loss with fewer leaves than level-wise growth.
   - Can overfit on small datasets (mitigated by max_depth or min_data_in_leaf).

**CatBoost (Categorical Boosting):**

Key innovations:

1. **Ordered Boosting:**
   - Standard boosting uses all data to compute residuals, causing target leakage.
   - CatBoost uses ordered boosting: for each step, compute residuals only on data not seen during training.
   - Uses random permutations of the training data — the residual for a sample is computed using a model trained on a subset of the permutation.
   - Reduces prediction shift and overfitting.

2. **Categorical feature handling (no one-hot encoding needed):**
   - For a categorical feature, CatBoost computes target statistics with a prior and smoothing:
   ```
   CatStat = (count_in_class + prior) / (total_count + 1)
   ```
   - Uses a random permutation: for each sample, the statistic is computed only from previous samples in the permutation.
   - Avoids target leakage inherent in simple target encoding.

3. **Symmetric trees (oblivious trees):**
   - All nodes at the same depth use the same splitting feature and threshold.
   - More balanced, less prone to overfitting.
   - Faster inference — predictions are made by traversing a single path, not multiple branches.

### Feature Importance

**1. Gain-based (impurity-based):**

```
Importance(f) = Σ over all nodes splitting on f of (n_node / n_total) × Δimpurity
```

- Built-in for tree models.
- Biased toward high-cardinality features (more possible split points).

**2. Split count (frequency):**

```
Importance(f) = Number of times feature f is used for splitting
```

- Simple but ignores depth/quality of splits.

**3. Permutation importance:**

```
Importance(f) = Score(model, data) - Score(model, data_with_f_shuffled)
```

- Model-agnostic; measures drop in performance when feature is randomly shuffled.
- Breaks the association between feature and target.
- More reliable than gain-based importance.

**4. SHAP (SHapley Additive exPlanations):**

```
SHAP(f, x) = Σ over subsets S not containing f of |S|!(M-|S|-1)!/M! × [E[y|x_S∪{f}] - E[y|x_S]]
```

- Based on Shapley values from cooperative game theory.
- Sum of SHAP values = prediction - base value.
- Properties: local accuracy, missingness, consistency.
- TreeSHAP: efficient O(TLD²) algorithm for tree models (T = trees, L = leaves, D = depth).

**Importance comparison:**

| Method | Local/Global | Model-specific | Direction |
|--------|-------------|----------------|-----------|
| Gain | Global | Trees only | Always positive |
| Split | Global | Trees only | Always positive |
| Permutation | Both | Agnostic | Always positive |
| SHAP | Both | Agnostic | Signed (positive/negative) |

### Interview Questions — Tree-Based Methods

**Q1: Explain the bias-variance decomposition of a single decision tree vs Random Forest.**

A single deep tree has low bias, high variance. Random Forest reduces variance by averaging many decorrelated trees. If B trees each have variance σ² and pairwise correlation ρ, the ensemble variance = ρσ² + (1-ρ)σ²/B. As B → ∞, variance → ρσ². Reducing ρ (via feature subsampling) is key.

**Q2: How does XGBoost differ from standard gradient boosting?**

XGBoost adds L1/L2 regularization, uses second-order gradients (Newton boosting), has built-in sparsity handling, supports column block parallelization, and uses weighted quantile sketches for approximate split finding.

**Q3: When would you use LightGBM over XGBoost?**

LightGBM is faster on large datasets due to GOSS and EFB. It uses leaf-wise growth, which converges faster but can overfit on small data. Use LightGBM when speed matters and data is large; use XGBoost for smaller datasets or when you need built-in handling of missing values.

**Q4: What is the Cold Start problem in CatBoost and how does ordered boosting solve it?**

Standard gradient boosting computes residuals on the same data used to train the model → prediction shift. Ordered boosting computes residuals using models trained on different data subsets → removes target leakage.

**Q5: Why does CatBoost not require one-hot encoding?**

CatBoost has built-in support for categorical features using ordered target statistics with a prior, avoiding the curse of dimensionality from one-hot encoding.

**Q6: Explain the SHAP value for a feature contribution. What are its properties?**

SHAP value = average marginal contribution of a feature across all possible feature subsets. Properties: local accuracy (predictions sum to SHAP values + base), missingness (missing features have SHAP = 0), consistency (if a model changes so a feature's contribution increases, its SHAP value doesn't decrease).

**Q7: What is OOB error and how is it used?**

OOB error uses the ~36.8% of data not sampled for each tree. It provides an unbiased estimate of test error without a separate validation set, equivalent to leave-one-out bootstrap.

**Q8: How does GOSS in LightGBM reduce data without losing accuracy?**

GOSS keeps all high-gradient instances (underfit) and randomly samples low-gradient instances (well-fit). The sampled low-gradient instances are multiplied by (1 - a)/b (where a = large-gradient fraction, b = sampling rate) to maintain the distribution. This focuses computation on the most informative samples.

**Q9: Explain the weighted quantile sketch in XGBoost.**

Instead of trying every possible split value, XGBoost proposes candidate split points weighted by the Hessian (second derivative). Candidates are chosen so each covers roughly equal total weight, ensuring splits are placed where the loss function changes most. Uses a sketch algorithm with ε-approximate quantiles.

**Q10: What is cost-complexity pruning?**

Grow a full tree, then prune subtrees using Cα(T) = R(T) + α|T|. Find the subtree that minimizes Cα for each α via weakest-link pruning: iteratively prune the node that gives the smallest increase in R per leaf removed.

---

## Support Vector Machines

### Maximum Margin Classifier

For linearly separable data, the maximum margin classifier finds the hyperplane that maximizes the distance to the nearest training point (the margin):

```
maximize  M
subject to yᵢ(wᵀxᵢ + b) ≥ M, ||w|| = 1
```

Equivalently (canonical form):

```
minimize ½||w||²
subject to yᵢ(wᵀxᵢ + b) ≥ 1
```

**Support vectors:** training points that lie exactly on the margin boundary (yᵢ(wᵀxᵢ + b) = 1). Only support vectors influence the decision boundary; all other points can be removed without changing the model.

### Kernel Trick

Kernels allow SVMs to find nonlinear decision boundaries without explicitly computing high-dimensional feature maps.

```
K(xᵢ, xⱼ) = ⟨φ(xᵢ), φ(xⱼ)⟩
```

The decision function becomes:

```
f(x) = Σ αᵢ yᵢ K(xᵢ, x) + b
```

**Common Kernels:**

**1. Linear Kernel:**
```
K(xᵢ, xⱼ) = xᵢᵀxⱼ
```
- No mapping — equivalent to linear SVM.
- Use when data is linearly separable or n_features >> n_samples.
- Fastest to train and predict.

**2. Polynomial Kernel:**
```
K(xᵢ, xⱼ) = (γ xᵢᵀxⱼ + r)ᵈ
```
- d = degree, γ = scale, r = coef0.
- Creates d-degree polynomial decision boundary.
- Use when you expect polynomial interactions.
- Risk of numerical instability at high d.

**3. RBF (Radial Basis Function / Gaussian) Kernel:**
```
K(xᵢ, xⱼ) = exp(-γ||xᵢ - xⱼ||²)
```
- Maps to infinite-dimensional Hilbert space.
- γ controls influence radius: small γ → smooth/broad; large γ → wiggly/narrow.
- Use when data is not linearly separable and you need a flexible boundary.
- Most commonly used kernel — good default.

**4. Sigmoid Kernel:**
```
K(xᵢ, xⱼ) = tanh(γ xᵢᵀxⱼ + r)
```
- Similar to a two-layer neural network.
- Not positive semi-definite for all parameters (may not guarantee convergence).
- Limited use in practice.

**When to use each kernel:**

| Kernel | Separability | Interpretability | Speed | Common use case |
|--------|-------------|-----------------|-------|-----------------|
| Linear | Linear only | High (feature weights) | Fastest | Text (high-d sparse), DNA |
| RBF | Nonlinear | Low | Moderate | Default choice |
| Polynomial | Nonlinear | Moderate (degree known) | Slow (high d) | Image similarity |
| Sigmoid | Nonlinear | Low | Moderate | Neural net comparison |

### Soft Margin SVM (Non-separable case)

For non-separable data, introduce slack variables ξᵢ ≥ 0:

```
minimize ½||w||² + C Σ ξᵢ
subject to yᵢ(wᵀxᵢ + b) ≥ 1 - ξᵢ
```

**C parameter:**
- Large C: low tolerance for misclassification (hard margin-like) → low bias, high variance.
- Small C: high tolerance for misclassification → high bias, low variance (more support vectors).
- C is the inverse of regularization strength (1/λ analogy).

**Hinge Loss:** SVMs minimize the hinge loss:
```
L(y, f(x)) = max(0, 1 - y·f(x))
```

### SVR (Support Vector Regression)

SVR uses ε-insensitive loss — only points outside an ε-tube contribute to the loss:

```
L(y, f(x)) = max(0, |y - f(x)| - ε)
```

The optimization problem:

```
minimize ½||w||² + C Σ (ξᵢ + ξᵢ*)
subject to:
  yᵢ - wᵀxᵢ - b ≤ ε + ξᵢ
  wᵀxᵢ + b - yᵢ ≤ ε + ξᵢ*
  ξᵢ, ξᵢ* ≥ 0
```

- ε defines the tube width — larger ε → fewer support vectors → simpler model.
- C controls the penalty for points outside the tube.
- Support vectors are points on or outside the ε-tube.
- SVR is robust to outliers (within ε, no loss).

### Dual Formulation

The primal problem is transformed into the dual using Lagrange multipliers:

```
maximize Σ αᵢ - ½ Σ Σ αᵢαⱼyᵢyⱼK(xᵢ, xⱼ)
subject to 0 ≤ αᵢ ≤ C, Σ αᵢyᵢ = 0
```

- αᵢ: Lagrange multipliers (dual coefficients).
- αᵢ > 0 for support vectors; αᵢ = 0 for other points.
- αᵢ = C for points inside the margin or misclassified.

Decision function: f(x) = Σ αᵢ yᵢ K(xᵢ, x) + b.

### Interview Questions — SVM

**Q1: Derive the dual of the SVM optimization problem.**

Start with primal: min ½||w||² s.t. yᵢ(wᵀxᵢ + b) ≥ 1. Lagrangian: L = ½||w||² - Σαᵢ[yᵢ(wᵀxᵢ + b) - 1]. KKT conditions: ∂L/∂w = 0 → w = Σαᵢyᵢxᵢ; ∂L/∂b = 0 → Σαᵢyᵢ = 0. Substitute back → dual: max Σαᵢ - ½ΣΣαᵢαⱼyᵢyⱼxᵢᵀxⱼ.

**Q2: Why does the kernel trick work?**

The dual formulation depends only on dot products xᵢᵀxⱼ. By replacing xᵢᵀxⱼ with K(xᵢ, xⱼ) = ⟨φ(xᵢ), φ(xⱼ)⟩, we implicitly work in a higher-dimensional space without computing φ(x). Mercer's theorem guarantees K is a valid kernel if it's positive semi-definite.

**Q3: How does the C parameter affect the SVM decision boundary?**

C controls the penalty for margin violation. Large C → hard margin (fewer support vectors, more complex boundary). Small C → soft margin (more support vectors, simpler boundary, regularized).

**Q4: When are support vectors useful?**

Support vectors are the critical training examples on the margin boundary. They're a natural summary of the training data — only SVs affect the model. This makes SVMs memory-efficient after training.

**Q5: How does SVR differ from standard regression?**

SVR has ε-insensitive loss — points within ε of the prediction contribute zero loss. This creates a "tube" around the regression line, making SVR robust to small errors and outliers.

**Q6: What happens if you use the wrong kernel?**

Linear kernel on nonlinear data → high bias (underfit). RBF kernel on linear data → high variance (overfit) unless γ is properly tuned. Sigmoid kernel with non-PSD parameters → optimization may not converge.

**Q7: How do you choose γ in the RBF kernel?**

γ = 1/(2σ²). Small γ → large σ → smooth, high-bias decision boundary. Large γ → small σ → wiggly, high-variance boundary. Use cross-validation (e.g., grid search over log scale: 10⁻³, 10⁻², ..., 10³).

**Q8: Can SVMs handle multi-class classification?**

Yes, via one-vs-one (OvO) or one-vs-rest (OvR). LibSVM (used by sklearn SVC) uses OvO by default: train C(n, 2) binary classifiers and vote.

**Q9: Explain the ε parameter in SVR.**

ε defines the width of the ε-insensitive tube. Points within ε of the prediction have zero loss. Larger ε → fewer support vectors → simpler model (higher bias). Smaller ε → more support vectors → more complex fit (higher variance).

**Q10: How does the SVM dual give us the kernel trick?**

The dual objective and decision function both depend only on xᵢᵀxⱼ dot products. Substituting K(xᵢ, xⱼ) for xᵢᵀxⱼ is equivalent to working in the RKHS induced by the kernel.

---

## Ensemble Methods

### Bagging vs Boosting vs Stacking

| Aspect | Bagging | Boosting | Stacking |
|--------|---------|----------|----------|
| **Training** | Parallel | Sequential | Parallel (base), then meta |
| **Goal** | Reduce variance | Reduce bias | Reduce both |
| **Base learners** | Strong (high variance) | Weak (high bias) | Diverse set |
| **Weighting** | Equal weight | Adaptive sample weights | Meta-model learns weights |
| **Overfitting** | Low | Risk if too many iterations | Risk without proper validation |
| **Key example** | Random Forest | XGBoost, AdaBoost | Multi-level stacking |

### Bias-Variance Decomposition of Ensembles

For an ensemble of B models with prediction f̂ₖ(x):

**Bagging:**
```
E[f_ensemble] = E[f̂ₖ]          (unbiased if base is unbiased)
Var[f_ensemble] = ρσ² + (1-ρ)σ²/B
```
- ρ = average pairwise correlation between base model predictions.
- Bagging reduces variance by averaging. As B → ∞, Var → ρσ².
- Random Forest further reduces ρ via feature subsampling.

**Boosting:**
```
Bias²_ensemble ≈ Bias²_base + residuals_from_iterations
Variance_ensemble ≈ B × Var_base (if no shrinkage)
```
- With shrinkage (ν < 1): reduces variance by down-weighting each learner.
- Bias decreases with each iteration (focus on hard examples).

### Stacking (Stacked Generalization)

**Architecture:**
- **Level-0 (Base models)**: A diverse set of models (e.g., RF, XGB, SVM, linear).
- **Level-1 (Meta-model)**: Learns how to best combine base model predictions.

**Training procedure:**

1. Split training data into K folds.
2. For each base model m:
   - Train on K-1 folds, predict on held-out fold.
   - Repeat for all K folds → generate out-of-fold predictions for entire training set.
3. Train meta-model on out-of-fold predictions (as features) with true labels.
4. For test predictions:
   - Retrain each base model on full training data.
   - Predict on test data with base models.
   - Feed test predictions to meta-model for final prediction.

**Why out-of-fold?**
- Using in-sample predictions trains meta-model on overfit predictions → poor generalization.
- Out-of-fold predictions are "honest" — the model hasn't seen those points.

### Voting — Hard vs Soft

**Hard voting (majority voting):**

```
ŷ = mode(ŷ₁, ŷ₂, ..., ŷₘ)
```

- Simple, doesn't require calibrated probabilities.
- Works well when classifiers are diverse.

**Soft voting (weighted probability averaging):**

```
ŷ = argmax Σ wₖ pₖ(y = c | x)
```

- Usually better than hard voting.
- Requires well-calibrated probabilities.
- Can weight models by performance.

### When Does Ensembling Help?

1. **Base models are diverse** — low correlation between errors (different algorithms, different hyperparameters, different feature subsets).
2. **Base models are skillful** — each performs better than random.
3. **Sufficient data** — stacking needs enough data for out-of-fold splits.
4. **Bias-variance tradeoff** — if models have low bias but high variance, bagging helps; if they have high bias, boosting helps.
5. **Diminishing returns** — after ~10-20 models, additional models rarely improve a well-tuned ensemble.

**When ensembling doesn't help:**
- All models make the same errors (e.g., identical architectures).
- Insufficient data for meta-model training.
- Meta-model overfits to base model predictions.

### Interview Questions — Ensemble Methods

**Q1: Why does bagging reduce variance more than it increases bias?**

Averaging B i.i.d. random variables reduces variance by factor B. Tree predictions are not independent (ρ > 0), so variance = ρσ² + (1-ρ)σ²/B. The bias of the ensemble equals the bias of a single tree (averaging doesn't change expected prediction).

**Q2: How does stacking differ from simple voting?**

Voting uses fixed rules (majority or average). Stacking learns an optimal combination: the meta-model can weight models based on their strengths, even learning nonlinear combinations.

**Q3: Why must stacking use out-of-fold predictions?**

Using in-sample predictions would give the meta-model an overfitted view — base models have memorized noise in the training data. Out-of-fold predictions simulate test performance, giving the meta-model an unbiased view of each model's generalization.

**Q4: What causes an ensemble to overfit?**

In boosting: too many iterations with low shrinkage (learns noise). In stacking: meta-model too complex with too few base models; or base models are too correlated. In RF: individual trees too deep with too few features.

**Q5: When would you use soft voting over hard voting?**

Soft voting uses probabilities, so it's better when models output well-calibrated probabilities. Hard voting is better when probabilities are poorly calibrated or when you want robustness to outliers.

---

## Clustering

### K-Means

**Algorithm:**

1. Initialize K centroids (randomly selected points or k-means++).
2. Assign each point to the nearest centroid (usually Euclidean distance).
3. Update each centroid to the mean of its assigned points.
4. Repeat steps 2-3 until convergence (centroids stabilize or iterations exceed limit).

**Objective:** Minimize within-cluster sum of squares (WCSS):
```
J = Σₖ Σ_{i∈Cₖ} ||xᵢ - μₖ||²
```

**Initialization methods:**

1. **Forgy**: Randomly select K points from the data as initial centroids.
   - Simple but can pick outliers.
   - May lead to empty clusters.

2. **K-means++**: Probabilistic initialization:
   - Pick first centroid uniformly at random.
   - For each subsequent centroid, pick x with probability proportional to D(x)² (squared distance from nearest existing centroid).
   - Results in better initial spread; O(log K) approximation to optimal.

**Convergence:** K-means always converges (monotonic decrease of J), but to a local optimum. Run multiple times with different initializations.

**Choosing K — Elbow Method:**
- Plot WCSS vs K.
- Look for the "elbow" where WCSS improvement levels off.

**Choosing K — Silhouette Score:**

```
s(i) = (b(i) - a(i)) / max(a(i), b(i))
```

- a(i) = mean distance to points in the same cluster.
- b(i) = mean distance to points in the nearest neighboring cluster.
- s(i) ∈ [-1, 1]: 1 = well-clustered, 0 = on boundary, -1 = misclassified.
- Average silhouette score across all points; higher is better.
- Unsupervised (doesn't require ground truth).

**Limitations:**
- Assumes spherical clusters of equal size.
- Sensitive to outliers.
- Requires K to be specified.
- Euclidean distance sensitive to feature scaling.

### DBSCAN (Density-Based Spatial Clustering of Applications with Noise)

**Parameters:**
- **eps (ε)**: Maximum distance for two points to be considered neighbors.
- **minPts**: Minimum number of points within ε to form a dense region.

**Point types:**
- **Core point**: Has at least minPts points within ε (including itself).
- **Border point**: Not a core point, but within ε of a core point.
- **Noise point**: Neither core nor border.

**Density-reachable:**
- Point p is directly density-reachable from q if p is within ε of q and q is a core point.
- p is density-reachable from q if there's a chain p₁, ..., pₙ where each pᵢ is directly density-reachable from pᵢ₋₁.
- Density-connected: two points are connected if there's a third point that is density-reachable from both.

**Algorithm:**
1. For each point, find all neighbors within ε.
2. Mark points with ≥ minPts neighbors as core.
3. For each core point, expand cluster: all density-reachable points (core and border) join the cluster.
4. Remaining points are noise.

**Properties:**
- Does not require K (number of clusters).
- Finds arbitrary-shaped clusters.
- Handles noise/outliers naturally.
- Border points can belong to only one cluster (deterministic assignment).
- Sensitive to eps and minPts choices.

### Hierarchical Clustering

**Agglomerative (bottom-up):**
1. Start with each point as its own cluster.
2. Merge the two closest clusters based on linkage criterion.
3. Repeat until all points are in one cluster.
4. Cut the dendrogram at a height that yields the desired number of clusters.

**Linkage criteria:**

| Linkage | Distance between clusters | Properties |
|---------|--------------------------|------------|
| Single | min||x - y||: x∈A, y∈B | Can form chains; sensitive to noise |
| Complete | max||x - y||: x∈A, y∈B | Compact clusters; favors spheres |
| Average | (1/|A||B|) Σ Σ ||x - y|| | Balanced; popular |
| Ward | Increase in WCSS from merging | Minimizes variance; spherical clusters |

**Dendrogram:**
- Tree diagram showing merge sequence.
- y-axis = distance/cluster similarity at merge.
- Cut horizontally to get clusters.
- Useful for visualizing hierarchical structure.

**Properties:**
- Deterministic (no random initialization).
- O(n³) naïve (O(n² log n) with priority queue).
- Not scalable to large datasets.
- Can use a precomputed distance matrix.

### Gaussian Mixture Models (GMM)

**Model:** Weighted sum of K Gaussian components:
```
p(x) = Σₖ πₖ N(x | μₖ, Σₖ)
```
- πₖ = mixing coefficient (Σ πₖ = 1, πₖ ≥ 0).
- μₖ = mean vector.
- Σₖ = covariance matrix (spherical, diagonal, or full).

**EM Algorithm (Expectation-Maximization):**

**E-step:** Compute responsibilities (posterior probability that point i belongs to component k):
```
γᵢₖ = πₖ N(xᵢ | μₖ, Σₖ) / Σⱼ πⱼ N(xᵢ | μⱼ, Σⱼ)
```

**M-step:** Update parameters using soft assignments:
```
Nₖ = Σᵢ γᵢₖ
πₖ_new = Nₖ / N
μₖ_new = (1/Nₖ) Σᵢ γᵢₖ xᵢ
Σₖ_new = (1/Nₖ) Σᵢ γᵢₖ (xᵢ - μₖ_new)(xᵢ - μₖ_new)ᵀ
```

**Log-likelihood:**
```
log p(X | π, μ, Σ) = Σᵢ log(Σₖ πₖ N(xᵢ | μₖ, Σₖ))
```

- Monotonically increases with each EM iteration.
- Converges to local optimum (use multiple restarts).
- Used for model selection (AIC/BIC on log-likelihood).

**GMM vs K-means:**
- GMM is a soft clustering (probabilistic assignments).
- GMM allows elliptical clusters of varying sizes.
- K-means is a special case of GMM with Σₖ = εI (spherical, equal variance).

### Evaluation Metrics

**Requiring ground truth labels:**

**Homogeneity:** Each cluster contains only members of a single class.
```
h = 1 - H(C|K) / H(C)
```
- H(C|K) = conditional entropy of classes given clusters.
- Perfect = 1.

**Completeness:** All members of a given class are assigned to the same cluster.
```
c = 1 - H(K|C) / H(K)
```

**V-Measure:** Harmonic mean of homogeneity and completeness.
```
V = (1 + β) × h × c / (β × h + c)
```
- β = 1 (equal weight) by default.

**Adjusted Rand Index (ARI):**
```
ARI = (RI - Expected_RI) / (max(RI) - Expected_RI)
```
- RI = (number of point pairs in same cluster AND same class + pairs in different clusters AND different classes) / total pairs.
- ARI ranges [-1, 1]; 0 = random clustering, 1 = perfect match.
- Corrects for chance.

**Not requiring ground truth:**

**Silhouette Score:** See K-means section above.
**Davies-Bouldin Index:** Average similarity between each cluster and its most similar one (lower is better).
**Calinski-Harabasz Index:** Ratio of between-cluster variance to within-cluster variance (higher is better).

### Interview Questions — Clustering

**Q1: Why does K-means converge? Is the solution optimal?**

K-means monotonically decreases WCSS: the assignment step minimizes WCSS for given centroids; the update step minimizes WCSS for given assignments. It converges to a local minimum, not the global optimum.

**Q2: How does k-means++ initialization work and why is it effective?**

K-means++ picks centroids sequentially, favoring points far from existing centroids. This achieves O(log K) approximation to the optimal WCSS and reduces the chance of poor local minima.

**Q3: What are the advantages of DBSCAN over K-means?**

DBSCAN doesn't require K, finds arbitrary shapes, handles noise, and is robust to outliers. K-means assumes spherical clusters of equal size and is sensitive to outliers.

**Q4: Explain the difference between core, border, and noise points in DBSCAN.**

Core: ≥ minPts within ε. Border: < minPts within ε but within ε of a core. Noise: neither.

**Q5: When would you use hierarchical clustering over K-means?**

When you need a dendrogram (exploratory analysis), don't know K, have small data, or need deterministic results.

**Q6: Compare single-linkage vs complete-linkage clustering.**

Single linkage produces chain-like clusters (chaining effect); complete linkage produces compact, spherical clusters. Single is sensitive to noise but can find elongated shapes.

**Q7: How does GMM differ from K-means and when would you prefer it?**

GMM is probabilistic (soft assignments), handles elliptical clusters of varying sizes and orientations. Prefer GMM when clusters overlap or have different shapes/sizes.

**Q8: Explain the EM algorithm for GMM.**

E-step: compute responsibilities (posterior probabilities of component membership). M-step: weighted MLE updates for π, μ, Σ using responsibilities. Guarantees monotonic log-likelihood increase.

**Q9: What is the adjusted Rand Index and why use it over raw accuracy?**

ARI adjusts for chance clustering. Raw accuracy would credit even random labels. ARI = 0 for random labeling, 1 for perfect match.

**Q10: How do you choose eps in DBSCAN?**

Plot k-distance graph (distance to k-th nearest neighbor, k = minPts). Find the "elbow" — the point where distances spike. This corresponds to the transition from dense to sparse regions.

---

## Dimensionality Reduction

### PCA (Principal Component Analysis)

**Goal:** Find the directions of maximum variance in the data.

**Mathematical derivation:**

Given centered data X (n × p), we want unit vector w₁ that maximizes variance of projected data:

```
Var(Xw₁) = w₁ᵀXᵀXw₁ = w₁ᵀCov(X)w₁  (up to 1/n)
```

Subject to w₁ᵀw₁ = 1. Using Lagrangian:

```
L = w₁ᵀΣw₁ - λ(w₁ᵀw₁ - 1)
∂L/∂w₁ = 2Σw₁ - 2λw₁ = 0
Σw₁ = λw₁
```

Thus w₁ = eigenvector of Σ (covariance matrix), λ = eigenvalue = variance along w₁.

**Procedure:**
1. Center (and scale) the data.
2. Compute covariance matrix Σ = (1/(n-1))XᵀX.
3. Compute eigenvectors and eigenvalues of Σ.
4. Sort eigenvalues descending; select top k eigenvectors.
5. Project: Z = XWₖ (n × k matrix).

**Explained Variance Ratio:**

```
EV_k = λₖ / Σᵢ λᵢ
```

- Proportion of total variance captured by each principal component.
- Cumulative EVR helps choose k (e.g., keep enough PCs to explain 95% variance).

**Whitening (sphering):**
- After PCA, transform so that components have unit variance:
  ```
  Z_white = Z / √λ
  ```
- Decorrelates features (diagonal covariance matrix).
- Used as preprocessing for ICA or other algorithms.

**PCA limitations:**
- Linear transformation only.
- Assumes variance = informativeness (can discard low-variance signal).
- Sensitive to scaling (always standardize first).
- PCs are hard to interpret (linear combinations of all features).

### t-SNE (t-Distributed Stochastic Neighbor Embedding)

**Goal:** Visualize high-dimensional data in 2D or 3D while preserving local structure.

**How it works:**

1. **High-dimensional similarities (pⱼ|ᵢ):**
   - For each point i, compute conditional probability that i would pick j as neighbor under a Gaussian centered at i.
   ```
   p_{j|i} = exp(-||xᵢ - xⱼ||² / 2σᵢ²) / Σ_{k≠i} exp(-||xᵢ - xₖ||² / 2σᵢ²)
   ```
   - σᵢ is set per-point so that the perplexity (measure of effective neighbors) equals a user parameter.

2. **Low-dimensional similarities (qⱼ|ᵢ):**
   - Use Student-t distribution (1 degree of freedom) instead of Gaussian:
   ```
   q_{j|i} = (1 + ||yᵢ - yⱼ||²)^{-1} / Σ_{k≠i} (1 + ||y�<｜begin▁of▁sentence｜>---
   ```
   - The heavy tails of the t-distribution alleviate the "crowding problem" (moderate distances in high-d become large in low-d).

3. **KL divergence minimization:**
   ```
   KL(P||Q) = Σᵢ Σⱼ p_{j|i} log(p_{j|i} / q_{j|i})
   ```
   - Minimized by gradient descent on the low-dimensional coordinates yᵢ.
   - KL divergence is asymmetric: penalizes q underestimating p (local structure preserved) more than overestimating p.

**Perplexity:**
- Related to the effective number of neighbors (σᵢ is tuned to achieve desired perplexity).
- Typical range: 5-50.
- Lower perplexity: focus on very local structure; higher: capture global structure.
- t-SNE is relatively robust to perplexity choice but can produce different visualizations.

**Properties:**
- Non-parametric (no projection for new points; must re-run).
- Stochastic (different runs give different results).
- Preserves local structure well; global structure less reliable.
- Distance/size of clusters in t-SNE are not meaningful.
- O(n²) time and memory (O(n log n) with Barnes-Hut approximation).

### UMAP (Uniform Manifold Approximation and Projection)

**How it differs from t-SNE:**

| Aspect | t-SNE | UMAP |
|--------|-------|------|
| Theoretical foundation | Information theory, stochastic neighbor embedding | Riemannian geometry, topological data analysis |
| Optimization | KL divergence | Cross-entropy (attractive + repulsive forces) |
| Initialization | Random | Spectral embedding (deterministic) |
| Distance preservation | Local only | Local + global balance |
| Speed | Slower (Barnes-Hut: O(n log n)) | Faster (stochastic gradient: O(n)) |
| Scalability | < 10K points typically | Can handle 100K+ points |
| Reproducibility | Low (different runs vary) | High (spectral init + deterministic optimization) |
| Hyperparameters | Perplexity (5-50) | n_neighbors (2-200), min_dist (0-1) |

**UMAP's key insight:**
- Builds a fuzzy topological representation of the high-dimensional data (a weighted graph).
- Searches for a low-dimensional representation where the fuzzy cross-entropy between the high-d and low-d graphs is minimized.
- n_neighbors controls local vs global balance (like perplexity in t-SNE).
- min_dist controls how tightly points are allowed to pack in low-d.

### Feature Selection vs Feature Extraction

| Aspect | Feature Selection | Feature Extraction |
|--------|------------------|-------------------|
| **Output** | Subset of original features | Transformed features (latent variables) |
| **Interpretability** | High (original features retained) | Low (linear combinations) |
| **Redundancy** | Removes irrelevant features | Combines correlated features |
| **Examples** | Filter, wrapper, embedded methods | PCA, t-SNE, autoencoders |
| **Overfitting** | Less risk if selection is validated | Risk of overfitting (especially autoencoders) |
| **When to use** | Need interpretable model | High-dimensional data, visualization |

**Feature Selection methods:**

1. **Filter methods:** Statistical measures (correlation, chi-squared, mutual information).
   - Fast, model-agnostic, but ignores feature interactions.

2. **Wrapper methods:** Forward/backward selection, recursive feature elimination (RFE).
   - Model-specific, but computationally expensive and prone to overfitting.

3. **Embedded methods:** Lasso, Ridge, tree-based importance.
   - Built into model training; balance of filter and wrapper.

### Interview Questions — Dimensionality Reduction

**Q1: Derive the PCA objective and show that it leads to eigenvectors of the covariance matrix.**

PCA maximizes variance of projected data: max wᵀΣw s.t. wᵀw = 1. Lagrangian → Σw = λw. Thus w is an eigenvector of Σ with eigenvalue λ.

**Q2: Why standardize data before PCA?**

PCA is sensitive to scales — a feature with larger variance dominates the first PC. Standardization (z-score) makes all features comparable.

**Q3: How do you choose the number of principal components?**

Use cumulative explained variance ratio (e.g., 95%), scree plot (elbow), or Kaiser criterion (eigenvalues > 1).

**Q4: Explain the "crowding problem" in t-SNE and how the t-distribution helps.**

In high dimensions, many points are at moderate distances. Gaussian assigns low probability to moderate distances → in low-d, they get pushed far apart (crowded). The t-distribution's heavy tail assigns higher probability to moderate distances → allows them to stay close in low-d.

**Q5: What is perplexity in t-SNE?**

Perplexity controls σᵢ for each point's Gaussian kernel: how many neighbors are considered "close." Higher perplexity = more global structure.

**Q6: How does UMAP achieve better global structure preservation than t-SNE?**

UMAP uses a graph-based approach and cross-entropy loss that balances attractive and repulsive forces equally. t-SNE's KL divergence over-weights local structure preservation.

**Q7: When would you use feature selection over PCA?**

When interpretability matters (keep original features), when features have individual meaning, or when you suspect mostly irrelevant features.

**Q8: What's the difference between PCA and factor analysis?**

PCA captures total variance with orthogonal components. Factor analysis captures shared variance with latent factors plus unique variance; factors can be rotated.

**Q9: Can PCA be kernelized?**

Yes, kernel PCA maps data to higher-dimensional space before computing PCs. Allows nonlinear dimensionality reduction.

**Q10: Why does t-SNE produce different results each run?**

t-SNE uses random initialization and stochastic optimization. The final visualization can vary, though global patterns often remain consistent.

---

## Anomaly Detection

### Isolation Forest

**Key idea:** Anomalies are "few and different" — they are easier to isolate (require fewer random splits) than normal points.

**Algorithm:**
1. Build an ensemble of isolation trees:
   - Randomly select a feature and split value between min and max of that feature.
   - Recursively partition until each point is isolated or max depth is reached.
2. Compute anomaly score based on average path length:
   ```
   s(x, n) = 2^{-E[h(x)] / c(n)}
   ```
   - h(x) = path length of x in a tree.
   - c(n) = average path length of unsuccessful search in BST (normalization factor).
   - E[h(x)] = average path length across all trees.
3. Score interpretation:
   - s ≈ 1: anomaly (very short path).
   - s < 0.5: normal point (long path).
   - s ≈ 0.5: ambiguous (no clear distinction).

**Properties:**
- Linear time complexity O(n).
- No distance computation needed.
- Works well with high-dimensional data (subsampling features).
- Contamination parameter sets expected proportion of anomalies.

### One-Class SVM

**Key idea:** Find a hypersphere (or hyperplane in feature space) that encloses most of the data.

**Schölkopf's formulation:**
- Separate data from origin with maximum margin.
- ν (nu) parameter: upper bound on the fraction of outliers, lower bound on the fraction of support vectors.

**Tax and Duin's SVDD (Support Vector Data Description):**
- Find the minimum-radius hypersphere that contains most training points.
- Points outside the sphere are anomalies.

**Properties:**
- Good for high-dimensional data.
- RBF kernel captures non-spherical boundaries.
- Sensitive to ν parameter (similar to contamination in IF).
- Training is O(n²) to O(n³) — not scalable.

### LOF (Local Outlier Factor)

**Key idea:** Compare local density of a point to the local density of its neighbors. Anomalies have lower density than their neighbors.

**Algorithm:**
1. For each point, compute k-distance (distance to k-th nearest neighbor).
2. Compute reachability distance:
   ```
   reach-distₖ(a, b) = max(k-distance(b), dist(a, b))
   ```
3. Compute local reachability density (LRD):
   ```
   LRDₖ(a) = 1 / (Σ_{b∈Nₖ(a)} reach-distₖ(a, b) / |Nₖ(a)|)
   ```
4. Compute LOF:
   ```
   LOFₖ(a) = (1/|Nₖ(a)|) Σ_{b∈Nₖ(a)} LRDₖ(b) / LRDₖ(a)
   ```
5. LOF > 1: anomaly (lower density than neighbors); LOF ≈ 1: comparable density.

**Properties:**
- Detects local anomalies (points anomalous relative to neighbors, not globally).
- Captures contextual anomalies (e.g., a $1000 purchase in a budget store vs luxury store).
- Sensitive to choice of k (minPts equivalent).

### Autoencoder-Based Anomaly Detection

**Architecture:**
- Input → Encoder → Bottleneck (low-dimensional) → Decoder → Reconstruction.

**Detection principle:**
- Train autoencoder to reconstruct normal data.
- Anomalies have high reconstruction error (the model hasn't seen their pattern).
- Reconstruction error = ||x - x̂||² (MSE between input and output).

**Threshold selection:**
- Compute reconstruction error on validation set (known normal data).
- Set threshold at e.g., mean + 3 × std, or at a given percentile.

**Variants:**
- Variational autoencoder (VAE): probabilistic, better for high-dimensional data.
- Denoising autoencoder: robust to small perturbations.
- Sparse autoencoder: uses sparsity regularization.

**Advantages:**
- Handles high-dimensional data (images, logs, time series).
- Learns complex, nonlinear patterns.
- No labels required (unsupervised).

### Applications in Production Monitoring

| Application | Method | Why it works |
|-------------|--------|--------------|
| Server metrics (CPU, memory, latency) | Isolation Forest | Fast, handles high-d feature space |
| Fraud detection | LOF + Autoencoder | Local anomalies + complex patterns |
| Network intrusion | One-Class SVM | Well-defined boundary in high-d |
| Log monitoring | Autoencoder | Learns normal log patterns |
| Manufacturing defect detection | PCA reconstruction error | Simple, interpretable |
| Credit card fraud | Isolation Forest + XGB | IF for speed (real-time), XGB for secondary pass |

### Interview Questions — Anomaly Detection

**Q1: Why does Isolation Forest use random splits?**

Anomalies are "few and different" — they require fewer random splits to isolate. The average path length across trees is inversely proportional to anomaly score.

**Q2: What is the difference between One-Class SVM and standard SVM?**

One-Class SVM separates data from the origin (or a hypersphere) rather than separating two classes. ν controls the tradeoff between boundaries and outliers.

**Q3: How does LOF detect local anomalies?**

LOF compares a point's local density to its neighbors' densities. If a point is in a sparse region but its neighbors are in dense regions, LOF > 1 (local anomaly). A point in a globally sparse region but with equally sparse neighbors has LOF ≈ 1 (normal, in context).

**Q4: How do you set the anomaly threshold for autoencoder-based detection?**

Compute reconstruction errors on a validation set of known normal data. Set threshold at μ + kσ or at a high percentile (e.g., 95th). Alternatively, use the elbow of the sorted reconstruction error curve.

**Q5: What happens if you train an autoencoder on data containing anomalies?**

The autoencoder will learn to reconstruct anomalies too, reducing detection performance. For robust training, use a small network (bottleneck limits capacity) or train on clean data.

**Q6: When would you use PCA-based reconstruction error over an autoencoder?**

PCA is linear, fast, and interpretable — good for low-dimensional data where linear assumptions hold. Autoencoders capture nonlinear patterns and handle high-dimensional data like images.

**Q7: Compare advantages of Isolation Forest vs One-Class SVM.**

Isolation Forest: O(n) time, scalable, handles high-d, no distance metric needed. One-Class SVM: can learn arbitrary boundaries (RBF kernel), but O(n²) time — not for large datasets.

**Q8: Define contamination in anomaly detection context.**

Contamination = expected proportion of anomalies in the dataset. Used for threshold setting in IF and parameter tuning in One-Class SVM. If unknown, use 0.1 as default or tune via cross-validation.

**Q9: How can anomaly detection be used for real-time monitoring?**

Train model offline on normal data; deploy with threshold. Each new sample gets an anomaly score in real-time (O(1) for IF inference). Flag and escalate if score exceeds threshold. Periodic retraining as data patterns evolve.

**Q10: What's the difference between novelty detection and outlier detection?**

Novelty detection: train on "clean" normal data, detect any deviation (e.g., new class in manufacturing). Outlier detection: train on data that may already contain outliers; the model must be robust to contamination.

---

## Imbalanced Learning

### Resampling Methods

**SMOTE (Synthetic Minority Oversampling Technique):**

1. For each minority sample, find its k nearest neighbors (also minority).
2. Randomly select one neighbor.
3. Create synthetic sample along the line connecting the sample and neighbor:
   ```
   x_new = x + λ × (x_neighbor - x), λ ∈ [0, 1]
   ```
4. Repeat to reach desired class balance.

**ADASYN (Adaptive Synthetic Sampling):**

- Like SMOTE but generates more synthetic samples for harder-to-learn minority examples (those with more majority neighbors).
- Density distribution: for each minority sample, rᵢ = Δᵢ / K (fraction of majority neighbors).
- Normalize rᵢ to get a probability distribution; generate more samples where rᵢ is high.

**Random Undersampling:**
- Randomly remove majority class samples to balance the classes.
- Simple and fast, but discards potentially informative majority samples.
- Can cause loss of decision boundary for majority class (information loss).

**Comparison:**

| Method | Pros | Cons |
|--------|------|------|
| SMOTE | Creates realistic synthetic samples | Can create noise if minority is sparse |
| ADASYN | Focuses on hard examples | May amplify noise |
| Random Undersampling | Fast, simple | Loses majority data |
| Random Oversampling | Keeps all data | Overfits (duplicates exact samples) |

### Algorithmic Methods

**Class Weight:**
- Assign higher weight to minority class in the loss function.
- For binary classification:
  ```
  w₀ = n_samples / (2 × n₀),  w₁ = n_samples / (2 × n₁)
  ```
- Used in logistic regression, SVM, tree-based models, neural networks.

**Focal Loss:**

- Modifies cross-entropy to focus on hard-to-classify examples:
  ```
  FL(pₜ) = -(1 - pₜ)^γ log(pₜ)
  ```
  - pₜ = predicted probability for the true class.
  - γ ≥ 0: focusing parameter. γ = 0 → standard CE; γ > 0 → down-weights easy examples.
- Particularly effective for object detection (RetinaNet).
- Reduces loss contribution from well-classified majority examples.

**Cost-Sensitive Learning:**
- Assign different misclassification costs to different classes.
- Example: cost of false negative (fraud) = 10× cost of false positive.
- Incorporate costs into decision threshold or loss function.
- Can be implemented via:
  - Weighted loss function.
  - Cost-sensitive decision trees (split criterion includes costs).
  - MetaCost: relabel training data based on minimum expected cost.

### Evaluation Metrics

**Precision-Recall Curve:**
- Precision = TP / (TP + FP) — among predicted positives, how many are correct.
- Recall = TP / (TP + FN) — among actual positives, how many are found.
- PR curve: recall on x-axis, precision on y-axis.
- **For imbalanced data, PR curve is more informative than ROC curve** (ROC can be overly optimistic when negatives dominate).

**F1 Score:**
```
F1 = 2 × precision × recall / (precision + recall)
```
- Harmonic mean of precision and recall.
- Best when you want balanced precision-recall tradeoff.
- Fβ: allows weighting recall β times more than precision.

**Matthews Correlation Coefficient (MCC):**
```
MCC = (TP×TN - FP×FN) / √((TP+FP)(TP+FN)(TN+FP)(TN+FN))
```
- Ranges [-1, 1]: 1 = perfect, 0 = random, -1 = total disagreement.
- Balanced measure even for severely imbalanced classes.
- Single metric that captures all four confusion matrix entries.
- Preferred over F1 when classes are highly imbalanced.

**Which metric to use:**

| Scenario | Recommended Metric |
|----------|-------------------|
| Balanced classes | Accuracy, F1 |
| Severely imbalanced | MCC, PR-AUC |
| Rare event detection | Recall @ k, precision @ desired recall |
| Cost-sensitive | Expected value (cost × confusion matrix) |
| Threshold-independent | PR-AUC, ROC-AUC (use cautiously) |

### Interview Questions — Imbalanced Learning

**Q1: How does SMOTE generate synthetic samples? What are its limitations?**

SMOTE creates samples by interpolating between a minority sample and one of its k nearest minority neighbors. Limitations: can generate noise in overlapping regions; doesn't consider majority neighbors (ADASYN addresses this); doesn't work well with high-dimensional sparse data.

**Q2: Why is the PR curve preferred over ROC for imbalanced data?**

ROC-AUC can be optimistically high when negatives dominate (false positive rate stays low). PR curve focuses on the positive class, which is the minority in imbalanced settings.

**Q3: How does focal loss help with class imbalance?**

Focal loss down-weights easy examples (mostly majority class) and focuses on hard examples (often minority). The (1-pₜ)^γ term reduces loss contribution from well-classified samples.

**Q4: When would you use algorithmic methods over resampling?**

Algorithmic (class weights, focal loss): no data duplication, no synthetic noise, but requires model support. Resampling: model-agnostic, but can cause overfitting (oversampling) or information loss (undersampling).

**Q5: What is the MCC and why is it better than F1 for imbalanced data?**

MCC uses all four confusion matrix cells and gives a balanced measure even when classes are very imbalanced. F1 ignores true negatives and can be inflated by predicting the majority class.

**Q6: How do you set the decision threshold for an imbalanced classifier?**

Move threshold from 0.5 toward the minority class to increase recall (at cost of precision). Choose threshold that maximizes F1, minimizes cost, or achieves desired recall rate on validation data.

**Q7: Can too much SMOTE oversampling hurt performance?**

Yes — generating too many synthetic minority samples can:
- Create unrealistically dense minority regions.
- Bridge the gap between classes (noise).
- Cause overfitting to synthetic patterns.
- Make the model think minority is more common than it is.

**Q8: Compare SMOTE with ADASYN.**

Both generate synthetic minority samples. ADASYN adaptively generates more samples for harder-to-learn examples (where majority neighbors dominate), while SMOTE generates uniformly. ADASYN can amplify noise in borderline regions.

**Q9: How do you evaluate an anomaly detection model when anomalies are rare (< 1%)?**

Use precision@k (check precision among top-k predicted anomalies), recall@k, or average precision (PR-AUC). Avoid accuracy (99% trivial). MCC also works well.

**Q10: What is cost-sensitive learning and how do you implement it?**

Cost-sensitive learning assigns asymmetric costs to different errors. Implement via: (1) weighted loss function, (2) cost-sensitive decision thresholds, (3) MetaCost (relabel training data by minimum expected cost), or (4) sampling with class weights.

---

## Quick Reference — Key Formulas

| Topic | Formula |
|-------|---------|
| OLS estimator | β̂ = (XᵀX)⁻¹Xᵀy |
| Ridge estimator | β̂ = (XᵀX + λI)⁻¹Xᵀy |
| Lasso objective | min ||y - Xβ||² + λ||β||₁ |
| Logistic sigmoid | p = 1 / (1 + e^{-Xβ}) |
| Log-odds | log(p/(1-p)) = Xβ |
| Log-loss | -[y log(p) + (1-y)log(1-p)] |
| R² | 1 - SS_res/SS_tot |
| Adjusted R² | 1 - (1-R²)(n-1)/(n-p-1) |
| AIC | 2k - 2ln(L̂) |
| BIC | k ln(n) - 2ln(L̂) |
| Gini impurity | 1 - Σ pₖ² |
| Entropy | -Σ pₖ log₂(pₖ) |
| Information gain | H(parent) - Σ w_child H(child) |
| SVM dual | max Σαᵢ - ½ΣΣαᵢαⱼyᵢyⱼK(xᵢ,xⱼ) |
| Hinge loss | max(0, 1 - y·f(x)) |
| k-means objective | Σₖ Σ||xᵢ - μₖ||² |
| Silhouette | (b(i)-a(i))/max(a(i),b(i)) |
| Homogeneity | 1 - H(C|K)/H(C) |
| Completeness | 1 - H(K|C)/H(K) |
| V-Measure | (1+β)hc/(βh+c) |
| ARI | (RI - E[RI])/(max(RI)-E[RI]) |
| GMM E-step | γᵢₖ = πₖN(xᵢ|μₖ,Σₖ)/ΣπⱼN(xᵢ|μⱼ,Σⱼ) |
| PCA eigenvalue | Σw = λw |
| SMOTE | x_new = x + λ(x_neighbor - x) |
| Focal loss | -(1-pₜ)^γ log(pₜ) |
| MCC | (TP×TN - FP×FN)/√((TP+FP)(TP+FN)(TN+FP)(TN+FN)) |

---

> Created for the AI Academy — Java Learning Lab